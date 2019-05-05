package com.mmall.service.impl;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.mmall.common.Const;
import com.mmall.common.ResponseCode;
import com.mmall.common.ServerResponse;
import com.mmall.dao.CategoryMapper;
import com.mmall.pojo.Category;
import com.mmall.pojo.User;
import com.mmall.service.ICategoryService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.servlet.http.HttpSession;
import java.util.Collection;
import java.util.List;
import java.util.Set;

@Service("iCategoryService")
public class CategoryServiceImpl implements ICategoryService {
    @Autowired
    private CategoryMapper categoryMapper;
    private Logger log= LoggerFactory.getLogger(CategoryServiceImpl.class);
    public ServerResponse addCategory(String categoryName, Integer parentId){
        if(parentId==null|| StringUtils.isBlank(categoryName)){
            return ServerResponse.createByErrorMessage("品类参数存在空值");
        }
        Category category=new Category();
        category.setName(categoryName);
        category.setParentId(parentId);
        category.setStatus(true);//刚建完 true
        int rowCount=categoryMapper.insert(category);
        if(rowCount>0){
            return ServerResponse.createBySuccessMessage("添加品类成功");
        }
        return ServerResponse.createByErrorMessage("添加失败");
    }
    public ServerResponse updateCategoryName(Integer categoryId,String categoryName){
        if(categoryId==null||StringUtils.isBlank(categoryName)){
            return ServerResponse.createByErrorMessage("参数错误");
        }
        Category category=new Category();
        category.setId(categoryId);
        category.setName(categoryName);
        int countRow=categoryMapper.updateByPrimaryKeySelective(category);
        if(countRow>0){
            return  ServerResponse.createBySuccess("更新品类成功");
        }
        return ServerResponse.createByErrorMessage("更新品类失败");
    }
    public ServerResponse<List<Category>> getChildParallelByParentid(Integer parentid){
        List<Category> categoryList=categoryMapper.selectCategoryChildByParentId(parentid);
        if(CollectionUtils.isEmpty(categoryList)){
           log.info("未找到当前分类子分类");
        }
        return  ServerResponse.createBySuccess(categoryList);
    }

    /**
     * 根据某一个节点id 找到其所有子节点id
     * @param categoryid
     * @return
     */
    public ServerResponse<List<Integer>>selectCategoryAndChildById(Integer categoryid){
        Set<Category> categorySet= Sets.newHashSet();
        //调用find 填充所有子节点到set
        findChidCategory(categorySet,categoryid);
        //遍历set 获取id 生成一个子id list
        List<Integer> categoryList= Lists.newArrayList();
        if(categoryid!=null){
            for(Category categoryItem:categorySet){
                categoryList.add(categoryItem.getId());
            }
        }

        return  ServerResponse.createBySuccess(categoryList);
    }
    //递归算法 算出子节点 填充set
    private Set<Category> findChidCategory(Set<Category> categorySet,Integer id){
        Category category=categoryMapper.selectByPrimaryKey(id);
        if(category!=null){
            categorySet.add(category);
        }
        List<Category> categoryList=categoryMapper.selectCategoryChildByParentId(id);
        for(Category categoryItem:categoryList){//for循环控制递归
            findChidCategory(categorySet,categoryItem.getId());
        }
        return categorySet;
    }

}


