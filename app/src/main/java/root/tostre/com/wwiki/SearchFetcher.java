package root.tostre.com.wwiki;

import android.os.AsyncTask;
import android.view.View;
import android.widget.ProgressBar;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by Macel on 13.05.17.
 * Handles the communications with the server when
 * searching for an article
 */

public class SearchFetcher extends AsyncTask<String, Void, String> {

    private ArrayList<String> articleList;
    private SearchActivity searchActivity;
    private ProgressBar progressBar;

    // Constructor
    public SearchFetcher(SearchActivity searchActivity){
        this.searchActivity = searchActivity;
        articleList = new ArrayList<String>();
    }

    @Override // Start the progress-circle to spin
    protected void onPreExecute() {
        super.onPreExecute();
        progressBar = (ProgressBar) searchActivity.findViewById(R.id.search_results_progressBar);
        progressBar.setVisibility(View.VISIBLE);
    }

    @Override // Sends seach to server, receives answer, creates list from answers
    protected String doInBackground(String... params) {
        String searchUrl = createUrl(params[0],params[1]);

        try {
            InputStream inputStream = new URL(searchUrl).openStream();

            BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
            StringBuilder sb = new StringBuilder();

            String line = null;

            while ((line = br.readLine()) != null) {
                sb.append(line + "\n");
            }

            JSONObject result = new JSONObject(sb.toString());
            JSONArray search = result.getJSONObject("query").getJSONArray("search");

            if(search.length() > 0){

                for(int i = 0; i < search.length(); i++){
                    JSONObject res = search.getJSONObject(i);
                    articleList.add(res.getString("title"));
                }

                inputStream.close();
            } else {
                articleList.add("No results");
            }

        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }


        return null;
    }

    @Override // Calls to update the search-result-list, hides loading spinner
    protected void onPostExecute(String s) {
        //super.onPostExecute(articleList);
        searchActivity.updateSearchResultsList(articleList);
        progressBar.setVisibility(View.GONE);

    }

    // Creates an url from the title of a page
    public String createUrl(String searchTerm, String apiEndpointSearch){
        searchTerm = searchTerm.replaceAll("\\s+", "%20");
        String newSearchUrl = apiEndpointSearch + searchTerm;
        return newSearchUrl;
    }

}
