package melted.tyrian.Local;

import com.google.gson.annotations.SerializedName;

import melted.tyrian.ANet.JInventory;

/**
 * Created by Stephen on 7/3/2015.
 */
public class Bag {

    private int id;

    private int size;

    private JInventory[] jInventory;

    public Item bagItem;

    public Item[] inventory;

    public Bag(int id, int size, JInventory[] ji) {
        this.id = id;
        this.size = size;
        this.jInventory = ji;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public JInventory[] getjInventory() {
        return jInventory;
    }
}
