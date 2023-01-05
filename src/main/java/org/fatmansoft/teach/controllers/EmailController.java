package org.fatmansoft.teach.controllers;


import org.fatmansoft.teach.payload.request.VerifyRequest;
import org.fatmansoft.teach.util.CalculatorUtil;
import org.fatmansoft.teach.util.SendmailUtil;
//import org.fatmansoft.teach.util.VerifyCodeUtil;
import org.fatmansoft.teach.util.VerificationCode;
import org.fatmansoft.teach.util.VerifyCodeUtil;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.io.File;
import java.io.FileInputStream;
import java.util.*;

//生成发送给邮箱的验证码
@CrossOrigin

@RestController
@RequestMapping("/home")
public class EmailController{

    /**
     * 发送系统验证
     *
     * @param toEmailAddress 收件人邮箱
     * @return
     */
    private String vcode;
//    @RequestMapping(value={"/sendEmailSystem/"},method={RequestMethod.POST})
    @PostMapping("/sendEmailSystem/")
    public String sendEmailSystem(@RequestParam("toEmailAddress") String toEmailAddress){
        try{
            //生成验证码
            String verifyCode = VerifyCodeUtil.generateVerifyCode(6);
            vcode = verifyCode;
            //邮件主题
            String emailTitle = "修改密码邮箱验证";

            //邮件内容
            String emailContent = "您正在进行邮箱验证，您的验证码为：" + verifyCode + "，请于10分钟内完成验证！";

            //发送邮件
            SendmailUtil.sendEmail(toEmailAddress, emailTitle, emailContent);
            return "success";
        }catch(Exception e){
            return "false";
        }
    }

    //后端验证码送给前端，不够安全，更换为在后端比对,此方法废弃
//    @PostMapping("/getVCode")
//    public String getVcode(){
//        return vcode;
//    }

    @PostMapping("/verify")
    public String verify(@Valid @RequestBody VerifyRequest verifyRequest){
        String usercode = verifyRequest.getCodeOfUser();
        System.out.println(usercode);
        if(usercode.equals(vcode))
            return "验证成功";

        return  "验证码不对";

    }
}