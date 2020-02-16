package cn.itcast.dao.user;

import cn.itcast.domain.user.UserAccountModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface UserAccountRepository extends JpaRepository<UserAccountModel,Integer> {
    UserAccountModel findByUserId(Integer userId);

    @Modifying
    @Query("update UserAccountModel u set u.total=u.total+?2,u.balance=u.balance+?2 where u.userId=?1")
    void updateMoney(Integer uid, double money);

    @Modifying
    @Query("update UserAccountModel account set account.inverstmentW = ?1, account.balance = ?2, account.recyclingInterest = ?3, account.addCapitalTotal = ?4, account.capitalTotal = ?5 where account.userId = ?6 and account.balance > ?2 ")
    void updateNewInvestmentUserAccount(double inverstmentW, double balance, double recyclingInterest, double addCapitalTotal, double capitalTotal, Integer userId);
}
