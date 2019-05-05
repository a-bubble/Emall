package com.mmall.controller.backend;

import com.google.common.collect.Maps;
import com.mmall.common.Const;
import com.mmall.common.ResponseCode;
import com.mmall.common.ServerResponse;
import com.mmall.pojo.Product;
import com.mmall.pojo.User;
import com.mmall.service.IFileService;
import com.mmall.service.IProductService;
import com.mmall.service.IUserservice;
import com.mmall.util.PropertiesUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.Map;

@Controller
@RequestMapping("/manage/product")
public class ProductManageController {
    @Autowired
    private IUserservice iUserservice;
    @Autowired
    private IProductService iProductService;
    @Autowired
    private IFileService iFileService;

    /**
     * 保存商品信息
     * @param session
     * @param product
     * @return
     */
    @RequestMapping(value = "save.do")
    @ResponseBody
    public ServerResponse peoductSave(HttpSession session, Product product){
       User user=(User)session.getAttribute(Const.CURRENT_USER);
       if(user==null){
           return ServerResponse.createByErrorCodeMessage(ResponseCode.NEEG_LOGIN.getCode(),"用户未登录");
       }
       if(iUserservice.checkAdminRole(user).isSuccess()){
         //增加产品
           return iProductService.saveOrUpdateProduct(product);
       }
        return ServerResponse.createByErrorMessage("无权限，请以管理员身份登录");
    }
    @RequestMapping(value = "setStatus.do")
    @ResponseBody
    public ServerResponse setSaleStatus(HttpSession session,Integer productId, Integer status){
        User user=(User)session.getAttribute(Const.CURRENT_USER);
        if(user==null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEEG_LOGIN.getCode(),"用户未登录");
        }
        if(iUserservice.checkAdminRole(user).isSuccess()){
            //增加产品
            return iProductService.setSaleStatus(productId,status);
        }
        return ServerResponse.createByErrorMessage("无权限，请以管理员身份登录");
    }

    /**
     * 获取产产品详情
     * @param session
     * @param productId
     * @return
     */
    @RequestMapping("getdetail.do")
    @ResponseBody
    public ServerResponse getDetail(HttpSession session,Integer productId){
        User user=(User)session.getAttribute(Const.CURRENT_USER);
        if(user==null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEEG_LOGIN.getCode(),"用户未登录");
        }
        if(iUserservice.checkAdminRole(user).isSuccess()){
            //增加产品
            return iProductService.manageProductDetail(productId);
        }
        return ServerResponse.createByErrorMessage("无权限，请以管理员身份登录");
    }

    /**
     * 获取商品list 分页信息
     * @param session
     * @param pageNUm
     * @param pageSize
     * @return
     */
    @RequestMapping("getlist.do")
    @ResponseBody
    public ServerResponse getList(HttpSession session, @RequestParam(value ="pageNUM",defaultValue = "1") int pageNUm, @RequestParam(value="pageSize",defaultValue = "10") int pageSize){
        User user=(User)session.getAttribute(Const.CURRENT_USER);
        if(user==null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEEG_LOGIN.getCode(),"用户未登录");
        }
        if(iUserservice.checkAdminRole(user).isSuccess()){
            //增加产品
            return iProductService.getProductList(pageNUm,pageSize);
        }
        return ServerResponse.createByErrorMessage("无权限，请以管理员身份登录");
    }

    /**
     * 商品搜索功能
     * @param session
     * @param productId
     * @param productName
     * @param pageNUm
     * @param pageSize
     * @return
     */
    @RequestMapping("search.do")
    @ResponseBody
    public ServerResponse searchProduct(HttpSession session,Integer productId,String productName, @RequestParam(value ="pageNUM",defaultValue = "1") int pageNUm, @RequestParam(value="pageSize",defaultValue = "10") int pageSize){
        User user=(User)session.getAttribute(Const.CURRENT_USER);
        if(user==null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEEG_LOGIN.getCode(),"用户未登录");
        }
        if(iUserservice.checkAdminRole(user).isSuccess()){
            //
            return iProductService.searchProduct(productName,productId,pageNUm,pageSize);
        }
        return ServerResponse.createByErrorMessage("无权限，请以管理员身份登录");
    }

    /**
     * 把产品图片上传至服务器
     *upload 文件夹 创建到web-app 与webif同级的
     * @param file
     * @param request
     * @return
     */
    @RequestMapping("upload.do")
    @ResponseBody
    public ServerResponse upload(@RequestParam(value="upload_file")MultipartFile file,HttpSession session,  HttpServletRequest request){
        User user=(User)session.getAttribute(Const.CURRENT_USER);
        if(user==null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEEG_LOGIN.getCode(),"用户未登录");
        }
        if(iUserservice.checkAdminRole(user).isSuccess()){
            String path=request.getSession().getServletContext().getRealPath("upload");
            String targetFile=iFileService.upload(file,path);
            String url= PropertiesUtil.getProperty("ftp.server.http.prefix")+targetFile;
            Map fileMap= Maps.newHashMap();
            fileMap.put("uri",targetFile);
            fileMap.put("url",url);
            return ServerResponse.createBySuccess(fileMap);
        }
        return ServerResponse.createByErrorMessage("无权限，请以管理员身份登录");

    }

    /**
     * 富文本上传文件 针对simeditor插件
     * @param session
     * @param file
     * @param request
     * @return
     */
    @RequestMapping("uploadRichTextFile.do")
    @ResponseBody
    public Map uploadRichTextFile(@RequestParam(value="upload_file") MultipartFile file,HttpSession session,  HttpServletRequest request, HttpServletResponse response){
        Map resultMap=Maps.newHashMap();
        User user=(User)session.getAttribute(Const.CURRENT_USER);
        if(user==null){
            resultMap.put("success",false);
            resultMap.put("msg","请登录");
            return resultMap;
        }
        if(iUserservice.checkAdminRole(user).isSuccess()){
            String path=request.getSession().getServletContext().getRealPath("upload");//upload为文件目录
           String targetFile=iFileService.upload(file,path);

           if(StringUtils.isBlank(targetFile)){
               resultMap.put("success",false);
               resultMap.put("msg","上传失败");
               return resultMap;
           }
            String url= PropertiesUtil.getProperty("ftp.server.http.prefix")+targetFile;
            resultMap.put("success",true);
            resultMap.put("msg","上传成功");
            resultMap.put("file_path",url);
            response.addHeader("Access-Control-Allow-Headers","X-File-Name");
            return resultMap;
        }
        resultMap.put("success",false);
        resultMap.put("msg","请以管理员身份登录");
        return resultMap;


    }
}
