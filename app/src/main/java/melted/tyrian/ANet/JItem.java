package melted.tyrian.ANet;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Stephen on 6/28/2015.
 */
public class JItem {

    @SerializedName("name")
    public String name;
    @SerializedName("description")
    public String description;
    @SerializedName("type")
    public String type;
    @SerializedName("level")
    public int level;
    @SerializedName("rarity")
    public String rarity;
    @SerializedName("vendor_value")
    public double vendor_value;
    //@SerializedName("game_types")
    //public String[] game_types;
    //@SerializedName("flags")
    //public String[] flags;
    //@SerializedName("restrictions")
    //public String[] restrictions;
    @SerializedName("id")
    public int ID;
    @SerializedName("icon")
    public String icon;
    //@SerializedName("details")
    //public String[] details;

    public JItem() {
    }
}
