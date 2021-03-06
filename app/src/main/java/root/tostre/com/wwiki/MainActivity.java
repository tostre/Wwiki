package root.tostre.com.wwiki;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Map;
import android.support.annotation.NonNull;

/**
 * MainActivity, connected to activity_main.xml
 * Handles everything that happens on the main ui
 */

public class MainActivity extends AppCompatActivity implements ArticleLoader.ArticleLoaderCallback{

    private Article article;
    private String currentFragment = "reader";
    private ReaderFragment readerFragment;
    private SavedFragment savedFragment;
    private RecentsFragment recentsFragment;
    private int loadingStatus = 0;
    private NestedScrollView content_container;
    private String text = "Try searching for an article in the upper right corner";
    private String title = "Wwiki";
    private String articleUrl;
    private String imgUrl;
    private Toolbar toolbar;
    private Menu menu;
    private ProgressBar progressBar;

    private ArrayList<Article> history = new ArrayList<>();
    private Article currentArticle;

    /**
     * Initializes the view and sets up variables that are used
     * later in the app
     */

    // Controls whats happens when the view is created
    protected void onCreate(Bundle savedInstanceState) {

        // Sets main activity as the initial view
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Create bottom menu_navigation and toolbar
        setBottomBar();
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Style the collapsing toolbar
        CollapsingToolbarLayout ct = (CollapsingToolbarLayout) findViewById(R.id.collapsingToolbar);
        ct.setCollapsedTitleTextColor(getResources().getColor(R.color.colorPrimary));
        ct.setExpandedTitleColor(getResources().getColor(R.color.white));
        ct.setContentScrimColor(getResources().getColor(R.color.colorToolbar));
        ct.setStatusBarScrimColor(getResources().getColor(R.color.colorStatusBar));

        // Sets the title of the last article
        SharedPreferences sharedPref = getSharedPreferences("tostre.wwiki.lastArticle", Context.MODE_PRIVATE);
        title = sharedPref.getString("lastTitle", "Wwiki");

        // Hide the loading spinner on start
        progressBar = (ProgressBar) findViewById(R.id.image_progressBar);
        progressBar.setVisibility(View.GONE);

        // Initialise content container (in which the fragments are inflated)
        content_container = (NestedScrollView) findViewById(R.id.content_container);

        // Sets reader fragment as the initial view
        changeFragment("reader");

        // Initialize default wiki
        createDefaultWikis();
    }

    // Fills the wikis-sharedPref with the default-wikis (DE & EN)
    private void createDefaultWikis(){
        SharedPreferences sharedPref = getSharedPreferences("tostre.wwiki.wikilist", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("Wikipedia (EN)", "https://en.wikipedia.org/w/api.php");
        editor.putString("Wikipedia (DE)", "https://de.wikipedia.org/w/api.php");
        editor.apply();

        Map<String, ?> wikis = sharedPref.getAll();
        ArrayList<String> wikiNames = new ArrayList<>();
        ArrayList<String> wikiEndpoints = new ArrayList<>();

        for(Map.Entry<String,?> entry : wikis.entrySet()){
            /*Log.d("DBG", "____________________________________________");
            Log.d("DBG",entry.getKey() + ": " + entry.getValue().toString());*/
            wikiNames.add(entry.getKey());
            wikiEndpoints.add((String) entry.getValue());
        }
    }

    @Override // Sets the menu defined in xml as the apps menu
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_overflow, menu);
        this.menu = menu;
        return true;
    }

    @Override // Called by calling invalidateOptionsMenu(), whenever menu_overflow needs updating
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem item_search = menu.findItem(R.id.overflow_search);
        MenuItem item_save = menu.findItem(R.id.overflow_save);
        MenuItem item_deleteRecents = menu.findItem(R.id.overflow_deleteRecents);
        MenuItem item_deleteSaved = menu.findItem(R.id.overflow_deleteSaved);

        switch (currentFragment){
            case "reader":
                item_search.setVisible(true);
                item_save.setVisible(true);
                item_deleteSaved.setVisible(false);
                item_deleteRecents.setVisible(false);

                SharedPreferences sharedPref = getSharedPreferences("tostre.wwiki.saved", Context.MODE_PRIVATE);

                // Update icon when article is saved or was already saved
                if(sharedPref.contains(title)){
                    menu.findItem(R.id.overflow_save).setIcon(getDrawable(R.drawable.icon_favorite_filled));
                } else {
                    menu.findItem(R.id.overflow_save).setIcon(getDrawable(R.drawable.icon_favorite_strokes));
                }

                break;
            case "saved":
                item_search.setVisible(false);
                item_save.setVisible(false);
                item_deleteSaved.setVisible(true);
                item_deleteRecents.setVisible(false);
                break;
            case "recents":
                item_search.setVisible(false);
                item_save.setVisible(false);
                item_deleteSaved.setVisible(false);
                item_deleteRecents.setVisible(true);
                break;
            default:
                break;
        }
        return super.onPrepareOptionsMenu(menu);
    }

    // Defines what happens when an menu_overflow-menu item is selected
    public boolean onOptionsItemSelected(MenuItem item) {
        SharedPreferences sharedPref;

        // Handle item selection
        switch (item.getItemId()) {
            case R.id.overflow_search:
                Intent intent = new Intent(this, SearchActivity.class);
                startActivityForResult(intent, 1);
                return true;
            case R.id.overflow_save:
                if(((CollapsingToolbarLayout) findViewById(R.id.collapsingToolbar)).getTitle() != "Wwiki"){
                    saveArticle();
                } else {
                    showSnackbar("Something went wrong. Try reloading");
                }
                return true;
            case R.id.overflow_deleteRecents:
                sharedPref = getSharedPreferences("tostre.wwiki.recentslist", Context.MODE_PRIVATE);
                sharedPref.edit().clear().apply();
                recentsFragment.populateRecentsList();
                showSnackbar("Articles removed from recents");
                return true;
            case R.id.overflow_deleteSaved:
                sharedPref = getSharedPreferences("tostre.wwiki.saved", Context.MODE_PRIVATE);
                sharedPref.edit().clear().apply();
                savedFragment.populateSavedList();
                showSnackbar("Articles deleted");
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
                        changeFragment("reader");
                        return true;
                    case R.id.navigation_saved:
                        changeFragment("saved");
                        return true;
                    case R.id.navigation_recents:
                        changeFragment("recents");
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
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

        // Receives the url from the searchActivity and loads the article
        if(requestCode == 1 && resultCode == RESULT_OK && networkInfo != null){
            articleUrl = data.getStringExtra("articleJsonUrl");
            imgUrl = data.getStringExtra("imageJsonUrl");
            // One instance of an AsyncTask can only be executed once, therefore
            // for every exectuion there must be created a new instance
            ArticleLoader articleLoader = new ArticleLoader(this,this);
            articleLoader.loadArticle(articleUrl, imgUrl);
            progressBar.setVisibility(View.VISIBLE);
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

        updateRecents(title, text);
        ((WebView) findViewById(R.id.content_text)).loadData(text, "text/html; charset=utf-8", "utf-8");

        SharedPreferences sharedPref = getSharedPreferences("tostre.wwiki.saved", Context.MODE_PRIVATE);

        if(sharedPref.contains(title)){
            menu.findItem(R.id.overflow_save).setIcon(getDrawable(R.drawable.icon_favorite_filled));
        } else {
            menu.findItem(R.id.overflow_save).setIcon(getDrawable(R.drawable.icon_favorite_strokes));
        }

        // Hide loading spinner only when both text and image have finished loading
        loadingStatus++;
        if(loadingStatus == 2){
            (findViewById(R.id.image_progressBar)).setVisibility(View.GONE);
            loadingStatus = 0;
        }
    }

    // Updates the img-related values in the article, updates view; called from imageFetcher
    public void updateArticleImage(Bitmap image){

        // Check if there's an image article
        if(image != null){
            ((ImageView) findViewById(R.id.article_image)).setImageBitmap(image);
        } else {
            // Set the default image
            ((ImageView) findViewById(R.id.article_image)).setImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.debug_stack));
        }

        // Hide loading spinner only when both text and image have finished loading
        loadingStatus++;
        if(loadingStatus == 2){
            (findViewById(R.id.image_progressBar)).setVisibility(View.GONE);
            loadingStatus = 0;
        }

    }

    // Saves all displayed articles into a shared preference
    private void updateRecents(String title, String text){
        // Update current title and text for later reference
        this.title = title;
        this.text = text;

        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MMM.yyyy");
        String date = dateFormat.format(calendar.getTime());

        // Saves all read past articles plus date
        SharedPreferences sharedPref = getSharedPreferences("tostre.wwiki.recentslist", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(title, date);
        editor.apply();

        // Saves the last Articles title
        SharedPreferences sharedPref2 = getSharedPreferences("tostre.wwiki.lastArticle", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor2 = sharedPref2.edit();
        sharedPref2.edit().clear().apply();
        editor2.putString("lastTitle", title);
        editor2.putString("lastText", text);
        editor2.apply();
    }

    // Adds the article title and its html to a shared preference
    private void saveArticle(){
        SharedPreferences sharedPref = getSharedPreferences("tostre.wwiki.saved", Context.MODE_PRIVATE);

        if(sharedPref.contains(title)){
            showSnackbar("Article already saved");
        } else {
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putString(title, text);
            editor.apply();
            showSnackbar("Article saved");


        }

        menu.findItem(R.id.overflow_save).setIcon(getDrawable(R.drawable.icon_favorite_filled));

/*
        Map<String, ?> values = sharedPref.getAll();
        for(Map.Entry<String,?> entry : values.entrySet()){
            //Log.d("DBG","Gespeichert!!!!!: " + entry.getKey() + ": " + entry.getValue().toString());
        }
*/

    }

    /**
     * This one method handles the switching of views, when the article is clicked,
     * what action items to display and how the appbar behaves
     */

    // Changes the fragment depending on the bottom menu_navigation item pressed
    public void changeFragment(String newFragment) {
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
                ((CollapsingToolbarLayout) findViewById(R.id.collapsingToolbar)).setTitle(title);
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
                ((CollapsingToolbarLayout) findViewById(R.id.collapsingToolbar)).setTitle("Saved");
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
                ((CollapsingToolbarLayout) findViewById(R.id.collapsingToolbar)).setTitle("Recents");
                break;

            default:
                break;
        }
        fragmentTransaction.commit();
    }

    /**
     * This method just shows a snackbar
     * @param message: Message to display
     */

    // SHow snackbar
    private void showSnackbar(String message){
        Snackbar snackbar = Snackbar.make((RelativeLayout) findViewById(R.id.screenspace_container), message, Snackbar.LENGTH_LONG);
        View snackbarView = snackbar.getView();
        TextView textView = (TextView) snackbarView.findViewById(android.support.design.R.id.snackbar_text);
        textView.setTextColor(getResources().getColor(R.color.colorAccent));
        snackbar.show();
    }


    @Override
    public void onBackPressed() {
        int state = history.indexOf(currentArticle);
        if(state > 0){
            Article article = history.get(state - 1);
            updateArticleImage(article.getImage());
            updateArticleText(article.getTitle(),article.getHtml());
            currentArticle = article;
        }else{
            super.onBackPressed();
        }
    }

    @Override
    public void onArticleLoaded(Article article) {
        history.add(article);
        currentArticle = article;
        ((ImageView) findViewById(R.id.article_image)).setImageBitmap(article.getImage());
        updateArticleImage(article.getImage());
        updateArticleText(article.getTitle(),article.getHtml());
        progressBar.setVisibility(View.GONE);

    }
}