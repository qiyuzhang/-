package cn.itcast.dao.user;

import cn.itcast.domain.user.UserMessage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserMessageRepository extends JpaRepository<UserMessage,Integer> {
}
