package org.fatmansoft.teach.models;

import javax.persistence.*;

@Entity
@Table(	name = "count",
        uniqueConstraints = {
        })
public class Count {
    @Id
    private Integer id;

    @OneToOne(targetEntity = Student.class)
    @JoinColumn(name="studentId")
    private Student student;//与学生进行多对一联系


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


}
