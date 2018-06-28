package com.bbz.utils;

import java.util.HashMap;
import java.util.Map;

public class ExcelTitleUtil {

	public static Map<Integer, String> invoiceExcelTitleMap = new HashMap<>();
	
	public static Map<Integer, String> invoiceExcelDataMap = new HashMap<>();
	
	public static Map<Integer, String> voucherExcelTitleMap = new HashMap<>();
	
	public static Map<Integer, String> voucherExcelDataMap = new HashMap<>();
	
	static {
		invoiceExcelTitleMap.put(0, "发票代码");
		invoiceExcelTitleMap.put(1, "发票号码");
		invoiceExcelTitleMap.put(2, "开票日期");
		invoiceExcelTitleMap.put(3, "发票类型");
		invoiceExcelTitleMap.put(4, "会计期间");
		invoiceExcelTitleMap.put(5, "摘要信息");
		invoiceExcelTitleMap.put(6, "合计金额");
		invoiceExcelTitleMap.put(7, "合计税额");
		invoiceExcelTitleMap.put(8, "是否固定资产");
		invoiceExcelTitleMap.put(9, "供应商名称");
		invoiceExcelTitleMap.put(10, "进销类型");
		invoiceExcelTitleMap.put(11, "上传时间");
		
		invoiceExcelDataMap.put(0, "fpdm");
		invoiceExcelDataMap.put(1, "fphm");
		invoiceExcelDataMap.put(2, "kprq");
		invoiceExcelDataMap.put(3, "invoice_type");
		invoiceExcelDataMap.put(4, "bill_date");
		invoiceExcelDataMap.put(5, "abstract_info");
		invoiceExcelDataMap.put(6, "hjje");
		invoiceExcelDataMap.put(7, "hjse");
		invoiceExcelDataMap.put(8, "is_fa");
		invoiceExcelDataMap.put(9, "supply_name");
		invoiceExcelDataMap.put(10, "invoice_inout_type");
		invoiceExcelDataMap.put(11, "create_time");
				
		voucherExcelTitleMap.put(0, "日期");
		voucherExcelTitleMap.put(1, "凭证类型");
		voucherExcelTitleMap.put(2, "凭证编号");
		voucherExcelTitleMap.put(3, "附件张数");
		voucherExcelTitleMap.put(4, "摘要");
		voucherExcelTitleMap.put(5, "科目代码");
		voucherExcelTitleMap.put(6, "借方");
		voucherExcelTitleMap.put(7, "贷方");
		voucherExcelTitleMap.put(8, "科目名称");
		voucherExcelTitleMap.put(9, "外币");
		voucherExcelTitleMap.put(10, "汇率");
		voucherExcelTitleMap.put(11, "制单人");
		voucherExcelTitleMap.put(12, "结算方式");
		voucherExcelTitleMap.put(13, "票号");
		voucherExcelTitleMap.put(14, "发生日期");
		voucherExcelTitleMap.put(15, "部门");
		voucherExcelTitleMap.put(16, "个人");
		voucherExcelTitleMap.put(17, "供应商名称");
		voucherExcelTitleMap.put(18, "业务员");
		voucherExcelTitleMap.put(19, "项目");
		
		voucherExcelDataMap.put(0, "bill_date");
		voucherExcelDataMap.put(1, "voucher_type");
		voucherExcelDataMap.put(2, "voucher_num");
		voucherExcelDataMap.put(3, "voucher_attachment_count");
		voucherExcelDataMap.put(4, "abstract_info");
		voucherExcelDataMap.put(5, "account_title_code");
		voucherExcelDataMap.put(6, "debit_amount");
		voucherExcelDataMap.put(7, "lender_amount");
		voucherExcelDataMap.put(8, "account_title_name");
		voucherExcelDataMap.put(9, "外币");
		voucherExcelDataMap.put(10, "汇率");
		voucherExcelDataMap.put(11, "producer_name");
		voucherExcelDataMap.put(12, "结算方式");
		voucherExcelDataMap.put(13, "票号");
		voucherExcelDataMap.put(14, "发生日期");
		voucherExcelDataMap.put(15, "部门");
		voucherExcelDataMap.put(16, "个人");
		voucherExcelDataMap.put(17, "supply_name");
		voucherExcelDataMap.put(18, "业务员");
		voucherExcelDataMap.put(19, "项目");
	}
	
}
