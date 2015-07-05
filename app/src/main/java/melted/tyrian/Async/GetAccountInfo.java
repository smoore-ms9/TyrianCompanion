package melted.tyrian.Async;

import android.os.AsyncTask;
import android.widget.TextView;

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
import melted.tyrian.ANet.JWorld;
import melted.tyrian.Helpers.KeyHelper;
import melted.tyrian.Local.Account;
import melted.tyrian.Local.Guild;
import melted.tyrian.Local.World;
import melted.tyrian.MainActivity;

/**
 * Created by Stephen on 7/3/2015.
 */
public class GetAccountInfo extends AsyncTask<Void, Void, Void> {

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
            connection = (HttpsURLConnection) new URL(APIHandler.getAuthUri(
                    APIHandler.BASE_API_ACC_URI, KeyHelper.key))
                    .openConnection();
            JsonReader reader = new JsonReader(
                    new InputStreamReader(connection.getInputStream()));
            JsonParser parser = new JsonParser();
            JsonElement jsonResponse = parser.parse(reader);
            JAccount jAccount = new Gson().fromJson(jsonResponse, JAccount.class);

            // Get World Info
            connection = (HttpsURLConnection) new URL(APIHandler.BASE_API_WORLD_URI +
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
                connection = (HttpsURLConnection) new URL(APIHandler.getAuthUriWithParams(
                        APIHandler.BASE_API_GUILD_URI + "?ids=" + guildIDs, NavigationDrawerFragment.key))
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