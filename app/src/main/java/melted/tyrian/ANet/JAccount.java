package melted.tyrian.ANet;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Stephen on 6/28/2015.
 */
public class JAccount {

    @SerializedName("id")
    public String id;

    @SerializedName("name")
    public String name;

    @SerializedName("world")
    public String world;

    @SerializedName("guilds")
    public String[] guilds;

    @SerializedName("created")
    public String created;

    public JAccount() {}
}
