package cn.itcast.service.admin;

import cn.itcast.dao.admin.AdminRepository;
import cn.itcast.domain.admin.AdminModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AdminService {

    @Autowired
    private AdminRepository adminRepository;

    public AdminModel login(String username,String password){
        AdminModel login = adminRepository.login(username, password);
        return login;
    }
}
