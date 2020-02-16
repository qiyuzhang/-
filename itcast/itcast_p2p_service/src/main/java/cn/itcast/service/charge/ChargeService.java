package cn.itcast.service.charge;

import cn.itcast.dao.bank.BankCardInfoRepository;
import cn.itcast.dao.user.UserAccountRepository;
import cn.itcast.service.user.UserService;
import cn.itcast.utils.HttpClientUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

@Service
@Transactional
public class ChargeService {

    @Autowired
    UserAccountRepository userAccountRepository;
    @Autowired
    BankCardInfoRepository bankCardInfoRepository;

    public void recharge(String bankCardNum, double money,Integer uid) {
        //访问webservice服务  完成银行转账
        //调用httpclient 访问mybank 资源
        Map<String,Object> map = new HashMap<>();
        map.put("bankCardNum",bankCardNum);
        map.put("money",money);

        String bank_url = HttpClientUtil.visitWebService(map, "bank_url");
        System.out.println("bank_url = " + bank_url);
        //根据银行转账结果完成  用户平台信息改变
//        System.out.println("chargeService================"+bankCardInfoRepository.findByBankCardNum(bankCardNum).toString());

        userAccountRepository.updateMoney(uid,money);

//        System.out.println("111111111111111111111111111111111111111111111111111");
    }
}
