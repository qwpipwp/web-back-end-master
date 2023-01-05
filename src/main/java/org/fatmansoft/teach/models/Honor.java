package org.fatmansoft.teach.models;

import javax.persistence.*;

@Entity
@Table(	name = "honor",
        uniqueConstraints = {
        })
public class Honor {
    @Id
    private Integer id;

    @ManyToOne(targetEntity = Student.class)
    @JoinColumn(name ="studentId")
    private Student student;//与学生类建立多对一关系
    private String honor;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Student getStudentId_honor() {
        return student;
    }

    public void setStudentId_honor(Student student) {
        this.student = student;
    }

    public String getHonor() {
        return honor;
    }

    public void setHonor(String honor) {
        this.honor = honor;
    }
}
