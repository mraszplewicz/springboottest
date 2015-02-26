package pl.mrasoft.springboottest.controllers.dto;

public class Wasteland2Weapon {

    private long id;
    private String name;
    private String type;

    public Wasteland2Weapon(long id, String name, String type) {
        this.id = id;
        this.name = name;
        this.type = type;
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }
}
