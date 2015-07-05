package melted.tyrian.Async;

import android.os.AsyncTask;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

import melted.tyrian.ANet.APIHandler;
import melted.tyrian.ANet.JAccount;
import melted.tyrian.ANet.JCharacter;
import melted.tyrian.ANet.JWorld;
import melted.tyrian.Adapters.CharacterListAdapter;
import melted.tyrian.Helpers.ConversionHelper;
import melted.tyrian.Helpers.KeyHelper;
import melted.tyrian.Local.Account;
import melted.tyrian.Local.Guild;
import melted.tyrian.Local.LCharacter;
import melted.tyrian.Local.World;
import melted.tyrian.MainActivity;

/**
 * Created by Stephen on 7/3/2015.
 */
public class GetCharacters extends AsyncTask<Void, Void, Void>{
    private final CharacterListAdapter mAdapter;

    public GetCharacters(CharacterListAdapter cla) {
        this.mAdapter = cla;
    }

    @Override
    protected Void doInBackground(Void... params) {
        try {
            // Get Account Info
            HttpsURLConnection connection;
            connection = (HttpsURLConnection) new URL(APIHandler.getAuthUriWithParams(
                    APIHandler.BASE_API_CHAR_URI + "?page=0", KeyHelper.key))
                    .openConnection();
            JsonReader reader = new JsonReader(
                    new InputStreamReader(connection.getInputStream()));
            JsonParser parser = new JsonParser();
            JsonElement jsonResponse = parser.parse(reader);
            ArrayList<JCharacter> jCharacters = new Gson().fromJson(jsonResponse,
                    new TypeToken<List<JCharacter>>() {}.getType());
            for (JCharacter jc : jCharacters) {
                LCharacter lc = ConversionHelper.fromJCharacter(jc);
                MainActivity.mCharacters.add(lc);
            }

        } catch (IOException e) {
            String t = "stub";
        }
        return null;
    }

    protected void onPostExecute(Void v) {
        mAdapter.notifyDataSetChanged();
    }
}
