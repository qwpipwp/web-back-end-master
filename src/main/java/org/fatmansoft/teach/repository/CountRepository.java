package org.fatmansoft.teach.repository;

import org.fatmansoft.teach.models.Count;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CountRepository extends JpaRepository<Count,Integer> {
    @Query(value = "select max(id) from Honor  ")
    Integer getMaxId();//获取最大id

    @Query(value = "from Count where ?1='' or student.studentName like %?1% or student.studentNum like %?1% ")
    List<Count> findCountListByNumName(String numName);//在荣誉数据库中以学号，姓名查找数据

    @Query(value = "select * from count  where ?1='' or studentId like %?1% or studentName like %?1% ", nativeQuery = true)
    List<Count> findCountListByNumNameNative(String numName);

}
