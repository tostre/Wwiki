package root.tostre.com.wwiki;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import java.util.Map;

/**
 * Displays a list of recently viewed articles
 */

public class RecentsFragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private String mParam1;
    private String mParam2;
    private View rootView;
    private OnFragmentInteractionListener mListener;

    // COnstructor, empty
    public RecentsFragment() {
        // Required empty public constructor
    }

    // New instance, returns the object
    public static RecentsFragment newInstance() {
        RecentsFragment fragment = new RecentsFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override // Default method
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override // Default method
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        rootView = inflater.inflate(R.layout.fragment_recents, container, false);
        populateRecentsList();
        return rootView;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    // Default method
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    // Default method
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    // Reads the entries from the sharedPreferences, creates new views according to the number of items in the
    // SharedPref and displays them (highly inefficient, will be reworked)
    public void populateRecentsList(){

        LinearLayout recentsList = (LinearLayout) rootView.findViewById(R.id.recents_list);
        recentsList.removeAllViews();

        SharedPreferences sharedPref = getActivity().getSharedPreferences("tostre.wwiki.recentslist", Context.MODE_PRIVATE);
        Map<String, ?> wikis = sharedPref.getAll();

        for(final Map.Entry<String,?> entry : wikis.entrySet()){

            LayoutInflater inflater = LayoutInflater.from(getActivity().getApplicationContext());
            View listEntry = inflater.inflate(R.layout.subfragment_listentry, recentsList, false);

            ((TextView) listEntry.findViewById(R.id.savedRecentsListItem_title)).setText(entry.getKey());
            ((TextView) listEntry.findViewById(R.id.savedRecentsListItem_info)).setText("abgerufen am: " + (String) entry.getValue());

            recentsList.addView(listEntry);

            // This command shall jump back to the reader and load the article when the user clicks on a list-entry
            // Loading the article doesn't work yet
            listEntry.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d("DBG", "Clicked: " + entry.getKey());
                    ((MainActivity) getActivity()).changeFragment("reader");
                    //((BottomNavigationView) getActivity().findViewById(R.id.navigation)).up
                }
            });
        }
    }

}
