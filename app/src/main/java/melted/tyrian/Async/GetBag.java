package melted.tyrian.Async;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import javax.net.ssl.HttpsURLConnection;

import melted.tyrian.ANet.APIHandler;
import melted.tyrian.ANet.JBankItem;
import melted.tyrian.ANet.JInventory;
import melted.tyrian.ANet.JItem;
import melted.tyrian.ANet.JMat;
import melted.tyrian.Adapters.BagAdapter;
import melted.tyrian.Adapters.BankAdapter;
import melted.tyrian.Helpers.KeyHelper;
import melted.tyrian.Local.Bag;
import melted.tyrian.Local.BankItem;
import melted.tyrian.Local.Item;
import melted.tyrian.Local.Mat;
import melted.tyrian.MainActivity;
import melted.tyrian.R;

/**
 * Created by Stephen on 7/4/2015.
 */
public class GetBag extends AsyncTask<Void, Void, Void> {

    private final BagAdapter mAdapter;
    private final int mSlot;
    private final Context mContext;
    private TextView tvName;
    private HashMap<Integer, Integer> mIdMap;
    private HashMap<Integer, Integer> mPosMap;

    public GetBag(BagAdapter a, int slot, TextView tvName, Context c) {
        this.mAdapter = a;
        this.mSlot = slot;
        this.tvName = tvName;
        this.mIdMap = new HashMap<>();
        this.mPosMap = new HashMap<>();
        this.mContext = c;
        Bag b = MainActivity.mSelCharacter.getBags()[mSlot];
        b.inventory = new Item[b.getSize()];
    }
    @Override
    protected Void doInBackground(Void... params) {

        try {
            Bag b = MainActivity.mSelCharacter.getBags()[mSlot];
            String queryIDs = Integer.toString(b.getId());
            this.mIdMap.put(b.getId(), 1);
            int i = 0;
            for (JInventory ji : b.getjInventory()) {
                if (ji != null) {
                    if (queryIDs.equals(""))
                        queryIDs = Integer.toString(ji.id);
                    else
                        queryIDs += "," + Integer.toString(ji.id);
                    this.mIdMap.put(ji.id, ji.count);
                    this.mPosMap.put(ji.id, i);
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

            for (JItem ji : items) {
                try {
                    if (ji != null) {
                        Item _item = new Item(ji.ID);
                        _item.setName(ji.name);
                        _item.setDescription(ji.description);
                        _item.setType(ji.type);
                        _item.setLevel(ji.level);
                        _item.setRarity(ji.rarity);
                        _item.setVendor_value(ji.vendor_value);
                        try {
                            InputStream in = new URL(ji.icon).openStream();
                            _item.setIcon(BitmapFactory.decodeStream(in));
                        } catch (FileNotFoundException e) {
                            _item.setIcon(BitmapFactory.decodeResource(mContext.getResources(),
                                    R.drawable.empty_slot));
                        }
                        //_item.game_types = item.game_types;
                        //_item.flags = item.flags;
                        //_item.restrictions = item.restrictions;
                        //_item.details = item.details;
                        _item.setCount(mIdMap.get(ji.ID));

                        if (_item.getID() == b.getId()) {
                            MainActivity.mSelCharacter.getBags()[mSlot].bagItem = _item;
                        } else
                            MainActivity.mSelCharacter.getBags()[mSlot]
                                    .inventory[mPosMap.get(ji.ID)] = _item;
                    }
                } catch (Exception e) {
                    String t = e.getMessage();
                    //TODO: May need to soft cache
                    //offset++;
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    protected void onPostExecute(Void v) {
        if (MainActivity.mSelCharacter.getBags()[mSlot].bagItem != null)
            tvName.setText(MainActivity.mSelCharacter.getBags()[mSlot].bagItem.getName());
        else tvName.setText("Bag " + Integer.toString(mSlot + 1));
        mAdapter.notifyDataSetChanged();
    }
}
