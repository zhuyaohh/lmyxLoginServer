package com.cvovo.gamemanager.game.persist.entity;

import javax.persistence.*;

@Entity
@Table(name = "text")
public class Text {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = true, length = 65)
    private String name;

    @Column(nullable = true, length = 65)
    private String password;

    public Text(String name, String password){
        this.name = name;
        this.password = password;
    }

    public Text() {}

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getPassword() {
        return password;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPassword(String password) {
        this.password = password;
    }


}
