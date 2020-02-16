package cn.itcast.dao.accountlog;

import cn.itcast.domain.accountLog.AccountLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountLogRepository extends JpaRepository<AccountLog,Integer> {
}
