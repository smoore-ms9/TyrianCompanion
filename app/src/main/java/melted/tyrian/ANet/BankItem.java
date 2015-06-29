package melted.tyrian.ANet;

import android.widget.ImageView;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Stephen on 6/28/2015.
 */
public class BankItem {

    @SerializedName("id")
    public int id;

    @SerializedName("count")
    public int count;

    //public Item item;

    public BankItem(){
        //this.item = null;
    }

    //public void setItem(ImageView iv)
    //{
    //    this.item = new Item(id, iv);
    //}
}
