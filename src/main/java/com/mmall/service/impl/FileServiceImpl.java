package com.mmall.service.impl;

import com.google.common.collect.Lists;
import com.mmall.service.IFileService;
import com.mmall.util.FTPUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.UUID;
@Service("iFileService")
public class FileServiceImpl implements IFileService {
    private Logger logger= LoggerFactory.getLogger(FileServiceImpl.class);
    public String upload(MultipartFile file,String path){
        String filename=file.getOriginalFilename();//原始文件名
        //获取扩展名 从后边开始获取 到第一个点 获取到点这个索引后加1 去掉点
        String fileExtentsName=filename.substring(filename.lastIndexOf(".")+1);
        String uploadFileName= UUID.randomUUID().toString()+"."+fileExtentsName;
        logger.info("开始上传文件，文件名：{}，路径：{}，新文件名：{}",filename,path,uploadFileName);
       //生成目录file 对文件夹进行判断
        File fileDir=new File(path);
        if(!fileDir.exists()){
           fileDir.setWritable(true);
           fileDir.mkdirs();//无s 当前层级 yous包含子级别
        }
        File targetFile=new File(path,uploadFileName);
        try {
            file.transferTo(targetFile);
            //todo 把target文件上传到ftp
            FTPUtil.uploadFiles(Lists.newArrayList(targetFile));
            targetFile.delete();
            //上传完 删除upload下面的文件
        }catch (IOException e){
            logger.error("上传异常",e);
            return null;
        }
       return targetFile.getName();
    }
}
