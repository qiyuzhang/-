package cn.itcast.dao.creditor;

import cn.itcast.domain.creditor.CreditorModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CreditorModelRepository extends JpaRepository<CreditorModel,Integer> {

    @Query("select cm from CreditorModel cm where cm.debtStatus=11302 and cm.matchedStatus in (11401,11403)")
    List<CreditorModel> findMatchCreditor();


    @Modifying
    @Query("update CreditorModel cm set cm.availableMoney=?2,cm.matchedMoney=?3,cm.matchedStatus=?4 where cm.id=?1")
    void updateCreditorStatus(Integer id, double i, double availableMoney, int i1);
}
