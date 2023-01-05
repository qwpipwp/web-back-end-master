package org.fatmansoft.teach.controllers;

import org.fatmansoft.teach.models.Practice;
import org.fatmansoft.teach.models.Student;
import org.fatmansoft.teach.payload.request.DataRequest;
import org.fatmansoft.teach.payload.response.DataResponse;
import org.fatmansoft.teach.repository.PracticeRepository;
import org.fatmansoft.teach.repository.StudentRepository;
import org.fatmansoft.teach.service.IntroduceService;
import org.fatmansoft.teach.util.CommonMethod;
import org.fatmansoft.teach.util.DateTimeTool;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.*;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/teach")

public class PracticeController {
    @Autowired
    private IntroduceService introduceService;
    @Autowired
    private PracticeRepository practiceRepository;
    @Autowired
    private StudentRepository studentRepository;//添加依赖


    public List getPracticeMapList(String numName){
        List dataList = new ArrayList();
        List<Practice> pList = practiceRepository.findPracticeListByNumName(numName);//通过查询方法建立页面
        if(pList == null || pList.size() == 0)
            return dataList;

        Practice prac;
        Map m;
        for(int i = 0; i < pList.size();i++) {
            prac = pList.get(i);
            m = new HashMap();
            m.put("id", prac.getId());
            m.put("studentNum",prac.getStudentId_practice().getStudentNum());//获取学号
            m.put("studentName",prac.getStudentId_practice().getStudentName());//获取学生姓名
            m.put("practiceNum",prac.getPracticeNum());//获取实践活动的编号
            m.put("practiceName",prac.getPracticeName());//获取实践活动的名字
            if("1".equals(prac.getPracticeKind())) {
                m.put("practiceKind","社会实践");
            }else if("2".equals(prac.getPracticeKind())) {
                m.put("practiceKind","学科竞赛");
            }else if("3".equals(prac.getPracticeKind())) {
                m.put("practiceKind","科研成果");
            }else if("4".equals(prac.getPracticeKind())) {
                m.put("practiceKind","培训讲座");
            }else if("5".equals(prac.getPracticeKind())) {
                m.put("practiceKind","创新创业");
            }else if("6".equals(prac.getPracticeKind())) {
                m.put("practiceKind","校外实习");
            }else if("7".equals(prac.getPracticeKind())) {
                m.put("practiceKind","其他");
            }//进行选择，确定实践活动的种类
            m.put("practiceDate", DateTimeTool.parseDateTime(prac.getPracticeDate(),"yyyy-MM-dd"));//获取实践活动的日期
            dataList.add(m);
        }
        return dataList;
    }

    @PostMapping("/practiceInit")//成绩页面的初始化方法
    @PreAuthorize("hasRole('ADMIN')")
    public DataResponse practiceInit(@Valid @RequestBody DataRequest dataRequest)
    {
        String studentName = dataRequest.getString("studentName");//以studentName为key值检索practice数据库中所有相关数据
        if(studentName == null)
        {
            studentName = "";//为空时传递空串，以显示数据库中所有数据
        }
        List<HashMap<String,Object>> mapList = getPracticeMapList(studentName);
        return CommonMethod.getReturnData(mapList);//返回数据
    }

    @PostMapping("/practiceQuery")//查询功能实现
    @PreAuthorize("hasRole('ADMIN')")
    public DataResponse practiceQuery(@Valid@RequestBody DataRequest dataRequest)
    {
        String numName = dataRequest.getString("numName");//获取从前端返回的查询值
        List<HashMap<String,Object>> mapList = getPracticeMapList(numName);//使用接口功能查询数据库，将数据存到list中
        return CommonMethod.getReturnData(mapList);//返回查询到的数据
    }

    @PostMapping("/practiceEditInit")//编辑页面的初始化
    @PreAuthorize("hasRole('ADMIN')")
    public DataResponse practiceEditInit(@Valid @RequestBody DataRequest dataRequest) {
        Integer id = dataRequest.getInteger("id");//获取key值id
        Practice p= null;
        Optional<Practice> op;
        if(id != null) {//添加条件提高稳定性
            op= practiceRepository.findById(id);//找寻id相应的数据进行处理
            if(op.isPresent()) {
                p = op.get();
            }
        }
        List kindList = new ArrayList();
        Map m;
        m = new HashMap();
        m.put("label","社会实践");
        m.put("value","1");
        kindList.add(m);
        m = new HashMap();
        m.put("label","学科竞赛");
        m.put("value","2");
        kindList.add(m);
        m = new HashMap();
        m.put("label","科研成果");
        m.put("value","3");
        kindList.add(m);
        m = new HashMap();
        m.put("label","培训讲座");
        m.put("value","4");
        kindList.add(m);
        m = new HashMap();
        m.put("label","创新创业");
        m.put("value","5");
        kindList.add(m);
        m = new HashMap();
        m.put("label","校外实习");
        m.put("value","6");
        kindList.add(m);
        m = new HashMap();
        m.put("label","其他");
        m.put("value","7");
        kindList.add(m);//编辑页面的选项处理，每个储存的数据对应活动的一种种类
        Map form = new HashMap();
        if(p != null) {
            form.put("id",p.getId());//获取id
            form.put("studentNum",p.getStudentId_practice().getStudentNum());//获取学号
            form.put("studentName",p.getStudentId_practice().getStudentName());//获取学生姓名
            form.put("practiceDate", DateTimeTool.parseDateTime(p.getPracticeDate(),"yyyy-MM-dd"));//获取活动的日期
            form.put("practiceKind",p.getPracticeKind());//获取活动的种类
            form.put("practiceName",p.getPracticeName());//获取活动的名称
            form.put("practiceNum",p.getPracticeNum());//获取活动的序号
        }
        form.put("kindList",kindList);//获取种类
        return CommonMethod.getReturnData(form);
    }

    public synchronized Integer getNewPracticeId(){//获取新id
        Integer id = practiceRepository.getMaxId();
        if(id == null)
            id = 1;
        else
            id = id+1;
        return id;
    }

    @PostMapping("/practiceEditSubmit")//活动的存储
    @PreAuthorize(" hasRole('ADMIN')")
    public DataResponse practiceEditSubmit(@Valid @RequestBody DataRequest dataRequest) {
        Map form = dataRequest.getMap("form"); //参数获取Map对象
        Integer id = CommonMethod.getInteger(form,"id");
        String studentId = CommonMethod.getString(form,"studentNum");//获取学生的id下同课程的id
        String practiceNum = CommonMethod.getString(form,"practiceNum");//获取前端的活动序号
        String practiceName = CommonMethod.getString(form,"practiceName");//获取前端输入的活动名称
        String practiceKind = CommonMethod.getString(form,"practiceKind");//获取前端选择的活动种类
        Date practiceDate = CommonMethod.getDate(form,"practiceDate");//获取活动的日期
        Practice p= null;
        Optional<Practice> op;
        if(id != null) {//选项提高安全性
            op= practiceRepository.findById(id); //查询对应数据库中主键为id的值的实体对象
            if(op.isPresent()) {
                p = op.get();
            }
        }
        if(p == null) {
            p = new Practice();  //不存在 创建实体对象
            id = getNewPracticeId();//获取鑫的主键，这个是线程同步问题;
            p.setId(id);//设置id
        }
        Student st;
        if(studentId != null) {
            st = studentRepository.findByStudentNum(studentId).get();//通过接口获取学生数据库中id值对应的相关数据，下同获取课程
            p.setStudentId_practice(st);  //设置属性
            studentRepository.save(st);
        }
        p.setPracticeNum(practiceNum);//获取活动序号
        p.setPracticeName(practiceName);//获取活动名称
        p.setPracticeKind(practiceKind);//获取活动种类
        p.setPracticeDate(practiceDate);//获取活动日期
        practiceRepository.save(p);//进行储存
        return CommonMethod.getReturnData(p.getId());
    }

    @PostMapping("/practiceDelete")//数据删除方法实现
    @PreAuthorize(" hasRole('ADMIN')")
    public DataResponse practiceDelete(@Valid @RequestBody DataRequest dataRequest) {
        Integer id = dataRequest.getInteger("id");//获取id值
        Practice p= null;
        Optional<Practice> op;
        if(id != null) {
            op= practiceRepository.findById(id); //查询获得实体对象
            if(op.isPresent()) {
                p = op.get();//在保证id存在的情况下进行id的获取
            }
        }
        if(p != null) {
            practiceRepository.delete(p);//删除id对应的数据
        }
        return CommonMethod.getReturnMessageOK();
    }
}