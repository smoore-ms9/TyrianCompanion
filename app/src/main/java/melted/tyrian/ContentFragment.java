package melted.tyrian;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.sql.Date;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

import melted.tyrian.ANet.APIHandles;
import melted.tyrian.ANet.JAccount;
import melted.tyrian.ANet.JKey;
import melted.tyrian.ANet.JWorld;
import melted.tyrian.Adapters.BankAdapter;
import melted.tyrian.Adapters.MatsAdapter;
import melted.tyrian.Helpers.KeyHelper;
import melted.tyrian.Local.Account;
import melted.tyrian.Local.Guild;
import melted.tyrian.Local.World;

/**
 * Created by Stephen on 7/2/2015.
 */
public class ContentFragment extends Fragment {
    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    private static int ARG_SECTION_NUMBER = 0;

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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        switch (ARG_SECTION_NUMBER) {
            case 0: //Overview tab selected
                MainActivity.toolbar.setTitle("Overview");
                return GetOverView(inflater, container);
            case 1: //Bank tab selected
                MainActivity.toolbar.setTitle("Bank");
                return GetBankView(inflater, container);
            case 2:
                MainActivity.toolbar.setTitle("Characters");
                return GetCharacterView(inflater, container);
            case 3:
                MainActivity.toolbar.setTitle("Commerce");
                return GetCommerceView(inflater, container);
            case 4:
                MainActivity.toolbar.setTitle("Guilds");
                return GetGuildView(inflater, container);
            case 100:
                MainActivity.toolbar.setTitle("Boss Timer");
                return GetTimerView(inflater, container);
            case 200:
                MainActivity.toolbar.setTitle("Map Viewer");
                return GetMapView(inflater, container);
            case 300:
                MainActivity.toolbar.setTitle("Matches");
                return GetMatchView(inflater, container);
            default:
                MainActivity.toolbar.setTitle("I am Error");
                return GetOverView(inflater, container);
        }
    }

    private View GetMatchView(LayoutInflater inflater, ViewGroup container) {
        View rootView = inflater.inflate(R.layout.fragment_coming_soon, container, false);
        MainActivity.spinner.setVisibility(View.INVISIBLE);
        return rootView;
    }

    private View GetMapView(LayoutInflater inflater, ViewGroup container) {
        View rootView = inflater.inflate(R.layout.fragment_coming_soon, container, false);
        MainActivity.spinner.setVisibility(View.INVISIBLE);
        return rootView;
    }

    private View GetTimerView(LayoutInflater inflater, ViewGroup container) {
        View rootView = inflater.inflate(R.layout.fragment_coming_soon, container, false);
        MainActivity.spinner.setVisibility(View.INVISIBLE);
        return rootView;
    }

    private View GetGuildView(LayoutInflater inflater, ViewGroup container) {
        View rootView = inflater.inflate(R.layout.fragment_coming_soon, container, false);
        MainActivity.spinner.setVisibility(View.INVISIBLE);
        return rootView;
    }

    private View GetCommerceView(LayoutInflater inflater, ViewGroup container) {
        View rootView = inflater.inflate(R.layout.fragment_coming_soon, container, false);
        MainActivity.spinner.setVisibility(View.INVISIBLE);
        return rootView;
    }

    private View GetCharacterView(LayoutInflater inflater, ViewGroup container) {
        View rootView = inflater.inflate(R.layout.fragment_coming_soon, container, false);
        MainActivity.spinner.setVisibility(View.INVISIBLE);
        return rootView;
    }

    private View GetOverView(LayoutInflater inflater, ViewGroup container) {
        final View rootView;
        MainActivity.spinner.setVisibility(View.INVISIBLE);
        if (MainActivity.mShowGuide) {
            rootView = inflater.inflate(R.layout.fragment_guide, container, false);
            TextView link = (TextView) rootView.findViewById(R.id.tv_link);
            link.setText(Html.fromHtml(
                    "<a href=\"https://account.guildwars2.com/account/api-keys\">" +
                            "https://account.guildwars2.com/account/api-keys</a> "));
            link.setMovementMethod(LinkMovementMethod.getInstance());
            final EditText et_key = (EditText) rootView.findViewById(R.id.et_key);
            final EditText et_key_name = (EditText) rootView.findViewById(R.id.et_key_name);
            final Button btn_save = (Button) rootView.findViewById(R.id.btn_save);
            btn_save.setEnabled(true);

            btn_save.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    btn_save.setEnabled(false);
                    String key = et_key.getText().toString();
                    String key_name = et_key_name.getText().toString();

                    JKey jKey = new JKey(key, key_name);

                    KeyHelper kHelper = new KeyHelper(getActivity());
                    kHelper.SaveKey(jKey);

                    InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(
                            Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(rootView.getWindowToken(), 0);

                    KeyHelper.key_map.put(key_name, key);
                    MainActivity.mNavigationDrawerFragment = (NavigationDrawerFragment)
                            getActivity().getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
                    MainActivity.mNavigationDrawerFragment.setUp(
                            R.id.navigation_drawer, MainActivity.mDrawer);

                    MainActivity.mShowGuide = false;

                    FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                    fragmentManager.beginTransaction()
                            .replace(R.id.container, ContentFragment.newInstance(0))
                            .commit();
                }
            });

        } else {
            rootView = inflater.inflate(R.layout.fragment_overview, container, false);
            MainActivity.spinner.setVisibility(View.INVISIBLE);

            Button btn_delete = (Button) rootView.findViewById(R.id.btn_delete);
            btn_delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String key = KeyHelper.key;

                    JKey keyToDelete = null;
                    for (JKey jk : MainActivity.jKeys) {
                        if (jk.key.equals(key));
                            keyToDelete = jk;
                    }

                    if (keyToDelete != null) {
                        AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).create();
                        alertDialog.setTitle("Delete Key");
                        alertDialog.setMessage("Are you sure you want to remove this key?");
                        final JKey finalKeyToDelete = keyToDelete;
                        alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, "Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (which == DialogInterface.BUTTON_POSITIVE) {
                                    KeyHelper kHelper = new KeyHelper(getActivity());
                                    if (kHelper.RemoveKey(finalKeyToDelete)) {
                                        MainActivity.jKeys.remove(finalKeyToDelete);
                                        KeyHelper.key_map.remove(finalKeyToDelete.name);
                                        if (MainActivity.jKeys.size() > 0) {
                                            KeyHelper.key = MainActivity.jKeys.get(0).key;
                                        } else MainActivity.mShowGuide = true;
                                    }
                                    MainActivity.ClearCache();
                                    MainActivity.mNavigationDrawerFragment = (NavigationDrawerFragment)
                                            getActivity().getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
                                    MainActivity.mNavigationDrawerFragment.setUp(
                                            R.id.navigation_drawer, MainActivity.mDrawer);
                                    FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                                    fragmentManager.beginTransaction()
                                            .replace(R.id.container, ContentFragment.newInstance(0))
                                            .commit();
                                }
                            }
                        });

                        alertDialog.setIcon(R.drawable.ic_delete_white_24dp);
                        alertDialog.show();
                    }
                }
            });

            TextView[] params = new TextView[4];
            params[0] = (TextView) rootView.findViewById(R.id.tv_account_id);
            params[1] = (TextView) rootView.findViewById(R.id.tv_account_name);
            params[2] = (TextView) rootView.findViewById(R.id.tv_server);
            params[3] = (TextView) rootView.findViewById(R.id.tv_creation_date);

            if (MainActivity.mAcc == null)
                new GetAccountInfo(params).execute();
            else {
                params[0].setText(MainActivity.mAcc.getId());
                params[1].setText(MainActivity.mAcc.getName());
                params[2].setText(MainActivity.mAcc.getWorld().getName());
                params[3].setText(MainActivity.mAcc.getCreated().toString());
            }
        }
        return rootView;
    }

    private class GetAccountInfo extends AsyncTask<Void, Void, Void> {

        private final TextView tv_id;
        private final TextView tv_name;
        private final TextView tv_server;
        private final TextView tv_created;

        public GetAccountInfo(TextView[] field_views) {
            this.tv_id = field_views[0];
            this.tv_name = field_views[1];
            this.tv_server = field_views[2];
            this.tv_created = field_views[3];
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                // Get Account Info
                HttpsURLConnection connection;
                connection = (HttpsURLConnection) new URL(APIHandles.getAuthUri(
                        APIHandles.BASE_API_ACC_URI, KeyHelper.key))
                        .openConnection();
                JsonReader reader = new JsonReader(
                        new InputStreamReader(connection.getInputStream()));
                JsonParser parser = new JsonParser();
                JsonElement jsonResponse = parser.parse(reader);
                JAccount jAccount = new Gson().fromJson(jsonResponse, JAccount.class);

                // Get World Info
                connection = (HttpsURLConnection) new URL(APIHandles.BASE_API_WORLD_URI +
                        "?ids=" + jAccount.world).openConnection();
                reader = new JsonReader(
                        new InputStreamReader(connection.getInputStream()));
                parser = new JsonParser();
                jsonResponse = parser.parse(reader);
                ArrayList<JWorld> jWorlds = new Gson().fromJson(jsonResponse,
                        new TypeToken<List<JWorld>>() {
                        }.getType());

                World world = new World(jWorlds.get(0).id, jWorlds.get(0).name);

                /*
                // Get Guild Info (Disabled for now)
                String guildIDs = "";
                for (String g : jAccount.guilds) {
                    if (guildIDs.equals("")) guildIDs = g;
                    else guildIDs += "," + g;
                }
                connection = (HttpsURLConnection) new URL(APIHandles.getAuthUriWithParams(
                        APIHandles.BASE_API_GUILD_URI + "?ids=" + guildIDs, NavigationDrawerFragment.key))
                        .openConnection();
                reader = new JsonReader(
                        new InputStreamReader(connection.getInputStream()));
                parser = new JsonParser();
                jsonResponse = parser.parse(reader);
                ArrayList<JGuild> jGuilds = new Gson().fromJson(jsonResponse,
                        new TypeToken<List<JGuild>>() {
                        }.getType());

                Guild[] guilds = new Guild[jGuilds.size()];
                int i = 0;
                for (JGuild jg : jGuilds) {
                    guilds[i] = new Guild();
                    i++;
                }
                */
                Guild[] guilds = new Guild[1];
                guilds[0] = new Guild();

                Date date = Date.valueOf(jAccount.created.substring(0, jAccount.created.indexOf("T")));

                MainActivity.mAcc = new Account(jAccount.id, jAccount.name, world,
                        date, guilds);

            } catch (IOException e) {
                String t = "stub";
            }
            return null;
        }

        protected void onPostExecute(Void v) {
            if (MainActivity.mAcc != null) {
                tv_id.setText(MainActivity.mAcc.getId());
                tv_name.setText(MainActivity.mAcc.getName());
                tv_server.setText(MainActivity.mAcc.getWorld().getName());
                tv_created.setText(MainActivity.mAcc.getCreated().toString());
            }
        }
    }

    private View GetBankView(LayoutInflater inflater, ViewGroup container) {
        View rootView = inflater.inflate(R.layout.fragment_bank, container, false);
        final LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        final RecyclerView rView = ((RecyclerView) rootView.findViewById(R.id.rv_container));
        rView.setLayoutManager(layoutManager);

        if (MainActivity.toolbar != null) {
            ArrayAdapter<String> sAdapter = new ArrayAdapter<String>(
                    getActivity(),
                    R.layout.spinner_dropdown,
                    NavigationDrawerFragment.SUB_BANK);
            MainActivity.spinner.setAdapter(sAdapter);
            MainActivity.spinner.setVisibility(View.VISIBLE);
            MainActivity.spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
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
        return rootView;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        ((MainActivity) activity).onSectionAttached(ARG_SECTION_NUMBER);
    }
}
