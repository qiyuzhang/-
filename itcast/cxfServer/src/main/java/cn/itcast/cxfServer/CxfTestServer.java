package cn.itcast.cxfServer;

import javax.jws.WebMethod;
import javax.jws.WebService;

@WebService
public interface CxfTestServer {

	@WebMethod
	public String sayHello(String name);
}
