package melted.tyrian.Local;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Stephen on 7/3/2015.
 */
public class Equipment {

    public int id;

    public String slot;

    public int[] upgrades;

    public int[] infusions;

    public int skin;

    public Item item;

    public Equipment(int id, int[] infusions, int skin, String slot, int[] upgrades) {
        this.id = id;
        this.infusions = infusions;
        this.skin = skin;
        this.slot = slot;
        this.upgrades = upgrades;
    }
}
