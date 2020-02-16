package cn.itcast.action.product;

import cn.itcast.action.common.BaseAction;
import cn.itcast.domain.product.ProductEarningRate;
import cn.itcast.service.product.ProductService;
import cn.itcast.utils.Response;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Controller
@Namespace("/productRate")
@Scope("prototype")
public class ProductRateAction extends BaseAction{

    @Autowired
    ProductService productService;

    //根据id查询理财产品利率
    @Action("yearInterest")
    public void getyearInterest() throws IOException {

        //获取理财产品id
        String pid = this.getRequest().getParameter("pid");

        //根据id查询利率
        List<ProductEarningRate> pers =  productService.findByPid(pid);

        List<Map<String,Object>> list = new ArrayList<>();
        for (ProductEarningRate rate : pers) {
            Map<String,Object> map = new HashMap<>();
            map.put("incomeRate",rate.getIncomeRate());
            map.put("endMonth",rate.getMonth());
            list.add(map);
        }

        this.getResponse().getWriter().write(Response.build().setStatus("1").setData(list).toJSON());
    }
}
