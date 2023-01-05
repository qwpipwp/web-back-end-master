package org.fatmansoft.teach.repository;

import org.fatmansoft.teach.models.Person;
import org.fatmansoft.teach.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.relational.core.sql.In;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


@Repository
public interface PersonRepository extends JpaRepository<Person, Integer> {

    @Query(value = "select max(personId) from Person ")
    Integer getMaxId();

    @Query(value = "select COALESCE(max(person_id), 1) + 1 as next_id from person", nativeQuery = true )
    Integer getNextId();

    @Query(value = "select person_id  from person where email=?1",nativeQuery = true)
    Integer getIdByEmail(String email);

    Boolean existsByEmail(String Email);

}
