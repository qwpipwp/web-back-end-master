package org.fatmansoft.teach.controllers;

import org.fatmansoft.teach.payload.request.BlogRequest;
import org.fatmansoft.teach.payload.request.ChangePasswordRequest;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.io.*;

@CrossOrigin

@RestController
@RequestMapping("/blog")
public class BlogController {
    @PostMapping("/create")
    public void createFile(@Valid @RequestBody BlogRequest blogRequest) throws IOException {
        //博客的路径随项目文件路径更改需要再次更改
        String filePath = "E:/web-front-end-master/public/md";
        String username = blogRequest.getUsername();
        filePath = filePath + "/" + username + "/" + "blog";
//        E:\web-front-end-master\public\md
        File dir = new File(filePath);
        // 一、检查放置文件的文件夹路径是否存在，不存在则创建
        if (!dir.exists()) {
            dir.mkdirs();// mkdirs创建多级目录
        }
        //读取文本第一行，取第一行做文件名，解决多行文本传入filename报错问题
        StringReader files = new StringReader(blogRequest.getText());
        BufferedReader bufferedReader = new BufferedReader(files);
        String line = bufferedReader.readLine();
//        System.out.println(line);
        String filename =line;
        System.out.println(filename);
        File checkFile = new File(filePath + "/" + filename + ".md");
        FileWriter writer = null;
        try {
            // 二、检查目标文件是否存在，不存在则创建
            if (!checkFile.exists()) {
                checkFile.createNewFile();// 创建目标文件
            }
            // 三、向目标文件中写入内容
            // FileWriter(File file, boolean append)，append为true时为追加模式，false或缺省则为覆盖模式
            String content = blogRequest.getText();
            writer = new FileWriter(checkFile, true);
            writer.append(content);
            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (null != writer)
                writer.close();
        }
    }
}
