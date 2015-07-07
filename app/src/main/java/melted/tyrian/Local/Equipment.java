package melted.tyrian.Local;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by Stephen on 7/3/2015.
 */
public class Equipment {

    public int id;

    public String slot;

    public Item[] upgrades;

    public Item[] infusions;

    public int[] upgradeIDs;

    public int[] infusionIDs;

    public Item skin;

    public Item item;

    private final ArrayList<String> TWO_HANDED_SLOTS = new ArrayList<String>() {
        { add("Greatsword"); add("Hammer"); add("LongBow"); add("Rifle"); add("ShortBow"); add("Staff"); }
    };

    public Equipment(int id, String slot, int[] upgradeIDs, int[] infusionIDs) {
        this.id = id;
        this.slot = slot;
        this.upgradeIDs = upgradeIDs;
        this.infusionIDs = infusionIDs;
        if (upgradeIDs != null)
            this.upgrades = new Item[upgradeIDs.length];
        if (infusionIDs != null)
            this.infusions = new Item[infusionIDs.length];
    }

    public boolean isTwoHanded() {
        //return TWO_HANDED_SLOTS.contains(item.getDetails()[0]);
        return false;
    }
}
