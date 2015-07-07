package melted.tyrian.Async;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

import javax.net.ssl.HttpsURLConnection;

import melted.tyrian.ANet.APIHandler;
import melted.tyrian.ANet.JEquipment;
import melted.tyrian.ANet.JInventory;
import melted.tyrian.ANet.JItem;
import melted.tyrian.Local.Bag;
import melted.tyrian.Local.Equipment;
import melted.tyrian.Local.Item;
import melted.tyrian.MainActivity;
import melted.tyrian.R;

/**
 * Created by Stephen on 7/6/2015.
 */
public class GetEquipment extends AsyncTask<Void, Void, Void> {

    private final HashMap<String, ImageView> mViews;
    private final Context mContext;
    private HashMap<Integer, Equipment> mIdMap;
    private HashMap<Integer, Integer> mPosMap;

    public GetEquipment(HashMap<String, ImageView> views, Context c) {
        this.mViews = views;
        this.mIdMap = new HashMap<>();
        this.mPosMap = new HashMap<>();
        this.mContext = c;
        MainActivity.mSelCharacter.eCached = false;
    }
    @Override
    protected Void doInBackground(Void... params) {

        try {
            String queryIDs = "";
            int i = 0;
            for (Equipment e : MainActivity.mSelCharacter.getEquipment()) {
                if (e != null) {
                    if (queryIDs.equals(""))
                        queryIDs = Integer.toString(e.id);
                    else
                        queryIDs += "," + Integer.toString(e.id);
                    this.mIdMap.put(e.id, e);
                    this.mPosMap.put(e.id, i);
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
                    //TODO: May need to soft cache
                    //offset++;
                }
            }
            for (Equipment _e : MainActivity.mSelCharacter.getEquipment()) {
                _e.item = mItems.get(_e.id);
            }
            MainActivity.mSelCharacter.eCached = true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    protected void onPostExecute(Void v) {
        for (Equipment e : MainActivity.mSelCharacter.getEquipment()) {
            if (e != null && e.item != null) {
                if (this.mViews.containsKey(e.slot))
                    this.mViews.get(e.slot).setImageBitmap(e.item.getIcon());
                if (e.isTwoHanded()) {
                    if (e.slot.contains("A1"))
                        this.mViews.get(e.slot.replace("A1", "A2")).setImageBitmap(e.item.getIcon());
                    else if (e.slot.contains("B1"))
                        this.mViews.get(e.slot.replace("B1", "B2")).setImageBitmap(e.item.getIcon());
                }
            }
        }
    }
}
