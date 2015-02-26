package pl.mrasoft.springboottest.controllers.dto;

public class Wasteland2NPC {

    private long id;
    private String name;
    private String location;

    public Wasteland2NPC(long id, String name, String location) {
        this.id = id;
        this.name = name;
        this.location = location;
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getLocation() {
        return location;
    }
}
