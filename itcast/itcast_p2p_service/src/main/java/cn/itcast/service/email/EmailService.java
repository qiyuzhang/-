package cn.itcast.service.email;

import cn.itcast.utils.IMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import java.util.HashMap;
import java.util.Map;

@Service
public class EmailService {
//    @Autowired
//    JavaMailSender javaMailSender;
//
//    @Value("${mail.default.from}")
//    private String from;

//    public void sendEmail(String email,String title,String content) throws MessagingException {
//        //创建一个邮件信息
//        MimeMessage mm = javaMailSender.createMimeMessage();
//
//        //邮件信息整合
//        MimeMessageHelper helper = new MimeMessageHelper(mm);
//
//        helper.setFrom(from);
//        helper.setSubject(title);
//        helper.setText(content,true);//true 代表 如果有html代码可以解析
//        helper.setTo(email);
//        System.out.println(" ==============send email================== ");
//        //发送邮件
//        javaMailSender.send(mm);
//
//    }

    @Autowired
    JmsTemplate jmsTemplate;

    public void sendEmail(String email,String title,String content) throws MessagingException {
        Map<String,Object> map = new HashMap<>();
        map.put(IMessage.MessageType,IMessage.EmailMessage);//存储消息类型
        map.put(IMessage.MessageContent,content);//存储消息内容
        map.put(IMessage.EmailMessageTitle,title);
        map.put(IMessage.EmailMessageTo,email);

        jmsTemplate.convertAndSend(map);
    }
}
