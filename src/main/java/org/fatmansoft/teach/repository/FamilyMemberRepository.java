package org.fatmansoft.teach.repository;

import org.fatmansoft.teach.models.FamilyMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface FamilyMemberRepository extends JpaRepository<FamilyMember,Integer> {
    @Query(value = "select max(id) from FamilyMember  ")
    Integer getMaxId();//获取最大id

    @Query(value = "from FamilyMember where ?1='' or student.studentName like %?1% or student.studentNum like %?1% ")
    List<FamilyMember> findFamilyMemberListByNumName(String numName);//在家庭关系数据库中以学号，姓名查找数据

    @Query(value = "select * from familyMember  where ?1='' or studentId like %?1% or studentName like %?1% ", nativeQuery = true)
    List<FamilyMember> findFamilyMemberListByNumNameNative(String numName);

}
