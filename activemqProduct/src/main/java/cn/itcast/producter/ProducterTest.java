package cn.itcast.producter;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:config/applicationContext.xml"})
public class ProducterTest {
    @Autowired
    JmsTemplate jmsTemplate;

    @Test
    public void productMsg(){
        jmsTemplate.convertAndSend("hello activemq");
    }
}
