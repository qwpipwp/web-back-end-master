package org.fatmansoft.teach.models;

import javax.persistence.*;

@Entity
@Table(	name = "familyMember",
        uniqueConstraints = {
        })
public class FamilyMember {
    @Id
    private Integer id;
    @ManyToOne(targetEntity = Student.class)
    @JoinColumn(name = "studentId")
    private Student student;//与学生类建立多对一关系

    private String name;
    private String sex;
    private String rel;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Student getStudentId_fa() {
        return student;
    }

    public void setStudentId_fa(Student student) {
        this.student = student;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getRel() {
        return rel;
    }

    public void setRel(String rel) {
        this.rel = rel;
    }
}
