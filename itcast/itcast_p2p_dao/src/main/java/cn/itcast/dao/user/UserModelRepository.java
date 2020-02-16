package cn.itcast.dao.user;

import cn.itcast.domain.user.UserModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface UserModelRepository extends JpaRepository<UserModel,Integer> {

    UserModel findByUsername(String username);
    UserModel findByPhone(String phone);
    UserModel findByUsernameAndPassword(String username,String password);

    @Modifying
    @Query("update UserModel u set u.phone=?1,u.phoneStatus=1 where u.id=?2")
    void updateUserPhone(String phone,Integer uid);

    @Modifying
    @Query("update UserModel u set u.realName=?1,u.realNameStatus=1,u.identity=?2 where u.id=?3")
    void updateUserNameStatus(String realName,String identity,Integer uid);

    @Modifying
    @Query("update UserModel u set u.email=?1 where u.id=?2")
    void updateUserAddEmail(String email,Integer userid);

    @Modifying
    @Query("update UserModel u set u.emailStatus=1 where u.id=?1")
    void updateUserEmailStatus(Integer userId);
}
