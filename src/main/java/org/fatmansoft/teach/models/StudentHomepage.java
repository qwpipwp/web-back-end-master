package org.fatmansoft.teach.models;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.Date;
import java.util.Set;

@Entity
@Table(	name = "studentHomepage",
        uniqueConstraints = {
        })
public class StudentHomepage {
    @Id
    private Integer id;
    @ManyToOne(targetEntity = Student.class)
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
