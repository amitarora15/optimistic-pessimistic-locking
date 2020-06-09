package com.amit.locking.service;

import com.amit.locking.entity.Shop;
import com.amit.locking.entity.User;
import com.amit.locking.repository.ShopRepo;
import com.amit.locking.repository.UserRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ShopService {

    private final ShopRepo shopRepo;

    private final UserRepo userRepo;

    private final UserService userService;

    public Optional<Shop> getShop(String name){
        return shopRepo.findShopByName(name);
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public Shop createShop(String name, String userName) {
        Optional<User> u = userRepo.findByName(userName);
        if(u.isPresent()) {
            Shop shop = new Shop();
            shop.setName(name);
            shop.setOwner(u.get());
            return shopRepo.save(shop);
        }
        return null;
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public Shop createShopWithUserType(String name, String type, User sameUser) {
        Optional<User> optionalUser = userRepo.findByType(type);
        if (optionalUser.isPresent()) {

            sameUser.setType("Contract");
            User returnUser = userService.saveUserInNewTransaction(sameUser);

            Shop shop = new Shop();
            shop.setName(name);

            User shopOwner = optionalUser.get();
            shop.setOwner(shopOwner);
            return shopRepo.save(shop);
        }
        return null;
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public Shop createShopWithUserTypeFetchAgain(String name, String type, User sameUser) {
        Optional<User> optionalUser = userRepo.findByType(type);
        if (optionalUser.isPresent()) {

            sameUser.setType("Contract");
            User returnUser = userService.saveUserInNewTransaction(sameUser);

            Shop shop = new Shop();
            shop.setName(name);

            shop.setOwner(returnUser);
            return shopRepo.save(shop);
        }
        return null;
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public Shop createShopFromWebRequest(String name, String type) throws InterruptedException {
        Optional<User> optionalUser = userRepo.findByType(type);
        if (optionalUser.isPresent()) {

            Thread.sleep(2000);

            Shop shop = new Shop();
            shop.setName(name);

            User shopOwner = optionalUser.get();
            shop.setOwner(shopOwner);
            return shopRepo.save(shop);
        }
        return null;
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public void deleteShop(Shop s) {
        shopRepo.delete(s);
    }
}
