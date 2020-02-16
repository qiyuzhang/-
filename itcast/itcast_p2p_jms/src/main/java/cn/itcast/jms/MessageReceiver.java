package cn.itcast.jms;

import javax.jms.MapMessage;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.mail.MessagingException;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import cn.itcast.jms.consumer.IMessageSender;
import cn.itcast.jms.consumer.MessageFactory;
import cn.itcast.jms.vo.MessageConstant;

public class MessageReceiver implements MessageListener {

	@Autowired
	private MessageFactory factory;
	private static Logger log = Logger.getLogger(MessageReceiver.class);

	/**
	 * <p>
	 * Title: onMessage
	 * </p>
	 * <p>
	 * Description:
	 * </p>
	 * 
//	 * @param arg0
	 * @see MessageListener#onMessage(Message)
	 *
	 */

	@Override
	public void onMessage(Message message) {
		if (message instanceof MapMessage) {
			MapMessage map = (MapMessage) message;
			try {
				processMessage(map);// 调用发送消息
				message.acknowledge(); // 自动通知确认
			} catch (Exception e) {
				log.error(e);
			}
		}
	}

	/**
	 * 处理消息发送流程
	 * 
//	 * @param messageDescription
	 *            具体的消息bean
	 * @throws MessagingException
	 */
	public void processMessage(MapMessage message) throws Exception {
		//根据消息类型来判断使用具体的发送消息类
		IMessageSender sender = factory.getMessageSender(message.getString(MessageConstant.MessageType));
		if (null == sender) {
			throw new RuntimeException("Not Support");
		}
		try {
			sender.sendMessage(message);
		} catch (Exception ex) {
			log.error(ex);
			throw new RuntimeException("Not Support");
		}
	}

}
