package cn.itcast.service.msm;

import cn.itcast.utils.IMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class MSMService {

    @Autowired
    JmsTemplate jmsTemplate;

    //完成向activemq发送要发送的短信的结构信息
    public void senMSM(String phone,String content){
        Map<String,Object> map = new HashMap<>();
        map.put(IMessage.MessageType,IMessage.SMSMessage);//存储消息类型
        map.put(IMessage.MessageContent,content);//存储消息内容
        map.put(IMessage.SMSNumbers,phone);

        jmsTemplate.convertAndSend(map);

    }

}
