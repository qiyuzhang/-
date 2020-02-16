package cn.itcast.service.product;

import cn.itcast.dao.accountlog.AccountLogRepository;
import cn.itcast.dao.product.*;
import cn.itcast.dao.user.UserAccountRepository;
import cn.itcast.domain.accountLog.AccountLog;
import cn.itcast.domain.product.*;
import cn.itcast.domain.user.UserAccountModel;
import cn.itcast.utils.ProductStyle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class ProductService {
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private ProductEarningRateRepository earningRateRepository;
    @Autowired
    ProductAccountRepository productAccountRepository;
    @Autowired
    UserAccountRepository userAccountRepository;
    @Autowired
    AccountLogRepository accountLogRepository;
    @Autowired
    MatchedModelRepository matchedModelRepository;
    @Autowired
    ExpectedReturnRepository expectedReturnRepository;

    public List<Product> findAll(){
        List<Product> list = productRepository.findAll();
        changeListStatusToChinese(list);
        return list;
    }

    public Product findById(Long id){

        Product one = productRepository.findOne(id);
        changeOneStatusToChinese(one);
        return one;
    }

    /**
     * 方法描述：将状态转换为中文
     *
     * @param products
     *            void
     */
    private void changeOneStatusToChinese(Product products) {
        List<Product> ps = new ArrayList<>();
        ps.add(products);
        changeListStatusToChinese(ps);
    }
    private void changeListStatusToChinese(List<Product> products) {
        if (null == products)
            return;
        for (Product product : products) {
            int way = product.getWayToReturnMoney();
            // 每月部分回款
            if (ProductStyle.REPAYMENT_WAY_MONTH_PART.equals(String.valueOf(way))) {
                product.setWayToReturnMoneyDesc("每月部分回款");
                // 到期一次性回款
            } else if (ProductStyle.REPAYMENT_WAY_ONECE_DUE_DATE.equals(String.valueOf(way))) {
                product.setWayToReturnMoneyDesc("到期一次性回款");
            }

            // 是否复投 isReaptInvest 136：是、137：否
            // 可以复投
            if (ProductStyle.CAN_REPEAR == product.getIsRepeatInvest()) {
                product.setIsRepeatInvestDesc("是");
                // 不可复投
            } else if (ProductStyle.CAN_NOT_REPEAR == product.getIsRepeatInvest()) {
                product.setIsRepeatInvestDesc("否");
            }
            // 年利率
            if (ProductStyle.ANNUAL_RATE == product.getEarningType()) {
                product.setEarningTypeDesc("年利率");
                // 月利率 135
            } else if (ProductStyle.MONTHLY_RATE == product.getEarningType()) {
                product.setEarningTypeDesc("月利率");
            }

            if (ProductStyle.NORMAL == product.getStatus()) {
                product.setStatusDesc("正常");
            } else if (ProductStyle.STOP_USE == product.getStatus()) {
                product.setStatusDesc("停用");
            }

            // 是否可转让
            if (ProductStyle.CAN_NOT_TRNASATION == product.getIsAllowTransfer()) {
                product.setIsAllowTransferDesc("否");
            } else if (ProductStyle.CAN_TRNASATION == product.getIsAllowTransfer()) {
                product.setIsAllowTransferDesc("是");
            }
        }
    }

    public List<ProductEarningRate> findRatesByProId(String proId) {
        List<ProductEarningRate> list = earningRateRepository.findByProductId(Integer.parseInt(proId));
        return list;
    }

    public void updateProduct(Product product) {
        List<ProductEarningRate> list = earningRateRepository.findByProductId((int) product.getProId());
        if(list != null && list.size()>0){
            earningRateRepository.deleteAllByProductId((int) product.getProId());
        }

        System.out.println(" ************************************************* ");
        earningRateRepository.save(product.getProEarningRate());
        productRepository.save(product);

    }

    public List<ProductEarningRate> findByPid(String pid) {
        return earningRateRepository.findByProductId(Integer.parseInt(pid));
    }

    public void addProductAccount(ProductAccount pa, UserAccountModel ua,
                                 AccountLog al, FundingNotMatchedModel fnm) {

        userAccountRepository.updateNewInvestmentUserAccount(ua.getInverstmentW(), ua.getBalance(), ua.getRecyclingInterest(), ua.getAddCapitalTotal(), ua.getCapitalTotal(), ua.getUserId());
//        if (i == 0) {
//            System.out.println("1---------i = " + i);
//            return 0;
//        }

        productAccountRepository.save(pa);
        int pId = pa.getpId();
        al.setpId(pId);
        fnm.setfInvestRecordId(pId); // 投资记录ID
        accountLogRepository.save(al);
        matchedModelRepository.save(fnm);
//        System.out.println("2------i = " + i);
        System.out.println(" ==========================ok ");
    }

    public void addExpectedReturn(ExpectedReturn er) {
        expectedReturnRepository.save(er);
    }
}
