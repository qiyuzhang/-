package cn.itcast.action.charges;

import cn.itcast.action.common.BaseAction;
import cn.itcast.action.filter.GetHttpResponseHeader;
import cn.itcast.cache.BaseCacheService;
import cn.itcast.domain.accountLog.AccountLog;
import cn.itcast.domain.bankCardInfo.BankCardInfo;
import cn.itcast.domain.matchManagement.WeigthRule;
import cn.itcast.domain.product.*;
import cn.itcast.domain.user.UserAccountModel;
import cn.itcast.service.bank.BankService;
import cn.itcast.service.charge.ChargeService;
import cn.itcast.service.message.NotificationService;
import cn.itcast.service.product.ProductService;
import cn.itcast.service.user.UserService;
import cn.itcast.service.weight.WeightRuleService;
import cn.itcast.utils.*;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;

@Controller
@Scope("prototype")
@Namespace("/charges")
public class ChargesAction extends BaseAction {
    private static Logger logger = Logger.getLogger(ChargesAction.class);
    @Autowired
    BaseCacheService baseCacheService;
    @Autowired
    BankService bankService;
    @Autowired
    ChargeService chargeService;
    @Autowired
    UserService userService;
    @Autowired
    ProductService productService;
    @Autowired
    WeightRuleService ruleService;
    @Autowired
    NotificationService notificationService;

    //充值操作
    @Action("charge")
    public void charge() throws IOException {
        //得到请求参数
        String chargeMoney = this.getRequest().getParameter("chargeMoney");//充值金额

        //设置响应数据编码
        this.getResponse().setCharacterEncoding("utf-8");

        String token = GetHttpResponseHeader.getHeadersInfo(this.getRequest());

        if (StringUtils.isEmpty(token)) {
            //token过期
            this.getResponse().getWriter().write(Response.build().setStatus(FrontStatusConstants.NULL_TOKEN).toJSON());
            return;
        }
        Map<String, Object> hmap = baseCacheService.getHmap(token);
        if (hmap == null && hmap.size() == 0) {
            //用户未登录
            this.getResponse().getWriter().write(Response.build().setStatus(FrontStatusConstants.NOT_LOGGED_IN).toJSON());
            return;
        }
        Integer uid = (Integer) hmap.get("id");

        //根据用户id得到银行卡信息
        BankCardInfo bci = bankService.findBankByUserId(uid);
        String bankCardNum = bci.getBankCardNum();

        //调用service完成充值操作
        chargeService.recharge(bankCardNum, Double.parseDouble(chargeMoney), uid);
        this.getResponse().getWriter().write(Response.build().setStatus("1").toJSON());
    }

    /**
     * @return void    插入状态，成功：1、失败：0
     * @throws
     * @Title: addMounthTake
     * 方法描述：月取计划
     * @version 1.0
     * //     * @param pProductId 产品id
     * //     * @param pAmount 购买金额
     * //     * @param pDeadline 购买期限
     * //     * @param pExpectedAnnualIncome 预期年化收益
     * //     * @param pMonthInterest 每月盈取利息
     * //     * @param pMonthlyExtractInterest 每月提取利息
     */
    @Action("addMayTake")
    public synchronized void addMayTake() {
        try {
            //获取请求信息
            String pDeadline = this.getRequest().getParameter("pDeadline");
            String pProductId = this.getRequest().getParameter("pProductId");
            String pAmount = this.getRequest().getParameter("pAmount");
            String pExpectedAnnualIncome = this.getRequest().getParameter("pExpectedAnnualIncome");
            String pMonthInterest = this.getRequest().getParameter("pMonthInterest");
            String pMonthlyExtractInterest = this.getRequest().getParameter("pMonthlyExtractInterest");

            String token = GetHttpResponseHeader.getHeadersInfo(getRequest());
            // 判断token是否为空
            if (StringUtils.isEmpty(token)) { // 判断token是否为空
                getResponse().getWriter().write(Response.build().setStatus("13").toJSON());
                return;
            }
            Map<String, Object> hmap = baseCacheService.getHmap(token);
            if (hmap == null || hmap.isEmpty()) {//判断用户是否为空
                getResponse().getWriter().write(Response.build().setStatus("15").toJSON());
                return;
            }

            int userId = (Integer) hmap.get("id");
            String name = (String) hmap.get("userName");
            Integer mainId = null; //账户的用户ID
            Integer earntype = null; //收益率类型
            Integer ptype = null; //产品类型
            String productName = null;
            Integer padvancetype = null; //提前赎回率类型
            Double pAdvanceRedemption = null;//提前赎回率
            Double uBalance = null; //账户可用余额
            Double uRecyclingInterest = null;//月取总额
            Double uInvestmentA = null;//已投资总额
            Double expectedAnnualIncome = null;//预期年化收益
            Double monthlyExtractInterest = null;//每月提取利息
            Double monthInterest = null;//每月盈取利息
            Double inverstmentW = null;//总计代收本金
            Double interestTotal = null;//总计代收利息
            Date date = new Date();//获取当前时间
            //获取结束日期
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.MONTH, +Integer.parseInt(pDeadline));
            Date endate = calendar.getTime();
            //获取预期年化收益
            ProductEarningRate rate = userService.selectYearInterest(Integer.parseInt(pProductId),Integer.parseInt(pDeadline));
            //获取每月盈取利息
            String monthWinInterestStr = BigDecimalUtil.monthWinInterest(pAmount, Double.toString(rate.getIncomeRate()));
            //校验
            // 购买金额名不能为空
            if (StringUtils.isEmpty(pProductId)) {
                getResponse().getWriter().write(Response.build().setStatus("18").toJSON());
                return;
            }
            // 购买金额名不能为空
            if (StringUtils.isEmpty(pAmount)) {
                getResponse().getWriter().write(Response.build().setStatus("19").toJSON());
                return;
            }
            // 购买金额最小值大于100
            if (Double.parseDouble(pAmount) < 100) {
                getResponse().getWriter().write(Response.build().setStatus("96").toJSON());
                return;
            }
            // 购买金额为100的整数倍
            if (Double.parseDouble(pAmount) % 100 != 0) {
                getResponse().getWriter().write(Response.build().setStatus("97").toJSON());
                return;
            }

            UserAccountModel account = userService.findAccountByUserId(userId);
            if (account == null) {
                logger.debug(account + "数据为空");
                getResponse().getWriter().write(Response.build().setStatus("2").toJSON());
                return;
            } else {
                mainId = account.getUserId(); //获取账户的用户id
                uBalance = account.getBalance(); //获取账户可用余额
                uRecyclingInterest = account.getRecyclingInterest();//月取总额
                uInvestmentA = account.getInverstmentA();//已投资总额
                inverstmentW = account.getInverstmentW();//总计代收本金
                interestTotal = account.getInterestTotal();//总计代收利息
            }

            Product product = productService.findById(Long.valueOf(pProductId));
            if (product == null) {
                logger.debug(product + "数据为空");
                getResponse().getWriter().write(Response.build().setStatus("2").toJSON());
                return;
            } else {
                earntype = product.getEarningType(); //获取收益率类型
                ptype = product.getProTypeId(); //获取产品类型
                padvancetype = product.getEarlyRedeptionType(); //获取提前赎回率类型
                productName = product.getProductName();

            }
            //新投资权重
            WeigthRule newInvestmentWeight = ruleService.getRuleByType(124);
            //资产总额
            Double uTotal = uBalance + inverstmentW + interestTotal;
            // 可用金额不足
            if ("".equals(uBalance) || uBalance <= 0.00 || Double.parseDouble(pAmount) - uBalance > 0) {
                getResponse().getWriter().write(Response.build().setStatus("17").toJSON());
                return;
            }
            // 账户总额不足
            if ("".equals(uTotal) || uTotal <= 0.00 || uBalance - uTotal > 0) {
                getResponse().getWriter().write(Response.build().setStatus("17").toJSON());
                return;
            }
            // 期限不能小于十二个月
            if (Integer.parseInt(pDeadline) < 12) {
                getResponse().getWriter().write(Response.build().setStatus("95").toJSON());
                return;
            }
            // 预期年化收益为空或与数据库中不等,重新计算
            if (StringUtils.isEmpty(pExpectedAnnualIncome) || Double.parseDouble(pExpectedAnnualIncome) != rate.getIncomeRate()) {
                expectedAnnualIncome = rate.getIncomeRate();
            } else {
                expectedAnnualIncome = Double.parseDouble(pExpectedAnnualIncome);
            }
            // 每月盈取利息为空或与计算值不等,重新计算
            if (StringUtils.isEmpty(pMonthInterest) || Double.parseDouble(pMonthInterest) != Double.parseDouble(monthWinInterestStr)) {
                monthInterest = Double.parseDouble(monthWinInterestStr);
            } else {
                monthInterest = Double.parseDouble(pMonthInterest);
            }
            // 每月提取利息为空
            if (pMonthlyExtractInterest == null || "".equals(pMonthlyExtractInterest)) {
                monthlyExtractInterest = 0.00;
            } else {
                monthlyExtractInterest = Double.parseDouble(pMonthlyExtractInterest);
            }
            // 每月提取利息小于每月盈取利息
            if (monthlyExtractInterest > new BigDecimal(Double.parseDouble(monthWinInterestStr)).setScale(2, RoundingMode.DOWN).doubleValue()) {
                getResponse().getWriter().write(Response.build().setStatus("21").toJSON());
                return;
            }
            //到期应回总本息
            String pEndInvestTotalMoney = BigDecimalUtil.endInvestTotalMoney(pAmount, pDeadline, Double.toString(expectedAnnualIncome), pMonthlyExtractInterest);
            //月取代收利息
            String mayReplaceInterestIncome = BigDecimalUtil.sub(pEndInvestTotalMoney.toString(), pAmount).toString();
            //交易后金额
            BigDecimal endTotal = BigDecimalUtil.sub(uBalance.toString(), pAmount);
            //账户可用余额
            BigDecimal balance = BigDecimalUtil.sub(uBalance.toString(), pAmount);
            //月取总额
            BigDecimal recyclingInterest = BigDecimalUtil.add(uRecyclingInterest.toString(), pAmount);
            //已投资总额
            BigDecimal investmentA = BigDecimalUtil.add(uInvestmentA.toString(), pAmount);
            //总计代收本金
            BigDecimal inverstmentw = BigDecimalUtil.add(inverstmentW.toString(), pAmount);
            //总计代收利息
            String interesttotal = BigDecimalUtil.add(interestTotal.toString(), mayReplaceInterestIncome).toString();
            //随机码
            String randomNO = RandomNumberUtil.randomNumber(date);

            //用户账户表
            ProductAccount productAccount = new ProductAccount();
            productAccount.setpUid(Long.parseLong(userId + "")); //用户ID
            productAccount.setpSerialNo("TZNO" + randomNO);//投资编号
            productAccount.setpProductId(Long.parseLong(pProductId));//产品ID
            productAccount.setpProductType(ptype);//产品类型
            productAccount.setpProductName(productName);
            productAccount.setpEarningsType(earntype);//收益率类型
            productAccount.setpAdvanceRedemption(pAdvanceRedemption);//提前赎回利率
            productAccount.setpAmount(Double.parseDouble(pAmount));//金额
            productAccount.setpDeadline(Integer.parseInt(pDeadline));//选择期限
            productAccount.setpExpectedAnnualIncome(expectedAnnualIncome);//预期年化收益
            productAccount.setpMonthInterest(monthInterest);//每月盈取利息
            productAccount.setpMonthlyExtractInterest(monthlyExtractInterest);//每月提取利息
            productAccount.setpBeginDate(date);//开始时间
            productAccount.setpEndDate(endate);//结束日期
            productAccount.setpStatus(10901);//投资状态
            productAccount.setpAvailableBalance(Double.parseDouble(pAmount));//可用余额
            productAccount.setpEndInvestTotalMoney(Double.valueOf(pEndInvestTotalMoney));//到期应回总本息
            productAccount.setpProspectiveEarnings(0.0);//预期收益
            productAccount.setpCurrentMonth(1);//当前期
            productAccount.setpCurrentRealTotalMoney(0.0);//当前期实回总本息
            productAccount.setpFrozenMoney(0.0);//冻结金额
            productAccount.setpEarnedInterest(0.0);//以赚取利息
            productAccount.setpDeductInterest(0.0);//扣去利息
            productAccount.setpNotInvestMoney(0.0);//未投资金额

            //修改用户账户信息
            UserAccountModel userAccount = new UserAccountModel();
            userAccount.setUserId(userId);//用户id
            userAccount.setBalance(balance.doubleValue());//账户可用余额
            userAccount.setInverstmentW(inverstmentw.doubleValue());//总计代收本金
            userAccount.setInterestTotal(BigDecimalUtil.formatNdecimal(new BigDecimal(interesttotal), 2).doubleValue());//总计代收利息
            userAccount.setRecyclingInterest(recyclingInterest.doubleValue());//月取总额
            userAccount.setInterestA(investmentA.doubleValue());//已投资总额

            //用户交易记录表 -- 类似交易记录日志
            AccountLog accountLog = new AccountLog();
            accountLog.setaUserId(userId);//用户id
            accountLog.setaMainAccountId(mainId); //主账户id
            accountLog.setaReceiveOrPay(11002);//收付
            accountLog.setaCurrentPeriod(1);//当前期
            accountLog.setaBeforeTradingMoney(uBalance);//交易前金额
            accountLog.setaAmount(Double.parseDouble(pAmount));//交易金额
            accountLog.setaAfterTradingMoney(endTotal.doubleValue()); //交易后金额
            accountLog.setaDate(date);//时间
            accountLog.setaType(133);//交易类型
            accountLog.setaTransferStatus(10404);//交易状态
            accountLog.setaTransferSerialNo("LSNO" + randomNO);//交易流水号
            accountLog.setaDescreption("月取计划" + "TZNO" + randomNO);//交易详情

            //投资的时候往待匹配的表中插入相应记录
            FundingNotMatchedModel fnm = new FundingNotMatchedModel();
            fnm.setfNotMatchedMoney(Double.parseDouble(pAmount));//待匹配金额
            fnm.setfFoundingType(124);//资金类型 -- 124  新增投资
            fnm.setfFoundingWeight(newInvestmentWeight.getWeigthValue());//资金权重
            fnm.setfIsLocked(10901);//是否锁定  --- 待匹配
            fnm.setfCreateDate(date);//创建时间

            productService.addProductAccount(productAccount, userAccount, accountLog, fnm);
//            if (result == 0) {
//                getResponse().getWriter().write(Response.build().setStatus("17").toJSON());
//                return;
//            }

            //获取月取预计收益值   数据存入T_USER_Expected_return表中
            ExpectedReturn er = new ExpectedReturn();
            for (int k = 0; k < Integer.parseInt(pDeadline); k++) {
                String yearMonth = TimestampUtils.nextMonth(date.getYear(), date.getMonth(), k);
                er.setExpectedDate(yearMonth);//年月
                er.setExpectedMoney(monthInterest);//月盈利
                er.setUserId(userId);//用户id
                er.setCreateDate(date);//当前时间
                er.setProductId(Integer.parseInt(pProductId));//产品id
                productService.addExpectedReturn(er);
            }
            //信息
            String message = "【传智P2P】亲爱的" + name + "，您已加入“月取计划”投资编号TZNO" + randomNO + "，您的投资款¥" + Double.parseDouble(pAmount) + "已在您的账户冻结，预计将于明日开始计息。";
//            notificationService.sendMessage(String.valueOf(userId), "投资成功", message);
            System.out.println("message = " + message);

            getResponse().getWriter().write(Response.build().setStatus("1").toJSON());
        } catch (Exception e) {
            logger.error("新增月取计划失败");
            try {
                getResponse().getWriter().write(Response.build().setStatus("0").toJSON());
            } catch (IOException e1) {
                logger.error(e1.getMessage());
            }
        }
    }

    // 理财产品购买
//    @Action("addMayTake")
//    public void addMayTake() throws IOException {
//        // 1.得到token
//        String token = GetHttpResponseHeader.getHeadersInfo(this.getRequest());
//        Map<String, Object> hmap = baseCacheService.getHmap(token);
//        Integer userId = (Integer) hmap.get("id");//用户id
//        String username = (String) hmap.get("userName");
//
//        // 2.获取请求参数
//        String pProductId = this.getRequest().getParameter("pProductId"); // 理财产品id
//        String pAmount = this.getRequest().getParameter("pAmount"); // 投资金额
//        String pDeadline = this.getRequest().getParameter("pDeadline"); // 期限
//        String pExpectedAnnualIncome = this.getRequest().getParameter("pExpectedAnnualIncome"); // 年化收益
//        String pMonthInterest = this.getRequest().getParameter("pMonthInterest"); // 月赢取利息
//        String pMonthlyExtractInterest = this.getRequest().getParameter("pMonthlyExtractInterest"); // 每月提取利息
//
//        // 总本息
//        String endInvestTotalMoney = BigDecimalUtil.endInvestTotalMoney(pAmount, pDeadline, pExpectedAnnualIncome, pMonthlyExtractInterest);
//
//        // 本次待收利息 =总本息-投资金额
//        BigDecimal mayInterrestIncome = BigDecimalUtil.sub(endInvestTotalMoney, pAmount);
//
//        // 3.封装用户帐户表信息
//        // 先查询用户帐户表数据
//        UserAccountModel userAccount = userService.findAccountByUserId(userId);
//        // a. 修改帐户中的余额 现余额=原余额-投资金额
//        BigDecimal _balance = BigDecimalUtil.sub(userAccount.getBalance(), Double.parseDouble(pAmount));
//        // b. 总计待收本金 =原总计待收本金+投资金额
//        BigDecimal _inverstmentW = BigDecimalUtil.add(userAccount.getInverstmentW(), Double.parseDouble(pAmount));
//        // c. 总计待收利息 =原总计待收利息+本次待收利息
//        BigDecimal _interestTotal = BigDecimalUtil.add(userAccount.getInterestTotal(),
//                mayInterrestIncome.doubleValue());
//        // d. 月取计划投资总额 原月取总额+投资金额
//        BigDecimal _recyclingInterest = BigDecimalUtil.add(userAccount.getRecyclingInterest(),
//                Double.parseDouble(pAmount));
//        // e. 已投资总额 原投资总额+投资金额
//        BigDecimal _inverstmentA = BigDecimalUtil.add(userAccount.getInverstmentA(), Double.parseDouble(pAmount));
//
//        UserAccountModel uam = new UserAccountModel();
//        uam.setBalance(_balance.doubleValue());
//        uam.setInverstmentW(_inverstmentW.doubleValue());
//        uam.setInterestTotal(_interestTotal.doubleValue());
//        uam.setRecyclingInterest(_recyclingInterest.doubleValue());
//        uam.setInverstmentA(_inverstmentA.doubleValue());
//        uam.setId(userAccount.getId());
//
//        // 4.ProductAccount表数据封装
//        ProductAccount pa = new ProductAccount();
//
//        // 它需要产品信息 ---从请求参数中可以获取产品id,从数据库中查询出产品信息
//        Product product = productService.findById(Long.valueOf(pProductId));
//        pa.setpProductName(product.getProductName());
//        pa.setpProductId(product.getProId());
//        // 它需要用户信息--token
//        pa.setpUid((long) userId);
//        pa.setUsername(username);
//        // 本身信息
//        // a. 开始时间 ---new Date()
//        Date date = new Date();
//        pa.setpBeginDate(date);
//        // b. 结束时间 ---new Date()+投资期限
//        Calendar c = Calendar.getInstance();
//        c.add(Calendar.MONTH, Integer.parseInt(pDeadline));
//        pa.setpEndDate(c.getTime());
//        // c. 投资编号—“TZNO”+随机id它是根据时间生成
//        String randomNum = RandomNumberUtil.randomNumber(date);
//        pa.setpSerialNo("TZNO" + randomNum);
//        // d. 投资金额----请求参数
//        pa.setpAmount(Double.parseDouble(pAmount));
//        // e. 投资期限---请求参数
//        pa.setpDeadline(Integer.parseInt(pDeadline));
//        // f. 年化率-----请求参数
//        pa.setpExpectedAnnualIncome(Double.parseDouble(pExpectedAnnualIncome));
//        // g. 月利息-----请求参数
//        pa.setpMonthInterest(Double.parseDouble(pMonthInterest));
//        // h. 月提取利息----请求参数
//        pa.setpMonthlyExtractInterest(Double.parseDouble(pMonthlyExtractInterest));
//        // i. 可用余额-----在用户帐户表中有
//        pa.setpAvailableBalance(_balance.doubleValue());
//        // j. 到期收回总本金----在用户帐户表中有
//        pa.setpEndInvestTotalMoney(_inverstmentW.doubleValue());
//        // k. 待匹配状态---从匹配状态工具类中获取 InvestStatus
//        pa.setpStatus(10901);
//        // l. 还有其它项
//        pa.setaCurrentPeriod(1);
//
//        // 5.交易流水
//        AccountLog accountLog = new AccountLog();
//        // 1.需要用户id
//        accountLog.setaUserId(userId);
//        // 2.主帐户id记录成用户id
//        accountLog.setaMainAccountId(userId);
//        // 3.需要投资记录的id----就是ProductAccount主键
//        // 4.当前期----第一期
//        accountLog.setaCurrentPeriod(1);
//        // 5.收付-----从工具类中获取InvestTradeType. PAY
//        accountLog.setaReceiveOrPay(11002);
//        // 6.交易流水号 LSNO+随机id
//        accountLog.setaTransferSerialNo("LSNO" + randomNum);
//        // 7.交易时间 new Date()
//        accountLog.setaDate(date);
//        // 8.交易类型 FundsFlowType. INVEST_TYPE
//        accountLog.setaType(133);
//        // 9.交易状态 FundsFlowType. INVEST_SUCCESS
//        accountLog.setaTransferStatus(10404);
//        // 10.交易前金额 记录的是交易前的余额
//        accountLog.setaBeforeTradingMoney(userAccount.getBalance());
//        ;
//        // 11.交易金额 投资的金额
//        accountLog.setaAmount(Double.parseDouble(pAmount));
//        // 12.交易后金额 记录的是交易后的余额
//        accountLog.setaAfterTradingMoney(_balance.doubleValue());
//        // 13.交易详情 月取操作+投资流水号
//        accountLog.setaDescreption("月取计划TZNO" + randomNum);
//
//        // 6.向待匹配资金表中插入数据
//        FundingNotMatchedModel fnmm = new FundingNotMatchedModel();
//        // 1.投资记录id---就是ProductAccount的id
//        // 2.待匹配金额----就是投资金额
//        fnmm.setfNotMatchedMoney(Double.parseDouble(pAmount));
//        // 3.资金类型 124 新增投资
//        fnmm.setfFoundingType(124);
//        // 4.投资时间 new Date
//        fnmm.setfCreateDate(date);
//        // 5.权重
//        WeigthRule wr = ruleService.getRuleByType(124);
//        fnmm.setfFoundingWeight(wr.getWeigthValue());
//        fnmm.setfIsLocked(10901);
//        fnmm.setUserId(userId);
//
//        System.out.println("---------------------------------------");
//        System.out.println("fnmm = " + fnmm.toString());
//
//        // 7.操作
//        productService.addProductAccount(pa, uam, accountLog, fnmm);
//
//        System.out.println("pDeadline = " + pDeadline);
//
//        // 8.预期收益操作
//        for (int i = 0; i < Integer.parseInt(pDeadline); i++) {
//            ExpectedReturn er = new ExpectedReturn();
//            // 封装数据
//            // 1. 用户id
//            er.setUserId(userId);
//            // 2. 产品id
//            er.setProductId((int) (product.getProId()));
//            // 3. 投资记录id
//            er.setInvestRcord(pa.getpId());
//            // 4. 收益日期 当前月份+1
//            er.setExpectedDate(TimestampUtils.nextMonth(date.getYear(), date.getMonth(), i));
//            // 5. 收益金额、-----从请求参数中获取
//            er.setExpectedMoney(Double.parseDouble(pMonthInterest));
//            // 6. 创建日期 new Date()
//            er.setCreateDate(date);
//            productService.addExpectedReturn(er);
//            System.out.println(i);
//        }
//        // 9.发送短信，发送邮件
//        System.out.println("完成理财产品购买操作");
//        // 10响应成功
//        this.getResponse().getWriter().write(Response.build().setStatus("1").toJSON());
//    }
}
