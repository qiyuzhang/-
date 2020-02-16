package cn.itcast.action.admin;

import cn.itcast.action.common.BaseAction;
import cn.itcast.domain.admin.AdminModel;
import cn.itcast.service.admin.AdminService;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import java.io.IOException;

@Controller
@Namespace("/account")
@Scope("prototype")
public class AdminAction extends BaseAction {

    @Autowired
    AdminService adminService;

    @Action("login")
    public  void login(){
        String username =this.getRequest().getParameter("username");
        String password =this.getRequest().getParameter("password");
//      测试输出
        System.out.println("password = " + password);
        System.out.println("username = " + username);

        try {
            AdminModel login = adminService.login(username, password);

            if(login != null){
               this.getResponse().getWriter().write("{\"status\":\"1\"}");
               return;
            }else{
                this.getResponse().getWriter().write("{\"status\":\"0\"}");
                return;
            }
       //     ServletActionContext.getResponse().getWriter().write("{\"status\":\"1\"}");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
