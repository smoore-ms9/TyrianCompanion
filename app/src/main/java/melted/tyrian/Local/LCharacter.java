package melted.tyrian.Local;

import java.lang.reflect.Array;
import java.util.ArrayList;

import melted.tyrian.ANet.JCharacter;

/**
 * Created by Stephen on 7/3/2015.
 */
public class LCharacter {

    private String name;

    private String race;

    private String gender;

    private String profession;

    private int level;

    private Guild guild;

    private String created;

    private int age;

    private int deaths;

    private ArrayList<Equipment> equipment;

    private Bag[] bags;

    public boolean eCached;
    public boolean uCached;
    public boolean iCached;

    public LCharacter() {
        eCached = false;
        uCached = false;
        iCached = false;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRace() {
        return race;
    }

    public void setRace(String race) {
        this.race = race;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getProfession() {
        return profession;
    }

    public void setProfession(String profession) {
        this.profession = profession;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public Guild getGuild() {
        return guild;
    }

    public void setGuild(Guild guild) {
        this.guild = guild;
    }

    public String getCreated() {
        return created;
    }

    public void setCreated(String created) {
        this.created = created;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public int getDeaths() {
        return deaths;
    }

    public void setDeaths(int deaths) {
        this.deaths = deaths;
    }

    public ArrayList<Equipment> getEquipment() {
        return equipment;
    }

    public void setEquipment(ArrayList<Equipment> equipment) {
        this.equipment = equipment;
    }

    public Bag[] getBags() {
        return bags;
    }

    public void setBags(Bag[] bags) {
        this.bags = bags;
    }

    //TODO: Grab adapter, fetch equip and bags, notify on done
    public void QueryInventory() {

    }
}
