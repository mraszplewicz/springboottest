package pl.mrasoft.springboottest.model;

import javax.persistence.*;

@Entity
public class Wasteland2NPC {

    @Id
    @GeneratedValue(generator = "default")
    private Long id;
    private String name;
    private String location;
    private String test;

    public Wasteland2NPC() {
    }

    public Wasteland2NPC(String name, String location) {
        this.name = name;
        this.location = location;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getLocation() {
        return location;
    }
}
