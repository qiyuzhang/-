package cn.itcast.dao.product;

import cn.itcast.domain.product.ProductEarningRate;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductEarningRateRepository extends JpaRepository<ProductEarningRate,Integer> {

    List<ProductEarningRate> findByProductId(Integer proId);
    void deleteAllByProductId(Integer proId);

//    @Query("select per from ProductEarningRate per where per.productId=?1 and per.month=?2 ")
//    public ProductEarningRate getEarningRateByPIdAndMonth(int pid, int mounth);

    ProductEarningRate findByProductIdAndMonth(int pid,int month);
}
