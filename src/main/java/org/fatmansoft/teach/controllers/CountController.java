package org.fatmansoft.teach.controllers;

import org.fatmansoft.teach.models.*;
import org.fatmansoft.teach.payload.request.DataRequest;
import org.fatmansoft.teach.payload.response.DataResponse;
import org.fatmansoft.teach.repository.*;
import org.fatmansoft.teach.service.IntroduceService;
import org.fatmansoft.teach.util.CommonMethod;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


// origins： 允许可访问的域列表
// maxAge:准备响应前的缓存持续的最大时间（以秒为单位）。
@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/teach")


public class CountController {
    //Java 对象的注入 我们定义的这下Java的操作对象都不能自己管理是由有Spring框架来管理的， TeachController 中要使用StudentRepository接口的实现类对象，
    // 需要下列方式注入，否则无法使用， studentRepository 相当于StudentRepository接口实现对象的一个引用，由框架完成对这个引用的复制，
    // TeachController中的方法可以直接使用
    @Autowired
    private IntroduceService introduceService;
    @Autowired
    private HonorRepository honorRepository;
    @Autowired
    private StudentRepository studentRepository;//添加依赖
    @Autowired
    private ActivityRepository activityRepository;//添加依赖
    @Autowired
    private PracticeRepository practiceRepository;//添加依赖
    @Autowired
    private ScoreRepository scoreRepository;//添加依赖
    @Autowired
    private CountRepository countRepository;//添加依赖




    //getCountMapList 查询所有学号或姓名与numName相匹配的学生信息，并转换成Map的数据格式存放到List
    //
    // Map 对象是存储数据的集合类，框架会自动将Map转换程用于前后台传输数据的Json对象，Map的嵌套结构和Json的嵌套结构类似，
    //下面方法是生成前端Table数据的示例，List的每一个Map对用显示表中一行的数据
    //Map 每个键值对，对应每一个列的值，
    //按照我们测试框架的要求，每个表的主键都是id, 生成表数据是一定要用m.put("id", s.getId());将id传送前端，前端不显示，
    //但在进入编辑页面是作为参数回传到后台.
    public List getCountMapList(String numName) {
        List dataList = new ArrayList();
        List<Count> cList = countRepository.findCountListByNumName(numName);  //数据库查询操作
        if(cList == null || cList.size() == 0)
            return dataList;
        Count c;
        Map m;
        for(int i = 0; i < cList.size();i++) {
            c =cList.get(i);
            m = new HashMap();
            m.put("id", c.getId());//从数据库中取值并添加到哈希表中，下同
            m.put("studentNum",c.getStudent().getStudentNum());
            m.put("studentName",c.getStudent().getStudentName());
            List<Score> sList = scoreRepository.findScoreListByNumName(numName);  //数据库查询操作
            m.put("courseCount",sList.size());
            List<Honor> hList = honorRepository.findHonorListByNumName(numName);  //数据库查询操作
            m.put("honorCount",hList.size());
            List<Activity> aList = activityRepository.findActivityListByNumName(numName);  //数据库查询操作
            m.put("activityCount",aList.size());
            List<Practice> pList = practiceRepository.findPracticeListByNumName(numName);  //数据库查询操作
            m.put("PracticeCount",pList.size());
            dataList.add(m);
        }
        return dataList;
    }
    //count页面初始化方法
    //Table界面初始是请求列表的数据，这里缺省查出所有学生的信息，传递字符“”给方法getCountMapList，返回所有学生数据，
    @PostMapping("/countInit")//成绩页面的初始化方法
    @PreAuthorize("hasRole('ADMIN')")
    public DataResponse countInit(@Valid @RequestBody DataRequest dataRequest) {
        String studentName = dataRequest.getString("studentName");//以studentName为key值检索COUNT数据库中所有相关数据
        if(studentName == null)
        {
            studentName = "";//为空时传递空串，以显示数据库中所有数据
        }
        List<HashMap<String,Object>> mapList = getCountMapList(studentName);
        return CommonMethod.getReturnData(mapList);//返回数据
    }
    //count页面点击查询按钮请求
    //Table界面初始是请求列表的数据，从请求对象里获得前端界面输入的字符串，作为参数传递给方法getCountMapList，返回所有学生数据，
    @PostMapping("/countQuery")
    @PreAuthorize("hasRole('ADMIN')")
    public DataResponse countQuery(@Valid @RequestBody DataRequest dataRequest) {
        String numName= dataRequest.getString("numName");//输入学生姓名或学生学号以查询内容
        List<HashMap<String,Object>> mapList = getCountMapList(numName);//使用接口功能查询数据库，将数据存
        return CommonMethod.getReturnData(mapList);  //按照测试框架规范会送Map的list
    }

}
