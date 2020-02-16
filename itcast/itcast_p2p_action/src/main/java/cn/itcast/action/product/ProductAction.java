package cn.itcast.action.product;

import cn.itcast.action.common.BaseAction;
import cn.itcast.domain.product.Product;
import cn.itcast.domain.product.ProductEarningRate;
import cn.itcast.service.product.ProductService;
import cn.itcast.utils.FrontStatusConstants;
import cn.itcast.utils.JsonMapper;
import cn.itcast.utils.Response;
import com.alibaba.fastjson.JSONArray;
import com.opensymphony.xwork2.ModelDriven;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Controller
@Namespace("/product")
@Scope("prototype")
public class ProductAction extends BaseAction implements ModelDriven<Product> {

    private Product product = new Product();

    @Autowired
    ProductService productService;

    //查找所有理财产品
    @Action("findAllProduct")
    public void findAllProduct(){
        //设置响应数据编码
        this.getResponse().setCharacterEncoding("utf-8");
        System.out.println("=============================================");
        List<Product> list = productService.findAll();
        System.out.println("list = " + list);
        try {
            this.getResponse().getWriter().write(Response.build().setStatus(FrontStatusConstants.SUCCESS).setData(list).toJSON());
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    //根据id查找理财产品
    @Action("findProductById")
    public void findProductById(){
        //设置响应数据编码
        this.getResponse().setCharacterEncoding("utf-8");
        System.out.println("----------------------------------------");
        String proId = this.getRequest().getParameter("proId");
        System.out.println("proId = " + proId);
        Product product = productService.findById(Long.parseLong(proId));
        System.out.println("product = " + product);
        try {
            this.getResponse().getWriter().write(Response.build().setStatus(FrontStatusConstants.SUCCESS).setData(product).toJSON());
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
    //根据id修改理财产品
    @Action("modifyProduct")
    public void modifyProduct() throws IOException {
        //设置响应数据编码
        this.getResponse().setCharacterEncoding("utf-8");
        System.out.println("-------------------***************---------------------");

        String parameter = this.getRequest().getParameter("proEarningRates");

        Map map = new JsonMapper().fromJson(parameter, Map.class);

        List<ProductEarningRate> pers = new ArrayList<>();
        for(Object key :map.keySet()){
            //key是月份 value是利率值
            ProductEarningRate productEarningRate = new ProductEarningRate();
            productEarningRate.setMonth(Integer.parseInt(key.toString()));
            productEarningRate.setIncomeRate(Double.parseDouble(map.get(key).toString()));
            productEarningRate.setProductId((int) product.getProId());//将封装理财产品的id添加到利率信息中
            pers.add(productEarningRate);
        }
        product.setProEarningRate(pers);//封住利率信息到理财产品
        productService.updateProduct(product);


        this.getResponse().getWriter().write(Response.build().setStatus(FrontStatusConstants.SUCCESS).toJSON());

    }

    //根据id查询利率
    @Action("findRates")
    public void findRates(){
        //设置响应数据编码
        this.getResponse().setCharacterEncoding("utf-8");
        System.out.println("----------------------------------------");

        String proId = this.getRequest().getParameter("proId");
        List<ProductEarningRate> list = productService.findRatesByProId(proId);

        try {
            this.getResponse().getWriter().write(Response.build().setStatus(FrontStatusConstants.SUCCESS).setData(list).toJSON());
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public Product getModel() {
        return product;
    }
}
