package org.fatmansoft.teach.repository;

import org.fatmansoft.teach.models.CourseInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CourseInfoRepository extends JpaRepository<CourseInfo,Integer> {


    @Query(value = "select max(id) from Course  ")
    Integer getMaxId();

    @Query(value = "from CourseInfo where ?1='' or course.courseNum like %?1% or course.courseName like %?1% ")
    List<CourseInfo> findCourseInfoListByNumName(String numName);
}
