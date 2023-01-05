package org.fatmansoft.teach.controllers;

import org.fatmansoft.teach.models.Course;
import org.fatmansoft.teach.models.CourseInfo;
import org.fatmansoft.teach.payload.request.DataRequest;
import org.fatmansoft.teach.payload.response.DataResponse;
import org.fatmansoft.teach.repository.CourseInfoRepository;
import org.fatmansoft.teach.repository.CourseRepository;
import org.fatmansoft.teach.util.CommonMethod;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.*;

// origins： 允许可访问的域列表
// maxAge:准备响应前的缓存持续的最大时间（以秒为单位）。
@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/teach")

public class CourseInfoController {
    //Java 对象的注入 我们定义的这下Java的操作对象都不能自己管理是由有Spring框架来管理的， TeachController 中要使用StudentRepository接口的实现类对象，
    // 需要下列方式注入，否则无法使用， studentRepository 相当于StudentRepository接口实现对象的一个引用，由框架完成对这个引用的复制，
    // TeachController中的方法可以直接使用
    @Autowired
    private CourseInfoRepository courseInfoRepository;
    @Autowired
    private CourseRepository courseRepository;

    //CourseInfoMapList 查询所有课程号和课程名与numName相匹配的课程信息，并转换成Map的数据格式存放到List
    //
    // Map 对象是存储数据的集合类，框架会自动将Map转换程用于前后台传输数据的Json对象，Map的嵌套结构和Json的嵌套结构类似，
    //下面方法是生成前端Table数据的示例，List的每一个Map对用显示表中一行的数据
    //Map 每个键值对，对应每一个列的值，
    //按照我们测试框架的要求，每个表的主键都是id, 生成表数据是一定要用m.put("id", s.getId());将id传送前端，前端不显示，
    //但在进入编辑页面是作为参数回传到后台.
    public List getCourseInfoMapList(String numName) {
        List dataList = new ArrayList();
        List<CourseInfo> sList = courseInfoRepository.findCourseInfoListByNumName(numName);  //数据库查询操作
        if (sList == null || sList.size() == 0)
            return dataList;
        CourseInfo s;
        Map m;
        for (int i = 0; i < sList.size(); i++) {
            s = sList.get(i);
            m = new HashMap();
            m.put("id", s.getId());
            m.put("courseNum", s.getCourse_info().getCourseNum());
            m.put("courseName", s.getCourse_info().getCourseName());
            m.put("courseInfo", s.getCourseInfo());
            m.put("textBook", s.getTextBook());
            m.put("resource", s.getResource());
            dataList.add(m);
        }
        return dataList;
    }

    //CourseInfo页面初始化方法
    //Table界面初始是请求列表的数据，这里缺省查出所有学生的信息，传递字符“”给方法getCourseInfoMapList，返回所有学生数据，
    @PostMapping("/courseInfoInit")//成绩页面的初始化方法
    @PreAuthorize("hasRole('ADMIN')")
    public DataResponse courseInfoInit(@Valid @RequestBody DataRequest dataRequest)
    {
        String courseName = dataRequest.getString("courseName");//以studentName为key值检索score数据库中所有相关数据
        if(courseName == null)
        {
            courseName = "";//为空时传递空串，以显示数据库中所有数据
        }
        List<HashMap<String,Object>> mapList = getCourseInfoMapList(courseName);
        return CommonMethod.getReturnData(mapList);//返回数据
    }

    @PostMapping("/courseInfoQuery")//查询功能实现
    @PreAuthorize("hasRole('ADMIN')")
    public DataResponse courseInfoQuery(@Valid@RequestBody DataRequest dataRequest)
    {
        String numName = dataRequest.getString("numName");//获取从前端返回的查询值
        List<HashMap<String,Object>> mapList = getCourseInfoMapList(numName);//使用接口功能查询数据库，将数据存到list中
        return CommonMethod.getReturnData(mapList);//返回查询到的数据
    }

    @PostMapping("/courseInfoEditInit")
    @PreAuthorize("hasRole('ADMIN')")
    public DataResponse courseInfoEditInit(@Valid @RequestBody DataRequest dataRequest) {
        Integer id = dataRequest.getInteger("id");
        CourseInfo s= null;
        Optional<CourseInfo> op;
        if(id != null) {
            op= courseInfoRepository.findById(id);
            if(op.isPresent()) {
                s = op.get();
            }
        }
        Map form = new HashMap();
        if(s != null) {
            form.put("id",s.getId());
            form.put("courseNum",s.getCourse_info().getCourseNum());
            form.put("courseName",s.getCourse_info().getCourseName());
            form.put("courseInfo",s.getCourseInfo());
            form.put("textBook",s.getTextBook());
            form.put("resource",s.getResource());
        }
        return CommonMethod.getReturnData(form); //这里回传包含学生信息的Map对象
    }
    //  CourseInfo信息提交按钮方法
    //相应提交请求的方法，前端把所有数据打包成一个Json对象作为参数传回后端，后端直接可以获得对应的Map对象form, 再从form里取出所有属性，复制到
    //实体对象里，保存到数据库里即可，如果是添加一条记录， id 为空，这是先 new CourseInfo 计算新的id， 复制相关属性，保存，如果是编辑原来的信息，
    //id 不为空。则查询出实体对象，复制相关属性，保存后修改数据库信息，永久修改
    public synchronized Integer getNewCourseInfoId(){
        Integer
                id = courseInfoRepository.getMaxId();  // 查询最大的id
        if(id == null)
            id = 1;
        else
            id = id+1;
        return id;
    };
    @PostMapping("/courseInfoEditSubmit")
    @PreAuthorize(" hasRole('ADMIN')")
    public DataResponse courseInfoEditSubmit(@Valid @RequestBody DataRequest dataRequest) {
        Map form = dataRequest.getMap("form"); //参数获取Map对象
        Integer id = CommonMethod.getInteger(form,"id");
        String courseId = CommonMethod.getString(form,"courseNum");  //Map 获取属性的值
        String courseInfo = CommonMethod.getString(form,"courseInfo");
        String textBook = CommonMethod.getString(form,"textBook");
        String resource = CommonMethod.getString(form,"resource");
        CourseInfo s= null;
        Optional<CourseInfo> op;
        if(id != null) {
            op= courseInfoRepository.findById(id);//查询对应数据库中主键为id的值的实体对象
            if(op.isPresent()) {
                s = op.get();
            }
        }
        if(s == null) {
            s = new CourseInfo();   //不存在 创建实体对象
            id = getNewCourseInfoId(); //获取鑫的主键，这个是线程同步问题;
            s.setId(id);  //设置新的id
        }
        Course c = null;
        if(courseId != null) {
            c = courseRepository.findByCourseNum(courseId).get();//通过接口获取课程数据库中id值对应的相关数据，下同获取课程
            s.setCourse_info(c);  //设置属性
            courseRepository.save(c);
        }
        s.setCourseInfo(courseInfo);
        s.setTextBook(textBook);
        s.setResource(resource);
        courseInfoRepository.save(s); //新建和修改都调用save方法
        return CommonMethod.getReturnData(s.getId());  // 将记录的id返回前端
    }

    //  CourseInfo信息删除方法
    //CourseInfo页面的列表里点击删除按钮则可以删除已经存在的学生信息， 前端会将该记录的id 回传到后端，方法从参数获取id，查出相关记录，调用delete方法删除
    @PostMapping("/courseInfoDelete")
    @PreAuthorize(" hasRole('ADMIN')")
    public DataResponse courseInfoDelete(@Valid @RequestBody DataRequest dataRequest) {
        Integer id = dataRequest.getInteger("id");  //获取id值
        CourseInfo s = null;
        Optional<CourseInfo> op;
        if (id != null) {
            op = courseInfoRepository.findById(id);
            if (op.isPresent()) {
                s = op.get();
            }
        }
        if (s != null) {
            courseInfoRepository.delete(s);    //数据库永久删除
        }
        return CommonMethod.getReturnMessageOK();  //通知前端操作正常
    }

}
