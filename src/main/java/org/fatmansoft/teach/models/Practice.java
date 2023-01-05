package org.fatmansoft.teach.models;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.Date;

@Entity
@Table(	name = "practice",
        uniqueConstraints = { })
public class Practice {
    @Id
    private Integer id;

    @ManyToOne(targetEntity = Student.class)
    @JoinColumn(name = "studentId")
    private Student student;//与学生类建立多对一关系

    @NotBlank
    @Size(max = 20)
    private String practiceNum;

    @Size(max = 50)
    private String practiceName;
    private Date practiceDate;
    private String practiceKind;

    public Student getStudentId_practice() {
        return student;
    }

    public void setStudentId_practice(Student student) {
        this.student = student;
    }


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getPracticeNum() {
        return practiceNum;
    }

    public void setPracticeNum(String practiceNum) {
        this.practiceNum = practiceNum;
    }

    public String getPracticeName() {
        return practiceName;
    }

    public void setPracticeName(String practiceName) {
        this.practiceName = practiceName;
    }

    public Date getPracticeDate() {
        return practiceDate;
    }

    public void setPracticeDate(Date practiceDate) {
        this.practiceDate = practiceDate;
    }

    public String getPracticeKind() {
        return practiceKind;
    }

    public void setPracticeKind(String practiceKind) {
        this.practiceKind = practiceKind;
    }


}
