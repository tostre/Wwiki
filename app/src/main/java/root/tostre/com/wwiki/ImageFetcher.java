package root.tostre.com.wwiki;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Picture;
import android.os.AsyncTask;
import android.util.DisplayMetrics;
import android.view.WindowManager;

import com.caverock.androidsvg.SVG;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

/**
 * Downloads the articles image
 */

public class ImageFetcher extends AsyncTask<String, Void, Bitmap> {

    private Context context;
    private InputStream inputStream;
    private ImageFetcherCallback callback;
    private DocumentBuilderFactory documentBuilderFactory;
    private DocumentBuilder documentBuilder;
    private Document document;
    private NodeList tagList;
    private Node tagNode;
    private Element tagElement;
    private String imgUrl;
    private Bitmap image;
    private String imgXmlUrl;

    public ImageFetcher(Context context, ImageFetcherCallback callback){
        this.context = context;
        this.callback = callback;
        image = null;

        try {
            documentBuilderFactory = DocumentBuilderFactory.newInstance();
            documentBuilder = documentBuilderFactory.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        }
    }

    @Override // Called, when imageFetcher.execute() is started
    protected void onPreExecute() {
        super.onPreExecute();
    }

    // Downloads the article image
    protected Bitmap doInBackground(String... urls) {
        imgXmlUrl = urls[0];

        try {
            inputStream = new URL(imgXmlUrl).openStream();

            StringBuilder stringBuilder = new StringBuilder();

            try(Reader reader = new BufferedReader(new InputStreamReader(
                    inputStream, Charset.forName(StandardCharsets.UTF_8.name())))){
                        int c = 0;
                        while ((c = reader.read()) != -1){
                            stringBuilder.append((char) c);
                }
            }

            String imgXml = stringBuilder.toString();
            StringReader stringReader = new StringReader(imgXml);
            InputSource inputSource = new InputSource(stringReader);
            document = documentBuilder.parse(inputSource);
            document.getDocumentElement().normalize();
            tagList = document.getElementsByTagName("original");
            tagNode = tagList.item(0);
            tagElement = (Element) tagNode;
            imgUrl = tagElement.getAttribute("source");

            // Transorms a svg into a bitmap
            if(imgUrl.indexOf(".svg") != -1){
                inputStream = new java.net.URL(imgUrl).openStream();

                SVG svg = SVG.getFromInputStream(inputStream);
                Picture picture = svg.renderToPicture();
                Bitmap bitmap = Bitmap.createBitmap(picture.getWidth(), picture.getHeight(), Bitmap.Config.ARGB_8888);
                Canvas canvas = new Canvas(bitmap);
                canvas.drawPicture(picture);
                image = bitmap;
            } else {
                inputStream = new java.net.URL(imgUrl).openStream();
                image = BitmapFactory.decodeStream(inputStream);
            }

            image = scaleDownBitmap(image);

            inputStream.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return image;

    }

    // Sets image in mainactivity
    protected void onPostExecute(Bitmap image) {
        callback.imageFetched(image);
    }

    // scales down the bitmap proportionally so that it fits the screens width
    private Bitmap scaleDownBitmap(Bitmap image){

        DisplayMetrics displayMetrics = new DisplayMetrics();
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        wm.getDefaultDisplay().getMetrics(displayMetrics);

        int screenWidth = displayMetrics.widthPixels;
        int imageHeight = image.getHeight();
        int imageWidth = image.getWidth();
        double scaleFactor = ((double)imageWidth)/screenWidth;

        imageHeight = (int) (imageHeight/scaleFactor);
        imageWidth = screenWidth;

        image = Bitmap.createScaledBitmap(image, imageWidth, imageHeight, true);

        return image;
    }

    public interface ImageFetcherCallback{
        abstract void imageFetched(Bitmap bitmap);
    }
}
