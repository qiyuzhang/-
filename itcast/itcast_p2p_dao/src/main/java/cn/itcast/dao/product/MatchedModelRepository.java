package cn.itcast.dao.product;

import cn.itcast.domain.product.FundingNotMatchedModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface MatchedModelRepository extends JpaRepository<FundingNotMatchedModel,Integer> {

    @Query("select fnmm from FundingNotMatchedModel fnmm where fnmm.fIsLocked=10901")
    List<FundingNotMatchedModel> findMatchFundingNotMatched();

    @Modifying
    @Query("update FundingNotMatchedModel fnmm set fnmm.fNotMatchedMoney=0,fnmm.fIsLocked=10905 where fnmm.fId=?1")
    void updateStatus(int getfId);
}
