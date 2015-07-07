package melted.tyrian.Async;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.widget.ImageView;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Locale;

import javax.net.ssl.HttpsURLConnection;

import melted.tyrian.ANet.APIHandler;
import melted.tyrian.ANet.JItem;
import melted.tyrian.Local.Equipment;
import melted.tyrian.Local.Item;
import melted.tyrian.MainActivity;
import melted.tyrian.R;

/**
 * Created by Stephen on 7/6/2015.
 */
public class GetEquipmentUpgrades extends AsyncTask<Void, Void, Void> {

    private final HashMap<String, ImageView> mViews;
    private final Context mContext;
    private HashMap<Integer, Equipment> mIdMap;

    public GetEquipmentUpgrades(HashMap<String, ImageView> views, Context c) {
        this.mViews = views;
        this.mIdMap = new HashMap<>();
        this.mContext = c;
        MainActivity.mSelCharacter.uCached = false;
    }
    @Override
    protected Void doInBackground(Void... params) {

        try {
            String queryIDs = "";
            int i = 0;
            for (Equipment e : MainActivity.mSelCharacter.getEquipment()) {
                if (e != null &&
                        e.upgradeIDs != null &&
                        e.upgradeIDs.length > 0 &&
                        e.upgradeIDs[0] != 0) {
                    for (int id : e.upgradeIDs) {
                        if (queryIDs.equals(""))
                            queryIDs = Integer.toString(id);
                        else
                            queryIDs += "," + Integer.toString(id);
                        this.mIdMap.put(id, e);
                    }
                }
                i++;
            }

            HttpURLConnection connection =
                    (HttpsURLConnection) new URL(APIHandler.BASE_API_ITEMS_URI
                            + "?ids=" + queryIDs
                            + "&lang=" + Locale.getDefault().getLanguage())
                            .openConnection();
            JsonReader reader = new JsonReader(
                    new InputStreamReader(connection.getInputStream()));
            JsonParser parser = new JsonParser();
            JsonElement jsonResponse = parser.parse(reader);
            JItem[] items = new Gson().fromJson(jsonResponse, JItem[].class);
            HashMap<Integer, Item> mItems = new HashMap<>();

            for (int _i = 0; _i < items.length; _i++) {
                try {
                    JItem ji = items[_i];
                    if (ji != null) {
                        Item _item = new Item(ji.ID);
                        _item.setName(ji.name);
                        _item.setDescription(ji.description);
                        _item.setType(ji.type);
                        _item.setLevel(ji.level);
                        _item.setRarity(ji.rarity);
                        _item.setVendor_value(ji.vendor_value);
                        //_item.game_types = item.game_types;
                        //_item.flags = item.flags;
                        //_item.restrictions = item.restrictions;
                        //_item.details = item.details;
                        _item.setCount(1);
                        try {
                            InputStream in = new URL(ji.icon).openStream();
                            _item.setIcon(BitmapFactory.decodeStream(in));
                            in.close();
                        } catch (FileNotFoundException e) {
                            _item.setIcon(BitmapFactory.decodeResource(mContext.getResources(),
                                    R.drawable.empty_slot));
                        }
                        mItems.put(_item.getID(), _item);
                    }
                } catch (Exception e) {
                    String t = e.getMessage();
                }
            }
            for (Equipment _e : MainActivity.mSelCharacter.getEquipment()) {
                if (_e.upgrades != null && _e.upgrades.length > 0)
                    _e.upgrades[0] = mItems.get(_e.upgradeIDs[0]);
                if (_e.upgrades != null && _e.upgrades.length > 1)
                    _e.upgrades[1] = mItems.get(_e.upgradeIDs[1]);
            }
            MainActivity.mSelCharacter.uCached = true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    protected void onPostExecute(Void v) {
        for (Equipment e : MainActivity.mSelCharacter.getEquipment()) {
            if (e.upgrades != null && e.upgrades.length > 0) {
                if (this.mViews.containsKey(e.slot + "U"))
                    this.mViews.get(e.slot + "U").setImageBitmap(e.upgrades[0].getIcon());
            }
            if (e.upgrades != null && e.upgrades.length > 1) {
                String slot = "";
                if (e.slot.contains("A1")) {
                    slot = e.slot.replace("A1", "A2U");
                    mViews.get("WeaponA2").setImageDrawable(mViews.get("WeaponA1").getDrawable());
                    mViews.get("WeaponA2").setImageAlpha(50);
                } else if (e.slot.contains("B1")) {
                    slot = e.slot.replace("B1", "B2U");
                    mViews.get("WeaponB2").setImageDrawable(mViews.get("WeaponB1").getDrawable());
                    mViews.get("WeaponB2").setImageAlpha(50);
                } else if (e.slot.contains("cA"))
                    slot = e.slot.replace("cA", "cAU2");
                else if (e.slot.contains("cB"))
                    slot = e.slot.replace("cB", "cBU2");
                if (mViews.containsKey(slot))
                    this.mViews.get(slot)
                            .setImageBitmap(e.upgrades[1].getIcon());
            }
        }
    }
}