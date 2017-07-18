package root.tostre.com.wwiki;

import android.app.FragmentManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Map;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link RecentsFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link RecentsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RecentsFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private View rootView;
    private OnFragmentInteractionListener mListener;
    private Menu menu;

    public RecentsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment RecentsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static RecentsFragment newInstance() {
        RecentsFragment fragment = new RecentsFragment();
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
        rootView = inflater.inflate(R.layout.fragment_recents, container, false);
        populateRecentsList();
        return rootView;
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

    // Reads the entries from the sharedPreferences, creates new views according to the number of items in the
    // SharedPref and displays them (highly inefficient, will be reworked)
    public void populateRecentsList(){

        LinearLayout recentsList = (LinearLayout) rootView.findViewById(R.id.recents_list);
        recentsList.removeAllViews();

        SharedPreferences sharedPref = getActivity().getSharedPreferences("tostre.wwiki.recentslist", Context.MODE_PRIVATE);
        Map<String, ?> wikis = sharedPref.getAll();

        for(Map.Entry<String,?> entry : wikis.entrySet()){

            LayoutInflater inflater = LayoutInflater.from(getActivity().getApplicationContext());
            View listEntry = inflater.inflate(R.layout.subfragment_listentry, recentsList, false);

            ((TextView) listEntry.findViewById(R.id.savedRecentsListItem_title)).setText(entry.getKey());
            ((TextView) listEntry.findViewById(R.id.savedRecentsListItem_info)).setText("abgerufen am: " + (String) entry.getValue());

            recentsList.addView(listEntry);
        }
    }
}
