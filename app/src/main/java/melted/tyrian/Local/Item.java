package melted.tyrian.Local;

import android.graphics.Bitmap;

/**
 * Created by Stephen on 6/28/2015.
 */
public class Item {

    private String name;
    private String description;
    private String type;
    private int level;
    private String rarity;
    private double vendor_value;
    private String[] game_types;
    private String[] flags;
    private String[] restrictions;
    private int ID;
    private Bitmap icon;
    private String[] details;
    private int count;

    public Item(int id) {
        this.ID = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public String getRarity() {
        return rarity;
    }

    public void setRarity(String rarity) {
        this.rarity = rarity;
    }

    public double getVendor_value() {
        return vendor_value;
    }

    public void setVendor_value(double vendor_value) {
        this.vendor_value = vendor_value;
    }

    public String[] getGame_types() {
        return game_types;
    }

    public void setGame_types(String[] game_types) {
        this.game_types = game_types;
    }

    public String[] getFlags() {
        return flags;
    }

    public void setFlags(String[] flags) {
        this.flags = flags;
    }

    public String[] getRestrictions() {
        return restrictions;
    }

    public void setRestrictions(String[] restrictions) {
        this.restrictions = restrictions;
    }

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public Bitmap getIcon() {
        return icon;
    }

    public void setIcon(Bitmap icon) {
        this.icon = icon;
    }

    public String[] getDetails() {
        return details;
    }

    public void setDetails(String[] details) {
        this.details = details;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
}