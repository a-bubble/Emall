package com.mmall.controller.backend;

import com.google.common.collect.Lists;
import com.mmall.common.Const;
import com.mmall.common.ResponseCode;
import com.mmall.common.ServerResponse;
import com.mmall.pojo.Category;
import com.mmall.pojo.User;
import com.mmall.service.ICategoryService;
import com.mmall.service.IUserservice;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;
import java.util.List;

@Controller
@RequestMapping("/manage/category")
public class CategoryManageController {
    @Autowired
    private ICategoryService iCategoryService;
    @Autowired
    private IUserservice iUserservice;
    @RequestMapping(value = "add_category.do")
    @ResponseBody
    public ServerResponse addCategory(HttpSession session,String categoryName,@RequestParam(value = "parentID",defaultValue = "0") int parentId){
        User user=(User)session.getAttribute(Const.CURRENT_USER);
        if(user==null){
            return ServerResponse.createByErrorMessage("用户未登录");
        }
        if(iUserservice.checkAdminRole(user).isSuccess()){
           return iCategoryService.addCategory(categoryName,parentId);
        }
        return ServerResponse.createByErrorMessage("无权限操作，需要管理员权限");
    }
    @RequestMapping(value = "set_categoryname.do")
    @ResponseBody
    public ServerResponse setCategoryName(HttpSession session,Integer categoryId,String categoryNme){
        User user=(User)session.getAttribute(Const.CURRENT_USER);
        if(user==null){
            return ServerResponse.createByErrorMessage(ResponseCode.NEEG_LOGIN.getDesc());
        }
        if(iUserservice.checkAdminRole(user).isSuccess()){
          return iCategoryService.updateCategoryName(categoryId,categoryNme);
        }
        return ServerResponse.createByErrorMessage("无权限");
    }
    @RequestMapping(value = "getchildparallel.do")
    @ResponseBody
    public ServerResponse getChildParallelCategory(HttpSession session,@RequestParam(value = "categortid",defaultValue = "0") Integer parentid){
        User user=(User)session.getAttribute(Const.CURRENT_USER);
        if(user==null){
            return ServerResponse.createByErrorMessage(ResponseCode.NEEG_LOGIN.getDesc());
        }
        if(iUserservice.checkAdminRole(user).isSuccess()){
            return iCategoryService.getChildParallelByParentid(parentid);
        }
        return ServerResponse.createByErrorMessage("无权限");
    }
    @RequestMapping(value = "get_deep_category.do")
    @ResponseBody
    public ServerResponse getCategoryAndDeepChildCategory(HttpSession session,@RequestParam(value = "parentid",defaultValue = "0")Integer parentid){

//        User user=(User)session.getAttribute(Const.CURRENT_USER);
//        if(user==null){
//            return ServerResponse.createByErrorMessage(ResponseCode.NEEG_LOGIN.getDesc());
//        }
//        if(iUserservice.checkAdminRole(user).isSuccess()){
//           //查询当前节点的id 和所有子节点的id

            return iCategoryService.selectCategoryAndChildById(parentid);
            // return iCategoryService.getChildParallelByParentid(parentid);
//        }
//        return ServerResponse.createByErrorMessage("无权限");
    }
}
