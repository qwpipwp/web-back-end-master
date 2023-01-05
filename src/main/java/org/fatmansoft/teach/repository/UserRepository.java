package org.fatmansoft.teach.repository;


import java.util.Optional;

import org.fatmansoft.teach.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.relational.core.sql.In;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
    Optional<User> findByUserName(String userName);
    Optional<User> findByPersonPerNum(String perNum);

    @Query(value = "select COALESCE(max(user_id), 1) + 1 as next_id from user u", nativeQuery = true )
    Integer getNextId();
    Optional<User> findByUserId(Integer userId);

    @Query(value= "select user_type_id from user u where user_name=?1",nativeQuery = true)
    Integer getRole(String username);

    @Query(value = "select userId from User where userName=?1")
    Integer getIdByUserName(String username);

    @Query(value = "select user_name from user where person_id=?1",nativeQuery = true)
    String getUsernameByPersonId(Integer personId);
    Boolean existsByUserName(String userName);
}