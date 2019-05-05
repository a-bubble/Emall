package com.mmall.dao;

import com.mmall.pojo.User;
import org.apache.ibatis.annotations.Param;

public interface UserMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(User record);

    int insertSelective(User record);

    User selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(User record);

    int updateByPrimaryKey(User record);
    int checkUsername(String username);
    User selectLogin(@Param("username") String username, @Param("password") String password);
    int checkEmail(String email);
    String selectQuestionByUsername(String username);
    int checkAnswer(@Param("username") String username,@Param("que") String question,@Param("ans") String answer);
    int updataPasswordByUsername(@Param("username") String usernaem,@Param("newpassword") String newPassword);
    int checkPassword(@Param("userid")Integer userid,@Param("password")String password);
    int checkEmailById(@Param("email") String email,@Param("id") Integer id);
    int insert2(User user);

}