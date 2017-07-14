package root.tostre.com.wwiki;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.Map;

public class SearchActivity extends AppCompatActivity {

    private ArrayList<String> articleList;
    private ArrayList<String> urlList;
    private String url;
    private String apiEndpointArticle = "https://en.wikipedia.org/w/api.php?format=json&redirects=yes&action=parse&disableeditsection=true&page=";
    private String apiEndpointImg ="https://en.wikipedia.org/w/api.php?action=query&prop=pageimages&piprop=original&format=json&titles=";
    private String apiEndpointSearch = "https://en.wikipedia.org/w/api.php?action=query&list=search&srlimit=20&format=json&srsearch=";

    private String end = "https://en.wikipedia.org/w/api.php";
    private String art = "water";

    private String apiParamsArticle = "?format=json&redirects=yes&action=parse&disableeditsection=true&page=";
    private String apiParamsImg = "?action=query&prop=pageimages&piprop=original&format=json&titles=";
    private String apiParamsSearch = "?action=query&list=search&srlimit=20&format=json&srsearch=";

    private Handler handler = new Handler();
    private Runnable runnable;
    private Menu menu;
    private Spinner wikiChooser;
    private ArrayList<String> nameList = new ArrayList<>();
    private ArrayList<String> endpointList = new ArrayList<>();

    private SharedPreferences sharedPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        // Creates and populates the WIki-Chooser
        wikiChooser = (Spinner) findViewById(R.id.wiki_chooser);

        populateSpinner();
        setSpinnerListener(wikiChooser);


        // Sets listener on listView
        startArticleLoaderFromList();
        getSupportActionBar().setElevation(0);

        ProgressBar progressBar = (ProgressBar) findViewById(R.id.search_results_progressBar);
        progressBar.setVisibility(View.GONE);
    }

    private void populateSpinner(){
        sharedPref = getSharedPreferences("tostre.wwiki.wikilist", Context.MODE_PRIVATE);
        Map<String, ?> wikis = sharedPref.getAll();
        // Clear spinner, so entries don't appear double when new wiki was saved
        nameList.clear();
        wikiChooser.setAdapter(null);
        // Stores names and enpoints from shared preferences in arraylists
        for(Map.Entry<String,?> entry : wikis.entrySet()){
            nameList.add(entry.getKey());
            endpointList.add((String) entry.getValue());
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.subfragment_spinner, nameList);
        // Apply the adapter to the spinner
        wikiChooser.setAdapter(adapter);
    }

    public void openSaveDialog(View view){
        AlertDialog.Builder builder = new AlertDialog.Builder(SearchActivity.this);
        builder.setTitle("Add new Wiki");
        final LayoutInflater inflater = getLayoutInflater();
        builder.setView(inflater.inflate(R.layout.dialog_newwiki, null));

        // Set up buttons and their behavior
        builder.setPositiveButton("Add", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Dialog newWikiDialog = (Dialog) dialog;
                // Get text from input fields
                EditText nameInput = (EditText) newWikiDialog.findViewById(R.id.newwiki_name);
                EditText endpointInput = (EditText) newWikiDialog.findViewById(R.id.newwiki_endpoint);
                String newWiki = nameInput.getText().toString();
                String newEndpoint = endpointInput.getText().toString();
                // Save new wiki to shared preferences
                saveWiki(newWiki, newEndpoint);
                populateSpinner();
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }

    // Adds listener to entries in spinner and updates the api-urls when tapped
    private void setSpinnerListener(Spinner wikiChooser){
        wikiChooser.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                apiEndpointArticle = endpointList.get(position) + apiParamsArticle;
                apiEndpointImg = endpointList.get(position) + apiParamsImg;
                apiEndpointSearch = endpointList.get(position) + apiParamsSearch;
                loadSearchResults();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    // Saves params to wiki-shared preferences
    private void saveWiki(String newWiki, String newEndpoint){
        SharedPreferences sharedPref = getSharedPreferences("tostre.wwiki.wikilist", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(newWiki, newEndpoint);
        editor.apply();
    }



    @Override //
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_search, menu);
        MenuItem searchItem = menu.findItem(R.id.action_search);
        this.menu = menu;

        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        searchItem.expandActionView();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                // Commit search after user has stopped typing for 300ms
                handler.removeCallbacks(runnable);

                runnable = new Runnable() {
                    @Override
                    public void run() {
                        loadSearchResults();
                    }
                };

                handler.postDelayed(runnable, 300);

                return true;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }

    private void loadSearchResults(){
        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);

        if(searchView.getQuery().toString() != null && !searchView.getQuery().toString().isEmpty()){
            SearchFetcher searchFetcher = new SearchFetcher(SearchActivity.this);
            searchFetcher.execute(searchView.getQuery().toString(), apiEndpointSearch);
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK:
            finish();
        }
        return false;
    }

    // Creates listener for listView that load the article from the listitem
    private void startArticleLoaderFromList(){
        final ListView search_results = (ListView) findViewById(R.id.search_results);

        search_results.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String selectedFromList = (String) search_results.getItemAtPosition(position);

                // Creates a searchable URL from the item clicked in the list
                String url = extractUrl(position);
                String imgUrl = extractImageUrl(position);
                // Ends the activity and sends the url back to the main activity
                Intent intent = new Intent();
                intent.putExtra("articleJsonUrl", url);
                intent.putExtra("imageJsonUrl", imgUrl);
                setResult(Activity.RESULT_OK, intent);
                finish();
            }
        });
    }

    // Updates the listView with search results gotten from the SearchFetcher
    public void updateSearchResultsList(ArrayList<String> articleList){
        this.articleList = articleList;
        // Populates the listview with the search results
        ListView listView = (ListView) findViewById(R.id.search_results);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.subfragment_listitems, articleList);
        listView.setAdapter(adapter);
    }

    // Creates an url from the title of a page
    public String extractUrl(int position){
        urlList = new ArrayList<String>();
        String title = articleList.get(position);
        title = title.replaceAll("\\s+", "%20");
        url = apiEndpointArticle + title;

        return url;
    }

    // Creates an imageurl from the title of a page
    public String extractImageUrl(int position){
        urlList = new ArrayList<String>();
        String title = articleList.get(position);
        title = title.replaceAll("\\s+", "%20");
        url = apiEndpointImg + title;

        return url;
    }

}
