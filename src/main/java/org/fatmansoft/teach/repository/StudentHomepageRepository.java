package org.fatmansoft.teach.repository;

import org.fatmansoft.teach.models.StudentHomepage;
import org.fatmansoft.teach.models.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StudentHomepageRepository extends JpaRepository<StudentHomepage,Integer> {

    @Query(value = "select max(id) from StudentHomepage  ")
    Integer getMaxId();//获取最大id

    @Query(value = "from StudentHomepage where ?1='' or student.studentName like %?1% or student.studentNum like %?1% ")
    List<StudentHomepage> findStudentHomepageListByNumName(String numName);//在活动数据库中以学号，姓名查找数据

    @Query(value = "select * from StudentHomepage  where ?1='' or studentId like %?1% or studentName like %?1% ", nativeQuery = true)
    List<StudentHomepage> findStudentHomepageListByNumNameNative(String numName);


}
