package org.fatmansoft.teach.controllers;

import org.fatmansoft.teach.util.VerificationCode;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.awt.image.BufferedImage;
import java.io.IOException;

@CrossOrigin

@RestController
@RequestMapping("/home")
public class ShiroController {
        public String tcode;
    @GetMapping("/verifyCode")
    public void verifyCode(HttpServletRequest request,HttpServletResponse response) throws IOException{
        VerificationCode code = new VerificationCode();
        BufferedImage image = code.getImage();
        String text = code.getText();
        HttpSession session = request.getSession(true);
        session.setAttribute("verify_code",text);
        tcode = (String) session.getAttribute("verify_code");
        System.out.println(tcode);
        VerificationCode.output(image,response.getOutputStream());
    }

    @GetMapping("/getTCode")
    public String getTCode(){
        return tcode.toLowerCase();
    }
}