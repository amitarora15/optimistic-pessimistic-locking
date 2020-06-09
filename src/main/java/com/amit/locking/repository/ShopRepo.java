package com.amit.locking.repository;

import com.amit.locking.entity.Shop;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ShopRepo extends CrudRepository<Shop, Long> {

    Optional<Shop> findShopByName(String name);
}
