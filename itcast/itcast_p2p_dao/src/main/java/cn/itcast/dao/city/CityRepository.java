package cn.itcast.dao.city;

import cn.itcast.domain.city.City;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CityRepository extends JpaRepository<City,Integer> {
    List<City> findByParentCityAreaNumIsNull();
    List<City> findByParentCityAreaNum(String cid);
}
