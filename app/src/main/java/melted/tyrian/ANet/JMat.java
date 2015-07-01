package melted.tyrian.ANet;

import android.widget.ImageView;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Stephen on 6/28/2015.
 */
public class JMat {

    @SerializedName("id")
    public int id;

    @SerializedName("count")
    public int count;

    @SerializedName("category")
    public int category;

    public JMat() {}
}
