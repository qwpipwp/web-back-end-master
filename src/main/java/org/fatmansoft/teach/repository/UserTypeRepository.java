package org.fatmansoft.teach.repository;

import org.fatmansoft.teach.models.EUserType;
import org.fatmansoft.teach.models.UserType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface UserTypeRepository extends JpaRepository<UserType, Long> {
    @Query(value="from UserType where name=?1")
    UserType findByName(EUserType name);
}