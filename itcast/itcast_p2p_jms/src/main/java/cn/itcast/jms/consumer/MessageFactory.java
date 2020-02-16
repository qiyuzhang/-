package cn.itcast.jms.consumer;

import javax.annotation.Resource;

import org.springframework.stereotype.Component;

import cn.itcast.jms.vo.MessageConstant;

/**
 * 
 * @ClassName: MessageFactory
 * @Description: 信息发送器的工厂,根据不同消息类型，创建不同的发送器
 */
@Component
public class MessageFactory {

	@Resource(name = "EmailMessageSenderImpl")
	private IMessageSender email;

	@Resource(name = "SMSMessageSenderImpl")
	private IMessageSender sms;

	/**
	 * 获取单例的发送类
	 * 
	 * @param tyep
	 * @return
	 */
	public IMessageSender getMessageSender(String tyep) {
		if (MessageConstant.EmailMessage.equals(tyep)) {
			return this.email;
		} else if (MessageConstant.SMSMessage.equals(tyep)) {
			return this.sms;
		} else {
			return null;
		}
	}
}
