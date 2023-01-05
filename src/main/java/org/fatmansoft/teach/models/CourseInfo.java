package org.fatmansoft.teach.models;

import javax.persistence.*;

@Entity
@Table(	name = "CourseInfo",
        uniqueConstraints = {
        })
public class CourseInfo {
    @Id
    private Integer id;

    @OneToOne(targetEntity = Course.class)
    @JoinColumn(name ="courseId")
    private Course course;

    private String courseInfo;
    private String textBook;
    private String resource;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Course getCourse_info() {
        return course;
    }
    public void setCourse_info(Course course) {
        this.course = course;
    }

    public String  getTextBook() {
        return textBook;
    }
    public void setTextBook(String textBook) {
        this.textBook = textBook;
    }

    public String getResource() {
        return resource;
    }
    public void setResource(String  resource) {
        this.resource = resource;
    }
    public String getCourseInfo() {
        return courseInfo;
    }
    public void setCourseInfo(String courseInfo) {
        this.courseInfo = courseInfo;
    }
}
