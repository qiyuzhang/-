package cn.itcast.dao.bank;

import cn.itcast.domain.bankCardInfo.Bank;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BankRepository extends JpaRepository<Bank,Integer> {
}
