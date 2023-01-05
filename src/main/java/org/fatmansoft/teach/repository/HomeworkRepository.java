package org.fatmansoft.teach.repository;

import org.fatmansoft.teach.models.Homework;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface HomeworkRepository extends JpaRepository<Homework,Integer> {
    @Query(value = "select max(id) from Homework  ")
    Integer getMaxId();//获取最大id

    @Query(value = "from Homework where ?1='' or student.studentName like %?1% or course.courseName like %?1% or student.studentNum like %?1% or course.courseNum like %?1%")
    List<Homework> findHomeworkListByNumName(String numName);//在作业数据库中以学号，姓名查找数据

    @Query(value = "select * from homework  where ?1='' or studentId like %?1% or courseId like %?1% ", nativeQuery = true)
    List<Homework> findHomeworkListByNumNameNative(String numName);

}
