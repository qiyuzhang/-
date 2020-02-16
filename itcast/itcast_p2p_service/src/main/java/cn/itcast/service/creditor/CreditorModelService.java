package cn.itcast.service.creditor;

import cn.itcast.dao.creditor.CreditorModelRepository;
import cn.itcast.dao.creditor.ICreditor4SqlDAO;
import cn.itcast.domain.creditor.CreditorModel;
import cn.itcast.util.constant.ClaimsType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class CreditorModelService {

    @Autowired
    CreditorModelRepository creditorModelRepository;
    
    @Autowired
    private ICreditor4SqlDAO creditor4SqlDao;


    public void addCreditor(CreditorModel creditorModel) {
        creditorModelRepository.save(creditorModel);
    }
    public void save(List<CreditorModel> list) {
        System.out.println("list = " + list);
        creditorModelRepository.save(list);
    }

    
    public void addMulti(List<CreditorModel> cs) {
        creditorModelRepository.save(cs);
    }

    // 多条件债权信息查询
    public List<CreditorModel> findCreditorByCondition(Map<String, Object> map) {
        return creditor4SqlDao.findCreditorByCondition(map);
    }

    // 多条件查询债权的统计信息
    public List<Object[]> findCreditorBySum(Map<String, Object> map) {
        return creditor4SqlDao.findCreditorBySum(map);
    }

    // 债权的审核
    public void checkCreditor(String[] id) {
        for (int i = 0; i < id.length; i++) {
            // 1.根据id去查询债权
            CreditorModel creditor = creditorModelRepository.findOne(Integer.parseInt(id[i].trim()));

            if(creditor!=null){
                //查找到
                //2.修改债权的状态
                creditor.setDebtStatus(ClaimsType.CHECKED);
            }
        }


    }
}
