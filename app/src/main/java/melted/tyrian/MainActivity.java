package melted.tyrian;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.support.v4.widget.DrawerLayout;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.ref.WeakReference;
import java.lang.reflect.Array;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import javax.net.ssl.HttpsURLConnection;

import melted.tyrian.ANet.*;
import melted.tyrian.Adapters.BankAdapter;
import melted.tyrian.Adapters.MatsAdapter;


public class MainActivity extends ActionBarActivity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks {

    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     * First change
     * Second Change
     */
    private NavigationDrawerFragment mNavigationDrawerFragment;

    /**
     * Used to store the last screen title. For use in {@link #restoreActionBar()}.
     */
    private static CharSequence mTitle;
    protected static Toolbar toolbar;
    private static Spinner spinner;


    // Bank view fields
    public static ArrayList<BankItem[]> bItems;
    public static ArrayList<ImageView> bThumbnails;
    public static boolean bCached;

    // Mats view fields

    public static boolean[] mCaches;

    public static ArrayList<Integer> mIDs;
    public static ArrayList<Mat>[] mCats;
    public static List<HashMap<Integer, JMat>> jMatsById;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        InitMatFields();
        InitBankFields();

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
            spinner = (Spinner) toolbar.findViewById(R.id.tool_spinner);
        }

        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);

        // Set up the drawer.
        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));
    }

    private void InitBankFields() {
        bItems = new ArrayList<>();
        bThumbnails = new ArrayList<>();
    }

    private void InitMatFields() {
        mCaches = new boolean[7];
        for (int i = 0; i < 7; i++) mCaches[i] = false;

        mCats = new ArrayList[7];
        for (int i = 0; i < 7; i++) mCats[i] = new ArrayList<>();

        mIDs = new ArrayList<>();
        jMatsById = new ArrayList<>();
        for (int i = 0; i < 7; i++) jMatsById.add(new HashMap<Integer, JMat>());
    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {
        // update the main content by replacing fragments
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.container, ContentFragment.newInstance(position + 1))
                .commit();
    }

    public void onSectionAttached(int number) {
        //TODO: Anything?
    }

    public void restoreActionBar() {
        //ActionBar actionBar = getSupportActionBar();
        //actionBar.setDisplayShowTitleEnabled(true);
        //actionBar.setTitle(mTitle);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!mNavigationDrawerFragment.isDrawerOpen()) {
            // Only show items in the action bar relevant to this screen
            // if the drawer is not showing. Otherwise, let the drawer
            // decide what to show in the action bar.
            getMenuInflater().inflate(R.menu.main, menu);
            //restoreActionBar();
            return true;
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
       // if (id == R.id.action_settings) {
        //    return true;
       // }

        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class ContentFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static int ARG_SECTION_NUMBER = 0;
        private RecyclerView rView;

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static ContentFragment newInstance(int sectionNumber) {
            ContentFragment fragment = new ContentFragment();
            Bundle args = new Bundle();
            ARG_SECTION_NUMBER = sectionNumber;
            fragment.setArguments(args);
            return fragment;
        }

        public ContentFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView;
            switch (ARG_SECTION_NUMBER) {
                case 1: //Bank tab selected
                    rootView = inflater.inflate(R.layout.fragment_bank, container, false);
                    final LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
                    layoutManager.setOrientation(LinearLayoutManager.VERTICAL);

                    rView = ((RecyclerView) rootView.findViewById(R.id.rv_container));
                    rView.setLayoutManager(layoutManager);

                    if (toolbar != null) {
                        ArrayAdapter<String> sAdapter = new ArrayAdapter<String>(
                                getActivity(),
                                R.layout.support_simple_spinner_dropdown_item,
                                NavigationDrawerFragment.SUB_BANK);
                        spinner.setAdapter(sAdapter);
                        spinner.setVisibility(View.VISIBLE);
                        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                switch(position) {
                                    case 0:
                                        BankAdapter rAdapter = new BankAdapter();
                                        rView.setAdapter(rAdapter);
                                        break;
                                    case 1:
                                        MatsAdapter mAdapter = new MatsAdapter();
                                        rView.setAdapter(mAdapter);
                                        break;
                                }
                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> parent) {

                            }
                        });
                    }

                    break;
                case 2:
                    rootView = inflater.inflate(R.layout.fragment_main, container, false);
                    spinner.setVisibility(View.INVISIBLE);
                    break;
                case 3:
                    rootView = inflater.inflate(R.layout.fragment_main, container, false);
                    spinner.setVisibility(View.INVISIBLE);
                    break;
                case 101:
                    rootView = inflater.inflate(R.layout.fragment_main, container, false);
                    spinner.setVisibility(View.INVISIBLE);
                    break;
                case 201:
                    rootView = inflater.inflate(R.layout.fragment_main, container, false);
                    spinner.setVisibility(View.INVISIBLE);
                    break;
                default:
                    rootView = inflater.inflate(R.layout.fragment_main, container, false);
                    spinner.setVisibility(View.INVISIBLE);
                    break;
            }
            UpdateTitle(ARG_SECTION_NUMBER);
            return rootView;
        }

        private void UpdateTitle(int iChild) {
            if (iChild == 0 || NavigationDrawerFragment.NAV_NODES == null) mTitle = "Tyrian Companion";
            else {
                int cIndex = (iChild % 100) - 1;
                String sIndex = NavigationDrawerFragment.NAV_SECTIONS[(int) Math.floor(iChild / 100)];
                mTitle = NavigationDrawerFragment.NAV_NODES.get(sIndex)[cIndex];
            }
            toolbar.setTitle(mTitle);
        }

        @Override
        public void onAttach(Activity activity) {
            super.onAttach(activity);
            ((MainActivity) activity).onSectionAttached(ARG_SECTION_NUMBER);
        }
    }
}
