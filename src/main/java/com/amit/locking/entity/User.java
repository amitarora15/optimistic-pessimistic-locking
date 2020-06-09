package com.amit.locking.entity;

import lombok.Data;

import javax.persistence.*;
import java.util.List;

@Entity
@Data
@Table(name = "user")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    @Column
    private String type;

    @OneToMany(mappedBy = "user")
    private List<Product> products;

    @Version
    @Column(columnDefinition = "integer default 0", nullable = false)
    private Long version;

    @OneToOne(mappedBy = "owner")
    private Shop shop;

}
