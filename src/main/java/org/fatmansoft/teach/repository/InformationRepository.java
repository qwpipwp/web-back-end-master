package org.fatmansoft.teach.repository;

import org.fatmansoft.teach.models.Information;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface InformationRepository extends JpaRepository<Information,Integer> {
    @Query(value = "select max(id) from Information  ")
    Integer getMaxId();//获取最大id

    @Query(value = "from Information where ?1='' or student.studentName like %?1% or student.studentNum like %?1% ")
    List<Information> findInformationListByNumName(String numName);//在信息数据库中以学号，姓名查找数据

    @Query(value = "select * from information  where ?1='' or student_studentName like %?1% ", nativeQuery = true)
    List<Information> findInformationListByNumNameNative(String numName);

    @Query(value = "from Information where ?1='' or student.id = ?1 ")
    List<Information> findInformationListByStudentId(Integer studentId);//在成绩数据库中用学生id查找成绩

}
