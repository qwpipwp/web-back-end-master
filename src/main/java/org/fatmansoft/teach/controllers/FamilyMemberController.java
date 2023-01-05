package org.fatmansoft.teach.controllers;

import org.fatmansoft.teach.models.FamilyMember;
import org.fatmansoft.teach.models.Student;
import org.fatmansoft.teach.payload.request.DataRequest;
import org.fatmansoft.teach.payload.response.DataResponse;
import org.fatmansoft.teach.repository.FamilyMemberRepository;
import org.fatmansoft.teach.repository.StudentRepository;
import org.fatmansoft.teach.service.IntroduceService;
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


public class FamilyMemberController {
    //Java 对象的注入 我们定义的这下Java的操作对象都不能自己管理是由有Spring框架来管理的， TeachController 中要使用StudentRepository接口的实现类对象，
    // 需要下列方式注入，否则无法使用， studentRepository 相当于StudentRepository接口实现对象的一个引用，由框架完成对这个引用的复制，
    // TeachController中的方法可以直接使用
    @Autowired
    private IntroduceService introduceService;
    @Autowired
    private FamilyMemberRepository familyMemberRepository;
    @Autowired
    private StudentRepository studentRepository;



    //getFamilyMemberMapList 查询所有学号或姓名与numName相匹配的学生信息，并转换成Map的数据格式存放到List
    //
    // Map 对象是存储数据的集合类，框架会自动将Map转换程用于前后台传输数据的Json对象，Map的嵌套结构和Json的嵌套结构类似，
    //下面方法是生成前端Table数据的示例，List的每一个Map对用显示表中一行的数据
    //Map 每个键值对，对应每一个列的值，
    //按照我们测试框架的要求，每个表的主键都是id, 生成表数据是一定要用m.put("id", s.getId());将id传送前端，前端不显示，
    //但在进入编辑页面是作为参数回传到后台.
    public List getFamilyMemberMapList(String numName) {
        List dataList = new ArrayList();
        List<FamilyMember> sList = familyMemberRepository.findFamilyMemberListByNumName(numName);  //数据库查询操作
        if(sList == null || sList.size() == 0)
            return dataList;
        FamilyMember s;
        Map m;
        for(int i = 0; i < sList.size();i++) {
            s = sList.get(i);
            m = new HashMap();
            m.put("id", s.getId());
            m.put("studentNum",s.getStudentId_fa().getStudentNum());//获取学号
            m.put("studentName",s.getStudentId_fa().getStudentName());//获取学生姓名
            m.put("name",s.getName());
            if("1".equals(s.getSex())) {    //数据库存的是编码，显示是名称
                m.put("sex","男");
            }else {
                m.put("sex","女");
            }//获取选择的性别
            m.put("rel",s.getRel());//获取关系
            dataList.add(m);
        }
        return dataList;
    }
    //familyMember页面初始化方法
    //Table界面初始是请求列表的数据，这里缺省查出所有学生的信息，传递字符“”给方法getFamilyMemberMapList，返回所有学生数据，
    @PostMapping("/familyMemberInit")//成绩页面的初始化方法
    @PreAuthorize("hasRole('ADMIN') or hasRole('TEACHER') ")
    public DataResponse familyMemberInit(@Valid @RequestBody DataRequest dataRequest)
    {
        String studentName = dataRequest.getString("studentName");//以studentName为key值检索FamilyMember数据库中所有相关数据
        if(studentName == null)
        {
            studentName = "";//为空时传递空串，以显示数据库中所有数据
        }
        List<HashMap<String,Object>> mapList = getFamilyMemberMapList(studentName);
        return CommonMethod.getReturnData(mapList);//返回数据
    }

    @PostMapping("/familyMemberQuery")//查询功能实现
    @PreAuthorize("hasRole('ADMIN')")
    public DataResponse familyMemberQuery(@Valid@RequestBody DataRequest dataRequest)
    {
        String numName = dataRequest.getString("numName");//获取从前端返回的查询值
        List<HashMap<String,Object>> mapList = getFamilyMemberMapList(numName);//使用接口功能查询数据库，将数据存到list中
        return CommonMethod.getReturnData(mapList);//返回查询到的数据
    }

    //familyMemberEdit初始化方法
    //familyMemberEdit编辑页面进入时首先请求的一个方法， 如果是Edit,再前台会把对应要编辑的那个学生信息的id作为参数回传给后端，我们通过Integer id = dataRequest.getInteger("id")
    //获得对应学生的id， 根据id从数据库中查出数据，存在Map对象里，并返回前端，如果是添加， 则前端没有id传回，Map 对象数据为空（界面上的数据也为空白）

    @PostMapping("/familyMemberEditInit")
    @PreAuthorize("hasRole('ADMIN') or hasRole('TEACHER') ")
    public DataResponse familyMemberEditInit(@Valid @RequestBody DataRequest dataRequest) {
        Integer id = dataRequest.getInteger("id");//获取key值id
        FamilyMember s= null;
        Optional<FamilyMember> op;
        if(id != null) {//添加条件提高稳定性
            op= familyMemberRepository.findById(id);//找寻id相应的数据进行处理
            if(op.isPresent()) {
                s = op.get();
            }
        }
        List sexList = new ArrayList();
        Map m;
        m = new HashMap();
        m.put("label","男");
        m.put("value","1");
        sexList.add(m);
        m = new HashMap();
        m.put("label","女");
        m.put("value","2");
        sexList.add(m);//编辑页面的选项处理，储存的数据对应性别
        Map form = new HashMap();
        if(s != null) {
            form.put("id",s.getId());
            form.put("studentNum",s.getStudentId_fa().getStudentNum());//获取学号
            form.put("studentName",s.getStudentId_fa().getStudentName());//获取学生姓名
            form.put("name",s.getName());//获取家长的姓名
            form.put("sex",s.getSex());//获取性别
            form.put("rel",s.getRel());//获取关系
        }
        form.put("sexList",sexList);
        return CommonMethod.getReturnData(form); //这里回传包含信息的Map对象
    }
    //  信息提交按钮方法
    //相应提交请求的方法，前端把所有数据打包成一个Json对象作为参数传回后端，后端直接可以获得对应的Map对象form, 再从form里取出所有属性，复制到
    //实体对象里，保存到数据库里即可，如果是添加一条记录， id 为空，这是先 new FamilyMember 计算新的id， 复制相关属性，保存，如果是编辑原来的信息，
    //id 不为空。则查询出实体对象，复制相关属性，保存后修改数据库信息，永久修改
    public synchronized Integer getNewFamilyMemberId(){
        Integer
                id = familyMemberRepository.getMaxId();  // 查询最大的id
        if(id == null)
            id = 1;
        else
            id = id+1;
        return id;
    };
    @PostMapping("/familyMemberEditSubmit")//家庭信息的处理
    @PreAuthorize("hasRole('ADMIN') or hasRole('TEACHER') ")
    public DataResponse familyMemberEditSubmit(@Valid @RequestBody DataRequest dataRequest) {
        Map form = dataRequest.getMap("form"); //参数获取Map对象
        Integer id = CommonMethod.getInteger(form,"id");
        String studentId = CommonMethod.getString(form,"studentNum");//获取学生的id下同课程的id
        String name = CommonMethod.getString(form,"name");//获取家长姓名
        String sex = CommonMethod.getString(form,"sex");//获取性别
        String rel = CommonMethod.getString(form,"rel");//获取关系
        FamilyMember s= null;
        Optional<FamilyMember> op;
        if(id != null) {//选项提高安全性
            op= familyMemberRepository.findById(id);  //查询对应数据库中主键为id的值的实体对象
            if(op.isPresent()) {
                s = op.get();
            }
        }
        if(s == null) {
            s = new FamilyMember();   //不存在 创建实体对象
            id = getNewFamilyMemberId(); //获取鑫的主键，这个是线程同步问题;
            s.setId(id);  //设置新的id
        }
        Student st;
        if(studentId != null) {
            st = studentRepository.findByStudentNum(studentId).get();//通过接口获取学生数据库中id值对应的相关数据，下同获取课程
            s.setStudentId_fa(st);  //设置属性
            studentRepository.save(st);
        }
        s.setName(name);//获取姓名
        s.setSex(sex);//获取性别
        s.setRel(rel);//获取关系
        familyMemberRepository.save(s);  //新建和修改都调用save方法
        return CommonMethod.getReturnData(s.getId());  // 将记录的id返回前端
    }

    //  信息删除方法
    //familyMember页面的列表里点击删除按钮则可以删除已经存在的学生信息， 前端会将该记录的id 回传到后端，方法从参数获取id，查出相关记录，调用delete方法删除
    @PostMapping("/familyMemberDelete")
    @PreAuthorize("hasRole('ADMIN') or hasRole('TEACHER') ")
    public DataResponse familyMemberDelete(@Valid @RequestBody DataRequest dataRequest) {
        Integer id = dataRequest.getInteger("id");  //获取id值
        FamilyMember s= null;
        Optional<FamilyMember> op;
        if(id != null) {
            op= familyMemberRepository.findById(id);   //查询获得实体对象
            if(op.isPresent()) {
                s = op.get();
            }
        }
        if(s != null) {
            familyMemberRepository.delete(s);    //数据库永久删除
        }
        return CommonMethod.getReturnMessageOK();  //通知前端操作正常
    }



}
