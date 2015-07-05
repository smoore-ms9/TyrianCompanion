package melted.tyrian.ANet;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Stephen on 7/3/2015.
 */
public class JCharacter {

    @SerializedName("name")
    public String name;

    @SerializedName("race")
    public String race;

    @SerializedName("gender")
    public String gender;

    @SerializedName("profession")
    public String profession;

    @SerializedName("level")
    public int level;

    @SerializedName("guild")
    public String guild;

    @SerializedName("created")
    public String created;

    @SerializedName("age")
    public int age;

    @SerializedName("deaths")
    public int deaths;

    @SerializedName("equipment")
    public JEquipment[] equipment;

    @SerializedName("bags")
    public JBag[] bags;

    public JCharacter() {}
}
