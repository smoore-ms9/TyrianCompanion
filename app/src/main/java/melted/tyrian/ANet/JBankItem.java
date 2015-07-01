package melted.tyrian.ANet;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Stephen on 6/28/2015.
 */
public class JBankItem {

    @SerializedName("id")
    public int id;

    @SerializedName("count")
    public int count;

    public JBankItem() {}
}
