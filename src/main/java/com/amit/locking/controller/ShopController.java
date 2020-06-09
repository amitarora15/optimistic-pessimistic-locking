package com.amit.locking.controller;

import com.amit.locking.entity.Shop;
import com.amit.locking.service.ShopService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/shop")
@RequiredArgsConstructor
public class ShopController {

    private final ShopService shopService;

    @PostMapping
    public void createShop(@RequestBody Shop shop) {
        try {
            shopService.createShopFromWebRequest(shop.getName(), shop.getOwner().getType());
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
