package melted.tyrian.ANet;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.widget.ImageView;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import com.google.gson.stream.JsonReader;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.ref.WeakReference;
import java.net.URL;
import java.util.Collections;
import java.util.Locale;

import javax.net.ssl.HttpsURLConnection;

import melted.tyrian.MainActivity;
import melted.tyrian.R;

/**
 * Created by Stephen on 6/28/2015.
 */
public class Item extends AsyncTask<Integer, Integer, Long> {

    private final WeakReference<ImageView> IV_REF;
    private final MainActivity.ContentFragment.BankAdapter.ImageAdapter adapter;

    private String name;
    private String description;
    private String type;
    private int level;
    private String rarity;
    private double vendor_value;
    private String[] game_types;
    private String[] flags;
    private String[] restrictions;
    private int ID;
    private Bitmap icon;
    private String[] details;

    public Item(int id, ImageView iv, MainActivity.ContentFragment.BankAdapter.ImageAdapter adapter)  {
        this.ID = id;
        this.IV_REF = new WeakReference<ImageView>(iv);
        this.adapter = adapter;
    }

    private void FetchItem(int id) throws IOException {
        HttpsURLConnection connection =
                (HttpsURLConnection) new URL(APIHandles.BASE_API_ITEMS_URI
                        + "?ids=" + id
                        + "&lang=" + Locale.getDefault().getLanguage())
                        .openConnection();

        // fetch data from server
        JsonReader reader = new JsonReader(
                new InputStreamReader(connection.getInputStream()));
        JsonParser parser = new JsonParser();
        JsonElement jsonResponse = parser.parse(reader);

        try {
            JItem[] items = new Gson().fromJson(jsonResponse, JItem[].class);
            JItem item = items[0];
            this.name = item.name;
            this.description = item.description;
            this.type = item.type;
            this.level = item.level;
            this.rarity = item.rarity;
            this.vendor_value = item.vendor_value;
            //this.game_types = item.game_types;
            //this.flags = item.flags;
            //this.restrictions = item.restrictions;
            Bitmap mIcon11 = null;
            InputStream in = new URL(item.icon).openStream();
            this.icon = BitmapFactory.decodeStream(in);
            //this.details = item.details;
        } catch (Exception e) {
            String t = e.getMessage();
        }
    }

    @Override
    protected Long doInBackground(Integer... params) {
        try {
            FetchItem(ID);
        } catch (IOException e) {
            //TODO: Error handle
            e.printStackTrace();
        }
        return (long) ID;
    }

    protected void onPostExecute(Long result) {
        if (IV_REF != null) {
            ImageView imageView = IV_REF.get();
            if (imageView != null) {
                if (icon != null) {
                    imageView.setImageBitmap(icon);
                } else {
                    imageView.setImageResource(R.drawable.empty_slot);
                }
                adapter.notifyDataSetChanged();
            }
        }
    }
}