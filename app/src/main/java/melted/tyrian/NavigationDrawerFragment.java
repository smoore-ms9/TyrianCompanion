package melted.tyrian;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.HashMap;

import melted.tyrian.ANet.APIHandles;
import melted.tyrian.ANet.JKey;
import melted.tyrian.Helpers.KeyHelper;

/**
 * Fragment used for managing interactions for and presentation of a navigation drawer.
 * See the <a href="https://developer.android.com/design/patterns/navigation-drawer.html#Interaction">
 * design guidelines</a> for a complete explanation of the behaviors implemented here.
 */
public class NavigationDrawerFragment extends Fragment {

    /**
     * Remember the position of the selected item.
     */
    private static final String STATE_SELECTED_POSITION = "selected_navigation_drawer_position";

    /**
     * Per the design guidelines, you should show the drawer on launch until the user manually
     * expands it. This shared preference tracks this.
     */
    private static final String PREF_USER_LEARNED_DRAWER = "navigation_drawer_learned";

    /**
     * A pointer to the current callbacks instance (the Activity).
     */
    private NavigationDrawerCallbacks mCallbacks;

    /**
     * Helper component that ties the action bar to the navigation drawer.
     */
    private ActionBarDrawerToggle mDrawerToggle;

    private DrawerLayout mDrawerLayout;
    private ExpandableListView mDrawerListView;
    public static Spinner mDrawerSpinner;
    private View mFragmentContainerView;

    private int mCurrentSelectedPosition = 0;
    private boolean mFromSavedInstanceState;
    private boolean mUserLearnedDrawer;

    private Toolbar toolbar;

    protected static final String[] NAV_SECTIONS = {
            "Account",
            "PvE",
            "PvP",
            "WvW"
    };

    protected static final String[] SUB_ACCOUNT = {
            "Overview",
            "Bank",
            "Characters",
            "Commerce",
            "Guilds"
    };

    protected static final String[] SUB_PVE = {
            "Boss Timer"
    };

    protected static final String[] SUB_PVP = {
            "Map Viewer"
    };

    protected static final String[] SUB_WVW = {
            "Matches"
    };

    protected static final String[] SUB_BANK = {
            "Personal",
            "Materials"
    };

    protected static final HashMap<String, String[]> NAV_NODES = new HashMap<String, String[]>(){
        {{  put(NAV_SECTIONS[0], SUB_ACCOUNT);
            put(NAV_SECTIONS[1], SUB_PVE);
            put(NAV_SECTIONS[2], SUB_PVP);
            put(NAV_SECTIONS[3], SUB_WVW); }};
    };

    private String[] keys;

    public NavigationDrawerFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Read in the flag indicating whether or not the user has demonstrated awareness of the
        // drawer. See PREF_USER_LEARNED_DRAWER for details.
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getActivity());
        mUserLearnedDrawer = sp.getBoolean(PREF_USER_LEARNED_DRAWER, false);

        if (savedInstanceState != null) {
            mCurrentSelectedPosition = savedInstanceState.getInt(STATE_SELECTED_POSITION);
            mFromSavedInstanceState = true;
        }

        // Select either the default item (0) or the last selected item.
        selectItem(mCurrentSelectedPosition);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        // Indicate that this fragment would like to influence the set of actions in the action bar.
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rView = inflater.inflate(
                R.layout.fragment_navigation_drawer, container, false);

        mDrawerSpinner = (Spinner) rView.findViewById(R.id.spin_key);

        mDrawerListView = (ExpandableListView) rView.findViewById(R.id.elv_drawer);

        mDrawerListView.setAdapter(DrawerAdapter(inflater, NAV_NODES));
        mDrawerListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(
                    ExpandableListView parent, View v,
                    int groupPosition, int childPosition, long id) {
                selectItem((int) DrawerAdapter(inflater, NAV_NODES)
                        .getChildId(groupPosition, childPosition));
                return true;
            }
        });

        return rView;
    }

    private BaseExpandableListAdapter DrawerAdapter(final LayoutInflater inflater, final HashMap<String, String[]> lNodes) {
        BaseExpandableListAdapter adapter = new BaseExpandableListAdapter() {

            @Override
            public int getGroupCount() {
                return lNodes.size();
            }

            @Override
            public int getChildrenCount(int groupPosition) {
                return lNodes.get(NAV_SECTIONS[groupPosition]).length;
            }

            @Override
            public String[] getGroup(int groupPosition) {
                return NAV_NODES.get(NAV_SECTIONS[groupPosition]);
            }

            @Override
            public String getChild(int groupPosition, int childPosition) {
                return NAV_NODES.get(NAV_SECTIONS[groupPosition])[childPosition];
            }

            @Override
            public long getGroupId(int groupPosition) {
                return groupPosition;
            }

            @Override
            public long getChildId(int groupPosition, int childPosition) {
                return (groupPosition*100) + childPosition;
            }

            @Override
            public boolean hasStableIds() {
                return true;
            }

            @Override
            public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
                if (convertView == null)
                    convertView = inflater.inflate(R.layout.fragment_section, null);

                TextView sHeader = (TextView) convertView
                        .findViewById(R.id.tv_header);
                sHeader.setText(NAV_SECTIONS[groupPosition]);
                sHeader.setTextColor(Color.parseColor("#fff5edc8"));

                //ImageView iv_left = (ImageView) convertView.findViewById(R.id.iv_left);
                ImageView iv_right = (ImageView) convertView.findViewById(R.id.iv_right);

                if (isExpanded) {
                    iv_right.setImageResource(R.drawable.ic_expand_less_white_24dp);
                } else {
                    iv_right.setImageResource(R.drawable.ic_expand_more_white_24dp);
                }

                return convertView;
            }

            @Override
            public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {

                if (convertView == null)
                    convertView = inflater.inflate(R.layout.fragment_child, null);

                TextView cHeader = (TextView) convertView
                        .findViewById(R.id.tv_header);

                cHeader.setText(getChild(groupPosition, childPosition));
                //cHeader.setTextColor(Color.parseColor("#fff5edc8"));
                return convertView;
            }

            @Override
            public boolean isChildSelectable(int groupPosition, int childPosition) {
                return true;
            }
        };
        return adapter;
    }

    public boolean isDrawerOpen() {
        return mDrawerLayout != null && mDrawerLayout.isDrawerOpen(mFragmentContainerView);
    }

    /**
     * Users of this fragment must call this method to set up the navigation drawer interactions.
     *
     * @param fragmentId   The android:id of this fragment in its activity's layout.
     * @param drawerLayout The DrawerLayout containing this fragment's UI.
     */
    public void setUp(int fragmentId, DrawerLayout drawerLayout) {
        if (KeyHelper.key_map == null) {
            KeyHelper.key_map = new HashMap<>();
            mDrawerSpinner.setVisibility(View.INVISIBLE);
        } else mDrawerSpinner.setVisibility(View.VISIBLE);

        keys = new String[KeyHelper.key_map.size()];
        KeyHelper.key_map.keySet().toArray(keys);
        final String[] spinner_items = new String[keys.length + 1];
        for (int i = 0; i < spinner_items.length; i++) {
            if (i < keys.length) spinner_items[i] = keys[i];
            else spinner_items[i] = " + Store a new key";
        }
        ArrayAdapter<String> sAdapter = new ArrayAdapter<>(
                getActivity(),
                R.layout.spinner_dropdown,
                spinner_items);
        mDrawerSpinner.setAdapter(sAdapter);
        mDrawerSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == keys.length) {
                    MainActivity.mShowGuide = true;
                    MainActivity.ClearCache();
                } else {
                    MainActivity.mShowGuide = false;
                    KeyHelper.key = KeyHelper.key_map.get(keys[position]);
                    MainActivity.ClearCache();
                }
                mDrawerLayout.closeDrawer(mFragmentContainerView);
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                fragmentManager.beginTransaction()
                        .replace(R.id.container, ContentFragment.newInstance(0))
                        .commit();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        mFragmentContainerView = getActivity().findViewById(fragmentId);
        mDrawerLayout = drawerLayout;

        // set a custom shadow that overlays the main content when the drawer opens
        mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
        // set up the drawer's list view with items and click listener

        // ActionBarDrawerToggle ties together the the proper interactions
        // between the navigation drawer and the action bar app icon.
        mDrawerToggle = new ActionBarDrawerToggle(
                getActivity(),                    /* host Activity */
                mDrawerLayout,                    /* DrawerLayout object */
                MainActivity.toolbar,
                R.string.navigation_drawer_open,  /* "open drawer" description for accessibility */
                R.string.navigation_drawer_close  /* "close drawer" description for accessibility */
        ) {
            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                if (!isAdded()) {
                    return;
                }
                getActivity().supportInvalidateOptionsMenu(); // calls onPrepareOptionsMenu()
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                if (MainActivity.mShowGuide && KeyHelper.key_map.size() > 0)
                    mDrawerSpinner.setSelection(0, true);
                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(
                        Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(drawerView.getWindowToken(), 0);
                if (!isAdded()) {
                    return;
                }

                if (!mUserLearnedDrawer) {
                    // The user manually opened the drawer; store this flag to prevent auto-showing
                    // the navigation drawer automatically in the future.
                    mUserLearnedDrawer = true;
                    SharedPreferences sp = PreferenceManager
                            .getDefaultSharedPreferences(getActivity());
                    sp.edit().putBoolean(PREF_USER_LEARNED_DRAWER, true).apply();
                }

                getActivity().supportInvalidateOptionsMenu(); // calls onPrepareOptionsMenu()
            }
        };

        // If the user hasn't 'learned' about the drawer, open it to introduce them to the drawer,
        // per the navigation drawer design guidelines.
        if (!mUserLearnedDrawer && !mFromSavedInstanceState) {
            //mDrawerLayout.openDrawer(mFragmentContainerView);
        }

        // Defer code dependent on restoration of previous instance state.
        mDrawerLayout.post(new Runnable() {
            @Override
            public void run() {
                mDrawerToggle.syncState();
            }
        });

        mDrawerLayout.setDrawerListener(mDrawerToggle);
    }

    private void selectItem(int position) {
        mCurrentSelectedPosition = position;
        if (mDrawerListView != null) {
            mDrawerListView.setItemChecked(position, true);
        }
        if (mDrawerLayout != null) {
            mDrawerLayout.closeDrawer(mFragmentContainerView);
        }
        if (mCallbacks != null) {
            mCallbacks.onNavigationDrawerItemSelected(position);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mCallbacks = (NavigationDrawerCallbacks) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException("Activity must implement NavigationDrawerCallbacks.");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallbacks = null;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(STATE_SELECTED_POSITION, mCurrentSelectedPosition);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Forward the new configuration the drawer toggle component.
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // If the drawer is open, show the global app actions in the action bar. See also
        // showGlobalContextActionBar, which controls the top-left area of the action bar.
        if (mDrawerLayout != null && isDrawerOpen()) {
            inflater.inflate(R.menu.global, menu);
            showGlobalContextActionBar();
        }
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Per the navigation drawer design guidelines, updates the action bar to show the global app
     * 'context', rather than just what's in the current screen.
     */
    private void showGlobalContextActionBar() {
        //ActionBar actionBar = getActionBar();
        //actionBar.setDisplayShowTitleEnabled(true);
        //actionBar.setTitle(R.string.app_name);
    }

    private ActionBar getActionBar() {
        return ((ActionBarActivity) getActivity()).getSupportActionBar();
    }

    /**
     * Callbacks interface that all activities using this fragment must implement.
     */
    public static interface NavigationDrawerCallbacks {
        /**
         * Called when an item in the navigation drawer is selected.
         */
        void onNavigationDrawerItemSelected(int position);
    }
}
