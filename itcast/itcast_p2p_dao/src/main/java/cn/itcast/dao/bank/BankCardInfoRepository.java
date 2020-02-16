package cn.itcast.dao.bank;

import cn.itcast.domain.bankCardInfo.BankCardInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface BankCardInfoRepository extends JpaRepository<BankCardInfo,Integer> {

    BankCardInfo findByUserId(Integer userId);

    BankCardInfo findByBankCardNum(String card);

    @Modifying
    @Query("update BankCardInfo b set b.money=b.money-?2 where b.bankCardNum=?1")
    void updateMoney(String card, Double money);
}
