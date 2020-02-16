package cn.itcast.cxfServer.impl;

import cn.itcast.cxfServer.CxfTestServer;

public class CxfTestServerImpl implements CxfTestServer {

	@Override
	public String sayHello(String name) {
		return "hello " + name;
	}

}
