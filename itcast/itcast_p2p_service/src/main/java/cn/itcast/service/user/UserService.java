package cn.itcast.service.user;

import cn.itcast.dao.product.ProductEarningRateRepository;
import cn.itcast.dao.user.UserAccountRepository;
import cn.itcast.dao.user.UserModelRepository;
import cn.itcast.domain.product.ProductEarningRate;
import cn.itcast.domain.user.UserAccountModel;
import cn.itcast.domain.user.UserModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class UserService {

    @Autowired
    UserModelRepository userModelRepository;
    @Autowired
    UserAccountRepository accountRepository;
    @Autowired
    ProductEarningRateRepository rateRepository;

    //校验用户名
    public UserModel checkUsername(String username) {
        UserModel userModel = userModelRepository.findByUsername(username);
        return userModel;
    }

    //校验手机号
    public UserModel checkPhone(String phone) {
        return userModelRepository.findByPhone(phone);
    }

    //添加用户
    public UserModel addUser(UserModel userModel) {
        UserModel save = userModelRepository.save(userModel);
        return save;
    }

    //添加用户账户
    public void addUserAccount(UserAccountModel userAccountModel) {
        accountRepository.save(userAccountModel);
    }

    public UserModel login(String username, String password) {
        return userModelRepository.findByUsernameAndPassword(username,password);
    }

    public UserModel findById(Integer id) {
        return userModelRepository.findOne(id);
    }

    public UserAccountModel findAccountByUserId(Integer id) {
        return accountRepository.findByUserId(id);
    }

    public void updateUserPhone(String phone, Integer id) {
        userModelRepository.updateUserPhone(phone,id);
    }

    public void updateUserNameStatus(String realName, String identity, Integer id) {
        userModelRepository.updateUserNameStatus(realName, identity, id);
    }

    public void addEmail(Integer userId, String email) {
        userModelRepository.updateUserAddEmail(email,userId);
    }

    public void updateEmailStatus(String userID) {
        userModelRepository.updateUserEmailStatus(Integer.parseInt(userID));
    }

    public ProductEarningRate selectYearInterest(int pid, int month) {
        return rateRepository.findByProductIdAndMonth(pid,month);
    }
}
