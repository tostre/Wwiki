package root.tostre.com.wwiki;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;

/**
 * Connected to fragmetn_reader
 * Loads the articles, puts them on hold when
 * the fragment is changed, etc.
 */

public class ReaderFragment extends Fragment {

    private WebView webView;
    private Bundle webViewBundle;
    private CharSequence lastTitle;

    // Constructor
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
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_reader, container, false);
        webView = (WebView) view;
        return view;
    }

    @Override // When the fragment is replaced, the current article is saved...
    public void onPause(){
        super.onPause();
        webViewBundle = new Bundle();
        webView.saveState(webViewBundle);

        SharedPreferences prefs = getActivity().getApplicationContext().getSharedPreferences(getActivity().getPackageName(), Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("lastUrl", webView.getUrl());
        editor.putString("lastTitle", (String) ((CollapsingToolbarLayout) getActivity().findViewById(R.id.collapsingToolbar)).getTitle());
        editor.commit();

        lastTitle = ((CollapsingToolbarLayout) getActivity().findViewById(R.id.collapsingToolbar)).getTitle();

    }

    @Override // ...and restored, when the fragment is loaded again
    public void onResume(){
        super.onResume();
        SharedPreferences sharedPref = getActivity().getApplicationContext().getSharedPreferences(getActivity().getPackageName(), Activity.MODE_PRIVATE);

        if(webView != null) {

            String lastUrl = sharedPref.getString("lastUrl","");
            if(!lastUrl.equals("")) {
                webView.loadUrl(lastUrl);
            }
        }

        if(lastTitle != null){
            ((CollapsingToolbarLayout) getActivity().findViewById(R.id.collapsingToolbar)).setTitle(sharedPref.getString("lastTitle", ""));
        }

    }

}
