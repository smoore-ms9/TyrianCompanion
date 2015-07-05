package melted.tyrian.ANet;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Stephen on 7/3/2015.
 */
public class JEquipment {

    @SerializedName("id")
    public int id;

    @SerializedName("slot")
    public String slot;

    @SerializedName("upgrades")
    public int[] upgrades;

    @SerializedName("infusions")
    public int[] infusions;

    @SerializedName("skin")
    public int skin;

    public JEquipment() {}
}
