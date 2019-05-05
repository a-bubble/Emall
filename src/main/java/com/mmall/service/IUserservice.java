package com.mmall.service;

import com.mmall.common.ServerResponse;
import com.mmall.pojo.User;
import org.springframework.stereotype.Service;

public interface IUserservice {

    ServerResponse<User> login(String username, String password);
    ServerResponse<String> register(User user);
    ServerResponse<String> checkValid(String str,String type);
    ServerResponse<String> selectQuestion(String username);
    ServerResponse<String> checkQandA(String username,String question,String answer);
    ServerResponse<String> forgetResetPassword(String username,String newPassword,String forgetToken);
    ServerResponse<String> resetPwd(String passwordOld,String passwordNew,User user);
    ServerResponse<User> updateInfo(User user);
    ServerResponse<User> getUserInfo(Integer userid);
    ServerResponse<String> register2(User user);
    ServerResponse checkAdminRole(User user);
}
