package cn.itcast.jms.consumer.impl;

import java.net.URLEncoder;
import java.util.List;
import java.util.regex.Pattern;

import javax.annotation.PostConstruct;
import javax.jms.MapMessage;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import cn.itcast.jms.Converter;
import cn.itcast.jms.MessageReceiver;
import cn.itcast.jms.consumer.IMessageSender;
import cn.itcast.jms.vo.MessageConstant;

@Component("SMSMessageSenderImpl")
public class SMSMessageSenderImpl implements IMessageSender {
	
	//@Value("#{configProperties['message.sms.url']}")	
	@Value("${message.sms.url}")
	private String url ;
	//@Value("#{configProperties['message.sms.key']}")
	@Value("${message.sms.key}")
	private String key ;
	//@Value("#{configProperties['message.sms.pass']}")
	@Value("${message.sms.pass}")
	private String pass ;
	//@Value("#{configProperties['message.sms.httpencoding']}")
	@Value("${message.sms.httpencoding}")
	private String encoding;
	
	private static Logger log = Logger.getLogger(MessageReceiver.class);

	@PostConstruct
	public void check(){
		if(StringUtils.isEmpty(key) || StringUtils.isEmpty(pass) || StringUtils.isEmpty(url) ){
			throw new RuntimeException("lost setting");
		}
		this.url = url.replace("{key}", key).replace("{password}", pass) ;
	}
	
	public static long lastSendTime = 0 ;
	
	public void sendMessage(MapMessage message) throws Exception{		
		List<String> numbers = Converter.converter(message.getObject(MessageConstant.SMSNumbers));
		log.debug("消息处理：：手机短信发送：：接受号码："+numbers.toString());
		String content = message.getString(MessageConstant.MessageContent);
		log.debug("消息处理：：手机短信发送：：发送内容："+content);		
		for(String number : numbers){
			if(StringUtils.isEmpty(number)){
				continue;
			}
			System.out.println("向"+number+"发送信息"+content);
			if(isMobleiNumber(number)){
				StringBuffer sb = new StringBuffer();
				//以下是参数
				sb.append(url);
				sb.append("id=").append(URLEncoder.encode(key, "gb2312"));
				sb.append("&pwd=").append(pass);
				sb.append("&to=").append(number);
				sb.append("&content=").append(URLEncoder.encode(content, "gb2312")); 
				sb.append("&time=").append("");

				try {
					HttpClient client = new HttpClient();
					HttpMethod method = new GetMethod(sb.toString());
					client.executeMethod(method);
					log.info("向" + number + "发送：" + message.getString(MessageConstant.MessageContent));
					log.info(method.getResponseBodyAsString());
					method.releaseConnection();
				} catch (Exception e) {
					log.error(e);
				}
			}
		}
	}
	
	private static Pattern pattern = Pattern.compile("^[1][0-9]{10}$");
	public static boolean isMobleiNumber(String str){
		return pattern.matcher(str).matches();
	}
}
