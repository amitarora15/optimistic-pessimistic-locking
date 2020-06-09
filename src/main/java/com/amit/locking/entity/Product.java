package com.amit.locking.entity;


import lombok.Data;

import javax.persistence.*;

@Entity
@Data
@Table(name = "product")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column
    private Long price;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Version
    @Column(columnDefinition = "integer default 0", nullable = false)
    private Long version;

}