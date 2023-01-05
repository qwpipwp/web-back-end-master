package org.fatmansoft.teach.service;

import org.fatmansoft.teach.models.Activity;
import org.fatmansoft.teach.models.Student;
import org.fatmansoft.teach.models.Score;
import org.fatmansoft.teach.models.Honor;
import org.fatmansoft.teach.models.Information;
import org.fatmansoft.teach.models.Practice;
import org.fatmansoft.teach.repository.ActivityRepository;
import org.fatmansoft.teach.repository.HonorRepository;
import org.fatmansoft.teach.repository.ScoreRepository;
import org.fatmansoft.teach.repository.StudentRepository;
import org.fatmansoft.teach.repository.InformationRepository;
import org.fatmansoft.teach.repository.PracticeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class IntroduceService {
    @Autowired
    private StudentRepository studentRepository;
    @Autowired
    private ScoreRepository scoreRepository;
    @Autowired
    private ActivityRepository activityRepository;
    @Autowired
    private HonorRepository honorRepository;
    @Autowired
    private InformationRepository informationRepository;
    @Autowired
    private PracticeRepository practiceRepository;
    public String getHtmlCount(String name){
        String content = "";
        content= "<!DOCTYPE html>";
        content += "<html>";
        content += "<head>";
        content += "<style>";
        content += "html { font-family: \"SourceHanSansSC\", \"Open Sans\";}";
        content += "</style>";
        content += "<meta charset='UTF-8' />";
        content += "<title>Insert title here</title>";
        content += "</head>";
        content += "<body>";

        content += "<table style='width: 100%;'>";
        content += "   <thead >";
        content += "     <tr style='text-align: center;font-size: 32px;font-weight:bold;'>";
        content += "        "+name+ " HTML </tr>";
        content += "   </thead>";
        content += "   </table>";


        content += "</body>";
        content += "</html>";
        return content;
    }
    //个人简历信息数据准备方法  请同学修改这个方法，请根据自己的数据的希望展示的内容拼接成字符串，放在Map对象里， attachList 可以方多段内容，具体内容有个人决定
    public Map getIntroduceDataMap(Integer studentId){
        List<Student> sList1 = studentRepository.findStudentListByNumName("张平");  //数据库查询操作
        Student s1;
        s1 = sList1.get(0);
        Map data = new HashMap();
        data.put("myName", "张平");   // 学生信息
        data.put("overview","学号："+s1.getStudentNum());  //学生基本信息综述

        List attachList = new ArrayList();

        Map m;

        List<Information> iList1 = informationRepository.findInformationListByNumName("张平");  //数据库查询操作
        Information i = null;
        i= iList1.get(0);

        m = new HashMap();
        m.put("title","电话号码");
        m.put("content",i.getTelephoneNumber());  // 社会实践综述

        attachList.add(m);

        m = new HashMap();
        m.put("title","学前信息");
        m.put("content",i.getPreEnrolmentInformation());  // 社会实践综述

        attachList.add(m);

        m = new HashMap();
        m.put("title","背景");
        m.put("content",i.getSocial());  // 社会实践综述

        attachList.add(m);

        List<Score> sList2 = scoreRepository.findScoreListByNumName("张平");  //数据库查询操作
        Score s2;
        s2 = sList2.get(0);
        double score1 = s2.getScore();
        m = new HashMap();
        m.put("title","学习成绩");   //
        m.put("content","数据库： "+s2.getScore());
        // 学生成绩综述
        attachList.add(m);

        s2 = sList2.get(1);
        double score2 = s2.getScore();
        m = new HashMap();
        m.put("title","学习成绩");   //
        m.put("content","数学： "+s2.getScore());
        // 学生成绩综述
        attachList.add(m);

        s2 = sList2.get(2);
        double score3 = s2.getScore();
        m = new HashMap();
        m.put("title","学习成绩");   //
        m.put("content","软件工程： "+s2.getScore());
        // 学生成绩综述
        attachList.add(m);

        s2 = sList2.get(3);
        double score4 = s2.getScore();
        m = new HashMap();
        m.put("title","学习成绩");   //
        m.put("content","C语言： "+s2.getScore());
        // 学生成绩综述
        attachList.add(m);

        List<Activity> aList1 = activityRepository.findActivityListByNumName("张平");  //数据库查询操作
        Activity a1 = null;
        a1= aList1.get(0);
        m = new HashMap();
        m.put("title","社会实践");
        m.put("content","活动编号："+a1.getActivityNum() +" "+"名称："+ a1.getActivityName());  // 社会实践综述

        attachList.add(m);

        m = new HashMap();
        m.put("title","实践时间");
        m.put("content",a1.getDates());  // 社会实践综述

        attachList.add(m);

        List<Honor> hList1 = honorRepository.findHonorListByNumName("张平");  //数据库查询操作
        Honor h = null;
        h= hList1.get(0);
        m = new HashMap();
        m.put("title","获得荣誉");
        m.put("content",h.getHonor());  // 社会实践综述

        attachList.add(m);

        List<Practice> pList1 = practiceRepository.findPracticeListByNumName("张平");  //数据库查询操作
        Practice p = null;
        p= pList1.get(0);

        m = new HashMap();
        m.put("title","实践名称");
        m.put("content","实践编号："+p.getPracticeNum() +" "+"名称："+ p.getPracticeName());  // 社会实践综述

        attachList.add(m);

        m = new HashMap();
        m.put("title","实践日期");
        m.put("content",p.getPracticeDate());  // 社会实践综述

        attachList.add(m);

        m = new HashMap();
        double allScore = ((score1+score2+score3+score4)/10-5)/10;
        m.put("title","综合成绩评测");
        m.put("content",allScore);  // 社会实践综述

        attachList.add(m);

        data.put("attachList",attachList);
        return data;
    }
}

