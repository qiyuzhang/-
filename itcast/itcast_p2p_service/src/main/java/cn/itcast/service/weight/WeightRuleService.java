package cn.itcast.service.weight;

import cn.itcast.dao.weight.WeightRuleRepository;
import cn.itcast.domain.matchManagement.WeigthRule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class WeightRuleService {

    @Autowired
    WeightRuleRepository weightRuleRepository;

    public WeigthRule getRuleByType(int i) {
        return weightRuleRepository.findByWeigthType(i);
    }
}
