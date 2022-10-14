package com.reggie.controller;

import com.reggie.common.R;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Objects;
import java.util.UUID;

/**
 * @ClassName CommonController
 * @Date 2022/10/7 19:07
 * 进行文件的上传和下载
 */
@RestController
@RequestMapping("/common")
public class CommonController {

    public static final Logger logger = LoggerFactory.getLogger(CommonController.class);

    @Value("${reggie.path}")
    private String basePath;

    @PostMapping("/upload")
    public R<String> upload(MultipartFile file) { //MultipartFile file 变量名必须与前端页面保持一致
        //file是一个临时文件，需要转存到指定位置，否则本次请求完成后临时文件会删除
        logger.info(file.getName());

        //原始文件名
        String fileType = Objects.requireNonNull(file.getOriginalFilename()).substring(file.getOriginalFilename().indexOf("."));

        //使用UUID重新生成文件名，防止文件名称重复造成文件覆盖
        String fileName = UUID.randomUUID().toString() + fileType;

        //创建一个目录对象
        File dir = new File(basePath);
        if (!dir.exists()) {
            //不存在则创建
            dir.mkdirs();
        }
        try {
            //将临时文件转存到指定位置
            file.transferTo(new File(basePath + fileName));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return R.success(fileName);
    }

    /**
     * @date: 2022/10/7 21:07
     * @remark: 文件下载
     */
    @GetMapping("/download")
    public void download(@RequestParam("name") String fileName, HttpServletResponse response) {
        try {
            //通过输入流，读取文件内容
            FileInputStream fileInputStream = new FileInputStream(new File(basePath + fileName));

            //通过输出流，将文件写回浏览器，在浏览器展示图片
            ServletOutputStream outputStream = response.getOutputStream();

            response.setContentType("image/jpeg");
            int len;
            byte[] bytes = new byte[1024];
            while ((len = fileInputStream.read(bytes)) != -1) {
                outputStream.write(bytes, 0, len);
                outputStream.flush();
            }

            outputStream.close();
            fileInputStream.close();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }








    }

 }
