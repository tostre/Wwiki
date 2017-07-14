package root.tostre.com.wwiki;

import android.app.Activity;
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
import android.text.InputType;
import android.text.Layout;
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
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Map;
import java.util.Timer;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        // Creates and populates the WIki-Chooser
        Spinner wikiChooser = (Spinner) findViewById(R.id.wiki_chooser);
        //ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.wikis, R.layout.subfragment_spinner);
        //ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.subfragment_spinner);


        SharedPreferences sharedPref = getSharedPreferences("tostre.wwiki.wikilist", Context.MODE_PRIVATE);
        Map<String, ?> wikis = sharedPref.getAll();

        ArrayList<String> wikiNames = new ArrayList<>();
        ArrayList<String> wikiEndpoints = new ArrayList<>();

        for(Map.Entry<String,?> entry : wikis.entrySet()){
            Log.d("DBG",entry.getKey() + ": " +
                    entry.getValue().toString());
            wikiNames.add(entry.getKey());
            wikiEndpoints.add((String) entry.getValue());
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.subfragment_spinner, wikiNames);


        // Apply the adapter to the spinner
        wikiChooser.setAdapter(adapter);
        setSpinnerListener(wikiChooser);
        // Sets listener on listView
        startArticleLoaderFromList();
        getSupportActionBar().setElevation(0);

        ProgressBar progressBar = (ProgressBar) findViewById(R.id.search_results_progressBar);
        progressBar.setVisibility(View.GONE);
    }

    private void setSpinnerListener(Spinner wikiChooser){
        wikiChooser.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch(position){
                    case 0:
                        apiEndpointArticle = "https://en.wikipedia.org/w/api.php?format=json&redirects=yes&action=parse&disableeditsection=true&page=";
                        apiEndpointImg ="https://en.wikipedia.org/w/api.php?action=query&prop=pageimages&piprop=original&format=json&titles=";
                        apiEndpointSearch = "https://en.wikipedia.org/w/api.php?action=query&list=search&srlimit=20&format=json&srsearch=";

                        apiEndpointArticle = end + apiParamsArticle;

                        loadSearchResults();
                        break;
                    case 1:
                        apiEndpointArticle = "https://de.wikipedia.org/w/api.php?format=json&redirects=yes&action=parse&disableeditsection=true&page=";
                        apiEndpointImg ="https://de.wikipedia.org/w/api.php?action=query&prop=pageimages&piprop=original&format=json&titles=";
                        apiEndpointSearch = "https://de.wikipedia.org/w/api.php?action=query&list=search&srlimit=20&format=json&srsearch=";
                        loadSearchResults();
                        break;
                    case 2:
                        //new NewWikiDialog().show();
                        //AlertDialog.Builder mBuilder = new AlertDialog.Builder(SearchActivity.this);
                        //View mView = getLayoutInflater().inflate()

                        AlertDialog.Builder builder = new AlertDialog.Builder(SearchActivity.this);
                        builder.setTitle("Add new Wiki");

                        // Set up the input
                        final EditText input = new EditText(SearchActivity.this);
                        // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
                        input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                        //builder.setView(input);


                        LayoutInflater inflater = getLayoutInflater();
                        builder.setView(inflater.inflate(R.layout.dialog_newwiki, null));


                        // Set up the buttons
                        builder.setPositiveButton("Add", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //String m_Text = input.getText().toString();
                                //String wikiname = findViewById(R.id.newwiki_name).toString();
                                //String wikiendpoint = findViewById(R.id.newwiki_endpoint).toString();
                                saveWiki("Wikipedia (NL)", "https://nl.wikipedia.com");
                            }
                        });

                        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });

                        builder.show();
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    private void saveWiki(String wiki, String endpoint){
        /*Context context = SearchActivity.this;
        SharedPreferences sharedPreferences = context.getSharedPreferences(getString(R.string.shared_preference_wikis), Context.MODE_PRIVATE);

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("Wikipedia (DE)", "Wikipedia DE");
        editor.commit();*/

        // Gain acces to file
        SharedPreferences sharedPref = getSharedPreferences("tostre.wwiki.wikilist", Context.MODE_PRIVATE);

        SharedPreferences.Editor editor = sharedPref.edit();
        //editor.putString(wiki, endpoint);
        //editor.putString("wiki2", "endpoint");
        //editor.putString("wiki3", "endpoint2");
        editor.apply();

        Map<String, ?> values = sharedPref.getAll();

        for(Map.Entry<String,?> entry : values.entrySet()){
            Log.d("DBG",entry.getKey() + ": " +
                    entry.getValue().toString());
        }


        // SHow saved data
        String name = sharedPref.getString("Wikipedia (NL)", "");
        Log.d("DBG", "sharedPRef: " + name);



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
                        SearchFetcher searchFetcher = new SearchFetcher(SearchActivity.this);
                        searchFetcher.execute(searchView.getQuery().toString(), apiEndpointSearch);
                        loadSearchResults();
                        Log.d("DBG", "runnable");
                        Log.d("DBG", "getQuery" + searchView.getQuery().toString());
                        Log.d("DBG", "searchEP" + apiEndpointSearch);
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
