package com.amit.locking.service;

import com.amit.locking.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlGroup;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@SqlGroup({
        @Sql(value = "classpath:createUser.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD),
        @Sql(value = "classpath:deleteUser.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
})
public class UserServiceTest {

    @Autowired
    private UserService service;

    @Test
    public void userUpdatedConcurrently_Failed_LockingException() {

        User testUser1 = service.getUser(1L);
        User testUser2 = service.getUser(1L);

        testUser1.setType("Contract");
        User returnUser1 = service.saveUser(testUser1);
        assertAll("User Save First Time Failed",
                () ->  { assertEquals(2, returnUser1.getVersion(), "User version should have been changed to 2");},
                () -> {assertEquals("Contract", returnUser1.getType(), "User type should have been changed to Contract");}
        );

        testUser2.setType("Permanent");
        Throwable e = assertThrows(ObjectOptimisticLockingFailureException.class, () -> {
            service.saveUser(testUser2);
        }, "Optimistic Locking Exception Not Thrown");

    }

    @Test
    public void userUpdatedSequentially_Success() {

        User testUser1 = service.getUser(1L);
        testUser1.setType("Contract");
        User returnUser1 = service.saveUser(testUser1);
        assertAll("User Save First Time Failed",
                () ->  { assertEquals(2, returnUser1.getVersion(), "User version should have been changed to 2");},
                () -> {assertEquals("Contract", returnUser1.getType(), "User type should have been changed to Contract");}
        );

        User testUser2 = service.getUser(1L);
        testUser2.setType("Permanent");
        User returnUser2 = service.saveUser(testUser2);
        assertAll("User Save Second Time Failed",
                () ->  { assertEquals(3, returnUser2.getVersion(), "User version should have been changed to 3");},
                () -> {assertEquals("Permanent", returnUser2.getType(), "User type should have been changed to Permanent");}
        );

    }

    @Test
    public void userOnForceIncrementOptimisticLock_versionUpdated() {

        User sameUser = service.getUser(1L);
        assertAll("User Get Failed",
                () ->  { assertEquals("Amit", sameUser.getName());},
                () ->  { assertEquals(1, sameUser.getVersion());}
        );

        Optional<User> optionalUser = service.getUser("Amit");
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            assertAll("User Get Failed with Force Increment and version not updated",
                    () ->  { assertEquals("Amit", user.getName());},
                    () ->  { assertEquals(2, user.getVersion(), "User version should have been changed to 2 for force increment");}
            );
        }
    }

    @Test
    public void userOnForceIncrementOptimisticLock_sameUserUpdatedWithOldVersion_Failed() {

        User user1 = service.getUser(1L);
        assertAll("User Get Failed",
                () ->  { assertEquals("Amit", user1.getName());},
                () ->  { assertEquals(1, user1.getVersion());}
        );

        Optional<User> sameUser = service.getUser("Amit");
        if (sameUser.isPresent()) {
            User user = sameUser.get();
            assertAll("User Get Failed with Force Increment and version not updated",
                    () ->  { assertEquals("Amit", user.getName());},
                    () ->  { assertEquals(2, user.getVersion());}
            );
        }

        user1.setType("Contract");
        Throwable e = assertThrows(ObjectOptimisticLockingFailureException.class, () -> {
            service.saveUser(user1);
        }, "Optimistic Locking Exception Not Thrown, as user's version updated to 2, but we are saving version 1");
    }


}
