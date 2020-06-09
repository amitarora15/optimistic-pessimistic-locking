package com.amit.locking.service;

import com.amit.locking.entity.Shop;
import com.amit.locking.entity.User;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlGroup;

import javax.annotation.Resource;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@SqlGroup({
        @Sql(value = "classpath:createUser.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD),
        @Sql(value = "classpath:deleteUser.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
})
public class ShopServiceTest {

    @Resource
    private ShopService shopServiceUnderTest;

    @Resource
    private UserService userService;

    private String shopName = "Mark & Spencer";

    @AfterEach
    public void cleanUpShop() {
        Optional<Shop> shop = shopServiceUnderTest.getShop(shopName);
        if(shop.isPresent())
            shopServiceUnderTest.deleteShop(shop.get());
    }

    @Test
    public void shopCreateWithUserUpdatedForceIncrementLock_updateUserConcurrently_Failed() {

        User user = userService.getUser(1L);
        assertAll("User Get Failed",
                () -> {
                    assertEquals("Amit", user.getName());
                },
                () -> {
                    assertEquals(1, user.getVersion());
                }
        );

        Shop shop = shopServiceUnderTest.createShop(shopName, "Amit");
        assertAll(
                () -> assertEquals(shopName, shop.getName(), "Different shop name"),
                () -> assertEquals(0, shop.getVersion(), "Different shop version")
        );

        user.setType("Contract");
        assertThrows(ObjectOptimisticLockingFailureException.class, () -> {
            userService.saveUser(user);
        }, "User should not be updated here as it was updated force increment lock by getting the user in createshop");
    }

    @Test
    public void shopCreateWithUserUpdatedForceIncrementLock_GetUserSuccessWithUpdatedVersion() {

        User user = userService.getUser(1L);
        assertAll("User Get Failed",
                () -> {
                    assertEquals("Amit", user.getName());
                },
                () -> {
                    assertEquals(1, user.getVersion());
                }
        );

        Shop shop = shopServiceUnderTest.createShop(shopName, "Amit");
        assertAll(
                () -> assertEquals(shopName, shop.getName(), "Different shop name"),
                () -> assertEquals(0, shop.getVersion(), "Different shop version")
        );

        User user1 = userService.getUser(1L);
        assertAll("User Get Failed",
                () -> {
                    assertEquals("Amit", user1.getName());
                },
                () -> {
                    assertEquals(2, user1.getVersion());
                }
        );
    }

    @Test
    public void shopCreateWithUserInOtimisticLock_updateUserConcurrently_Failed() {
        User user = userService.getUser(1L);
        assertAll("User Get Failed",
                () -> {
                    assertEquals("Amit", user.getName());
                },
                () -> {
                    assertEquals(1, user.getVersion());
                }
        );
        assertThrows(ObjectOptimisticLockingFailureException.class, () -> {
            shopServiceUnderTest.createShopWithUserType(shopName, "Permanent", user);
        });

        User updatedUser = userService.getUser(1L);
        assertAll("User Get Failed",
                () -> {
                    assertEquals("Amit", updatedUser.getName());
                },
                () -> {
                    assertEquals(2, updatedUser.getVersion());
                }
        );
    }

    @Test
    public void shopCreateWithUserInOtimisticLock_updateUserConcurrentlyUseLatestUser_Success() {
        User user = userService.getUser(1L);
        assertAll("User Get Failed",
                () -> {
                    assertEquals("Amit", user.getName());
                },
                () -> {
                    assertEquals(1, user.getVersion());
                }
        );

        assertThrows(ObjectOptimisticLockingFailureException.class, () -> {
            shopServiceUnderTest.createShopWithUserType(shopName, "Permanent", user);
        }, "Transaction is dirty, so should not be allowed even if user is fetched again and saved");

        User updatedUser = userService.getUser(1L);
        assertAll("User Get Failed",
                () -> {
                    assertEquals("Amit", updatedUser.getName());
                },
                () -> {
                    assertEquals(2, updatedUser.getVersion());
                }
        );
    }
}
