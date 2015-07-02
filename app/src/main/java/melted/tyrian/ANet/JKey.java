package melted.tyrian.ANet;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Stephen on 7/2/2015.
 */
public class JKey {

    @SerializedName("key")
    public String key;

    @SerializedName("name")
    public String name;

    public JKey() {}

    public JKey(String key, String name) {
        this.key = key;
        this.name = name;
    }
}
