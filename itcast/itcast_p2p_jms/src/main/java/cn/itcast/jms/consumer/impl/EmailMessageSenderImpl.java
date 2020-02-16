package cn.itcast.jms.consumer.impl;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.jms.MapMessage;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import cn.itcast.jms.Converter;
import cn.itcast.jms.consumer.IMessageSender;
import cn.itcast.jms.vo.MessageConstant;

@Component("EmailMessageSenderImpl")
public class EmailMessageSenderImpl implements IMessageSender {

	@Autowired
	private JavaMailSender mailSender = null;

	@Value("${mail.default.from}")
	private String defaultMailFrom = null;

	public void sendMessage(MapMessage message) throws Exception {

		MimeMessage mime = this.mailSender.createMimeMessage();
		MimeMessageHelper mimehelper = new MimeMessageHelper(mime, true);
		String from = message.getString(MessageConstant.EmailMessageFrom);
		if (StringUtils.isEmpty(from)) {
			if (StringUtils.isEmpty(defaultMailFrom)) {
				throw new RuntimeException("from is Null");
			}
			from = this.defaultMailFrom;
		}
		// 设置发送人
		mimehelper.setFrom(from, "传智播客");
		// 设置主题
		String title = message.getString(MessageConstant.EmailMessageTitle);
		mimehelper.setSubject(StringUtils.isEmpty(title) ? "" : title);
		// 设置发送日期
		mimehelper.setSentDate(new Date());
		// 设置邮件内容为HTML超文本格式
		mimehelper.setText(message.getString(MessageConstant.MessageContent), true);
		// 设置收件人
		InternetAddress[] addrs = this.getAddr(message.getObject(MessageConstant.EmailMessageTo));
		if (null == addrs || 0 == addrs.length) {
			throw new RuntimeException("Message is Null");
		}
		mimehelper.setTo(addrs);
		// 设置抄送人
		addrs = this.getAddr(message.getObject(MessageConstant.EmailMessageCC));
		if (null != addrs && addrs.length > 0) {
			mimehelper.setCc(addrs);
		}
		// 设置暗送
		addrs = this.getAddr(message.getObject(MessageConstant.EmailMessageBCC));
		if (null != addrs && addrs.length > 0) {
			mimehelper.setBcc(addrs);
		}
		mailSender.send(mime);// 将邮件发送
	}

	private InternetAddress[] getAddr(Object addr) throws AddressException {
		List<String> addrList = Converter.converter(addr);
		InternetAddress[] internetAddressArry = new InternetAddress[addrList.size()];
		int size = 0;
		for (String string : addrList) {
			if (StringUtils.isEmpty(string)) {
				continue;
			}
			internetAddressArry[size++] = new InternetAddress(string);
		}
		return 0 == size ? null : Arrays.copyOfRange(internetAddressArry, 0, size);
	}
}