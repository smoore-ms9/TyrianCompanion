package melted.tyrian;

import android.app.Activity;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.support.v4.widget.DrawerLayout;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.net.URL;
import java.sql.Date;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

import melted.tyrian.ANet.*;
import melted.tyrian.Adapters.BankAdapter;
import melted.tyrian.Adapters.MatsAdapter;
import melted.tyrian.Helpers.KeyHelper;
import melted.tyrian.Local.Account;
import melted.tyrian.Local.BankItem;
import melted.tyrian.Local.Guild;
import melted.tyrian.Local.Mat;
import melted.tyrian.Local.World;


public class MainActivity extends ActionBarActivity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks {

    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     * First change
     * Second Change
     */
    public static NavigationDrawerFragment mNavigationDrawerFragment;

    private static CharSequence mTitle;
    protected static Toolbar toolbar;
    protected static Spinner spinner;

    // Account field
    protected static Account mAcc;

    // Bank view fields
    public static ArrayList<BankItem[]> bItems;
    public static ArrayList<ImageView> bThumbnails;
    public static boolean bCached;

    // Mats view fields
    public static boolean[] mCaches;
    public static ArrayList<Integer> mIDs;
    public static ArrayList<Mat>[] mCats;
    public static List<HashMap<Integer, JMat>> jMatsById;

    //Init fields
    public static boolean mShowGuide;
    public static ArrayList<JKey> jKeys;
    public static DrawerLayout mDrawer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        InitMatFields();
        InitBankFields();
        try {
            InitKeys();
        } catch (IOException e) {
            e.printStackTrace();
        }


        toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
            spinner = (Spinner) toolbar.findViewById(R.id.tool_spinner);
        }

        mDrawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);

        // Set up the drawer.
        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer, mDrawer);
    }

    private void InitKeys() throws IOException {
        try {
            FileInputStream fis = openFileInput(APIHandles.KEY_FILENAME);
            JsonReader reader = new JsonReader(
                    new InputStreamReader(fis));
            JsonParser parser = new JsonParser();
            JsonElement jsonResponse = parser.parse(reader);
            jKeys = new Gson().fromJson(jsonResponse,
                    new TypeToken<List<JKey>>() {
                    }.getType());
            fis.close();

            if (jKeys != null && jKeys.size() > 0) {
                this.mShowGuide = false;
                KeyHelper.key_map = new HashMap<>();
                for (JKey jk : jKeys)
                    KeyHelper.key_map.put(jk.name, jk.key);
                KeyHelper.key = jKeys.get(0).key;
            }
            else this.mShowGuide = true;


        } catch (Exception e) {
            this.mShowGuide = true;
        }
    }

    private static void InitBankFields() {
        bItems = new ArrayList<>();
        bThumbnails = new ArrayList<>();
    }

    private static void InitMatFields() {
        mCaches = new boolean[7];
        for (int i = 0; i < 7; i++) mCaches[i] = false;

        mCats = new ArrayList[7];
        for (int i = 0; i < 7; i++) mCats[i] = new ArrayList<>();

        mIDs = new ArrayList<>();
        jMatsById = new ArrayList<>();
        for (int i = 0; i < 7; i++) jMatsById.add(new HashMap<Integer, JMat>());
    }

    public static void ClearCache() {
        mAcc = null;
        InitBankFields();
        InitMatFields();
    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {
        // update the main content by replacing fragments
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.container, ContentFragment.newInstance(position))
                .commit();
    }

    public void onSectionAttached(int number) {
        //TODO: Anything?
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
}
