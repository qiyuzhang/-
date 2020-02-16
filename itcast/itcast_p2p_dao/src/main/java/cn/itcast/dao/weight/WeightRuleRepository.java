package cn.itcast.dao.weight;

import cn.itcast.domain.matchManagement.WeigthRule;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WeightRuleRepository extends JpaRepository<WeigthRule,Integer> {

    WeigthRule findByWeigthType(Integer type);
}
