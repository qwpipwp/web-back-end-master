package org.fatmansoft.teach.models;

import javax.persistence.*;

@Entity
@Table(	name = "homework",
        uniqueConstraints = {
        })
public class Homework {
    @Id
    private Integer id;

    @ManyToOne(targetEntity = Student.class)
    @JoinColumn(name ="studentId")
    private Student student;//与学生类建立多对一关系

    @ManyToOne(targetEntity = Course.class)
    @JoinColumn(name ="courseId")
    private Course course;//与学生类建立多对一关系
    private String homework;
    private String homeworkIsDone;
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Student getStudent() {
        return student;
    }

    public void setStudent(Student student) {
        this.student = student;
    }

    public Course getCourse() {
        return course;
    }

    public void setCourse(Course course) {
        this.course = course;
    }

    public String getHomework() {
        return homework;
    }

    public void setHomework(String  homework) {
        this.homework = homework;
    }

    public String getHomeworkIsDone() {
        return homeworkIsDone;
    }

    public void setHomeworkIsDone(String  homeworkIsDone) {
        this.homeworkIsDone = homeworkIsDone;
    }
}
