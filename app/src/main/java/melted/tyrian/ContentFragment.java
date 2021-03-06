package melted.tyrian;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.MarginLayoutParams;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.HashMap;

import melted.tyrian.ANet.JKey;
import melted.tyrian.Adapters.BagAdapter;
import melted.tyrian.Adapters.BankAdapter;
import melted.tyrian.Adapters.CharacterListAdapter;
import melted.tyrian.Adapters.MatsAdapter;
import melted.tyrian.Async.GetAccountInfo;
import melted.tyrian.Async.GetCharacters;
import melted.tyrian.Async.GetEquipment;
import melted.tyrian.Async.GetEquipmentInfusions;
import melted.tyrian.Async.GetEquipmentUpgrades;
import melted.tyrian.Helpers.KeyHelper;
import melted.tyrian.Listeners.OnSwipeTouchListener;
import melted.tyrian.Local.Bag;
import melted.tyrian.Local.Equipment;

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
        if (MainActivity.mShowGuide) {
            final View rootView = inflater.inflate(R.layout.fragment_guide, container, false);
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
            return rootView;

        } else switch (ARG_SECTION_NUMBER) {
            case 0: //Overview tab selected
                MainActivity.toolbar.setTitle("Overview");
                return GetOverView(inflater, container);
            case 1: //Bank tab selected
                ((TextView)MainActivity.toolbar.findViewById(R.id.tool_title)).setText("Bank");
                return GetBankView(inflater, container);
            case 2:
                ((TextView)MainActivity.toolbar.findViewById(R.id.tool_title)).setText("Characters");
                return GetCharacterView(inflater, container);
            case 3:
                ((TextView)MainActivity.toolbar.findViewById(R.id.tool_title)).setText("Commerce");
                return GetCommerceView(inflater, container);
            case 4:
                ((TextView)MainActivity.toolbar.findViewById(R.id.tool_title)).setText("Guilds");
                return GetGuildView(inflater, container);
            case 100:
                ((TextView)MainActivity.toolbar.findViewById(R.id.tool_title)).setText("Boss Timer");
                return GetTimerView(inflater, container);
            case 200:
                ((TextView)MainActivity.toolbar.findViewById(R.id.tool_title)).setText("Map Viewer");
                return GetMapView(inflater, container);
            case 300:
                ((TextView)MainActivity.toolbar.findViewById(R.id.tool_title)).setText("Matches");
                return GetMatchView(inflater, container);
            case 999:
                ((TextView)MainActivity.toolbar.findViewById(R.id.tool_title)).setText("Characters");
                return GetCharacterDetailView(inflater, container);
            default:
                ((TextView)MainActivity.toolbar.findViewById(R.id.tool_title)).setText("I am Error");
                return GetOverView(inflater, container);
        }
    }

    private View GetCharacterDetailView(final LayoutInflater inflater, ViewGroup container) {
        MainActivity.mTabLayout.removeAllTabs();
        MainActivity.mTabLayout.setVisibility(View.VISIBLE);
        ((TextView)MainActivity.toolbar.findViewById(R.id.tool_title)).setText(MainActivity.mSelCharacter.getName());
        View rootView = inflater.inflate(R.layout.fragment_character_details, container, false);

        final LinearLayout tab_container = (LinearLayout) rootView.findViewById(R.id.ll_tab_content);

        final int[] mCurrentTab = {0};
        MainActivity.mTabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                mCurrentTab[0] = tab.getPosition();
                switch (tab.getPosition()) {
                    case 0:
                        tab_container.removeAllViews();
                        tab_container.addView(inflater.inflate(R.layout.tab_c_details, null));
                        ((TextView) tab_container.findViewById(R.id.tv_c_name))
                                .setText(MainActivity.mSelCharacter.getName());
                        ((TextView) tab_container.findViewById(R.id.tv_lvl))
                                .setText(Integer.toString(MainActivity.mSelCharacter.getLevel()));
                        ((TextView) tab_container.findViewById(R.id.tv_race))
                                .setText(MainActivity.mSelCharacter.getRace());
                        ((TextView) tab_container.findViewById(R.id.tv_gender))
                                .setText(MainActivity.mSelCharacter.getGender());
                        ((TextView) tab_container.findViewById(R.id.tv_prof))
                                .setText(MainActivity.mSelCharacter.getProfession());
                        ((TextView) tab_container.findViewById(R.id.tv_deaths))
                                .setText(Integer.toString(MainActivity.mSelCharacter.getDeaths()));

                        int age_sec = MainActivity.mSelCharacter.getAge();
                        int d = age_sec / 86400;
                        int h = (age_sec - (d * 86400)) / 3600;
                        int m = (age_sec - (d * 86400) - (h * 3600)) / 60;
                        int s = age_sec - (d * 86400) - (h * 3600) - (m * 60);
                        ((TextView) tab_container.findViewById(R.id.tv_c_age))
                                .setText(Integer.toString(d) + " Days, " +
                                        Integer.toString(h) + " Hrs, " +
                                        Integer.toString(m) + " Mins, " +
                                        Integer.toString(s) + " Secs");

                        String created = MainActivity.mSelCharacter.getCreated();
                        ((TextView) tab_container.findViewById(R.id.tv_created))
                                .setText(created.substring(0, created.indexOf("T")));
                        break;
                    case 1:
                        tab_container.removeAllViews();
                        tab_container.addView(inflater.inflate(R.layout.tab_c_equipment, null));

                        HashMap<String, ImageView> eViews = new HashMap<>();
                        eViews.put("HelmAquatic", (ImageView)tab_container.findViewById(R.id.iv_aquahelm));
                        eViews.put("Backpack", (ImageView)tab_container.findViewById(R.id.iv_back));
                        eViews.put("Coat", (ImageView)tab_container.findViewById(R.id.iv_chest));
                        eViews.put("Boots", (ImageView)tab_container.findViewById(R.id.iv_feet));
                        eViews.put("Gloves", (ImageView)tab_container.findViewById(R.id.iv_arm));
                        eViews.put("Helm", (ImageView)tab_container.findViewById(R.id.iv_head));
                        eViews.put("Leggings", (ImageView)tab_container.findViewById(R.id.iv_leg));
                        eViews.put("Shoulders", (ImageView)tab_container.findViewById(R.id.iv_shoulder));
                        eViews.put("Accessory1", (ImageView)tab_container.findViewById(R.id.iv_ear1));
                        eViews.put("Accessory2", (ImageView)tab_container.findViewById(R.id.iv_ear2));
                        eViews.put("Ring1", (ImageView)tab_container.findViewById(R.id.iv_ring1));
                        eViews.put("Ring2", (ImageView)tab_container.findViewById(R.id.iv_ring2));
                        eViews.put("Amulet", (ImageView)tab_container.findViewById(R.id.iv_neck));
                        eViews.put("WeaponAquaticA", (ImageView)tab_container.findViewById(R.id.iv_aqua1));
                        eViews.put("WeaponAquaticB", (ImageView)tab_container.findViewById(R.id.iv_aqua2));
                        eViews.put("WeaponA1", (ImageView)tab_container.findViewById(R.id.iv_main1));
                        eViews.put("WeaponA2", (ImageView)tab_container.findViewById(R.id.iv_off1));
                        eViews.put("WeaponB1", (ImageView)tab_container.findViewById(R.id.iv_main2));
                        eViews.put("WeaponB2", (ImageView)tab_container.findViewById(R.id.iv_off2));
                        eViews.put("Sickle", (ImageView)tab_container.findViewById(R.id.iv_sickle));
                        eViews.put("Axe", (ImageView)tab_container.findViewById(R.id.iv_axe));
                        eViews.put("Pick", (ImageView) tab_container.findViewById(R.id.iv_pickaxe));

                        if (!MainActivity.mSelCharacter.eCached)
                            new GetEquipment(eViews, getActivity()).execute();
                        else for (Equipment e : MainActivity.mSelCharacter.getEquipment()) {
                            if (e != null && e.item != null) {
                                if (eViews.containsKey(e.slot))
                                    eViews.get(e.slot).setImageBitmap(e.item.getIcon());
                            }
                        }

                        HashMap<String, ImageView> uViews = new HashMap<>();
                        uViews.put("HelmAquaticU", (ImageView)tab_container.findViewById(R.id.iv_aquahelm_upgrade));
                        uViews.put("CoatU", (ImageView)tab_container.findViewById(R.id.iv_chest_upgrade));
                        uViews.put("BootsU", (ImageView)tab_container.findViewById(R.id.iv_feet_upgrade));
                        uViews.put("GlovesU", (ImageView)tab_container.findViewById(R.id.iv_arm_upgrade));
                        uViews.put("HelmU", (ImageView)tab_container.findViewById(R.id.iv_head_upgrade));
                        uViews.put("LeggingsU", (ImageView)tab_container.findViewById(R.id.iv_leg_upgrade));
                        uViews.put("ShouldersU", (ImageView)tab_container.findViewById(R.id.iv_shoulder_upgrade));
                        uViews.put("WeaponAquaticAU", (ImageView)tab_container.findViewById(R.id.iv_aqua1_upgrade1));
                        uViews.put("WeaponAquaticBU", (ImageView)tab_container.findViewById(R.id.iv_aqua2_upgrade1));
                        uViews.put("WeaponAquaticAU2", (ImageView)tab_container.findViewById(R.id.iv_aqua1_upgrade2));
                        uViews.put("WeaponAquaticBU2", (ImageView)tab_container.findViewById(R.id.iv_aqua2_upgrade2));
                        uViews.put("WeaponA1U", (ImageView)tab_container.findViewById(R.id.iv_main1_upgrade));
                        uViews.put("WeaponA2U", (ImageView)tab_container.findViewById(R.id.iv_off1_upgrade));
                        uViews.put("WeaponB1U", (ImageView)tab_container.findViewById(R.id.iv_main2_upgrade));
                        uViews.put("WeaponB2U", (ImageView)tab_container.findViewById(R.id.iv_off2_upgrade));

                        //TODO: Once you get details, get rid of these and use those
                        uViews.put("WeaponA1", (ImageView)tab_container.findViewById(R.id.iv_main1));
                        uViews.put("WeaponA2", (ImageView)tab_container.findViewById(R.id.iv_off1));
                        uViews.put("WeaponB1", (ImageView)tab_container.findViewById(R.id.iv_main2));
                        uViews.put("WeaponB2", (ImageView)tab_container.findViewById(R.id.iv_off2));

                        if (!MainActivity.mSelCharacter.uCached)
                            new GetEquipmentUpgrades(uViews, getActivity()).execute();
                        else for (Equipment e : MainActivity.mSelCharacter.getEquipment()) {
                            if (e != null && e.upgrades != null && e.upgrades.length > 0) {
                                if (uViews.containsKey(e.slot + "U") && e.upgrades[0] != null)
                                    uViews.get(e.slot + "U").setImageBitmap(e.upgrades[0].getIcon());
                            }
                        }

                        HashMap<String, ImageView> iViews = new HashMap<>();
                        iViews.put("HelmAquaticI", (ImageView)tab_container.findViewById(R.id.iv_aquahelm_infusion));
                        iViews.put("CoatI", (ImageView)tab_container.findViewById(R.id.iv_chest_infusion));
                        iViews.put("BootsI", (ImageView)tab_container.findViewById(R.id.iv_feet_infusion));
                        iViews.put("GlovesI", (ImageView)tab_container.findViewById(R.id.iv_arm_infusion));
                        iViews.put("HelmI", (ImageView)tab_container.findViewById(R.id.iv_head_infusion));
                        iViews.put("LeggingsI", (ImageView)tab_container.findViewById(R.id.iv_leg_infusion));
                        iViews.put("ShouldersI", (ImageView)tab_container.findViewById(R.id.iv_shoulder_infusion));
                        //iViews.put("WeaponAquaticAI", (ImageView)tab_container.findViewById(R.id.iv_aqua1_infusion1));
                        //iViews.put("WeaponAquaticBI", (ImageView)tab_container.findViewById(R.id.iv_aqua2_infusion1));
                        iViews.put("WeaponA1I", (ImageView)tab_container.findViewById(R.id.iv_main1_infusion));
                        iViews.put("WeaponA2I", (ImageView)tab_container.findViewById(R.id.iv_off1_infusion));
                        iViews.put("WeaponB1I", (ImageView)tab_container.findViewById(R.id.iv_main2_infusion));
                        iViews.put("WeaponB2I", (ImageView)tab_container.findViewById(R.id.iv_off2_infusion));

                        if (!MainActivity.mSelCharacter.iCached)
                            new GetEquipmentInfusions(iViews, getActivity()).execute();
                        else for (Equipment e : MainActivity.mSelCharacter.getEquipment()) {
                            if (e != null && e.infusions != null && e.infusions.length > 0) {
                                if (iViews.containsKey(e.slot + "I") && e.infusions[0] != null)
                                    iViews.get(e.slot + "I").setImageBitmap(e.infusions[0].getIcon());
                            }
                        }
                        break;
                    case 2:
                        tab_container.removeAllViews();
                        tab_container.addView(inflater.inflate(R.layout.tab_c_inventory, null));

                        LinearLayout ll = (LinearLayout) tab_container.findViewById(R.id.ll_inventory);
                        tab_container.findViewById(R.id.sv_inventory).setOnTouchListener(
                                new OnSwipeTouchListener(getActivity()) {
                            @Override
                            public void onSwipeRight() {
                                if (mCurrentTab[0] > 0)
                                    MainActivity.mTabLayout.getTabAt(mCurrentTab[0] - 1).select();
                            }

                            @Override
                            public void onSwipeLeft() {
                                if (mCurrentTab[0] < MainActivity.mTabLayout.getTabCount() - 1)
                                    MainActivity.mTabLayout.getTabAt(mCurrentTab[0] + 1).select();
                            }
                        });
                        int i = 0;
                        for (Bag b : MainActivity.mSelCharacter.getBags()) {
                            View _v = inflater.inflate(R.layout.grid_bag, null);

                            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                                    LinearLayout.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                            layoutParams.setMargins(0, 0, 0, 32);
                            //layoutParams.height = 120 * ((int)Math.ceil(b.getSize() / 10.0)); //this is in pixels

                            TextView tv = (TextView) _v.findViewById(R.id.tv_title);

                            GridView gv = (GridView) _v.findViewById(R.id.gv_grid);
                            gv.setAdapter(new BagAdapter(i, tv, getActivity()));
                            ll.addView(_v, layoutParams);
                            i++;
                        }

                        break;
                    default:
                        break;
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        TabLayout.Tab t1 = MainActivity.mTabLayout.newTab().setText("Details");
        TabLayout.Tab t2 = MainActivity.mTabLayout.newTab().setText("Equipment");
        TabLayout.Tab t3 = MainActivity.mTabLayout.newTab().setText("Inventory");
        MainActivity.mTabLayout.addTab(t1);
        MainActivity.mTabLayout.addTab(t2);
        MainActivity.mTabLayout.addTab(t3);

        rootView.setOnTouchListener(new OnSwipeTouchListener(getActivity()) {
            @Override
            public void onSwipeRight() {
                if (mCurrentTab[0] > 0)
                    MainActivity.mTabLayout.getTabAt(mCurrentTab[0] - 1).select();
            }

            @Override
            public void onSwipeLeft() {
                if (mCurrentTab[0] < MainActivity.mTabLayout.getTabCount() - 1)
                    MainActivity.mTabLayout.getTabAt(mCurrentTab[0] + 1).select();
            }
        });
        return rootView;
    }

    private View GetMatchView(LayoutInflater inflater, ViewGroup container) {
        MainActivity.mTabLayout.removeAllTabs();
        MainActivity.mTabLayout.setVisibility(View.GONE);
        View rootView = inflater.inflate(R.layout.fragment_coming_soon, container, false);
        return rootView;
    }

    private View GetMapView(LayoutInflater inflater, ViewGroup container) {
        MainActivity.mTabLayout.removeAllTabs();
        MainActivity.mTabLayout.setVisibility(View.GONE);
        View rootView = inflater.inflate(R.layout.fragment_coming_soon, container, false);
        return rootView;
    }

    private View GetTimerView(LayoutInflater inflater, ViewGroup container) {
        MainActivity.mTabLayout.removeAllTabs();
        MainActivity.mTabLayout.setVisibility(View.GONE);
        View rootView = inflater.inflate(R.layout.fragment_coming_soon, container, false);
        return rootView;
    }

    private View GetGuildView(LayoutInflater inflater, ViewGroup container) {
        MainActivity.mTabLayout.removeAllTabs();
        MainActivity.mTabLayout.setVisibility(View.GONE);
        View rootView = inflater.inflate(R.layout.fragment_coming_soon, container, false);
        return rootView;
    }

    private View GetCommerceView(LayoutInflater inflater, ViewGroup container) {
        MainActivity.mTabLayout.removeAllTabs();
        MainActivity.mTabLayout.setVisibility(View.GONE);
        View rootView = inflater.inflate(R.layout.fragment_coming_soon, container, false);
        return rootView;
    }

    private View GetCharacterView(LayoutInflater inflater, ViewGroup container) {
        MainActivity.mTabLayout.removeAllTabs();
        MainActivity.mTabLayout.setVisibility(View.GONE);
        View rootView = inflater.inflate(R.layout.fragment_characters, container, false);

        final LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        RecyclerView rv_characters = (RecyclerView) rootView.findViewById(R.id.rv_container);
        rv_characters.setLayoutManager(layoutManager);

        CharacterListAdapter cla = new CharacterListAdapter(getActivity());
        rv_characters.setAdapter(cla);

        if (MainActivity.mCharacters.size() == 0)
            new GetCharacters(cla).execute();

        return rootView;
    }

    private View GetOverView(LayoutInflater inflater, ViewGroup container) {
        MainActivity.mTabLayout.removeAllTabs();
        MainActivity.mTabLayout.setVisibility(View.GONE);
        final View rootView;
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

            Button btn_delete = (Button) rootView.findViewById(R.id.btn_delete);
            btn_delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String key = KeyHelper.key;

                    JKey keyToDelete = null;
                    for (JKey jk : MainActivity.jKeys) {
                        if (jk.key.equals(key)) ;
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

    private View GetBankView(LayoutInflater inflater, ViewGroup container) {
        MainActivity.mTabLayout.removeAllTabs();
        MainActivity.mTabLayout.setVisibility(View.VISIBLE);
        View rootView = inflater.inflate(R.layout.fragment_bank, container, false);

        final LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        final RecyclerView rView = ((RecyclerView) rootView.findViewById(R.id.rv_container));
        rView.setLayoutManager(layoutManager);

        final int[] mCurrentTab = new int[1];
        MainActivity.mTabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                mCurrentTab[0] = tab.getPosition();
                switch(tab.getPosition()) {
                    case 0:
                        BankAdapter rAdapter = new BankAdapter();
                        rView.setAdapter(rAdapter);
                        break;
                    case 1:
                        MatsAdapter mAdapter = new MatsAdapter();
                        rView.setAdapter(mAdapter);
                        break;
                    default: break;
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        TabLayout.Tab t1 = MainActivity.mTabLayout.newTab().setText("Personal");
        TabLayout.Tab t2 = MainActivity.mTabLayout.newTab().setText("Materials");
        MainActivity.mTabLayout.addTab(t1);
        MainActivity.mTabLayout.addTab(t2);

        rootView.setOnTouchListener(new OnSwipeTouchListener(getActivity()) {
            @Override
            public void onSwipeRight() {
                if (mCurrentTab[0] > 0)
                    MainActivity.mTabLayout.getTabAt(mCurrentTab[0] - 1).select();
            }

            @Override
            public void onSwipeLeft() {
                if (mCurrentTab[0] < MainActivity.mTabLayout.getTabCount() - 1)
                    MainActivity.mTabLayout.getTabAt(mCurrentTab[0] + 1).select();
            }
        });

        return rootView;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        ((MainActivity) activity).onSectionAttached(ARG_SECTION_NUMBER);
    }
}
