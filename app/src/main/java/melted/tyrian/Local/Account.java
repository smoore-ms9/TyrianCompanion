package melted.tyrian.Local;

import java.util.Date;

/**
 * Created by Stephen on 7/2/2015.
 */
public class Account {

    private String id;

    private String name;

    private World world;

    private Date created;

    private Guild[] guilds;

    public Account(String id, String name, World world, Date created, Guild[] guilds) {
        this.id = id;
        this.name = name;
        this.world = world;
        this.created = created;
        this.guilds = guilds;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public World getWorld() {
        return world;
    }

    public Date getCreated() {
        return created;
    }

    public Guild[] getGuilds() {
        return guilds;
    }
}
