package cn.itcast.service.bank;

import cn.itcast.dao.bank.BankCardInfoRepository;
import cn.itcast.dao.bank.BankRepository;
import cn.itcast.dao.city.CityRepository;
import cn.itcast.domain.bankCardInfo.Bank;
import cn.itcast.domain.bankCardInfo.BankCardInfo;
import cn.itcast.domain.city.City;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class BankService {

    @Autowired
    BankRepository bankRepository;
    @Autowired
    BankCardInfoRepository bankCardInfoRepository;
    @Autowired
    CityRepository cityRepository;

    public BankCardInfo findBankByUserId(Integer id) {
        return bankCardInfoRepository.findByUserId(id);
    }

    public List<Bank> findAllBanks() {
        return bankRepository.findAll();
    }

    public List<City> findProvinces() {
        return cityRepository.findByParentCityAreaNumIsNull();
    }

    public List<City> findCitys(String parseInt) {
        return cityRepository.findByParentCityAreaNum(parseInt);
    }

    public void addBank(BankCardInfo bankCardInfo) {
        bankCardInfoRepository.save(bankCardInfo);
    }

    public BankCardInfo findByCard(String card) {
        return bankCardInfoRepository.findByBankCardNum(card);
    }

    public void updateMoney(String card, Double money) {
        bankCardInfoRepository.updateMoney(card,money);
    }
}
