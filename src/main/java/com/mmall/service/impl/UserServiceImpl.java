package com.mmall.service.impl;

import com.mmall.common.Const;
import com.mmall.common.ServerResponse;
import com.mmall.common.TokenCache;
import com.mmall.dao.UserMapper;
import com.mmall.pojo.User;
import com.mmall.service.IUserservice;
import com.mmall.util.MD5Util;
import jdk.nashorn.internal.runtime.logging.Logger;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.rmi.CORBA.Util;
import java.util.UUID;
@Logger
@Service("iUserService")
public class UserServiceImpl implements IUserservice {
    @Autowired
    private UserMapper userMapper;


    @Override
    /**
     *
     */
    public ServerResponse<User> login(String username, String password) {
        int resultCount = userMapper.checkUsername(username);
        if (resultCount == 0) {
            return ServerResponse.createByErrorMessage("用户名不存在");
        }
        //todo 密码登陆MD5
        String md5Password=MD5Util.MD5EncodeUtf8(password);
        System.out.println("**********************"+password+md5Password);
        User user = userMapper.selectLogin(username, md5Password);
        if (user == null) {
            return ServerResponse.createByErrorMessage("密码错误");
        }
        user.setPassword(StringUtils.EMPTY);//把密码赋值为空 安全
        return ServerResponse.createBySuccess("登录成功", user);
    }
    public ServerResponse<String> register(User user){



        ServerResponse response=this.checkValid(user.getUsername(),Const.USERNAME);
        if(!response.isSuccess()){
            return ServerResponse.createByErrorMessage("用户名已存在");
        }
        response=this.checkValid(user.getEmail(),Const.EMAIL);
        if(!response.isSuccess()){
            return ServerResponse.createByErrorMessage("该邮箱已经注册");
        }
//        int resultCount=userMapper.checkUsername(user.getUsername());
//        if(resultCount>0){
//
//        }
//        resultCount=userMapper.checkEmail(user.getEmail());
//        if(resultCount>0){
//
//        }
        user.setRole(Const.Role.ROLE_COSTUMER);
        //MD5加密
        user.setPassword(MD5Util.MD5EncodeUtf8(user.getPassword()));
        int resultCount=userMapper.insertSelective(user);
        if(resultCount==0){
            return ServerResponse.createByErrorMessage("注册失败");
        }

        return ServerResponse.createBySuccessMessage("注册成功");
    }
    public ServerResponse<String> checkValid(String str,String type){
        if(org.apache.commons.lang3.StringUtils.isNotBlank(type)){
            if(Const.EMAIL.equals(type)){
                 int resultCount=userMapper.checkEmail(str);
                 if(resultCount>0){
                     return ServerResponse.createByErrorMessage("邮箱已存在");
                 }
            }
            if(Const.USERNAME.equals(type)){
                int resultCpunt=userMapper.checkUsername(str);
                if(resultCpunt>0){
                    return ServerResponse.createByErrorMessage("用户名已经存在");
                }
            }
        }
        else{
            return ServerResponse.createByErrorMessage("参数错误");
        }
        return ServerResponse.createBySuccessMessage("校验成功");
    }

    /**
     *
     * @param username
     * @return
     */
    public ServerResponse<String> selectQuestion(String username){
        ServerResponse validResponse=this.checkValid(username,Const.USERNAME);
        if(validResponse.isSuccess()){
            return ServerResponse.createByErrorMessage("用户未注册");
        }
        String question=userMapper.selectQuestionByUsername(username);
        if(org.apache.commons.lang3.StringUtils.isNotBlank(question)){
            return ServerResponse.createBySuccess(question);
        }
        return ServerResponse.createByErrorMessage("找回密码问题为空");
    }

    /**
     *
     * @param username
     * @param question
     * @param answer
     * @return
     */
    public ServerResponse<String> checkQandA(String username,String question,String answer){
        int count=userMapper.checkAnswer(username,question,answer);
        if(count>0){
            String forgetToken= UUID.randomUUID().toString();//重复概率非常低
            TokenCache.setKey(TokenCache.TOKEN_PREFIX+username,forgetToken);
            return ServerResponse.createBySuccess(forgetToken);
        }
        return ServerResponse.createByErrorMessage("问题答案错误");
    }
    public ServerResponse<String> forgetResetPassword(String username,String newPassword,String forgetToken){
        if(org.apache.commons.lang3.StringUtils.isBlank(forgetToken)){
            return ServerResponse.createByErrorMessage("token为空，请重新传参");
        }
        ServerResponse validReaponse=this.checkValid(username,Const.USERNAME);
        if(validReaponse.isSuccess()){
            return ServerResponse.createByErrorMessage("用户不存在");
        }
        String token=TokenCache.getKey(TokenCache.TOKEN_PREFIX+username);
        if(org.apache.commons.lang3.StringUtils.isBlank(token)){
            return ServerResponse.createByErrorMessage("token过期");
        }
        if(org.apache.commons.lang3.StringUtils.equals(forgetToken,token)){
            String md5Pwd=MD5Util.MD5EncodeUtf8(newPassword);
            int rowCount=userMapper.updataPasswordByUsername(username,md5Pwd);
            if(rowCount==1){
                return ServerResponse.createBySuccessMessage("重置成功！");
            }
        }
        else{
            return ServerResponse.createByErrorMessage("token错误");
        }
        return ServerResponse.createByErrorMessage("重置失败");
    }
    public ServerResponse<String> resetPwd(String passwordOld,String passwordNew,User user){
        String Md5Pwd=MD5Util.MD5EncodeUtf8(passwordOld);
        String Md5PwdNew=MD5Util.MD5EncodeUtf8(passwordNew);

        int resultCount=userMapper.checkPassword(user.getId(),Md5Pwd);
        if(resultCount<1){
            return ServerResponse.createByErrorMessage("旧密码错误，请重新输入");
        }
        else{
            user.setPassword(Md5PwdNew);
            resultCount=userMapper.updateByPrimaryKey(user);
            if(resultCount==1){
                return ServerResponse.createBySuccessMessage("重置成功");
            }
        }
        return ServerResponse.createByErrorMessage("重置失败");
    }
    public ServerResponse<User> updateInfo(User user){
        int emailCount=userMapper.checkEmailById(user.getEmail(),user.getId());
        if(emailCount>0){
            return ServerResponse.createByErrorMessage("邮箱已经存，请尝试新的email");
        }
        User updateUser=new User();
        updateUser.setId(user.getId());
        updateUser.setAnswer(user.getAnswer());
        updateUser.setQuestion(user.getQuestion());
        updateUser.setEmail(user.getEmail());
        updateUser.setPhone(user.getPhone());
        int updateCount=userMapper.updateByPrimaryKeySelective(updateUser);
        if(updateCount>0){
            return ServerResponse.createBySuccess("更新成功",updateUser);
        }
        return ServerResponse.createByErrorMessage("更新失败");
    }
    public ServerResponse<User> getUserInfo(Integer userid){
        User user=userMapper.selectByPrimaryKey(userid);
        if(user==null){
            return ServerResponse.createByErrorMessage("找不到当前用户");
        }
        user.setPassword(StringUtils.EMPTY);

        return ServerResponse.createBySuccess(user);
    }
    public ServerResponse<String> register2(User user){
        ServerResponse response=this.checkValid(user.getUsername(),Const.USERNAME);
        if(!response.isSuccess()){
            return ServerResponse.createByErrorMessage("用户名已存在");
        }
        response=this.checkValid(user.getEmail(),Const.EMAIL);
        if(!response.isSuccess()){
            return ServerResponse.createByErrorMessage("该邮箱已经注册");
        }
//        int resultCount=userMapper.checkUsername(user.getUsername());
//        if(resultCount>0){
//
//        }
//        resultCount=userMapper.checkEmail(user.getEmail());
//        if(resultCount>0){
//
//        }
        user.setRole(Const.Role.ROLE_COSTUMER);
        //MD5加密
        user.setPassword(MD5Util.MD5EncodeUtf8(user.getPassword()));
        int resultCount=userMapper.insert2(user);
        if(resultCount==0){
            return ServerResponse.createByErrorMessage("注册失败");
        }

        return ServerResponse.createBySuccessMessage("注册成功");
    }
    public ServerResponse checkAdminRole(User user){
        if(user!=null&&user.getRole().intValue()==Const.Role.ROLE_ADMIN){
            return ServerResponse.createBySuccess();
        }
        return ServerResponse.createByError();
    }
}
