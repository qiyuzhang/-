package cn.itcast.action.user;

import cn.itcast.action.common.BaseAction;
import cn.itcast.action.filter.GetHttpResponseHeader;
import cn.itcast.cache.BaseCacheService;
import cn.itcast.domain.user.UserAccountModel;
import cn.itcast.domain.user.UserModel;
import cn.itcast.service.user.UserService;
import cn.itcast.utils.*;
import com.alibaba.fastjson.JSONObject;
import com.opensymphony.xwork2.ModelDriven;
import org.apache.commons.lang.StringUtils;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import java.io.IOException;
import java.util.*;

@Controller
@Scope("prototype")
@Namespace("/user")
public class UserAction extends BaseAction implements ModelDriven<UserModel> {

    @Autowired
    private BaseCacheService baseCacheService;
    @Autowired
    UserService userService;

    private UserModel userModel = new UserModel();

    @Action("uuid")
    public void uuid() {
        //1.随机生成一个UUID
        String uuid = UUID.randomUUID().toString();
        //2.存储到redis
        baseCacheService.set(uuid, uuid);
        baseCacheService.expire(uuid, 3 * 60);//三分钟过期
        //3.响应到浏览器  响应一个UUID和status
        try {
            this.getResponse().getWriter().write(Response.build().setStatus("1").setUuid(uuid).toJSON());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Action("validateCode")//得到图片验证码
    public void validateCode() {
        //得到请求参数tokenUUID
        String tokenUuid = this.getRequest().getParameter("tokenUuid");
        //2.判断它在redis中是否存在
        String uuid = baseCacheService.get(tokenUuid);
        try {
            if (StringUtils.isEmpty(uuid)) {
                //为空,说明可能存在风险
                this.getResponse().getWriter().write(Response.build().setStatus("-999").toJSON());
                return;
            }
            //3.产生图片验证码

            //获取验证码信息
            String rundomStr = ImageUtil.getRundomStr();
            //将信息存储到redis中
            baseCacheService.del(uuid);
            baseCacheService.set(tokenUuid, rundomStr);
            baseCacheService.expire(tokenUuid, 3 * 60);
            //将验证码信息响应到浏览器
            ImageUtil.getImage(rundomStr, this.getResponse().getOutputStream());

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    //校验用户名是否存在
    @Action("validateUserName")
    public void validateUserName() throws IOException {
        //得到参数----用户名
        String username = this.getRequest().getParameter("username");
        //判断是否存在
        UserModel userModel = userService.checkUsername(username);
        if (userModel != null) {
            //用户名被占用
            this.getResponse().getWriter().write(Response.build().setStatus(FrontStatusConstants.ALREADY_EXIST_OF_USERNAME).toJSON());
            return;
        } else {
            //校验通过
            this.getResponse().getWriter().write(Response.build().setStatus(FrontStatusConstants.SUCCESS).toJSON());
            return;
        }
    }

    //校验手机号码是否存在
    @Action("validatePhone")
    public void validatePhone() throws IOException {
        //得到参数----手机号
        String phone = this.getRequest().getParameter("phone");
        //判断是否存在
        UserModel userModel = userService.checkPhone(phone);

        if (userModel != null) {
            //手机号被占用
            System.out.println(" = 手机号占用 =====================================");
            this.getResponse().getWriter().write(Response.build().setStatus(FrontStatusConstants.MOBILE_ALREADY_REGISTER).toJSON());
            return;
        } else {
            //校验通过
            System.out.println(" = 验证通过 =====================================");
            this.getResponse().getWriter().write(Response.build().setStatus(FrontStatusConstants.SUCCESS).toJSON());
            return;
        }
    }

    //验证录入的验证码是否正确
    @Action("codeValidate")
    public void codeValidate() throws IOException {
        String signUuid = this.getRequest().getParameter("signUuid");//uuid
        String signCode = this.getRequest().getParameter("signCode");//输入的验证码

        //redis中获取uuid
        String s = baseCacheService.get(signUuid);

//        if(StringUtils.isEmpty(signCode)){
//            //录入为空
//
//        }
//        if(StringUtils.isEmpty(s)){
//            //验证码为空
//            this.getResponse().getWriter().write(Response.build().setStatus(FrontStatusConstants.NULL_OF_VALIDATE_CARD).toJSON());
//        }

        //验证是否正确
        if (signCode.equalsIgnoreCase(s)) {
            this.getResponse().getWriter().write(Response.build().setStatus(FrontStatusConstants.NULL_OF_VALIDATE_CARD).toJSON());
            return;
        } else {
            this.getResponse().getWriter().write(Response.build().setStatus(FrontStatusConstants.INPUT_ERROR_OF_VALIDATE_CARD).toJSON());
            return;
        }
    }

    //实现注册
    @Action("signup")
    public void regist() throws IOException {
        //封装请求参数   使用模型驱动

        //密码加密处理
        String md5 = MD5Util.md5(userModel.getUsername() + userModel.getPassword().toLowerCase());
        userModel.setPassword(md5);

        //请求service添加用户
        UserModel b = userService.addUser(userModel);
        if (b != null) {
            //注册用户成功
            //-----创建账户
            UserAccountModel userAccountModel = new UserAccountModel();
            userAccountModel.setUserId(b.getId());
            userService.addUserAccount(userAccountModel);
            //----用户存储在redis
            String s = generateUserToken(b.getUsername());

            //相应数据  --status --data --token
            //-------data
            Map<String, Object> map = new HashMap<>();
            map.put("id", b.getId());

            this.getResponse().getWriter().write(Response.build().setStatus(FrontStatusConstants.SUCCESS).setData(map).setToken(s).toJSON());

        } else {
            //注册用户失败
            this.getResponse().getWriter().write(Response.build().setStatus(FrontStatusConstants.BREAK_DOWN).toJSON());
        }

    }

    @Override
    public UserModel getModel() {
        return userModel;
    }

    //工具
    public String generateUserToken(String userName) {

        try {
            // 生成令牌
            String token = TokenUtil.generateUserToken(userName);

            // 根据用户名获取用户
            UserModel user = userService.checkUsername(userName);
            // 将用户信息存储到map中。
            Map<String, Object> tokenMap = new HashMap<String, Object>();
            tokenMap.put("id", user.getId());
            tokenMap.put("userName", user.getUsername());
            tokenMap.put("phone", user.getPhone());
            tokenMap.put("userType", user.getUserType());
            tokenMap.put("payPwdStatus", user.getPayPwdStatus());
            tokenMap.put("emailStatus", user.getEmailStatus());
            tokenMap.put("realName", user.getRealName());
            tokenMap.put("identity", user.getIdentity());
            tokenMap.put("realNameStatus", user.getRealNameStatus());
            tokenMap.put("payPhoneStatus", user.getPhoneStatus());

            baseCacheService.del(token);
            baseCacheService.setHmap(token, tokenMap); // 将信息存储到redis中

            // 获取配置文件中用户的生命周期，如果没有，默认是30分钟
            String tokenValid = ConfigurableConstants.getProperty("token.validity", "30");
            tokenValid = tokenValid.trim();
            baseCacheService.expire(token, Long.valueOf(tokenValid) * 60);

            return token;
        } catch (Exception e) {
            e.printStackTrace();
//            logger.debug("token", e);
            return Response.build().setStatus("-9999").toJSON();
        }
    }

    //登录
    @Action("login")
    public void login() throws IOException {
        //1.得到请求参数 用户名密码已经封装到user对象中了 取signuuid,signcode就行
        String signUuid = this.getRequest().getParameter("signUuid");
        String signCode = this.getRequest().getParameter("signCode");
        //2.验证
        //非空验证
        if(StringUtils.isEmpty(signCode)){
            this.getResponse().getWriter().write(Response.build().setStatus(FrontStatusConstants.NULL_OF_VALIDATE_CARD).toJSON());
            return;
        }
        if(StringUtils.isEmpty(signUuid)){
            this.getResponse().getWriter().write(Response.build().setStatus("-999").toJSON());
            return;
        }
        //判断验证码
        String _signCode = baseCacheService.get(signUuid);//获取redis中的验证码
        if(!_signCode.equalsIgnoreCase(signCode)){
            //验证码不正确
            this.getResponse().getWriter().write(Response.build().setStatus(FrontStatusConstants.INPUT_ERROR_OF_VALIDATE_CARD).toJSON());
            return;
        }
        //3.调用service完成登录操作

        //判断是否是手机号,可以使用手机号登录
        String str = userModel.getUsername();//得到页面录入的用户名/手机号
        //判断是否是手机号
        if (CommomUtil.isMobile(str)){
            UserModel um = userService.checkPhone(str);
            str=um.getUsername();
        }

        //调用service中的根据username,password查询用户操作
        String password = MD5Util.md5(str + userModel.getPassword().toLowerCase());//数据库是加密的 需加密处理密码再查询
        UserModel u=userService.login(str,password);
        //4.向服务端返回数据
        //判断是否成功
        if(u!=null){
            //成功 将用户存储到redis中
            String token = generateUserToken(u.getUsername());
            //处理数据
            HashMap<String, Object> map = new HashMap<>();
            map.put("userName",u.getUsername());
            map.put("id", u.getId());
            this.getResponse().getWriter().write(Response.build().setStatus(FrontStatusConstants.SUCCESS).setData(map).setToken(token).toJSON());
            return;
        }else{
            //失败 返回用户名与密码不正确
            this.getResponse().getWriter().write(Response.build().setStatus(FrontStatusConstants.ERROR_OF_USERNAME_PASSWORD).toJSON());
            return;
        }

    }

    //退出
    @Action("logout")
    public void logout() throws IOException {
        //得到token
        String token = this.getRequest().getHeader("token");

        Map<String, Object> s = baseCacheService.getHmap(token);
        if(s == null && s.size() == 0){
            this.getResponse().getWriter().write(Response.build().setStatus(FrontStatusConstants.NULL_TOKEN).toJSON());
            return ;
        }
        //在redis中删除token
        baseCacheService.del(token);
        //响应状态码
        this.getResponse().getWriter().write(Response.build().setStatus("1").toJSON());
    }

    //获取用户安全级别
    @Action("userSecure")
    public void getUserSecure() throws IOException {
        //得到token
        String token = GetHttpResponseHeader.getHeadersInfo(this.getRequest());
        if(StringUtils.isEmpty(token)){
            this.getResponse().getWriter().write(Response.build().setStatus(FrontStatusConstants.NULL_TOKEN).toJSON());
            return;
        }
        //通过token查找redis  用户id
        Map<String, Object> hmap = baseCacheService.getHmap(token);
        if(hmap == null && hmap.size()==0){
            this.getResponse().getWriter().write(Response.build().setStatus(FrontStatusConstants.NOT_LOGGED_IN).toJSON());
            return;
        }
        Integer id = (Integer) hmap.get("id");

        //根据 用户id获取用户对象
        UserModel u = userService.findById(id);

        //响应数据到浏览器
        List<Map<String,Object>> list = new ArrayList<>();

        Map<String,Object> map = new HashMap<>();
        map.put("phoneStatus",u.getPhoneStatus());
        map.put("realNameStatus",u.getRealNameStatus());
        map.put("payPwdStatus",u.getPayPwdStatus());
        map.put("emailStatus",u.getEmailStatus());

        list.add(map);
        this.getResponse().getWriter().write(Response.build().setStatus("1").setData(list).toJSON());
    }

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

    //获取用户详细安全信息
    @Action("userSecureDetailed")
    public void userSecureDetailed() throws IOException {
        //得到token
        String token = GetHttpResponseHeader.getHeadersInfo(this.getRequest());
        if(StringUtils.isEmpty(token)){
            this.getResponse().getWriter().write(Response.build().setStatus(FrontStatusConstants.NULL_TOKEN).toJSON());
            return;
        }
        //通过token查找redis  用户id
        Map<String, Object> hmap = baseCacheService.getHmap(token);
        if(hmap == null && hmap.size()==0){
            this.getResponse().getWriter().write(Response.build().setStatus(FrontStatusConstants.NOT_LOGGED_IN).toJSON());
            return;
        }
        Integer id = (Integer) hmap.get("id");

        //根据 用户id获取用户对象
        UserModel u = userService.findById(id);

        //响应数据到浏览器
        List<Map<String,Object>> list = new ArrayList<>();

        Map<String,Object> map = new HashMap<>();
        map.put("phoneStatus",u.getPhoneStatus());
        map.put("realNameStatus",u.getRealNameStatus());
        map.put("payPwdStatus",u.getPayPwdStatus());
        map.put("emailStatus",u.getEmailStatus());
        map.put("passwordstatus",u.getPassword());
        map.put("username",u.getUsername());
        map.put("phone",u.getPhone());

        list.add(map);
        this.getResponse().getWriter().write(Response.build().setStatus("1").setData(list).toJSON());
    }
}
