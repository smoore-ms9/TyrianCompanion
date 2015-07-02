package melted.tyrian.Local;

/**
 * Created by Stephen on 7/2/2015.
 */
public class World {
    private final String id;
    private final String name;

    public World(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
