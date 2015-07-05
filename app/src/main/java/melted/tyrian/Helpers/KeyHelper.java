package melted.tyrian.Helpers;

import android.app.Activity;
import android.content.Context;

import com.google.gson.Gson;

import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashMap;

import melted.tyrian.ANet.APIHandler;
import melted.tyrian.ANet.JKey;
import melted.tyrian.MainActivity;

/**
 * Created by Stephen on 7/2/2015.
 */
public class KeyHelper {

    public static String key = "";
    public static HashMap<String, String> key_map;
    private final Activity mActivity;

    public KeyHelper(Activity _activity) {
        this.mActivity = _activity;
    }

    public boolean SaveKey(JKey key) {
        if (MainActivity.jKeys == null)
            MainActivity.jKeys = new ArrayList<>();

        MainActivity.jKeys.add(key);

        try {
            FileOutputStream fos = mActivity.openFileOutput(
                    APIHandler.KEY_FILENAME, Context.MODE_PRIVATE);

            String jsonResponse = new Gson().toJson(MainActivity.jKeys);
            fos.write(jsonResponse.getBytes());
            fos.close();


            return true;
        } catch (Exception e) {
            MainActivity.mShowGuide = true;
            return false;
        }
    }

    public boolean RemoveKey(JKey key) {
        if (MainActivity.jKeys == null)
            MainActivity.jKeys = new ArrayList<>();

        if (MainActivity.jKeys.contains(key)) {
            MainActivity.jKeys.remove(key);

            try {
                FileOutputStream fos = mActivity.openFileOutput(
                        APIHandler.KEY_FILENAME, Context.MODE_PRIVATE);

                String jsonResponse = new Gson().toJson(MainActivity.jKeys);
                fos.write(jsonResponse.getBytes());
                fos.close();

                return true;
            } catch (Exception e) {
                MainActivity.mShowGuide = true;
                return false;
            }
        } else return true;
    }
}
