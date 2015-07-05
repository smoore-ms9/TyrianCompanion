package melted.tyrian.Helpers;

import android.content.Context;
import android.content.res.Resources;
import android.util.TypedValue;

import melted.tyrian.ANet.JBag;
import melted.tyrian.ANet.JCharacter;
import melted.tyrian.ANet.JEquipment;
import melted.tyrian.Local.Bag;
import melted.tyrian.Local.Equipment;
import melted.tyrian.Local.Item;
import melted.tyrian.Local.LCharacter;

/**
 * Created by Stephen on 7/3/2015.
 */
public class ConversionHelper {

    public static LCharacter fromJCharacter(JCharacter jc) {
        LCharacter c = new LCharacter();

        c.setAge(jc.age);
        c.setGender(jc.gender);
        c.setCreated(jc.created);
        c.setDeaths(jc.deaths);
        c.setLevel(jc.level);
        c.setName(jc.name);
        c.setProfession(jc.profession);
        c.setRace(jc.race);
        //TODO: Set guild, equipment and bags
        Bag[] bags = new Bag[jc.bags.length];
        for (int i = 0; i < jc.bags.length; i++) {
            JBag jb = jc.bags[i];
            Bag b = new Bag(jb.id, jb.size, jb.inventory);
            bags[i] = b;
        }
        c.setBags(bags);

        Equipment[] eq = new Equipment[jc.equipment.length];
        for (int i = 0; i < jc.equipment.length; i++) {
            JEquipment je = jc.equipment[i];
            Equipment e = new Equipment(je.id, je.infusions, je.skin, je.slot, je.upgrades);
        }
        return c;
    }
}
