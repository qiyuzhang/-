package cn.itcast.dao.product;

import cn.itcast.domain.product.ProductAccount;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductAccountRepository extends JpaRepository<ProductAccount,Integer> {
}
