package org.fatmansoft.teach.controllers;

import com.openhtmltopdf.extend.impl.FSDefaultCacheStore;
import org.fatmansoft.teach.models.Student;
import org.fatmansoft.teach.models.StudentHomepage;
import org.fatmansoft.teach.payload.request.DataRequest;
import org.fatmansoft.teach.payload.response.DataResponse;
import org.fatmansoft.teach.repository.*;
import org.fatmansoft.teach.service.IntroduceService;
import org.fatmansoft.teach.util.CommonMethod;
import org.fatmansoft.teach.util.DateTimeTool;
import org.fatmansoft.teach.util.Method;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ResourceLoader;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


import javax.validation.Valid;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.*;

// origins： 允许可访问的域列表
// maxAge:准备响应前的缓存持续的最大时间（以秒为单位）。
@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/teach")

public class StudentHomepageController {
    //Java 对象的注入 我们定义的这下Java的操作对象都不能自己管理是由有Spring框架来管理的， TeachController 中要使用StudentRepository接口的实现类对象，
    // 需要下列方式注入，否则无法使用， studentRepository 相当于StudentRepository接口实现对象的一个引用，由框架完成对这个引用的复制，
    // TeachController中的方法可以直接使用
    @Value("${attach.folder}")
    private String attachFolder;

    @Autowired
    private StudentRepository studentRepository;
    @Autowired
    private CourseRepository courseRepository;
    @Autowired
    private ScoreRepository scoreRepository;
    @Autowired
    private FamilyMemberRepository familyMemberRepository;
    @Autowired
    private StudentHomepageRepository studentHomepageRepository;
    @Autowired
    private IntroduceService introduceService;
    @Autowired
    private ResourceLoader resourceLoader;
    private FSDefaultCacheStore fSDefaultCacheStore = new FSDefaultCacheStore();

    //getStudentMapList 查询所有学号或姓名与numName相匹配的学生信息，并转换成Map的数据格式存放到List
    //
    // Map 对象是存储数据的集合类，框架会自动将Map转换程用于前后台传输数据的Json对象，Map的嵌套结构和Json的嵌套结构类似，
    //下面方法是生成前端Table数据的示例，List的每一个Map对用显示表中一行的数据
    //Map 每个键值对，对应每一个列的值，如m.put("studentNum",s.getStudentNum())， studentNum这一列显示的是具体的学号的值
    //按照我们测试框架的要求，每个表的主键都是id, 生成表数据是一定要用m.put("id", s.getId());将id传送前端，前端不显示，
    //但在进入编辑页面是作为参数回传到后台.
    public List getStudentHomepageMapList(String numName) {
        List dataList = new ArrayList();
        List<StudentHomepage> sList = studentHomepageRepository.findStudentHomepageListByNumName(numName);  //数据库查询操作
        if(sList == null || sList.size() == 0)
            return dataList;
        StudentHomepage s;
        Map m;
        String courseParas,studentNameParas;
        for(int i = 0; i < sList.size();i++) {
            s = sList.get(i);
            m = new HashMap();
            m.put("id", s.getId());
            m.put("studentNum",s.getStudent().getStudentNum());
            m.put("studentName",s.getStudent().getStudentName());
            if("1".equals(s.getStudent().getSex())) {    //数据库存的是编码，显示是名称
                m.put("sex","男");
            }else {
                m.put("sex","女");
            }
            m.put("age",s.getStudent().getAge());
            m.put("dept",s.getStudent().getDept());
            m.put("birthday", DateTimeTool.parseDateTime(s.getStudent().getBirthday(),"yyyy-MM-dd"));  //时间格式转换字符串
            dataList.add(m);
        }
        return dataList;
    }
    //student页面初始化方法
    //Table界面初始是请求列表的数据，这里缺省查出所有学生的信息，传递字符“”给方法getStudentMapList，返回所有学生数据，
    @PostMapping("/studentHomepageInit")
    @PreAuthorize("hasRole('ADMIN')")
    public DataResponse studentHomepageInit(@Valid @RequestBody DataRequest dataRequest) {
        List dataList = getStudentHomepageMapList("");
        return CommonMethod.getReturnData(dataList);  //按照测试框架规范会送Map的list
    }
    //student页面点击查询按钮请求
    //Table界面初始是请求列表的数据，从请求对象里获得前端界面输入的字符串，作为参数传递给方法getStudentMapList，返回所有学生数据，
    @PostMapping("/studentHomepageQuery")
    @PreAuthorize("hasRole('ADMIN')")
    public DataResponse studentHomepageQuery(@Valid @RequestBody DataRequest dataRequest) {
        String numName= dataRequest.getString("numName");
        List dataList = getStudentHomepageMapList(numName);
        return CommonMethod.getReturnData(dataList);  //按照测试框架规范会送Map的list
    }
    //  学生信息删除方法
    //Student页面的列表里点击删除按钮则可以删除已经存在的学生信息， 前端会将该记录的id 回传到后端，方法从参数获取id，查出相关记录，调用delete方法删除
    @PostMapping("/studentHomepageDelete")
    @PreAuthorize(" hasRole('ADMIN')")
    public DataResponse studentHomepageDelete(@Valid @RequestBody DataRequest dataRequest) {
        Integer id = dataRequest.getInteger("id");  //获取id值
        StudentHomepage s= null;
        Optional<StudentHomepage> op;
        if(id != null) {
            op= studentHomepageRepository.findById(id);   //查询获得实体对象
            if(op.isPresent()) {
                s = op.get();
            }
        }
        if(s != null) {
            studentHomepageRepository.delete(s);    //数据库永久删除
        }
        return CommonMethod.getReturnMessageOK();  //通知前端操作正常
    }

    //studentEdit初始化方法
    //studentEdit编辑页面进入时首先请求的一个方法， 如果是Edit,再前台会把对应要编辑的那个学生信息的id作为参数回传给后端，我们通过Integer id = dataRequest.getInteger("id")
    //获得对应学生的id， 根据id从数据库中查出数据，存在Map对象里，并返回前端，如果是添加， 则前端没有id传回，Map 对象数据为空（界面上的数据也为空白）

    @PostMapping("/studentHomepageEditInit")
    @PreAuthorize("hasRole('ADMIN')")
    public DataResponse studentHomepageEditInit(@Valid @RequestBody DataRequest dataRequest) {
        Integer id = dataRequest.getInteger("id");
        StudentHomepage s= null;
        Optional<StudentHomepage> op;
        if(id != null) {
            op= studentHomepageRepository.findById(id);
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
        sexList.add(m);
        Map form = new HashMap();
        String image0="";
        if(s != null) {
            form.put("studentNum",s.getStudent().getStudentNum());
            form.put("studentName",s.getStudent().getStudentName());
            form.put("sex",s.getStudent().getSex());  //这里不需要转换
            form.put("age",s.getStudent().getAge());
            form.put("birthday", DateTimeTool.parseDateTime(s.getStudent().getBirthday(),"yyyy-MM-dd")); //这里需要转换为字符串
            image0  = getStudentHomepageImageString(attachFolder,s.getStudent().getStudentNum(), "0");
        }
        form.put("sexList",sexList);
        Map data = new HashMap();
        data.put("form",form);
        data.put("sexList",sexList);
        data.put("image0",image0);
        return CommonMethod.getReturnData(data); //这里回传包含学生信息的Map对象
    }
//  学生信息提交按钮方法
    //相应提交请求的方法，前端把所有数据打包成一个Json对象作为参数传回后端，后端直接可以获得对应的Map对象form, 再从form里取出所有属性，复制到
    //实体对象里，保存到数据库里即可，如果是添加一条记录， id 为空，这是先 new Student 计算新的id， 复制相关属性，保存，如果是编辑原来的信息，
    //id 不为空。则查询出实体对象，复制相关属性，保存后修改数据库信息，永久修改
    public synchronized Integer getNewStudentHomepageId(){
        Integer
        id = studentHomepageRepository.getMaxId();  // 查询最大的id
        if(id == null)
            id = 1;
        else
            id = id+1;
        return id;
    };
    @PostMapping("/studentHomepageEditSubmit")
    @PreAuthorize(" hasRole('ADMIN')")
    public DataResponse studentHomepageEditSubmit(@Valid @RequestBody DataRequest dataRequest) {
        Integer id = dataRequest.getInteger("id");
        Map form = dataRequest.getMap("form"); //参数获取Map对象
        String studentId = CommonMethod.getString(form,"studentNum");//获取学生的id下同课程的id
        StudentHomepage sh= null;
        Student s = null;
        Optional<StudentHomepage> op;
        if(id != null) {
            op= studentHomepageRepository.findById(id);  //查询对应数据库中主键为id的值的实体对象
            if(op.isPresent()) {
                sh = op.get();
            }
        }
        if(sh == null) {
            sh = new StudentHomepage();   //不存在 创建实体对象
            id = getNewStudentHomepageId(); //获取鑫的主键，这个是线程同步问题;
            sh.setId(id);  //设置新的id
        }
        if(studentId != null) {
            s = studentRepository.findByStudentNum(studentId).get();//通过接口获取学生数据库中id值对应的相关数据，下同获取课程
            sh.setStudent(s);  //设置属性
            studentRepository.save(s);
        }
        studentHomepageRepository.save(sh);  //新建和修改都调用save方法
        return CommonMethod.getReturnData(sh.getId());  // 将记录的id返回前端
    }

    @PostMapping("/uploadStudentHomepageImage")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public DataResponse uploadStudentHomepageImage(@RequestParam Map pars, @RequestParam("file") MultipartFile file) {
        String studentNum = CommonMethod.getString(pars,"studentNum");
        String no = CommonMethod.getString(pars,"no");
        String oFileName = file.getOriginalFilename();
        oFileName = oFileName.toUpperCase();
        try{
            InputStream in = file.getInputStream();
            int size = (int)file.getSize();
            byte [] data = new byte[size];
            in.read(data);
            in.close();
            String fileName =attachFolder + "images/" + studentNum + "-" + no + ".JPG";
            FileOutputStream out = new FileOutputStream(fileName);
            out.write(data);
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return CommonMethod.getReturnMessageOK();
    }
    public String getStudentHomepageImageString(String attachFolder, String studentNum, String no) {
        String fileName =attachFolder + "images/" + studentNum + "-" + no + ".JPG";
        File file = new File(fileName);
        if (!file.exists())
            return "";
        try {
            FileInputStream in = new FileInputStream(file);
            int size = (int) file.length();
            byte data[] = new byte[size];
            in.read(data);
            in.close();
            String imgStr = "data:image/png;base64,";
            String s = new String(Base64.getEncoder().encode(data));
            imgStr = imgStr + s;
            return imgStr;
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    @PostMapping("/getStudentHomepageImage")
    @PreAuthorize("hasRole('ADMIN')")
    public DataResponse getStudentHomepageImage(@Valid @RequestBody DataRequest dataRequest) {
        String  studentNum= dataRequest.getString("studentNum");
        String no = dataRequest.getString("no");

        String str = getStudentHomepageImageString(attachFolder,studentNum, no);
        return CommonMethod.getReturnData(str);
    }

}
