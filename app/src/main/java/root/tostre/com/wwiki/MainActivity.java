package root.tostre.com.wwiki;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;

public class MainActivity extends AppCompatActivity{

    private Article article;
    private String currentFragment = "reader";
    private ReaderFragment readerFragment;
    private SavedFragment savedFragment;
    private RecentsFragment recentsFragment;
    private int loadingStatus = 0;
    private NestedScrollView content_container;

    private NetworkInfo networkInfo;
    private ArrayList<String> recentArticles;
    private String myTable = "<table border=1>" +
            "<tr>" +
            "<td>row 1, cell 1</td>" +
            "<td>row 1, cell 2</td>" +
            "</tr>" +
            "<tr>" +
            "<td>row 2, cell 1</td>" +
            "<td>row 2, cell 2</td>" +
            "</tr>" +
            "</table>";
    //
    private TouchyWebView wv;


    /**
     * Initializes the view and sets up variables that are used
     * later in the app
     */

    // Controls whats happens when the view is created
    protected void onCreate(Bundle savedInstanceState) {
        // Sets main activity as the initial view
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Create bottom menu_navigation and tollbar
        setBottomBar();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Hide the loading spinner on start
        ProgressBar progressBar = (ProgressBar) findViewById(R.id.image_progressBar);
        progressBar.setVisibility(View.GONE);

        // Initialize WebView
        wv = (TouchyWebView) findViewById(R.id.content_text);
        //wv = (WebView) findViewById(R.id.content_text);
        //wv.getSettings().setJavaScriptEnabled(true);

        // Save data that is frequently used
        content_container = (NestedScrollView) findViewById(R.id.content_container);

        // Sets reader fragment as the initial view
        changeContentView("reader");


        createDefaultWikis();
    }

    private void test(){
        SharedPreferences sharedPref = getSharedPreferences("tostre.wwiki.wikilist", Context.MODE_PRIVATE);
        Map<String, ?> values = sharedPref.getAll();

        for(Map.Entry<String,?> entry : values.entrySet()){
            Log.d("DBG",entry.getKey() + ": " +
                    entry.getValue().toString());
        }
    }

    private void createDefaultWikis(){
        SharedPreferences sharedPref = getSharedPreferences("tostre.wwiki.wikilist", Context.MODE_PRIVATE);

        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("Wikipedia (EN)", "https://en.wikipedia.org/w/api.php");
        editor.putString("Wikipedia (DE)", "https://de.wikipedia.org/w/api.php");
        editor.apply();
    }


    /**
     * Additional view initialization: Sets up the toolbar (and
     * its behavior) and the bottombar (and its behavior)
     */

    @Override // Sets the menu defined in xml as the apps menu
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_overflow, menu);
        return true;
    }

    @Override // Called by calling invalidateOptionsMenu(), whenever menu_overflow needs updating
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem item_search = menu.findItem(R.id.overflow_search);
        MenuItem item_save = menu.findItem(R.id.overflow_save);
        MenuItem item_delete = menu.findItem(R.id.overflow_delete);

        switch (currentFragment){
            case "reader":
                item_search.setVisible(true);
                item_save.setVisible(true);
                item_delete.setVisible(false);
                break;
            case "saved":
                item_search.setVisible(false);
                item_save.setVisible(false);
                item_delete.setVisible(true);
                break;
            case "recents":
                item_search.setVisible(false);
                item_save.setVisible(false);
                item_delete.setVisible(true);
                break;
            default:
                break;
        }
        return super.onPrepareOptionsMenu(menu);
    }

    // Defines what happens when an menu_overflow-menu item is selected
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.overflow_search:
                Intent intent = new Intent(this, SearchActivity.class);
                startActivityForResult(intent, 1);
                return true;
            case R.id.overflow_save:
                // Debug method
                updateArticleText("titke", getResources().getString(R.string.large_text));
                return true;
            case R.id.overflow_delete:
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    // Initializes bottom bar behavior
    private void setBottomBar() {
        BottomNavigationView.OnNavigationItemSelectedListener navigationSelectedListener = new BottomNavigationView.OnNavigationItemSelectedListener() {
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.navigation_reader:
                        changeContentView("reader");
                        return true;
                    case R.id.navigation_saved:
                        changeContentView("saved");
                        return true;
                    case R.id.navigation_recents:
                        changeContentView("recents");
                        return true;
                }
                return false;
            }
        };

        // Controls behavior when navBar item is selected, that's already active
        BottomNavigationView.OnNavigationItemReselectedListener navigationReselectedListener = new BottomNavigationView.OnNavigationItemReselectedListener() {
            @Override
            public void onNavigationItemReselected(@NonNull MenuItem item) {
                content_container.fullScroll(View.FOCUS_UP);
            }
        };

        BottomNavigationView bottomNavigationViewBar = (BottomNavigationView) findViewById(R.id.navigation);
        bottomNavigationViewBar.setOnNavigationItemSelectedListener(navigationSelectedListener);
        bottomNavigationViewBar.setOnNavigationItemReselectedListener(navigationReselectedListener);
    }

    /**
     * This block opens the searchActivity, gets its arguments from
     * when it's closed and gives it to the view and article updater
     * methods
     */

    @Override // Gets called when searchActivity has finished, gets jsonUrls from its intent
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        networkInfo = connectivityManager.getActiveNetworkInfo();

        // Receives the url from the searchActivity and loads the article
        if(requestCode == 1 && resultCode == RESULT_OK && networkInfo != null){
            // One instance of an AsyncTask can only be executed once, therefore
            // for every exectuion there must be created a new instance
            ArticleFetcher articleFetcher = new ArticleFetcher(this);
            ImageFetcher imageFetcher = new ImageFetcher(this);

            articleFetcher.execute(data.getStringExtra("articleJsonUrl"));
            imageFetcher.execute(data.getStringExtra("imageJsonUrl"));
        }
    }

    /**
     * This block gets its arguments from the searchActivity (see block
     * above), takes them, updates the article object and the view
     * with them
     */

    // Updates the text-related values in the article, updates view; called from articleFetcher
    public void updateArticleText(String title, String text){
        ((CollapsingToolbarLayout) findViewById(R.id.collapsingToolbar)).setTitle(title);
        /*
        text = "<HTML>\n" +
                "<HEAD>\n" +
                "<TITLE>Your Title Here</TITLE>\n" +
                "</HEAD>\n" +
                "<BODY BGCOLOR=\"FFFFFF\">\n" +
                "<CENTER><IMG SRC=\"clouds.jpg\" ALIGN=\"BOTTOM\"> </CENTER>\n" +
                "<HR>\n" +
                "<a href=\"http://somegreatsite.com\">Link Name</a>\n" +
                "is a link to another nifty site\n" +
                "<H1>This is a Header</H1>\n" +
                "<H2>This is a Medium Header</H2>\n" +
                "Send me mail at <a href=\"mailto:support@yourcompany.com\">\n" +
                "support@yourcompany.com</a>.\n" +
                "<P> This is a new paragraph!\n" +
                "<P> <B>This is a new paragraph!</B>\n" +
                "<BR> <B><I>This is a new sentence without a paragraph break, in bold italics.</I></B>\n" +
                "<HR>\n" +
                "</BODY>\n" +
                "</HTML>";

        text = "<table border=1>" +
                "<tr>" +
                "<td>row 1, cell 1</td>" +
                "<td>row 1, cell 2</td>" +
                "</tr>" +
                "<tr>" +
                "<td>row 2, cell 1</td>" +
                "<td>row 2, cell 2</td>" +
                "</tr>" +
                "</table>";*/

        ((WebView) findViewById(R.id.content_text)).loadData(text, "text/html; charset=utf-8", "utf-8");

        loadingStatus++;
        if(loadingStatus == 2){
            (findViewById(R.id.image_progressBar)).setVisibility(View.GONE);
            loadingStatus = 0;
        }

        readerFragment = (ReaderFragment) getSupportFragmentManager().findFragmentByTag("reader");
        //readerFragment.updateView(title, text);
    }

    // Updates the img-related values in the article, updates view; called from imageFetcher
    public void updateArticleImage(Bitmap image, String imgUrl){

        // Check if there's an image article
        if(image != null){
            ((ImageView) findViewById(R.id.article_image)).setImageBitmap(image);
        } else {
            // Set the default image
            ((ImageView) findViewById(R.id.article_image)).setImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.debug_stack));
        }




    }

    /**
     * Oh boy, this is a big one. This one methods with the measurements of a block
     * handles the switching of views, when the article is clicked, what action items
     * to display and how the appbar behaves
     */

    // Changes the fragment depending on the bottom menu_navigation item pressed
    private void changeContentView(String newFragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        AppBarLayout appBarLayout = (AppBarLayout) findViewById(R.id.appBarLayout);
        CollapsingToolbarLayout collapsingToolbar = (CollapsingToolbarLayout) findViewById(R.id.collapsingToolbar);
        AppBarLayout.LayoutParams params = (AppBarLayout.LayoutParams) collapsingToolbar.getLayoutParams();

        currentFragment = newFragment;

        switch (newFragment) {
            case "reader":

                if (readerFragment == null){
                    readerFragment = ReaderFragment.newInstance("", "");
                }

                fragmentTransaction.replace(R.id.content_container, ReaderFragment.newInstance("", ""), "reader");
                // Enables collapsing toolbar
                content_container.setNestedScrollingEnabled(true);
                params.setScrollFlags(AppBarLayout.LayoutParams.SCROLL_FLAG_SCROLL | AppBarLayout.LayoutParams.SCROLL_FLAG_ENTER_ALWAYS | AppBarLayout.LayoutParams.SCROLL_FLAG_ENTER_ALWAYS_COLLAPSED);
                invalidateOptionsMenu();
                break;

            case "saved":

                if (savedFragment == null){
                    savedFragment = SavedFragment.newInstance();
                }

                // Replaces children of content_container with new fragment
                fragmentTransaction.replace(R.id.content_container, savedFragment);
                // Disables collapsing toolbar
                appBarLayout.setExpanded(false);
                content_container.setNestedScrollingEnabled(false);
                params.setScrollFlags(AppBarLayout.LayoutParams.SCROLL_FLAG_SCROLL | AppBarLayout.LayoutParams.SCROLL_FLAG_EXIT_UNTIL_COLLAPSED);
                invalidateOptionsMenu();
                break;

            case "recents":

                if (recentsFragment == null){
                    recentsFragment = RecentsFragment.newInstance();
                }
                // Replaces children of content_container with new fragment
                fragmentTransaction.replace(R.id.content_container, recentsFragment);
                // Disables collapsing toolbar
                appBarLayout.setExpanded(false);
                content_container.setNestedScrollingEnabled(false);
                params.setScrollFlags(AppBarLayout.LayoutParams.SCROLL_FLAG_SCROLL | AppBarLayout.LayoutParams.SCROLL_FLAG_EXIT_UNTIL_COLLAPSED);
                invalidateOptionsMenu();
                break;

            default:
                break;
        }
        fragmentTransaction.commit();
    }


















}