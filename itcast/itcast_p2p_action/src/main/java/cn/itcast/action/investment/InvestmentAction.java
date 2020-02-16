package cn.itcast.action.investment;

import cn.itcast.action.common.BaseAction;
import cn.itcast.action.filter.GetHttpResponseHeader;
import cn.itcast.cache.BaseCacheService;
import cn.itcast.domain.user.UserAccountModel;
import cn.itcast.service.user.UserService;
import cn.itcast.utils.FrontStatusConstants;
import cn.itcast.utils.Response;
import org.apache.commons.lang.StringUtils;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import java.io.IOException;
import java.util.Map;


@Controller
@Namespace("/investment")
@Scope("prototype")
public class InvestmentAction extends BaseAction{

    @Autowired
    BaseCacheService baseCacheService;
    @Autowired
    UserService userService;

    //判断账户余额是否可以进行投资
    @Action("checkAccount")
    public void checkAccount() throws IOException {
        double account = Double.parseDouble(this.getRequest().getParameter("account"));

        String token = GetHttpResponseHeader.getHeadersInfo(this.getRequest());
        if (StringUtils.isEmpty(token)) {
            //token过期
            this.getResponse().getWriter().write(Response.build().setStatus(FrontStatusConstants.NULL_TOKEN).toJSON());
            return;
        }
        Map<String, Object> hmap = baseCacheService.getHmap(token);
        if (hmap == null && hmap.size() == 0) {
            //用户未登录
            this.getResponse().getWriter().write(Response.build().setStatus("15").toJSON());
            return;
        }

        Integer id = (Integer) hmap.get("id");//用户id

        UserAccountModel userAccountModel = userService.findAccountByUserId(id);
        double balance = userAccountModel.getBalance();//余额

        if(balance>=account){
            //可以购买
            this.getResponse().getWriter().write(Response.build().setStatus("1").toJSON());
            return;
        }
        else{
            //余额不足
            this.getResponse().getWriter().write(Response.build().setStatus("17").toJSON());
            return;
        }
    }
}
