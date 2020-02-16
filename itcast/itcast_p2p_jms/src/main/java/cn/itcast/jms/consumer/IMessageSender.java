package cn.itcast.jms.consumer;

import javax.jms.MapMessage;

public interface IMessageSender {
	public void sendMessage(MapMessage message) throws Exception;
}
