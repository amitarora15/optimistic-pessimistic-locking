package com.amit.locking.entity;

import lombok.Data;

import javax.persistence.*;

@Entity
@Data
@Table(name = "shop")
public class Shop {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    @Version
    @Column(columnDefinition = "integer default 1", nullable = false)
    private Long version;

    @OneToOne(optional = false)
    private User owner;

}
