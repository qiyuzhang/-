package cn.itcast.service.message;

import java.util.Map;

import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.Message;
import javax.jms.Session;

import cn.itcast.utils.IMessage;
import org.springframework.jms.support.converter.MessageConversionException;
import org.springframework.jms.support.converter.MessageConverter;


public class InnerMessageConverter implements MessageConverter{
	@Override
	public Message toMessage(Object object, Session session) throws JMSException, MessageConversionException {
		MapMessage message = session.createMapMessage();

		@SuppressWarnings("all")
		Map<String, Object> map = (Map) object;

		message.setObject("title", map.get(IMessage.EmailMessageTitle));
		message.setObject("content", map.get(IMessage.MessageContent));
		message.setObject("to", map.get(IMessage.EmailMessageTo));
//		message.setObject("cc", map.get(IMessage.EmailMessageCC));
		message.setObject("type", map.get(IMessage.MessageType));
		message.setObject("number",map.get(IMessage.SMSNumbers));

		return message;
	}

	@Override
	public Object fromMessage(Message message) throws JMSException, MessageConversionException {
		return message;
	}
}
