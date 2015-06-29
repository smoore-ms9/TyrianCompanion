package melted.tyrian.ANet;

import android.widget.ImageView;

/**
 * Created by Stephen on 6/28/2015.
 */
public class Mat {

    private final int id;
    private Item item;
    private int count;
    private int category;

    public Mat(int id, int count, int category)
    {
        this.id = id;
        this.count = count;
        this.category = category;
    }

    public void setItem(ImageView iv)
    {
        //this.item = new Item(id, iv);
    }
}
