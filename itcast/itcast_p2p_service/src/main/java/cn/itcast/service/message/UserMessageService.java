package cn.itcast.service.message;

import java.text.SimpleDateFormat;
import java.util.Date;

import cn.itcast.dao.user.UserMessageRepository;
import cn.itcast.domain.user.UserMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserMessageService{
	@Autowired
	public UserMessageRepository userMessageDao;

	//添加站内信息
	public void insertMessages(String userId, String messageContent) {
		UserMessage message = new UserMessage();
		message.setS_receive_user_id(Integer.valueOf(userId));  //接受用户ID
		message.setMessageContent(messageContent); //信息内容
		message.setMessageState(0); //未读信息
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hm:ss"); 
		message.setSendTime(sdf.format(new Date())); //发送时间
		userMessageDao.save(message);
	}

}
