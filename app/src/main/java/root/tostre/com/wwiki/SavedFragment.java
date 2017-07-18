package root.tostre.com.wwiki;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.internal.BottomNavigationMenu;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Map;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link SavedFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link SavedFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SavedFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private View rootView;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private Menu menu;

    private OnFragmentInteractionListener mListener;

    public SavedFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment SavedFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SavedFragment newInstance() {
        SavedFragment fragment = new SavedFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_saved, container, false);
        populateSavedList();
        return rootView;
    }

    // Reads the entries from the sharedPreferences, creates new views according to the number of items in the
    // SharedPref and displays them (highly inefficient, will be reworked)
    public void populateSavedList(){

        LinearLayout savedList = (LinearLayout) rootView.findViewById(R.id.saved_list);
        savedList.removeAllViews();

        SharedPreferences sharedPref = getActivity().getSharedPreferences("tostre.wwiki.saved", Context.MODE_PRIVATE);
        Map<String, ?> wikis = sharedPref.getAll();

        for(final Map.Entry<String,?> entry : wikis.entrySet()){

            LayoutInflater inflater = LayoutInflater.from(getActivity().getApplicationContext());
            View listEntry = inflater.inflate(R.layout.subfragment_listentry, savedList, false);

            ((TextView) listEntry.findViewById(R.id.savedRecentsListItem_title)).setText(entry.getKey());
            ((TextView) listEntry.findViewById(R.id.savedRecentsListItem_info)).setText("offline verfügbar");

            savedList.addView(listEntry);

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

/**
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater){
        inflater.inflate(R.menu.menu_overflow, menu);
        this.menu = menu;
        // Updates which menu items are shown when fragment is displayed
        MenuItem search = menu.findItem(R.id.overflow_search).setVisible(false);
        MenuItem save = menu.findItem(R.id.overflow_save).setVisible(false);
        MenuItem recents = menu.findItem(R.id.overflow_delete).setVisible(true);

        super.onCreateOptionsMenu(menu, inflater);
    }*/

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    public void loadSavedArticle(View view){
        Log.d("DBG", (String) "hi");
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
