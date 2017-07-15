package root.tostre.com.wwiki;


import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.safety.Whitelist;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.URL;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import root.tostre.com.wwiki.MainActivity;
import root.tostre.com.wwiki.R;

// Async task should be used to make short calculations
// in the background and publish the results on the ui
// It takes 3 parameters:
// 1: String: Used to pass as parameters for doInBackground
// 2: progress type: Type of variable used in doBackground when publishProgress is called
// 3: result: End result, gets returned by doInBackground, also parameter for onPostExecute
public class ArticleFetcher extends AsyncTask<String, Void , ArrayList<String>>{

    private InputStream inputStream;
    private MainActivity mainActivity;
    private DocumentBuilderFactory documentBuilderFactory;
    private DocumentBuilder documentBuilder;
    private Document document;
    private NodeList tagList;
    private Node tagNode;
    private Element tagElement;
    private String articleJsonUrl;
    ArrayList<String> articleTextArray;
    private ProgressBar progressBar;


    // Called when an ArticleFetcher object is created
    public ArticleFetcher(MainActivity mainActivity){
        this.mainActivity = mainActivity;
    }

    @Override
    protected void onPreExecute() {
        progressBar = (ProgressBar) mainActivity.findViewById(R.id.image_progressBar);
        progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    protected ArrayList<String> doInBackground(String... urls) {
        articleJsonUrl = urls[0];
        articleTextArray = new ArrayList<String>();

        try {
            documentBuilderFactory = DocumentBuilderFactory.newInstance();
            documentBuilder = documentBuilderFactory.newDocumentBuilder();
            inputStream = new URL(articleJsonUrl).openStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
            StringBuilder sb = new StringBuilder();
            String line = "";
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }

            JSONObject json = new JSONObject(sb.toString());

            document = documentBuilder.parse(new InputSource(new StringReader(json.getJSONObject("parse").getJSONObject("text").getString("*"))));
            document.getDocumentElement().normalize();

            articleTextArray.add(json.getJSONObject("parse").getString("title"));


            String textp = json.getJSONObject("parse").getJSONObject("text").getString("*");
            textp = new HtmlCleaner().cleanHtmlString(textp);
            articleTextArray.add(textp);
            // Add the url to the articleArray
            articleTextArray.add(articleJsonUrl);
            inputStream.close();

        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        } /*catch (SAXException e) {
            e.printStackTrace();
        }*/ catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        }
        return articleTextArray;
    }


    @Override // Called when doInBackground is finished
    protected void onPostExecute(ArrayList<String> articleTextArray) {
        mainActivity.updateArticleText(articleTextArray.get(0), articleTextArray.get(1));

        try {
            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }








}


