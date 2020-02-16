package cn.itcast.action.account;

import cn.itcast.action.common.BaseAction;
import cn.itcast.action.filter.GetHttpResponseHeader;
import cn.itcast.cache.BaseCacheService;
import cn.itcast.domain.user.UserAccountModel;
import cn.itcast.service.user.UserService;
import cn.itcast.utils.FrontStatusConstants;
import cn.itcast.utils.Response;
import com.alibaba.fastjson.JSONObject;
import com.opensymphony.xwork2.ModelDriven;
import org.apache.commons.lang.StringUtils;
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
@Namespace("/account")
@Scope("prototype")
public class AccountAction extends BaseAction implements ModelDriven<UserAccountModel> {

    private UserAccountModel userAccountModel = new UserAccountModel();

    @Autowired
    UserService userService;
    @Autowired
    private BaseCacheService baseCacheService;

    //获取用户账户信息，在用户中心显示
    @Action("accountHomepage")
    public void accountHomepage() throws IOException {
        //得到token
        String token = GetHttpResponseHeader.getHeadersInfo(this.getRequest());
        System.out.println("token = " + token);
        if(StringUtils.isEmpty(token)){
            this.getResponse().getWriter().write(Response.build().setStatus(FrontStatusConstants.NULL_TOKEN).toJSON());
            return;
        }

        //根据token 在redis中获取用户信息 id
        Map<String, Object> hmap = baseCacheService.getHmap(token);
        System.out.println("hmap = " + hmap);
        if(hmap == null && hmap.size()==0){
            this.getResponse().getWriter().write(Response.build().setStatus(FrontStatusConstants.NOT_LOGGED_IN).toJSON());
            return;
        }
        Integer id = (Integer) hmap.get("id");

        //调用service 根据用户id查询用户账户信息
        UserAccountModel userAccountModel = userService.findAccountByUserId(id);

        //响应到浏览器
        List<JSONObject> list = new ArrayList<>();

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("u_total",userAccountModel.getTotal());//账户总额
        jsonObject.put("u_balance",userAccountModel.getBalance());//余额
        jsonObject.put("u_interest_a",userAccountModel.getInterestA());//收益

        list.add(jsonObject);

        System.out.println("account ------------------- list = " + list);
        this.getResponse().getWriter().write(Response.build().setStatus("1").setData(list).toJSON());
    }

    @Override
    public UserAccountModel getModel() {
        return userAccountModel;
    }
}
