package melted.tyrian.ANet;

/**
 * Created by Stephen on 6/28/2015.
 */
public class APIHandles {

    public static final String BASE_API_URI = "https://api.guildwars2.com/v2";
    public static final String BASE_API_ITEMS_URI = BASE_API_URI + "/items";
    public static final String BASE_API_ACC_URI   = BASE_API_URI + "/account";
    public static final String BASE_API_BANK_URI  = BASE_API_URI + "/account/bank";
    public static final String BASE_API_MATS_URI  = BASE_API_URI + "/account/materials";

    public static String getAuthUri(String base, String key) {
        return base + "?access_token=" + key;
    }

    public static String getAuthUriWithParams(String base, String key) {
        return base + "&access_token=" + key;
    }
}
