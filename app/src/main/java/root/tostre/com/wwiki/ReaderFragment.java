package root.tostre.com.wwiki;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.TextView;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class ReaderFragment extends Fragment {

    private CollapsingToolbarLayout collapsingToolbar;
    private WebView webView;




    // Constructor, never used
    public ReaderFragment() {
        // Required empty public constructor
    }

    // When the fragment is created it will display the current article from activity
    public static ReaderFragment newInstance(String title, String extract) {
        ReaderFragment fragment = new ReaderFragment();
        Bundle args = new Bundle();
        args.putString("title", title);
        args.putString("extract", extract);
        fragment.setArguments(args);
        return fragment;
    }

    // Used for initializing all non-ui-variables
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    // Used for initializing all ui-variables
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {


        setHasOptionsMenu(true);

        // Get required view variables
        //collapsingToolbar = (CollapsingToolbarLayout) getActivity().findViewById(R.id.collapsingToolbar);
        //webView = (WebView) view.findViewById(R.id.content_text);

        //displayLastArticle();


        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_reader, container, false);
        return view;
    }

    // Updates the view with the values in the current article-arraylist
    public void displayLastArticle() {

        //webView.loadData("HALLO", "text/html", "utf-8");


        SharedPreferences sharedPref = this.getActivity().getSharedPreferences("tostre.wwiki.lastArticle", Context.MODE_PRIVATE);

        Map<String, ?> values = sharedPref.getAll();
        String lastTitle = sharedPref.getString("lastTitle", "No last title");



        SharedPreferences sharedPref2 = this.getActivity().getSharedPreferences("tostre.wwiki.lastArticle", Context.MODE_PRIVATE);
        String lastText = sharedPref2.getString("lastText", "No last text");

        //((TextView) getView().findViewById(R.id.content_text)).setText(Html.fromHtml(lastText));
        //((MainActivity) getActivity()).updateArticleText(lastTitle, lastText);

        //Log.d("DBG", "LASTTITLE: " + lastTitle);
        //Log.d("DBG", "LASTTEXT: " + lastText);
    }







/**
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater){
        inflater.inflate(R.menu.menu_overflow, menu);
        this.menu = menu;
        // Updates which menu items are shown when fragment is displayed
        MenuItem search = menu.findItem(R.id.overflow_search).setVisible(true);
        MenuItem save = menu.findItem(R.id.overflow_save).setVisible(true);
        MenuItem recents = menu.findItem(R.id.overflow_delete).setVisible(false);

        search.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                loadArticle();
                return true;
            }
        });
        super.onCreateOptionsMenu(menu, inflater);
    }*/

}
