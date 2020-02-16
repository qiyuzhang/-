package cn.itcast.test;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import cn.itcast.cxfServer.CxfTestServer;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:config/applicationContext.xml")
public class CxfClientTest {

	@Autowired
	@Qualifier("cxfClient")
	private CxfTestServer cxfServer;

	@Test
	public void cxfTest() {
		String sayHello = cxfServer.sayHello("tom");

		System.out.println(sayHello);
	}
}
