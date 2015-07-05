package melted.tyrian.ANet;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Stephen on 7/3/2015.
 */
public class JBag {

    @SerializedName("id")
    public int id;

    @SerializedName("size")
    public int size;

    @SerializedName("inventory")
    public JInventory[] inventory;

    public JBag() {}
}
