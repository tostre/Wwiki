package root.tostre.com.wwiki;

import android.os.AsyncTask;
import android.view.View;
import android.widget.ProgressBar;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by Macel on 07.05.17.
 * Downloads the articles and passes them to the MainActivity
 */

public class ArticleFetcher extends AsyncTask<String, Void , ArrayList<String>>{

    private InputStream inputStream;
    private ArticleFetcherCallback callback;
    private String articleJsonUrl;
    private ArrayList<String> articleTextArray;
    private ProgressBar progressBar;

    // Called when an ArticleFetcher object is created
    public ArticleFetcher(ArticleFetcherCallback callback){
        this.callback = callback;
    }

    @Override // Called, when articleFetcher.execute() is started
    protected void onPreExecute() {

    }

    @Override // Saves the articles data (text, title, etc.) in an array
    protected ArrayList<String> doInBackground(String... urls) {
        articleJsonUrl = urls[0];
        articleTextArray = new ArrayList<String>();

        try {
            inputStream = new URL(articleJsonUrl).openStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
            StringBuilder sb = new StringBuilder();
            String line = "";
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }

            JSONObject json = new JSONObject(sb.toString());
            String textp = new HtmlCleaner().cleanHtmlString(json.getJSONObject("parse").getJSONObject("text").getString("*"));

            // Add the url to the articleArray
            articleTextArray.add(json.getJSONObject("parse").getString("title"));
            articleTextArray.add(textp);
            articleTextArray.add(articleJsonUrl);

            inputStream.close();

        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return articleTextArray;
    }

    @Override // Called when doInBackground is finished
    protected void onPostExecute(ArrayList<String> articleTextArray) {
        callback.articleTextFetched(articleTextArray.get(0), articleTextArray.get(1));
        try {
            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public interface ArticleFetcherCallback{
        abstract void articleTextFetched(String title, String htmlContent);
    }
}


