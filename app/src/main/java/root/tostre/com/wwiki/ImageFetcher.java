package root.tostre.com.wwiki;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.DisplayMetrics;
import android.util.JsonReader;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.ProgressBar;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

public class ImageFetcher extends AsyncTask<String, Void, Bitmap> {

    private InputStream inputStream;
    private MainActivity mainActivity;
    private DocumentBuilderFactory documentBuilderFactory;
    private DocumentBuilder documentBuilder;
    private Document document;
    private NodeList tagList;
    private Node tagNode;
    private Element tagElement;
    private String imgUrl;
    private Bitmap image;
    private String imgXmlUrl;
    private ProgressBar progressBar;

    public ImageFetcher(MainActivity mainActivity){
        this.mainActivity = mainActivity;
        image = null;

        try {
            documentBuilderFactory = DocumentBuilderFactory.newInstance();
            documentBuilder = documentBuilderFactory.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        progressBar = (ProgressBar) mainActivity.findViewById(R.id.image_progressBar);
        progressBar.setVisibility(View.VISIBLE);
    }

    protected Bitmap doInBackground(String... urls) {
        imgXmlUrl = urls[0];

        /*
        try {
            inputStream = new URL(imgXmlUrl).openStream();
            document = documentBuilder.parse(inputStream);
            document.getDocumentElement().normalize();
            tagList = document.getElementsByTagName("original");
            tagNode = tagList.item(0);
            tagElement = (Element) tagNode;
            imgUrl = tagElement.getAttribute("source");
            inputStream = new java.net.URL(imgUrl).openStream();
            image = BitmapFactory.decodeStream(inputStream);
            //Bitmap b = BitmapFactory.decode
            image = scaleDownBitmap(image);
            inputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return image;*/

        String result = null;

        try{
            inputStream = new URL(imgXmlUrl).openStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"), 8);
            StringBuilder sb = new StringBuilder();

            String line = null;
            while ((line = reader.readLine()) != null)
            {
                sb.append(line + "\n");
            }
            result = sb.toString();

            JSONObject jobject = new JSONObject(result);
            JSONObject jImage = jobject.getJSONObject("original");

            String imgUrl = jImage.getString("source");

            inputStream = new java.net.URL(imgUrl).openStream();
            image = BitmapFactory.decodeStream(inputStream);
            //Bitmap b = BitmapFactory.decode
            image = scaleDownBitmap(image);
            inputStream.close();



        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return image;

    }

    protected void onPostExecute(Bitmap image) {
        // Set image in mainactivity
        //((ImageView) mainActivity.findViewById(R.id.article_image)).setImageBitmap(image);
        mainActivity.updateArticleImage(image, imgUrl);
        progressBar.setVisibility(View.GONE);
    }

    // scales down the bitmap proportionally so that it fits the screens width
    private Bitmap scaleDownBitmap(Bitmap image){

        DisplayMetrics displayMetrics = new DisplayMetrics();
        mainActivity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

        int screenWidth = displayMetrics.widthPixels;
        int imageHeight = image.getHeight();
        int imageWidth = image.getWidth();
        int scaleFactor = imageWidth/screenWidth;

        imageHeight = imageHeight/scaleFactor;
        imageWidth = screenWidth;

        image = Bitmap.createScaledBitmap(image, imageWidth, imageHeight, true);

        Log.d("DBG", "imgHeight: " + Integer.toString(imageHeight) + "\n");
        Log.d("DBG", "imgWidth: " + Integer.toString(imageWidth) + "\n");

        return image;
    }






}
