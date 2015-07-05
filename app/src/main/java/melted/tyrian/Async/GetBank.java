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
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

import melted.tyrian.ANet.APIHandler;
import melted.tyrian.ANet.JBankItem;
import melted.tyrian.Adapters.BankAdapter;
import melted.tyrian.Helpers.KeyHelper;
import melted.tyrian.Local.BankItem;
import melted.tyrian.MainActivity;

/**
 * Created by Stephen on 7/3/2015.
 */
public class GetBank extends AsyncTask<Void, Void, Void> {
    private final BankAdapter bAdapter;

    public GetBank(BankAdapter adapter) {
        this.bAdapter = adapter;
    }

    @Override
    protected Void doInBackground(Void... params) {
        bAdapter.loading = true;
        MainActivity.bCached = false;
        bAdapter.index = 0;
        HttpsURLConnection connection;
        try {
            connection = (HttpsURLConnection) new URL(APIHandler.getAuthUri(
                    APIHandler.BASE_API_BANK_URI, KeyHelper.key))
                    .openConnection();

            // fetch data from server
            JsonReader reader = new JsonReader(
                    new InputStreamReader(connection.getInputStream()));
            JsonParser parser = new JsonParser();
            JsonElement jsonResponse = parser.parse(reader);
            ArrayList<JBankItem> jbItems = new Gson().fromJson(jsonResponse,
                    new TypeToken<List<JBankItem>>() {
                    }.getType());
            BankItem[] _bItems = new BankItem[30];
            MainActivity.bItems = new ArrayList<>();
            MainActivity.bItems.add(_bItems);
            int i = 0;
            for (JBankItem _j : jbItems) {
                BankItem _b;
                if (_j != null) {
                    _b = new BankItem(_j.id, _j.count, i);
                } else {
                    _b = new BankItem(0, 0, i);
                }
                if (i >= 30) {
                    _bItems = new BankItem[30];
                    MainActivity.bItems.add(_bItems);
                    i = 0;
                }
                _bItems[i] = _b;
                i++;
            }
            MainActivity.bCached = true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    protected void onPostExecute(Void v) {
        bAdapter.loading = false;
        bAdapter.notifyDataSetChanged();
    }
}
