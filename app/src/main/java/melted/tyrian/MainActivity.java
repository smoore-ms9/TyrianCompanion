package melted.tyrian;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.support.annotation.DrawableRes;
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
import android.widget.Spinner;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

import melted.tyrian.ANet.*;


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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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

        // Bank view fields
        private ArrayList<BankItem> bItems;
        private ArrayList<ImageView> bThumbnails;

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
                case 1:
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
                                    case 2:
                                        TPAdapter tpAdapter = new TPAdapter();
                                        rView.setAdapter(tpAdapter);
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

        //TODO: THIS WHOLE ADAPTER. Create a nested Gridview for bank tabs.
        public class BankAdapter extends RecyclerView.Adapter<BankAdapter.BankViewHolder> {

            private boolean loading = false;

            // Provide a suitable constructor (depends on the kind of dataset)
            public BankAdapter() {
            }

            private class PopulateBItems extends AsyncTask<Void, Void, ArrayList<BankItem>>
            {

                private final BankAdapter adapter;

                public PopulateBItems(BankAdapter adapter) {
                    this.adapter = adapter;
                }
                @Override
                protected ArrayList<BankItem> doInBackground(Void... params) {
                    loading = true;
                    HttpsURLConnection connection;
                    try {
                        connection = (HttpsURLConnection) new URL(APIHandles.getAuthUri(
                                APIHandles.BASE_API_BANK_URI, NavigationDrawerFragment.key))
                                .openConnection();

                        // fetch data from server
                        JsonReader reader = new JsonReader(
                                new InputStreamReader(connection.getInputStream()));
                        JsonParser parser = new JsonParser();
                        JsonElement jsonResponse = parser.parse(reader);
                        bItems = new Gson().fromJson(jsonResponse,
                                 new TypeToken<List<BankItem>>() {}.getType());

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    loading = false;
                    return bItems;
                }

                protected void onPostExecute(Long result) {
                    adapter.notifyDataSetChanged();
                }
            }

            // Provide a reference to the views for each data item
            // Complex data items may need more than one view per item, and
            // you provide access to all the views for a data item in a view holder
            public class BankViewHolder extends RecyclerView.ViewHolder {
                // each data item is just a string in this case
                public TextView mTitle;
                public GridView mGrid;

                public BankViewHolder(CardView cv) {
                    super(cv);
                    mTitle = (TextView) cv.findViewById(R.id.tv_title);
                    mGrid = (GridView) cv.findViewById(R.id.gv_grid);
                    mGrid.setAdapter(new ImageAdapter(cv.getContext()));
                    mGrid.setOnTouchListener(new View.OnTouchListener() {
                        @Override
                        public boolean onTouch(View v, MotionEvent event) {
                            v.getParent().requestDisallowInterceptTouchEvent(true);
                            return false;
                        }
                    });
                }
            }

            // Create new views (invoked by the layout manager)
            @Override
            public BankAdapter.BankViewHolder onCreateViewHolder(ViewGroup parent,
                                                                 int viewType) {
                // create a new view
                CardView card = (CardView) LayoutInflater.
                        from(parent.getContext()).
                        inflate(R.layout.card_banktab, parent, false);

                //TODO: Get the bank info from API
                if (bItems == null & !loading)
                    new PopulateBItems(this).execute();

                // set the view's size, margins, paddings and layout parameters...
                BankViewHolder vh = new BankViewHolder(card);
                return vh;
            }

            // Replace the contents of a view (invoked by the layout manager)
            @Override
            public void onBindViewHolder(BankViewHolder holder, int position) {
                // - get element from your dataset at this position
                // - replace the contents of the view with that element
                if (bItems == null) holder.mTitle.setText("Please Refresh");
                else if (bItems.get(position) != null)
                    holder.mTitle.setText(Integer.toString(bItems.size()) + " slots found:");
            }

            // Return the size of your dataset (invoked by the layout manager)
            @Override
            public int getItemCount() {
                //TODO: Get the count
                return 1;
            }

            //TODO: Pass in images from api
            public class ImageAdapter extends BaseAdapter {
                private Context mContext;

                public ImageAdapter(Context c) {
                    mContext = c;
                }

                public int getCount() {
                    if (bItems == null) return 1;
                    else return bItems.size();
                }

                public Object getItem(int position) {
                    if (bItems == null) return "Test";
                    else return bItems.get(position);
                }

                public long getItemId(int position) {
                    return position;
                }

                // create a new ImageView for each item referenced by the Adapter
                public View getView(int position, View convertView, ViewGroup parent) {
                    ImageView imageView;
                    if (bThumbnails == null)
                        bThumbnails = new ArrayList<ImageView>();
                    if (bThumbnails == null || bThumbnails.size() <= position) {
                        if (convertView == null) {
                            // if it's not recycled, initialize some attributes
                            imageView = new ImageView(mContext);
                            imageView.setLayoutParams(new GridView.LayoutParams(100, 100));
                            imageView.setScaleType(ImageView.ScaleType.FIT_XY);
                            imageView.setPadding(8, 8, 8, 8);
                        } else {
                            imageView = (ImageView) convertView;
                        }

                        if (bItems != null) {
                            BankItem b = bItems.get(position);
                            if (b != null) {
                                Item i = new Item(b.id, imageView, this);
                                i.execute();
                            } else {
                               imageView.setImageResource(R.drawable.empty_slot);
                            }
                            bThumbnails.add(imageView);
                        }
                        return imageView;
                    } else {
                        return bThumbnails.get(position);
                    }
                }
            }
        }

        public class MatsAdapter extends RecyclerView.Adapter<MatsAdapter.HomeViewHolder> {

            // Provide a reference to the views for each data item
            // Complex data items may need more than one view per item, and
            // you provide access to all the views for a data item in a view holder
            public class HomeViewHolder extends RecyclerView.ViewHolder {
                // each data item is just a string in this case
                public TextView mTitle;
                public GridView mGrid;

                public HomeViewHolder(CardView cv) {
                    super(cv);
                    mTitle = (TextView) cv.findViewById(R.id.tv_title);
                    mGrid = (GridView) cv.findViewById(R.id.gv_grid);
                    mGrid.setAdapter(new ImageAdapter(cv.getContext()));
                }
            }

            // Provide a suitable constructor (depends on the kind of dataset)
            public MatsAdapter() {
                //TODO: Get the bank info from API
            }

            // Create new views (invoked by the layout manager)
            @Override
            public MatsAdapter.HomeViewHolder onCreateViewHolder(ViewGroup parent,
                                                                 int viewType) {
                // create a new view
                CardView card = (CardView) LayoutInflater.
                        from(parent.getContext()).
                        inflate(R.layout.card_banktab, parent, false);

                // set the view's size, margins, paddings and layout parameters...
                HomeViewHolder vh = new HomeViewHolder(card);
                return vh;
            }

            // Replace the contents of a view (invoked by the layout manager)
            @Override
            public void onBindViewHolder(HomeViewHolder holder, int position) {
                // - get element from your dataset at this position
                // - replace the contents of the view with that element
                holder.mTitle.setText("Test Tab");
            }

            // Return the size of your dataset (invoked by the layout manager)
            @Override
            public int getItemCount() {
                //TODO: Get the count
                return 1;
            }

            //TODO: Pass in images from api
            public class ImageAdapter extends BaseAdapter {
                private Context mContext;

                public ImageAdapter(Context c) {
                    mContext = c;
                }

                public int getCount() {
                    return mThumbIds.length;
                }

                public Object getItem(int position) {
                    return null;
                }

                public long getItemId(int position) {
                    return 0;
                }

                // create a new ImageView for each item referenced by the Adapter
                public View getView(int position, View convertView, ViewGroup parent) {
                    ImageView imageView;
                    if (convertView == null) {
                        // if it's not recycled, initialize some attributes
                        imageView = new ImageView(mContext);
                        imageView.setLayoutParams(new GridView.LayoutParams(196, 196));
                        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                        imageView.setPadding(8, 8, 8, 8);
                    } else {
                        imageView = (ImageView) convertView;
                    }

                    imageView.setImageResource(mThumbIds[position]);
                    return imageView;
                }

                // references to our images
                private Integer[] mThumbIds = {
                        R.drawable.abc_ic_menu_copy_mtrl_am_alpha, R.drawable.ic_drawer,
                        R.drawable.abc_ic_menu_copy_mtrl_am_alpha, R.drawable.ic_drawer,
                        R.drawable.abc_ic_menu_copy_mtrl_am_alpha, R.drawable.ic_drawer,
                        R.drawable.abc_ic_menu_copy_mtrl_am_alpha, R.drawable.ic_drawer,
                        R.drawable.abc_ic_menu_copy_mtrl_am_alpha
                };
            }
        }

        public class TPAdapter extends RecyclerView.Adapter<TPAdapter.HomeViewHolder> {

            // Provide a reference to the views for each data item
            // Complex data items may need more than one view per item, and
            // you provide access to all the views for a data item in a view holder
            public class HomeViewHolder extends RecyclerView.ViewHolder {
                // each data item is just a string in this case
                public TextView mTitle;
                public GridView mGrid;

                public HomeViewHolder(CardView cv) {
                    super(cv);
                    mTitle = (TextView) cv.findViewById(R.id.tv_title);
                    mGrid = (GridView) cv.findViewById(R.id.gv_grid);
                    mGrid.setAdapter(new ImageAdapter(cv.getContext()));
                }
            }

            // Provide a suitable constructor (depends on the kind of dataset)
            public TPAdapter() {
                //TODO: Get the bank info from API
            }

            // Create new views (invoked by the layout manager)
            @Override
            public TPAdapter.HomeViewHolder onCreateViewHolder(ViewGroup parent,
                                                                 int viewType) {
                // create a new view
                CardView card = (CardView) LayoutInflater.
                        from(parent.getContext()).
                        inflate(R.layout.card_banktab, parent, false);

                // set the view's size, margins, paddings and layout parameters...
                HomeViewHolder vh = new HomeViewHolder(card);
                return vh;
            }

            // Replace the contents of a view (invoked by the layout manager)
            @Override
            public void onBindViewHolder(HomeViewHolder holder, int position) {
                // - get element from your dataset at this position
                // - replace the contents of the view with that element
                holder.mTitle.setText("Test Tab");
            }

            // Return the size of your dataset (invoked by the layout manager)
            @Override
            public int getItemCount() {
                //TODO: Get the count
                return 1;
            }

            //TODO: Pass in images from api
            public class ImageAdapter extends BaseAdapter {
                private Context mContext;

                public ImageAdapter(Context c) {
                    mContext = c;
                }

                public int getCount() {
                    return mThumbIds.length;
                }

                public Object getItem(int position) {
                    return null;
                }

                public long getItemId(int position) {
                    return 0;
                }

                // create a new ImageView for each item referenced by the Adapter
                public View getView(int position, View convertView, ViewGroup parent) {
                    ImageView imageView;
                    if (convertView == null) {
                        // if it's not recycled, initialize some attributes
                        imageView = new ImageView(mContext);
                        imageView.setLayoutParams(new GridView.LayoutParams(196, 196));
                        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                        imageView.setPadding(8, 8, 8, 8);
                    } else {
                        imageView = (ImageView) convertView;
                    }

                    imageView.setImageResource(mThumbIds[position]);
                    return imageView;
                }

                // references to our images
                private Integer[] mThumbIds = {
                        R.drawable.abc_ic_menu_copy_mtrl_am_alpha, R.drawable.ic_drawer,
                        R.drawable.abc_ic_menu_copy_mtrl_am_alpha, R.drawable.ic_drawer,
                        R.drawable.abc_ic_menu_copy_mtrl_am_alpha, R.drawable.ic_drawer,
                        R.drawable.abc_ic_menu_copy_mtrl_am_alpha, R.drawable.ic_drawer,
                        R.drawable.abc_ic_menu_copy_mtrl_am_alpha
                };
            }
        }
    }
}
