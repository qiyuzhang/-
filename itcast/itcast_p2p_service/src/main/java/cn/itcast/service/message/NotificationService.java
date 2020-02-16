package cn.itcast.service.message;

import cn.itcast.dao.user.UserModelRepository;
import cn.itcast.service.email.EmailService;
import cn.itcast.service.msm.MSMService;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cn.itcast.domain.user.UserModel;

import javax.mail.MessagingException;

@Service
public class NotificationService {
	private static Logger logger = Logger.getLogger(NotificationService.class);

	@Autowired
	private UserModelRepository userDao;


	@Autowired
	private UserMessageService messageService;

	@Autowired
	private MSMService sMSMessageServcie;
	
	@Autowired
	private EmailService emailMessageService;

	public void sendAllTypeOfMessages(Integer userId, String messagesContent) {
		// 查询用户信息
		UserModel user = userDao.findOne(userId);
		// 判断用户是否存在
		if (null == user)
			throw new RuntimeException("没有此用户");

		

		// 发送短信消息
		if (1 == user.getPhoneStatus()) {
			if (StringUtils.isNotBlank(user.getPhone())) {
				sMSMessageServcie.senMSM(user.getPhone(), messagesContent);
				logger.debug("短信信息压入消息队列成功");
			}
		}

		// 发送站内信
		messageService.insertMessages(String.valueOf(userId), messagesContent);
		logger.debug("成功将短信信息，邮件消息压入消息队列，等待发送；成功储存站内信到数据库");

	}

	public void sendMessage(String userId, String operationType, String messageblob) throws MessagingException {
		String email = null;
		String phone = null;
		//1.通过用户id 查询 用户的手机号 ,邮箱
		UserModel user = userDao.findOne(Integer.parseInt(userId));
		if(null != user){
			email = user.getEmail();
			phone = user.getPhone();
		}
		messageService.insertMessages(userId, messageblob);
		emailMessageService.sendEmail(email,"邮件通知", messageblob);
		sMSMessageServcie.senMSM(phone, messageblob);
	}

}
