package org.fatmansoft.teach.controllers;


import org.fatmansoft.teach.models.Course;
import org.fatmansoft.teach.models.Score;
import org.fatmansoft.teach.models.Student;
import org.fatmansoft.teach.payload.request.DataRequest;
import org.fatmansoft.teach.payload.response.DataResponse;
import org.fatmansoft.teach.repository.CourseRepository;
import org.fatmansoft.teach.repository.ScoreRepository;
import org.fatmansoft.teach.repository.StudentRepository;
import org.fatmansoft.teach.util.CommonMethod;
import org.fatmansoft.teach.util.Method;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.*;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/teach")
public class ScoreController {
    @Autowired
    private ScoreRepository scoreRepository;
    @Autowired
    private StudentRepository studentRepository;
    @Autowired
    private CourseRepository courseRepository;//添加依赖

    public ArrayList<HashMap<String,Object>> getScoreMapList(String numName)//收集数据以供前端页面展示
    {
        List<Score> scores = scoreRepository.findScoreListByNumName(numName);
        ArrayList<HashMap<String,Object>> mapList = new ArrayList<>();
        for(final Score s : scores)
        {
            final HashMap<String,Object> m = new HashMap<>();
            m.put("id",s.getId());//从数据库中取值并添加到哈希表中，下同
            m.put("studentNum",s.getStudent().getStudentNum());
            m.put("studentName",s.getStudent().getStudentName());
            m.put("courseNum",s.getCourse().getCourseNum());
            m.put("courseName",s.getCourse().getCourseName());
            m.put("score",s.getScore());
            mapList.add(m);//添加到列表中

        }
        return mapList;//返回列表
    }

    @PostMapping("/scoreInit")//成绩页面的初始化方法
    @PreAuthorize("hasRole('ADMIN')")
    public DataResponse scoreInit(@Valid @RequestBody DataRequest dataRequest)
    {
        String studentName = dataRequest.getString("studentName");//以studentName为key值检索score数据库中所有相关数据
        if(studentName == null)
        {
            studentName = "";//为空时传递空串，以显示数据库中所有数据
        }
        List<HashMap<String,Object>> mapList = getScoreMapList(studentName);
        return CommonMethod.getReturnData(mapList);//返回数据
    }

    @PostMapping("/scoreQuery")//查询功能实现
    @PreAuthorize("hasRole('ADMIN')")
    public DataResponse scoreQuery(@Valid@RequestBody DataRequest dataRequest)
    {
        String numName = dataRequest.getString("numName");//获取从前端返回的查询值
        List<HashMap<String,Object>> mapList = getScoreMapList(numName);//使用接口功能查询数据库，将数据存到list中
        return CommonMethod.getReturnData(mapList);//返回查询到的数据
    }

    @PostMapping("/scoreEditInit")//编辑页面初始化功能
    @PreAuthorize("hasRole('ADMIN')")
    public DataResponse scoreEditInit(@Valid@RequestBody DataRequest dataRequest)
    {
        Integer id = dataRequest.getInteger("id");//从数据库中取出id值
        Score sc= null;
        Student s;
        Course c;
        Optional<Score> op;
        if(id != null) {
            op = scoreRepository.findById(id);//通过接口寻找id值作为key值对应的数据，以便接下来的编辑操作
            if (op.isPresent()) {//确认id对应数据是否存在，提高程序的鲁棒性
                sc = op.get();
            }
        }
        Map form = new HashMap();
        if(sc != null) {//当成绩类不为空时，进行数据的传递
                form.put("id",sc.getId());//获取id
                form.put("studentNum",sc.getStudent().getStudentNum());//获取学号
                form.put("studentName",sc.getStudent().getStudentName());//获取学生姓名
                form.put("courseNum",sc.getCourse().getCourseNum());//获取课程号
                form.put("courseName",sc.getCourse().getCourseName());//获取课程名称
                form.put("score",sc.getScore());//获取成绩
            }
        return CommonMethod.getReturnData(form);//返回编辑数据
    }

    public synchronized Integer getNewScoreId() {
        Integer id = scoreRepository.getMaxId();
        return (id == null ? 1 : id + 1);
    }//在添加新内容时进行score数据库中id的增加

    @PostMapping("/scoreEditSubmit")//成绩信息提交实现方法
    @PreAuthorize("hasRole('ADMIN')")
    public DataResponse scoreEditSubmit(@Valid@RequestBody DataRequest dataRequest)
    {
        Map form = dataRequest.getMap("form"); //参数获取Map对象
        Integer id = CommonMethod.getInteger(form,"id");//获取key值id
        String studentId = CommonMethod.getString(form,"studentNum");//获取学生的id下同课程的id
        String courseId = CommonMethod.getString(form,"courseNum");
        Double score = CommonMethod.getDouble(form,"score");//获取成绩
        Score sc= null;
        Student s = null;
        Course c = null;
        Optional<Score> op;
        if(id != null) {
            op= scoreRepository.findById(id);  //查询对应数据库中主键为id的值的实体对象
            if(op.isPresent()) {
                sc = op.get();
            }
        }
        if(sc == null) {
            sc = new Score();   //不存在 创建实体对象
            id = getNewScoreId(); //获取鑫的主键，这个是线程同步问题;
            sc.setId(id);  //设置新的id
        }
        if(studentId != null) {
            s = studentRepository.findByStudentNum(studentId).get();//通过接口获取学生数据库中id值对应的相关数据，下同获取课程
            c = courseRepository.findByCourseNum(courseId).get();
            sc.setStudent(s);  //设置属性
            sc.setCourse(c);
            s.addCourse(c);//多对多的实现，将课程和学生建立联系，下同
            c.addStudent(s);
            studentRepository.save(s);
            courseRepository.save(c);
        }
        String S;
        S=score+"";
        if(Method.IsDouble(S)) {
            sc.setScore(score);
        }else {
            return CommonMethod.getReturnMessageError("请输入合法数字");
        }
        scoreRepository.save(sc);//新建和修改都调用save方法
        return CommonMethod.getReturnData(sc.getId());
    }

    @PostMapping("/scoreDelete")//数据的删除方法
    @PreAuthorize("hasRole('ADMIN')")
    public DataResponse scoreDelete(@Valid@RequestBody DataRequest dataRequest)
    {
        final Integer id = dataRequest.getInteger("id");//获取需要删除的数据的id值
        if(id != null)//检索不为空的id值，提高程序稳定性
        {
            Optional<Score> op = scoreRepository.findById(id);//通过接口查找id对应的数据
            if(op.isPresent())//检查数据是否为空，提高稳定性
            {
                final Score s = op.get();//设定常量获取数据
                scoreRepository.delete(s);//调用接口删除方法删除数据
            }
        }
        return CommonMethod.getReturnMessageOK();//返回删除后的结果
    }

}
