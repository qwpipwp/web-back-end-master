package org.fatmansoft.teach.models;

import javax.persistence.*;

@Entity
@Table(	name = "information",
        uniqueConstraints = {
        })
public class Information {
    @Id
    private Integer id;

    @ManyToOne(targetEntity = Student.class)
    @JoinColumn(name ="studentId")
    private Student student;//与学生类建立多对一关系
    private Integer telephoneNumber;

    private String preEnrolmentInformation;

    private String social;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Student getStudentId_information() {
        return student;
    }

    public void setStudentId_information(Student student) {
        this.student = student;
    }

    public Integer getTelephoneNumber() {
        return telephoneNumber;
    }

    public void setTelephoneNumber(Integer telephoneNumber) {
        this.telephoneNumber = telephoneNumber;
    }

    public String getPreEnrolmentInformation() {
        return preEnrolmentInformation;
    }

    public void setPreEnrolmentInformation(String preEnrolmentInformation) {
        this.preEnrolmentInformation = preEnrolmentInformation;
    }

    public String getSocial() {
        return social;
    }

    public void setSocial(String social) {
        this.social = social;
    }
}
