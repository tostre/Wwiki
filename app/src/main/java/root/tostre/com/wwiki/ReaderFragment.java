package root.tostre.com.wwiki;

import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.Fragment;
import android.support.v4.widget.NestedScrollView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.TextView;

public class ReaderFragment extends Fragment {

    private CollapsingToolbarLayout collapsingToolbar;
    private WebView contentTextView;




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

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_reader, container, false);
        setHasOptionsMenu(true);

        // Get required view variables
        collapsingToolbar = (CollapsingToolbarLayout) getActivity().findViewById(R.id.collapsingToolbar);
        contentTextView = (WebView) view.findViewById(R.id.content_text);
        // Initiate first displayed article
        updateView(getArguments().getString("article"), getArguments().getString("extract"));
        return view;
    }

    // Updates the view with the values in the current article-arraylist
    public void updateView(String title, String extract) {
        collapsingToolbar.setTitle(title);
        //contentTextView.setText(extract);
    }






    public int[] getScrollState(){
        NestedScrollView content_container = (NestedScrollView) getActivity().findViewById(R.id.content_container);
        int[] scrollState = {content_container.getScrollX(), content_container.getScrollY()};

        Log.d("DBG", Integer.toString(content_container.getScrollX()) + "//" + Integer.toString(content_container.getScrollY())+ "\n");

        return scrollState;
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
