package org.fatmansoft.teach.repository;

import org.fatmansoft.teach.models.Attendence;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface AttendenceRepository extends JpaRepository<Attendence,Integer> {

    @Query(value = "select max(id) from Attendence  ")
    Integer getMaxId();//获取最大id

    @Query(value = "from Attendence where ?1='' or student.studentName like %?1% or course.courseName like %?1% or student.studentNum like %?1% or course.courseNum like %?1%")
    List<Attendence> findAttendenceListByNumName(String numName);//在考勤数据库中以学号，姓名查找数据

    @Query(value = "select * from attendence where ?1='' or studentId like %?1% or courseId like %?1% ", nativeQuery = true)
    List<Attendence> findAttendenceListByNumNameNative(String numName);

}
