package com.pivotallabs.orm;


import javax.persistence.*;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

import static javax.persistence.FetchType.LAZY;

@XmlRootElement
@Table
@Entity
public class User {

    @Id
    @Column(length = 256)
    private String name;

    @Column(length = 256)
    private String email;
    @ManyToMany(fetch = LAZY, cascade = CascadeType.ALL)
    @JoinTable(name = "user_role", joinColumns = {@JoinColumn(name = "user_name")}, inverseJoinColumns = {@JoinColumn(name = "role_name")})
    private List<Role> roles;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public List<Role> getRoles() {
        return roles;
    }

    public void setRoles(List<Role> roles) {
        this.roles = roles;
    }
}
