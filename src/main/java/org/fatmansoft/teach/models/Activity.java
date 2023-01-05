package org.fatmansoft.teach.models;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.Date;

@Entity
@Table(	name = "activity",
        uniqueConstraints = {
        })
public class Activity {
    @Id
    private Integer id;
    @NotBlank
    @Size(max = 20)
    private String activityNum;
    @ManyToOne(targetEntity = Student.class)
    @JoinColumn(name ="studentId")
    private Student student;//与学生类建立多对一关系

    @Size(max = 50)
    private String activityName;
    private Date dates ;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Student getStudentId_activity() {
        return student;
    }

    public void setStudentId_activity(Student student) {
        this.student = student;
    }

    public String getActivityNum() {
        return activityNum;
    }

    public void setActivityNum(String activityNum) {
        this.activityNum = activityNum;
    }

    public String getActivityName() {
        return activityName;
    }

    public void setActivityName(String activityName) {
        this.activityName =activityName;
    }
    public Date getDates() {
        return dates;
    }

    public void setDates(Date dates) {
        this.dates = dates;
    }


}
