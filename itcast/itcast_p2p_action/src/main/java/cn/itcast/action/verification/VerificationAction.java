package cn.itcast.action.verification;

import cn.itcast.action.common.BaseAction;
import cn.itcast.action.filter.GetHttpResponseHeader;
import cn.itcast.cache.BaseCacheService;
import cn.itcast.domain.user.UserModel;
import cn.itcast.service.email.EmailService;
import cn.itcast.service.msm.MSMService;
import cn.itcast.service.user.UserService;
import cn.itcast.utils.*;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import java.io.IOException;
import java.util.Map;


@Controller
@Scope("prototype")
@Namespace("/verification")
public class VerificationAction extends BaseAction {

    @Autowired
    BaseCacheService baseCacheService;
    @Autowired
    UserService userService;
    @Autowired
    EmailService emailService;
    @Autowired
    MSMService msmService;

    //发送短信验证码操作
    @Action("sendMessage")
    public void sendMessage() throws IOException {

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


        //获取手机号
        String phone = this.getRequest().getParameter("phone");
        //获取验证码
        String num = RandomStringUtils.randomNumeric(6);//随机得到六位数字
        String content = "P2P短信验证" + num;
//        MSMUtils.SendSms(phone,content);
        System.out.println("向" + phone + "发送验证信息：" + content);
//        msmService.senMSM(phone,content);


        //存入redis
        baseCacheService.set(phone, num);
        baseCacheService.expire(phone, 3 * 60);

        //响应到浏览器
        this.getResponse().getWriter().write(Response.build().setStatus("1").toJSON());

    }

    @Action("addPhone")
    public void addPhone() throws IOException {

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

        //获取请求参数
        String phone = this.getRequest().getParameter("phone");
        String phoneCode = this.getRequest().getParameter("phoneCode");

        //判断验证码是否正确
        String s = baseCacheService.get(phone);

        if (!s.equals(phoneCode)) {
            //验证码不正确
            this.getResponse().getWriter().write(Response.build().setStatus(FrontStatusConstants.INPUT_ERROR_OF_VALIDATE_CARD).toJSON());
        }

        UserModel userModel = userService.findById((Integer) hmap.get("id"));
        if (userModel.getPhoneStatus() == 1) {
            this.getResponse().getWriter().write(Response.build().setStatus(FrontStatusConstants.MOBILE_ALREADY_REGISTER).toJSON());
        }

        userService.updateUserPhone(phone, (Integer) hmap.get("id"));
        this.getResponse().getWriter().write(Response.build().setStatus(FrontStatusConstants.SUCCESS).toJSON());

    }

    @Action("verifiRealName")
    public void verifiRealName() throws IOException {
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

        //得到实名 和身份证号
        String realName = this.getRequest().getParameter("realName");
        String identity = this.getRequest().getParameter("identity");

        //对比

        //service修改 实名状态 身份证号
        userService.updateUserNameStatus(realName, identity, (Integer) hmap.get("id"));

        this.getResponse().getWriter().write(Response.build().setStatus(FrontStatusConstants.SUCCESS).toJSON());
    }

    //发送验证邮件
    @Action("auth")
    public void auth() throws Exception {
        //接受参数
        String userId = this.getRequest().getParameter("userId");
        String username = this.getRequest().getParameter("username");
        String email = this.getRequest().getParameter("email");

        String title = "P2P邮箱验证激活";
        String enc = SecretUtil.encrypt(userId);
        String content = EmailUtils.getMailCapacity(email, enc, username);
        emailService.sendEmail(email, title, content);

        System.out.println("================send success=================");
        UserModel userModel = userService.findById(Integer.parseInt(userId));
        if (userModel.getEmail() == null && userModel.getEmailStatus() == 0) {
            //绑定邮箱
            userService.addEmail(Integer.parseInt(userId), email);
        }
        //响应到浏览器
        this.getResponse().getWriter().write(Response.build().setStatus("1").toJSON());
    }

    //绑定邮箱操作
    @Action("emailactivation")
    public void emailactivation() {
        System.out.println("绑定邮箱=========================================");
        this.getResponse().setContentType("text/html;charset=utf-8");
        String us = this.getRequest().getParameter("us");
        try {
            String userID = SecretUtil.decode(us);

            //校验

            //修改emailStatus 状态
            userService.updateEmailStatus(userID);


            this.getResponse().getWriter().write("邮箱认证成功");
        } catch (Exception e) {
            e.printStackTrace();
            try {
                this.getResponse().getWriter().write("邮箱认证失败");
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    @Action("validateSMS")
    public void validateSMS() throws IOException {
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

        //获取请求参数
        String phone = this.getRequest().getParameter("phone");
        String phoneCode = this.getRequest().getParameter("code");

        //判断验证码是否正确
        String s = baseCacheService.get(phone);

        if (s.equals(phoneCode)) {
            System.out.println("s = " + s);
            this.getResponse().getWriter().write(Response.build().setStatus(FrontStatusConstants.SUCCESS).toJSON());
        }else {
            //验证码不正确
            this.getResponse().getWriter().write(Response.build().setStatus(FrontStatusConstants.INPUT_ERROR_OF_VALIDATE_CARD).toJSON());
        }

    }

}
