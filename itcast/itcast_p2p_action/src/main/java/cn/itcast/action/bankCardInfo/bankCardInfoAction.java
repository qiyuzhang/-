package cn.itcast.action.bankCardInfo;

import cn.itcast.action.common.BaseAction;
import cn.itcast.action.filter.GetHttpResponseHeader;
import cn.itcast.cache.BaseCacheService;
import cn.itcast.domain.bankCardInfo.Bank;
import cn.itcast.domain.bankCardInfo.BankCardInfo;
import cn.itcast.domain.city.City;
import cn.itcast.domain.user.UserModel;
import cn.itcast.service.bank.BankService;
import cn.itcast.service.user.UserService;
import cn.itcast.utils.FrontStatusConstants;
import cn.itcast.utils.Response;
import cn.itcast.utils.TokenUtil;
import com.opensymphony.xwork2.ModelDriven;
import org.apache.commons.lang.StringUtils;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@Controller
@Namespace("/bankCardInfo")
@Scope("prototype")
public class bankCardInfoAction extends BaseAction implements ModelDriven<BankCardInfo> {

    private BankCardInfo bankCardInfo = new BankCardInfo();

    @Autowired
    BaseCacheService baseCacheService;
    @Autowired
    BankService bankService;
    @Autowired
    UserService userService;

    //查询用户银行卡信息
    @Action("findBankInfoByUsername")
    public void findBankInfoByUsername() throws IOException {
        //设置响应数据编码
        this.getResponse().setCharacterEncoding("utf-8");

        //请求参数
        String username = this.getRequest().getParameter("username");

        String token = GetHttpResponseHeader.getHeadersInfo(this.getRequest());

        if (StringUtils.isEmpty(token)) {
            //token过期
            this.getResponse().getWriter().write(Response.build().setStatus(FrontStatusConstants.NULL_TOKEN).toJSON());
            return;
        }

        String uname = TokenUtil.getTokenUserName(token);
        Integer uid = null;
        //验证用户名
        if(uname.startsWith(username)){

            Map<String, Object> hmap = baseCacheService.getHmap(token);
            if (hmap == null && hmap.size() == 0) {
                //用户未登录
                this.getResponse().getWriter().write(Response.build().setStatus(FrontStatusConstants.NOT_LOGGED_IN).toJSON());
                return;
            }

            uid = (Integer) hmap.get("id");//用户id
        }

        if(uid != null){
            //根据用户id查询银行卡信息
            BankCardInfo bank = bankService.findBankByUserId(uid);
            if(bank != null){
                this.getResponse().getWriter().write(Response.build().setStatus(FrontStatusConstants.SUCCESS).setData(bank).toJSON());
            }
            else{
                this.getResponse().getWriter().write(Response.build().setStatus(FrontStatusConstants.BREAK_DOWN).toJSON());
            }
        }else{
            this.getResponse().getWriter().write(Response.build().setStatus(FrontStatusConstants.NOT_LOGGED_IN).toJSON());
        }

    }

    //查询所有银行信息
    @Action("findAllBanks")
    public void findAllBanks() throws IOException {
        //设置响应数据编码
        this.getResponse().setCharacterEncoding("utf-8");

        String token = GetHttpResponseHeader.getHeadersInfo(this.getRequest());

        if (StringUtils.isEmpty(token)) {
            //token过期
            this.getResponse().getWriter().write(Response.build().setStatus(FrontStatusConstants.NULL_TOKEN).toJSON());
            return;
        }
        Map<String, Object> hmap = baseCacheService.getHmap(token);
        if (hmap == null && hmap.size() == 0) {
            //用户未登录
            this.getResponse().getWriter().write(Response.build().setStatus(FrontStatusConstants.NOT_LOGGED_IN).toJSON());
            return;
        }

        List<Bank> list = bankService.findAllBanks();
        if(list != null){
            this.getResponse().getWriter().write(Response.build().setStatus(FrontStatusConstants.SUCCESS).setData(list).toJSON());
        }
    }

    //查询所有省份
    @Action("findProvince")
    public void findProvince() throws IOException {
        //设置响应数据编码
        this.getResponse().setCharacterEncoding("utf-8");

        String token = GetHttpResponseHeader.getHeadersInfo(this.getRequest());

        if (StringUtils.isEmpty(token)) {
            //token过期
            this.getResponse().getWriter().write(Response.build().setStatus(FrontStatusConstants.NULL_TOKEN).toJSON());
            return;
        }
        Map<String, Object> hmap = baseCacheService.getHmap(token);
        if (hmap == null && hmap.size() == 0) {
            //用户未登录
            this.getResponse().getWriter().write(Response.build().setStatus(FrontStatusConstants.NOT_LOGGED_IN).toJSON());
            return;
        }

        List<City> list = bankService.findProvinces();
        if(list != null){
            this.getResponse().getWriter().write(Response.build().setStatus(FrontStatusConstants.SUCCESS).setData(list).toJSON());
        }
    }

    @Action("findUserInfo")
    public void findUserInfo() throws IOException {
        //设置响应数据编码
        this.getResponse().setCharacterEncoding("utf-8");

        //请求参数
        String username = this.getRequest().getParameter("username");

        String token = GetHttpResponseHeader.getHeadersInfo(this.getRequest());

        if (StringUtils.isEmpty(token)) {
            //token过期
            this.getResponse().getWriter().write(Response.build().setStatus(FrontStatusConstants.NULL_TOKEN).toJSON());
            return;
        }

        String uname = TokenUtil.getTokenUserName(token);
        Integer uid = null;
        //验证用户名
        if(uname.startsWith(username)){

            Map<String, Object> hmap = baseCacheService.getHmap(token);
            if (hmap == null && hmap.size() == 0) {
                //用户未登录
                this.getResponse().getWriter().write(Response.build().setStatus(FrontStatusConstants.NOT_LOGGED_IN).toJSON());
                return;
            }

            uid = (Integer) hmap.get("id");//用户id
        }

        if(uid != null){
            UserModel userModel = userService.findById(uid);
            this.getResponse().getWriter().write(Response.build().setStatus(FrontStatusConstants.SUCCESS).setData(userModel).toJSON());

        }

    }


    @Action("findCity")
    public void findCity() throws IOException {
        //设置响应数据编码
        this.getResponse().setCharacterEncoding("utf-8");

        String cityAreaNum = this.getRequest().getParameter("cityAreaNum");

        String token = GetHttpResponseHeader.getHeadersInfo(this.getRequest());

        if (StringUtils.isEmpty(token)) {
            //token过期
            this.getResponse().getWriter().write(Response.build().setStatus(FrontStatusConstants.NULL_TOKEN).toJSON());
            return;
        }
        Map<String, Object> hmap = baseCacheService.getHmap(token);
        if (hmap == null && hmap.size() == 0) {
            //用户未登录
            this.getResponse().getWriter().write(Response.build().setStatus(FrontStatusConstants.NOT_LOGGED_IN).toJSON());
            return;
        }

        List<City> list = bankService.findCitys(cityAreaNum);
        this.getResponse().getWriter().write(Response.build().setStatus(FrontStatusConstants.SUCCESS).setData(list).toJSON());

    }

    //完成绑定银行卡
    @Action("addBankCardInfo")
    public void addBankCardInfo() throws IOException {
        //设置响应数据编码
        this.getResponse().setCharacterEncoding("utf-8");

        String token = GetHttpResponseHeader.getHeadersInfo(this.getRequest());

        if (StringUtils.isEmpty(token)) {
            //token过期
            this.getResponse().getWriter().write(Response.build().setStatus(FrontStatusConstants.NULL_TOKEN).toJSON());
            return;
        }
        Map<String, Object> hmap = baseCacheService.getHmap(token);
        if (hmap == null && hmap.size() == 0) {
            //用户未登录
            this.getResponse().getWriter().write(Response.build().setStatus(FrontStatusConstants.NOT_LOGGED_IN).toJSON());
            return;
        }

        Integer uid = (Integer) hmap.get("id");
        BankCardInfo bci = bankService.findBankByUserId(uid);
        System.out.println("bankCardInfo = " + bci);
        if(bci != null){
            //用户已开通账户
            this.getResponse().getWriter().write(Response.build().setStatus(FrontStatusConstants.CARDINFO_ALREADY_EXIST).toJSON());
            return;
        }else{
            //绑定用户id
            bankCardInfo.setUserId(uid);
            bankService.addBank(bankCardInfo);
            this.getResponse().getWriter().write(Response.build().setStatus(FrontStatusConstants.SUCCESS).toJSON());
            return;
        }

    }

    @Override
    public BankCardInfo getModel() {
        return this.bankCardInfo;
    }
}
