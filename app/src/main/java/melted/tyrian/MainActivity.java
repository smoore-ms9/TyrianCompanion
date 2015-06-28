package melted.tyrian;

import android.app.Activity;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.support.v4.widget.DrawerLayout;
import android.widget.TextView;


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
    private CharSequence mTitle;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null) {
            toolbar.setTitle(R.string.app_name);
            setSupportActionBar(toolbar);
        }

        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
        mTitle = getTitle();

        // Set up the drawer.
        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));
    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {
        // update the main content by replacing fragments
        //TODO: THIS IS WHERE YOU WILL CHANGE THE VIEW
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.container, PlaceholderFragment.newInstance(position + 1))
                .commit();
    }

    public void onSectionAttached(int number) {
        if (number == 0 || NavigationDrawerFragment.NAV_NODES == null) mTitle = getTitle();
        else {
            int cIndex = (number % 100) - 1;
            String sIndex = NavigationDrawerFragment.NAV_SECTIONS[(int) Math.floor(number / 100)];
            mTitle = NavigationDrawerFragment.NAV_NODES.get(sIndex)[cIndex];
        }
    }

    public void restoreActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(mTitle);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!mNavigationDrawerFragment.isDrawerOpen()) {
            // Only show items in the action bar relevant to this screen
            // if the drawer is not showing. Otherwise, let the drawer
            // decide what to show in the action bar.
            getMenuInflater().inflate(R.menu.main, menu);
            restoreActionBar();
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
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static int ARG_SECTION_NUMBER = 0;

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            ARG_SECTION_NUMBER = sectionNumber;
            fragment.setArguments(args);
            return fragment;
        }

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            String[] testSet = {"one","two"};
            RecAdapter rAdapter = new RecAdapter(testSet);
            View rootView;
            switch(ARG_SECTION_NUMBER){
                case 1: rootView = inflater.inflate(R.layout.fragment_bank, container, false);
                    final LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
                    layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
                    ((RecyclerView) rootView.findViewById(R.id.rv_container)).setLayoutManager(layoutManager);
                    ((RecyclerView) rootView.findViewById(R.id.rv_container)).setAdapter(rAdapter);
                    break;
                case 2: rootView = inflater.inflate(R.layout.fragment_main, container, false);
                    break;
                case 3: rootView = inflater.inflate(R.layout.fragment_main, container, false);
                    break;
                case 101: rootView = inflater.inflate(R.layout.fragment_main, container, false);
                    break;
                case 201: rootView = inflater.inflate(R.layout.fragment_main, container, false);
                    break;
                default: rootView = inflater.inflate(R.layout.fragment_main, container, false);
                    break;
            }
            return rootView;
        }

        @Override
        public void onAttach(Activity activity) {
            super.onAttach(activity);
            ((MainActivity) activity).onSectionAttached(ARG_SECTION_NUMBER);
        }

        //TODO: THIS WHOLE ADAPTER. Create a nested Gridview for bank tabs.
        public class RecAdapter extends RecyclerView.Adapter<RecAdapter.ViewHolder> {
            //TODO: Change this to an array of BankTab objects
            //TODO: ...Create BankTab objects
            private String[] mDataset;

            // Provide a reference to the views for each data item
            // Complex data items may need more than one view per item, and
            // you provide access to all the views for a data item in a view holder
            public class ViewHolder extends RecyclerView.ViewHolder {
                // each data item is just a string in this case
                public TextView mTextView;
                public ViewHolder(TextView v) {
                    super(v);
                    mTextView = v;
                }
            }

            // Provide a suitable constructor (depends on the kind of dataset)
            public RecAdapter(String[] myDataset) {
                mDataset = myDataset;
            }

            // Create new views (invoked by the layout manager)
            @Override
            public RecAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                           int viewType) {
                // create a new view
                View v = new TextView(getActivity());
                // set the view's size, margins, paddings and layout parameters...
                ViewHolder vh = new ViewHolder((TextView)v);
                return vh;
            }

            // Replace the contents of a view (invoked by the layout manager)
            @Override
            public void onBindViewHolder(ViewHolder holder, int position) {
                // - get element from your dataset at this position
                // - replace the contents of the view with that element
                holder.mTextView.setText(mDataset[position]);

            }

            // Return the size of your dataset (invoked by the layout manager)
            @Override
            public int getItemCount() {
                return mDataset.length;
            }
        }
    }

}
