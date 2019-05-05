package com.mmall.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import com.mmall.common.Const;
import com.mmall.common.ResponseCode;
import com.mmall.common.ServerResponse;
import com.mmall.dao.CategoryMapper;
import com.mmall.dao.ProductMapper;
import com.mmall.pojo.Category;
import com.mmall.pojo.Product;
import com.mmall.service.ICategoryService;
import com.mmall.service.IProductService;
import com.mmall.util.DataTimeUtil;
import com.mmall.util.PropertiesUtil;
import com.mmall.vo.ProductDetailVo;
import com.mmall.vo.productListVo;
import net.sf.jsqlparser.schema.Server;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service("iProductService")
public class ProductServiceImpl implements IProductService {
    @Autowired
    private ProductMapper productMapper;
    @Autowired
    private CategoryMapper categoryMapper;
    @Autowired
    private ICategoryService iCategoryService;

    /**
     * 新增或更新产品信息
     * @param product
     * @return
     */
    public ServerResponse saveOrUpdateProduct(Product product){
        if(product!=null){
            if(StringUtils.isNotBlank(product.getSubImages())){
                String[] subImageArray=product.getSubImages().split(",");
                if(subImageArray.length>0){
                    product.setMainImage(subImageArray[0]);
                }
            }
            if(product.getId()!=null){
               int rowCount=productMapper.updateByPrimaryKey(product);
               if(rowCount>0){
                   return ServerResponse.createBySuccess("更新产品成功");
               }
               return ServerResponse.createByErrorMessage("更新产品失败");
            }
            else{
                int rowCount=productMapper.insert(product);
                if(rowCount>0){
                    return ServerResponse.createBySuccess("产品更新成功");
                }
                return ServerResponse.createByErrorMessage("更新失败");
            }
        }
        return ServerResponse.createByErrorMessage("参数不正确");
    }
    public ServerResponse<String> setSaleStatus(Integer prodecuId,Integer status){
        if(prodecuId==null||status==null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUEMENT.getCode(),ResponseCode.ILLEGAL_ARGUEMENT.getDesc());
        }
        Product product=new Product();
        product.setId(prodecuId);
        product.setStatus(status);
        int rowCount=productMapper.updateByPrimaryKeySelective(product);
        if(rowCount>0){
            return ServerResponse.createBySuccess("修改商品销售状态成功");
        }
        return ServerResponse.createByErrorMessage("修改商品销售状态失败");
    }
    public ServerResponse<ProductDetailVo> manageProductDetail(Integer productId){
        if(productId==null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUEMENT.getCode(),ResponseCode.ILLEGAL_ARGUEMENT.getDesc());

        }
        Product product=productMapper.selectByPrimaryKey(productId);
        if(product==null){
            return ServerResponse.createByErrorMessage("商品已下架");
        }
        ProductDetailVo productDetailVo=assembleProductDetailVo(product);
        return ServerResponse.createBySuccess(productDetailVo);
    }
    private ProductDetailVo assembleProductDetailVo(Product product){
       ProductDetailVo productDetailVo=new ProductDetailVo();
       productDetailVo.setId(product.getId());
       productDetailVo.setStatus(product.getStatus());
       productDetailVo.setSubImage(product.getSubImages());
       productDetailVo.setSubtitle(product.getSubtitle());
       productDetailVo.setCategoryId(product.getCategoryId());
       productDetailVo.setName(product.getName());
       productDetailVo.setDetail(product.getDetail());
       productDetailVo.setStock(product.getStock());
       productDetailVo.setMianInamge(product.getMainImage());
       productDetailVo.setImageHost(PropertiesUtil.getProperty("ftp.server.http.prefix","http://img.happymmall.com/"));
       Category category= categoryMapper.selectByPrimaryKey(product.getCategoryId());
       if(category==null){
           productDetailVo.setParentCategoryId(0);
       }
       else{
           productDetailVo.setParentCategoryId(category.getParentId());
       }
       productDetailVo.setCreateTime(DataTimeUtil.dateToStr(product.getCreateTime()));
       productDetailVo.setUpdateTime(DataTimeUtil.dateToStr(product.getUpdateTime()));

       return productDetailVo;
    }

    /**
     * 商品信息分页
     * @param pageNUm
     * @param pageSize
     * @return
     */
    public ServerResponse getProductList(int pageNUm,int pageSize){
        //startPage start  填充自己的sql逻辑 pagehelper收尾
        PageHelper.startPage(pageNUm,pageSize);
        List<Product> productList=productMapper.selectList();
        List<productListVo> productListVoList= Lists.newArrayList();
        for(Product productItem:productList){
            productListVo productListVo=assembleProductListVo(productItem);
            productListVoList.add(productListVo);
        }
        PageInfo pageResult=new PageInfo(productList);
        pageResult.setList(productListVoList);
        return ServerResponse.createBySuccess(pageResult);
    }
    private productListVo assembleProductListVo(Product product){
     productListVo productListVo=new productListVo();
     productListVo.setId(product.getId());
     productListVo.setName(product.getName());
     productListVo.setCategoryId(product.getCategoryId());
     productListVo.setImageHost(PropertiesUtil.getProperty("ftp.server.http.prefix","http://img.happymmall.com/"));
     productListVo.setMianImage(product.getMainImage());
     productListVo.setPrice(product.getPrice());
     productListVo.setSubtitle(product.getSubtitle());
     productListVo.setStatus(product.getStatus());
     return productListVo;
    }
    public ServerResponse<PageInfo> searchProduct(String productName,Integer productId,int pageNum,int pageSize)
    {
        PageHelper.startPage(pageNum,pageSize);
        if(StringUtils.isNotBlank(productName)){
            productName=new StringBuilder().append("%").append(productName).append("%").toString();
        }
        List<productListVo> productListVoList= Lists.newArrayList();

        List<Product> productList=productMapper.selectByNameAndId(productName,productId);
        for(Product productItem:productList){
            productListVo productListVo=assembleProductListVo(productItem);
            productListVoList.add(productListVo);
        }
        PageInfo pageResult=new PageInfo(productList);
        pageResult.setList(productListVoList);
        return ServerResponse.createBySuccess(pageResult);

    }

    /**
     * 前台商品信息详情获取
     * @param productId
     * @return
     */
    public ServerResponse<ProductDetailVo> getProductDetail(Integer productId){
        if(productId==null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUEMENT.getCode(),ResponseCode.ILLEGAL_ARGUEMENT.getDesc());
        }
        Product product=productMapper.selectByPrimaryKey(productId);
        if(product==null){
            return ServerResponse.createByErrorMessage("商品已经已经被删除");
        }
        if(product.getStatus()!= Const.ProductStatusEnum.ON_SALE.getCode()){
            return ServerResponse.createByErrorMessage("商品已经下架");
        }
        ProductDetailVo productDetailVo=assembleProductDetailVo(product);
        return ServerResponse.createBySuccess(productDetailVo);
    }
    public ServerResponse<PageInfo> getPeoductByKeywordAndId(String keyword,Integer categortid,int pagenum,int pagesize,String orderby){
        if(StringUtils.isBlank(keyword)&&categortid==null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUEMENT.getCode(),ResponseCode.ILLEGAL_ARGUEMENT.getDesc());
        }
        //如果传的是父类ID 会获取所有子类
        List<Integer> categoryIdList=new ArrayList<>();
        if(categortid!=null){
            Category category=categoryMapper.selectByPrimaryKey(categortid);
            if(category==null&&StringUtils.isBlank(keyword)){
                //这种情况 没有结果 不报错 只是未命中数据
                //返回空结果集
                PageHelper.startPage(pagenum,pagesize);
                List<productListVo> productListVoList=Lists.newArrayList();
                PageInfo pageInfo=new PageInfo(productListVoList);
                return ServerResponse.createBySuccess(pageInfo);
            }
            categoryIdList=iCategoryService.selectCategoryAndChildById(category.getId()).getData();

        }
        if(StringUtils.isNotBlank(keyword)){
            keyword=new StringBuilder().append("%").append(keyword).append("%").toString();
        }
        //排序chuli
        PageHelper.startPage(pagenum,pagesize);
        if(StringUtils.isNotBlank(orderby)){
          if(Const.ProductListOrderby.PRICE_ASC_DESC.contains(orderby));
          String[] orderByArr=orderby.split("_");
          PageHelper.orderBy(orderByArr[0]+" "+orderByArr[1]);
        }
        List<Product> productList=productMapper.selectByNameAndCategoryIds(StringUtils.isBlank(keyword)?null:keyword,categoryIdList.size()==0?null:categoryIdList);
        List<productListVo> productListVoList=Lists.newArrayList();
        for(Product product:productList){
            productListVo productListVo=assembleProductListVo(product);
            productListVoList.add(productListVo);
        }
        PageInfo pageInfo=new PageInfo(productList);
        pageInfo.setList(productListVoList);
        return ServerResponse.createBySuccess(pageInfo);
    }
}
