package cn.itcast.server.impl;

import cn.itcast.domain.bankCardInfo.BankCardInfo;
import cn.itcast.server.Bank;
import cn.itcast.service.bank.BankService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class BankImpl implements Bank {

	@Autowired
	BankService bankService;

	@Override
	public boolean recharge(String param) {
		System.out.println(param); //bankCardNum=1111111111111111111&money=1000.0
		// System.out.println("卡号:" + bankCardId + ",要转账:" + money + "元");

		//1.处理请求参数
//		String[] split = param.split("&");
//
//		Double money = Double.parseDouble(split[0].split("=")[1]);
//		String card = split[1].split("=")[1];
//		System.out.println("card = " + card);
//		System.out.println("money = " + money);

		//2.根据银行卡号去表中查询
//		BankCardInfo bankCardInfo = bankService.findByCard(card);
//		if(bankCardInfo == null){
//			System.out.println(" 没有查询到银行卡信息。。。");
//			return false;
//		}

//		System.out.println("bankCardInfo = " + bankCardInfo);

		//3.判断银行余额是否大于转账金额，进行转账
//		if(bankCardInfo.getMoney() < money){
			//余额不足
//			System.out.println(" =============账户余额不足 ");
//		}
//		bankService.updateMoney(card,money);
//		System.out.println("2222222222222222222222222222222222222222222");
		return true;
	}

	
	
	
	@Override
	public void show() {
		System.out.println("bank show......");
	}

}
