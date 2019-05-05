package com.mmall.controller.portal;

import com.github.pagehelper.PageInfo;
import com.mmall.common.ServerResponse;
import com.mmall.service.IProductService;
import com.mmall.vo.ProductDetailVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/product/")
public class ProductController {
    @Autowired
    private IProductService productService;
    private Logger logger= LoggerFactory.getLogger(ProductController.class);
    /**
     * 前台获取商品详细信息 根据关键字 id搜索
     * @param productid
     * @return
     */
    @RequestMapping("detail.do")
    @ResponseBody
    public ServerResponse<ProductDetailVo> getDetail(Integer productid){
      return productService.getProductDetail(productid);
    }
    @RequestMapping("search.do")
    @ResponseBody
    public ServerResponse<PageInfo> list(@RequestParam(value="keyword",required = false)String keyWord,@RequestParam(value="categoryid",required = false)Integer categoryid,
                                         @RequestParam(value="pageNum",defaultValue = "1") int pageNUm,@RequestParam(value="pageSize",defaultValue = "10") int pageSize,
                                         @RequestParam(value="orderby",defaultValue = "")String orderBy){
        System.out.println("*********"+keyWord+"^^^^^^"+categoryid);
        logger.info("!!!!!!!!!!!!!!!!!!"+keyWord+"<<<<<<<<<<<<"+categoryid);
     return productService.getPeoductByKeywordAndId(keyWord,categoryid,pageNUm,pageSize,orderBy);
    }
}
