package melted.tyrian.Async;

import android.graphics.BitmapFactory;
import android.os.AsyncTask;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.net.ssl.HttpsURLConnection;

import melted.tyrian.ANet.APIHandler;
import melted.tyrian.ANet.JItem;
import melted.tyrian.ANet.JMat;
import melted.tyrian.Adapters.MatsAdapter;
import melted.tyrian.Helpers.KeyHelper;
import melted.tyrian.Local.Item;
import melted.tyrian.Local.Mat;
import melted.tyrian.MainActivity;

/**
 * Created by Stephen on 7/3/2015.
 */
public class GetMats extends AsyncTask<Void, Void, Void> {
    private final MatsAdapter.MatsItemAdapter mAdapter;
    private final int mCardNum;

    public GetMats(MatsAdapter.MatsItemAdapter adapter, int cardNum) {
        this.mAdapter = adapter;
        this.mCardNum = cardNum;
    }

    @Override
    protected Void doInBackground(Void... params) {
        MatsAdapter.mLoadingFlags[mCardNum] = true;
        HttpsURLConnection connection;
        JsonReader reader;
        JsonParser parser;
        JsonElement jsonResponse;
        try {
            if (MainActivity.mIDs.size() == 0 && !MatsAdapter.preLoading) {
                MatsAdapter.preLoading = true;
                MainActivity.mIDs = new ArrayList<>();
                connection = (HttpsURLConnection) new URL(APIHandler.getAuthUri(
                        APIHandler.BASE_API_MATS_URI, KeyHelper.key))
                        .openConnection();

                reader = new JsonReader(
                        new InputStreamReader(connection.getInputStream()));
                parser = new JsonParser();
                jsonResponse = parser.parse(reader);

                ArrayList<JMat> jMats = new Gson().fromJson(jsonResponse,
                        new TypeToken<List<JMat>>() {
                        }.getType());

                for (JMat _j : jMats) {
                    if (_j != null) {
                        MainActivity.mIDs.add(_j.id);
                        MainActivity.jMatsById.get(MatsAdapter.CAT_NUMS.get(_j.category)).put(_j.id, _j);
                    }
                }
                MatsAdapter.preLoading = false;
            }

            while (MatsAdapter.preLoading) {
                Thread.sleep(10);
            }

            String queryIDs = "";
            for (int id : MainActivity.jMatsById.get(mCardNum).keySet()) {
                if (queryIDs.equals(""))
                    queryIDs = Integer.toString(id);
                else
                    queryIDs += "," + Integer.toString(id);
            }

            connection =
                    (HttpsURLConnection) new URL(APIHandler.BASE_API_ITEMS_URI
                            + "?ids=" + queryIDs
                            + "&lang=" + Locale.getDefault().getLanguage())
                            .openConnection();

            // fetch data from server
            reader = new JsonReader(
                    new InputStreamReader(connection.getInputStream()));
            parser = new JsonParser();
            jsonResponse = parser.parse(reader);
            JItem[] items = new Gson().fromJson(jsonResponse, JItem[].class);
            int offset = 0;
            if (MainActivity.mCats[mCardNum].size() == 0 || !MainActivity.mCaches[mCardNum])
                for (JItem ji : items) {
                    try {
                        if (ji != null) {
                            JMat jm = MainActivity.jMatsById.get(mCardNum).get(ji.ID);
                            InputStream in = new URL(ji.icon).openStream();
                            Item _item = new Item(ji.ID);
                            _item.setName(ji.name);
                            _item.setDescription(ji.description);
                            _item.setType(ji.type);
                            _item.setLevel(ji.level);
                            _item.setRarity(ji.rarity);
                            _item.setVendor_value(ji.vendor_value);
                            _item.setIcon(BitmapFactory.decodeStream(in));
                            //_item.game_types = item.game_types;
                            //_item.flags = item.flags;
                            //_item.restrictions = item.restrictions;
                            //_item.details = item.details;
                            Mat _m = new Mat(jm.id, jm.count, jm.category,
                                    MainActivity.mIDs.indexOf(jm.id));
                            _m.setItem(_item);
                            MainActivity.mCats[mCardNum].add(_m);
                        }
                    } catch (Exception e) {
                        String t = e.getMessage();
                        offset++;
                    }
                }
            if (MainActivity.mCats[mCardNum].size() == (items.length - offset))
                MainActivity.mCaches[mCardNum] = true;
        } catch (IOException e) {
            MainActivity.mCats[mCardNum] = new ArrayList<>();
            e.printStackTrace();
        } catch (InterruptedException e) {
            MainActivity.mCats[mCardNum] = new ArrayList<>();
            e.printStackTrace();
        }
        return null;
    }

    protected void onPostExecute(Void v) {
        MatsAdapter.mLoadingFlags[mCardNum] = false;
        mAdapter.notifyDataSetChanged();
    }
}
