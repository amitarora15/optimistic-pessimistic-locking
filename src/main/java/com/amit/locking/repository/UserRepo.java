package com.amit.locking.repository;

import com.amit.locking.entity.User;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.persistence.LockModeType;
import java.util.Optional;

@Repository
public interface UserRepo extends CrudRepository<User, Long> {

    @Lock(LockModeType.OPTIMISTIC_FORCE_INCREMENT)
    @Query("SELECT u FROM User u WHERE u.name = :name")
    Optional<User> findByName(@Param("name") String name);

    @Lock(LockModeType.OPTIMISTIC)
    @Query("SELECT u FROM User u WHERE u.type = :type")
    Optional<User> findByType(@Param("type") String type);

}
