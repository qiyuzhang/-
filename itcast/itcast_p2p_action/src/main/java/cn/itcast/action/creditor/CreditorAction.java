package cn.itcast.action.creditor;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.stream.events.EndDocument;

import cn.itcast.service.creditor.CreditorModelService;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import cn.itcast.action.common.BaseAction;
import cn.itcast.domain.creditor.CreditorModel;
import cn.itcast.domain.creditor.CreditorSumModel;
import cn.itcast.utils.FrontStatusConstants;
import cn.itcast.utils.Response;

@Namespace("/creditor")
@Controller
@Scope("prototype")
public class CreditorAction extends BaseAction {

	@Autowired
	private CreditorModelService creditorService;

	// 债权的审核
	@Action("checkCreditor")
	public void checkCreditor() {
		// 1.得到请求参数
		String ids = this.getRequest().getParameter("ids");

		// 处理ids
		String[] id = ids.split(",");

		// 2.调用service完成审核
		creditorService.checkCreditor(id);
		// 3.响应状态1

		try {
			this.getResponse().getWriter().write(Response.build().setStatus("1").toJSON());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	// 查询债权信息---多条件
	@Action("getCreditorlist")
	public void getCreditorlist() {
		this.getResponse().setCharacterEncoding("utf-8");
		// dDebtNo=&dContractNo=&dDebtTransferredDateStart=&dDebtTransferredDateEnd=&dDebtStatus=&dMatchedStatus=&offsetnum=1
		// 1.获取请求参数
		String dDebtNo = this.getRequest().getParameter("dDebtNo"); // 标的编号
																	// --债权的编号
		String dContractNo = this.getRequest().getParameter("dContractNo"); // 借款的id
																			// ---全同编号
		String dDebtTransferredDateStart = this.getRequest().getParameter("dDebtTransferredDateStart"); // 债权转入日期
																										// 开始
		String dDebtTransferredDateEnd = this.getRequest().getParameter("dDebtTransferredDateEnd"); // 债权转入日期
																									// 结束
		String dDebtStatus = this.getRequest().getParameter("dDebtStatus"); // 债权的状态
		String dMatchedStatus = this.getRequest().getParameter("dMatchedStatus"); // 债权的匹配状态
		String offsetnum = this.getRequest().getParameter("offsetnum"); // 页码
		// 2.处理请求参数--将请求参数类型处理并封装到Map中，后续调用service时只需要将map传递就可以。
		Map<String, Object> map = new HashMap<String, Object>();
		if (StringUtils.isNotBlank(dDebtNo)) {
			map.put("dDebtNo", dDebtNo);
		}
		if (StringUtils.isNotBlank(dContractNo)) {
			map.put("dContractNo", dContractNo);
		}
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		if (StringUtils.isNotBlank(dDebtTransferredDateStart)) {
			try {
				map.put("dDebtTransferredDateStart", sdf.parse(dDebtTransferredDateStart));
			} catch (ParseException e) {
				try {
					this.getResponse().getWriter()
							.write(Response.build().setStatus(FrontStatusConstants.PARAM_VALIDATE_FAILED).toJSON());
					return;
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		}

		if (StringUtils.isNotBlank(dDebtTransferredDateEnd)) {
			try {
				map.put("dDebtTransferredDateEnd", sdf.parse(dDebtTransferredDateEnd));
			} catch (ParseException e) {
				try {
					this.getResponse().getWriter()
							.write(Response.build().setStatus(FrontStatusConstants.PARAM_VALIDATE_FAILED).toJSON());
					return;
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		}

		if (StringUtils.isNotBlank(dDebtStatus)) {
			map.put("dDebtStatus", Integer.parseInt(dDebtStatus));
		}
		if (StringUtils.isNotBlank(dMatchedStatus)) {
			map.put("dMatchedStatus", Integer.parseInt(dMatchedStatus));
		}
		// 处理分页信息
		int pageNum = Integer.parseInt(offsetnum); // 页码
		int currentNum = 10;// 每页条数
		int startIndex = (pageNum - 1) * currentNum;
		map.put("currentNum", currentNum);
		map.put("startIndex", startIndex);

		// 3.调用service查询债权信息
		// 3.1查询债权信息--多条件
		List<CreditorModel> cms = creditorService.findCreditorByCondition(map);

		// 注意:需要将债权状态及债权的匹配状态处理
		// <option value="">全部</option>
		// <option value="11301">未审核</option>
		// <option value="11302">已审核</option>
		// <option value="11303">正常还款</option>
		// <option value="11304">已结清</option>
		// <option value="11305">提前结清</option>
		// <option value="11306">结算失败</option>
		for (CreditorModel cm : cms) {
			// 处理债权状态
			if (cm.getDebtStatus() == 11301) {
				cm.setDebtStatusDesc("未审核");
			}
			if (cm.getDebtStatus() == 11302) {
				cm.setDebtStatusDesc("已审核");
			}
			if (cm.getDebtStatus() == 11303) {
				cm.setDebtStatusDesc("正常还款");
			}
			if (cm.getDebtStatus() == 11304) {
				cm.setDebtStatusDesc("已结清");
			}
			if (cm.getDebtStatus() == 11305) {
				cm.setDebtStatusDesc("提前结清");
			}
			if (cm.getDebtStatus() == 11306) {
				cm.setDebtStatusDesc("结算失败");
			}

			// 处理债权匹配状态
			// <option value="11401">部分匹配</option>
			// <option value="11402">完全匹配</option>
			// <option value="11403">未匹配</option>

			if (cm.getMatchedStatus() == 11401) {
				cm.setMatchedStatusDesc("部分匹配");
			}
			if (cm.getMatchedStatus() == 11402) {
				cm.setMatchedStatusDesc("完全匹配");
			}
			if (cm.getMatchedStatus() == 11403) {
				cm.setMatchedStatusDesc("未匹配");
			}

		}

		// 3.2查询债权的统计信息
		List<Object[]> cmsSum = creditorService.findCreditorBySum(map);
		// 将查询的结果封装到CreditorSumModel对象中
		CreditorSumModel csm = new CreditorSumModel();
		csm.setdIdCount(Integer.parseInt(cmsSum.get(0)[0].toString()));
		csm.setdDebtMoneySum((Double.parseDouble(cmsSum.get(0)[1].toString())));
		csm.setdAvailableMoneySum(Double.parseDouble(cmsSum.get(0)[2].toString()));

		List<CreditorSumModel> csms = new ArrayList<CreditorSumModel>();
		csms.add(csm);

		// 4.将查询结果响应到浏览器
		Map<String, Object> data = new HashMap<String, Object>();
		data.put("date", cms);
		data.put("datasum", csms);
		try {
			this.getResponse().getWriter().write(Response.build().setStatus("1").setData(data).toJSON());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Action("download")
	public void download() {
		// 1.将资源读取
		// 1.1得到文件位置
		String path = this.getRequest().getSession().getServletContext()
				.getRealPath("/WEB-INF/excelTemplate/ClaimsBatchImportTemplate.xlsx");
		// 设置下载时两个响应头
		String mimeType = this.getRequest().getSession().getServletContext()
				.getMimeType("ClaimsBatchImportTemplate.xlsx");
		this.getResponse().setHeader("content-type", mimeType);
		this.getResponse().setHeader("content-disposition",
				"attachment;filename=" + (new Date().toLocaleString() + ".xlsx"));
		// 1.2获取输入流
		FileInputStream fis = null;
		try {
			fis = new FileInputStream(path);
			// 2.将资源写出,使用response来获取流
			OutputStream os = this.getResponse().getOutputStream();
			IOUtils.copy(fis, os);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if (fis != null) {
				try {
					fis.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

	}
}
