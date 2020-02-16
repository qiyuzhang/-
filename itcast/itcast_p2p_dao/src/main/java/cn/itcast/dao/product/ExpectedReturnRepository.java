package cn.itcast.dao.product;

import cn.itcast.domain.product.ExpectedReturn;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ExpectedReturnRepository extends JpaRepository<ExpectedReturn,Integer> {
}
