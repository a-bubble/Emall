package com.mmall.util;

import org.apache.commons.net.ftp.FTPClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;


public class FTPUtil {
    private static final Logger log= LoggerFactory.getLogger(FTPUtil.class);
    private static String ftpIP=PropertiesUtil.getProperty("ftp.server.ip");
    private static String ftpUser=PropertiesUtil.getProperty("ftp.user");
    private static String ftpPass=PropertiesUtil.getProperty("ftp.pass");
    private String ip;
    public FTPUtil(String ip,int port,String user,String pwd){
        this.ip=ip;
        this.port=port;
        this.user=user;
        this.pwd=pwd;

    }
    public static boolean uploadFiles(List<File> fileList) throws IOException{
        FTPUtil ftpUtil=new FTPUtil(ftpIP,21,ftpUser,ftpPass);
        boolean result=ftpUtil.uploadFile("img",fileList);

        log.info("开始连接服务器，上传结果：{}");
         return result;
    }
    private boolean uploadFile(String remotePath,List<File> fileList) throws IOException{
        boolean uoload=true;
        FileInputStream fis=null;
        if(connectServer(this.getIp(),this.port,this.user,this.pwd)){
            try{
                ftpClient.changeWorkingDirectory(remotePath);
                ftpClient.setBufferSize(1024);
                ftpClient.setControlEncoding("UTF-8");
                //设置为二进制文件类型 防止乱码
                ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE);
                ftpClient.enterLocalPassiveMode();//打开本地被动模式
                for(File fileItem:fileList){
                   fis=new FileInputStream(fileItem);
                   ftpClient.storeFile(fileItem.getName(),fis);
                }
            }catch (IOException e){
                log.error("上传文件异常",e);
            }
            finally {
                fis.close();
                ftpClient.disconnect();
            }
        }
       return uoload;
    }
    private boolean connectServer(String  ip,int port,String user,String pass){
        ftpClient=new FTPClient();
        boolean isSuccess=false;
        try{
        ftpClient.connect(ip);
        isSuccess=ftpClient.login(user,pass);
        }
        catch (IOException e){

        }
        return isSuccess;
    }
    public static String getFtpIP() {
        return ftpIP;
    }

    public static void setFtpIP(String ftpIP) {
        FTPUtil.ftpIP = ftpIP;
    }

    public static String getFtpUser() {
        return ftpUser;
    }

    public static void setFtpUser(String ftpUser) {
        FTPUtil.ftpUser = ftpUser;
    }

    public static String getFtpPass() {
        return ftpPass;
    }

    public static void setFtpPass(String ftpPass) {
        FTPUtil.ftpPass = ftpPass;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPwd() {
        return pwd;
    }

    public void setPwd(String pwd) {
        this.pwd = pwd;
    }

    public FTPClient getFtpClient() {
        return ftpClient;
    }

    public void setFtpClient(FTPClient ftpClient) {
        this.ftpClient = ftpClient;
    }

    private int port;
    private String user;
    private String pwd;
    private FTPClient ftpClient;

}
