package pl.mrasoft.springboottest.model;

import javax.persistence.*;

@Entity
public class Wasteland2Weapon {

    @Id
    @GeneratedValue(generator = "default")
    private Long id;
    private String name;
    private String type;

    public Wasteland2Weapon() {
    }

    public Wasteland2Weapon(String name, String type) {
        this.name = name;
        this.type = type;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }
}
