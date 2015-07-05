package melted.tyrian.ANet;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Stephen on 7/3/2015.
 */
public class JInventory {

    @SerializedName("id")
    public int id;

    @SerializedName("count")
    public int count;

    public JInventory() {}
}
