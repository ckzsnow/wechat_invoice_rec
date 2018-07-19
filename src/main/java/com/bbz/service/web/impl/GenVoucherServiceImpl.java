package com.bbz.service.web.impl;

import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import com.bbz.service.web.IGenVoucherService;
import com.bbz.utils.ExcelTitleUtil;

@Service("genVoucherService")
public class GenVoucherServiceImpl implements IGenVoucherService {
	
	private static final Logger logger = LoggerFactory.getLogger(GenVoucherServiceImpl.class);
	
	@Autowired
	private JdbcTemplate jdbcTemplate;
	
	@SuppressWarnings("unchecked")
	@Override
	public Map<String, String> genVoucher(String userId, String userName, String billDate, boolean isDetail) {
		Map<String, String> retMap = new HashMap<>();
		List<Map<String, Object>> voucherDataList = new ArrayList<>();
		List<Map<String, Object>> invoiceDataList = new ArrayList<>();
		List<Map<String, Object>> faDepreciationDataList = new ArrayList<>();
		Map<Integer, Object> invoiceOutputToExcelMap = new HashMap<>();
		String sqlVoucher = "select * from voucher where user_id=? and bill_date=?";
		String sqlFaDepreciation = "select * from fa_depreciation where user_id=? and bill_date=? and net_value>0";
		String sqlInvoice = "select * from invoice left join invoice_item on invoice_item.invoice_id=invoice.id where invoice.user_id=? and invoice.bill_date=?";
		String sqlvoucherNumMax = "select max(voucher_num) max_voucher_num from voucher where bill_date=?";
		int voucherNumMax = 0;
		try{
			voucherDataList = jdbcTemplate.queryForList(sqlVoucher, userId, billDate);
			invoiceDataList = jdbcTemplate.queryForList(sqlInvoice, userId, billDate);
			faDepreciationDataList = jdbcTemplate.queryForList(sqlFaDepreciation, userId, billDate);
			voucherNumMax = jdbcTemplate.queryForObject(sqlvoucherNumMax, new Object[]{billDate}, Integer.class);
			
			//由invoice和invoice_item的级联结果进行合并摘要，生成invoiceOutputToExcelMap
			//key是invoice的ID,value是对应票据的详细信息，多个摘要会合并成一个摘要，因此
			//invoiceDataList中隶属于同一张票据的摘要会最终合并成一条记录
			for(Map<String, Object> invoiceDataMap : invoiceDataList){
				if(!invoiceOutputToExcelMap.containsKey((Integer)invoiceDataMap.get("id"))) {
					invoiceOutputToExcelMap.put((Integer)invoiceDataMap.get("id"), new HashMap<String, Object>());
					Map<String, Object> map = (Map<String, Object>)invoiceOutputToExcelMap.get((Integer)invoiceDataMap.get("id"));
					map.put("fpdm", (String)invoiceDataMap.get("fpdm"));
					map.put("fphm", (String)invoiceDataMap.get("fphm"));
					map.put("kprq", (String)invoiceDataMap.get("kprq"));
					map.put("invoice_type", (Integer)invoiceDataMap.get("invoice_type") == 0?"普通发票":"专用发票");
					map.put("bill_date", (String)invoiceDataMap.get("bill_date"));
					if(invoiceDataMap.get("abstract_info") != null &&
							!((String)invoiceDataMap.get("abstract_info")).isEmpty()) {
						map.put("abstract_info", (String)invoiceDataMap.get("abstract_info"));
					}
					map.put("hjje", String.valueOf((Double)invoiceDataMap.get("amount")));
					map.put("hjse", String.valueOf((Double)invoiceDataMap.get("tax")));
					map.put("is_fa", (Integer)invoiceDataMap.get("is_fa") == 0 ? "否":"是");
					map.put("supply_name", (String)invoiceDataMap.get("supply_name"));
					map.put("invoice_inout_type", (Integer)invoiceDataMap.get("invoice_inout_type") == 0 ? "进项票":"销项票");
					map.put("create_time", (String)invoiceDataMap.get("create_time"));
				} else {
					if(invoiceDataMap.get("abstract_info") != null &&
							!((String)invoiceDataMap.get("abstract_info")).isEmpty()) {
						Map<String, Object> map = (Map<String, Object>)invoiceOutputToExcelMap.get((Integer)invoiceDataMap.get("id"));
						if(map.containsKey("abstract_info")) {
							map.put("abstract_info", (String)map.get("abstract_info") + "," + (String)invoiceDataMap.get("abstract_info"));
						} else {
							map.put("abstract_info", (String)invoiceDataMap.get("abstract_info"));
						}
					}
				}
			}
			
			//生成excel
			//第一步，创建excel，生成invoice对应的sheet
			HSSFWorkbook workbook = new HSSFWorkbook();
			HSSFSheet invoiceSheet = workbook.createSheet("invoice");
			invoiceSheet.autoSizeColumn(1, true);
			HSSFRow row = invoiceSheet.createRow(0);
			for(Entry<Integer, String> entry : ExcelTitleUtil.invoiceExcelTitleMap.entrySet()){
				HSSFCell cell = row.createCell(entry.getKey());
				cell.setCellType(HSSFCell.CELL_TYPE_STRING);
				cell.setCellValue(entry.getValue());
				invoiceSheet.setColumnWidth(entry.getKey(), entry.getValue().getBytes().length*256);
			}
			int rowIndex = 1;
			for(Entry<Integer, Object> invoiceEntry : invoiceOutputToExcelMap.entrySet()){
				row = invoiceSheet.createRow(rowIndex);
				for(int colIndex = 0; colIndex<((Map<String, String>)invoiceEntry).size(); colIndex++){
					HSSFCell cell = row.createCell(colIndex);
					cell.setCellType(HSSFCell.CELL_TYPE_STRING);
					String colValue = ((Map<String, String>)invoiceEntry.getValue()).get(ExcelTitleUtil.invoiceExcelDataMap.get(colIndex));
					cell.setCellValue(colValue);
					invoiceSheet.setColumnWidth(colIndex, colValue.getBytes().length*256);
				}
				rowIndex++;
			}
			
			//第二步，生成voucher对应的sheet
			HSSFSheet voucherSheet = workbook.createSheet("voucher");
			voucherSheet.autoSizeColumn(1, true);
			row = voucherSheet.createRow(0);
			for(Entry<Integer, String> entry : ExcelTitleUtil.voucherExcelTitleMap.entrySet()){
				HSSFCell cell = row.createCell(entry.getKey());
				cell.setCellType(HSSFCell.CELL_TYPE_STRING);
				cell.setCellValue(entry.getValue());
				invoiceSheet.setColumnWidth(entry.getKey(), entry.getValue().getBytes().length*256);
			}
			rowIndex = 1;
			for(Map<String, Object> voucherEntry : voucherDataList){
				row = voucherSheet.createRow(rowIndex);
				for(int colIndex = 0; colIndex<ExcelTitleUtil.voucherExcelDataMap.size(); colIndex++){
					HSSFCell cell = row.createCell(colIndex);
					cell.setCellType(HSSFCell.CELL_TYPE_STRING);
					String colValue = "";
					if(voucherEntry.containsKey(ExcelTitleUtil.voucherExcelDataMap.get(colIndex))){
						colValue = String.valueOf(voucherEntry.get(ExcelTitleUtil.voucherExcelDataMap.get(colIndex)));
					}
					if(!isDetail && ("supply_name").equals(ExcelTitleUtil.voucherExcelDataMap.get(colIndex))) colValue = "";
					cell.setCellValue(colValue);
					invoiceSheet.setColumnWidth(colIndex, colValue.getBytes().length*256);
				}
				rowIndex++;
			}
			for(Map<String, Object> faDepreciationEntry : faDepreciationDataList){
				row = voucherSheet.createRow(rowIndex);
				Map<String, String> infoMap = new HashMap<>();
				String[] billDateInfos = billDate.split("-");
				String abstractInfo = billDateInfos[0] + "年" + billDateInfos[1] + "月" + (String)faDepreciationEntry.get("abstract_info") + "折旧";
				infoMap.put("bill_date", billDate);
				infoMap.put("voucher_type", "转账凭证");
				infoMap.put("voucher_num", String.valueOf(voucherNumMax+1));
				infoMap.put("voucher_attachment_count", "1");
				infoMap.put("abstract_info", abstractInfo);
				infoMap.put("debit_amount", String.valueOf(faDepreciationEntry.get("monthly_depreciation")));
				infoMap.put("account_title_code", "560212");
				infoMap.put("account_title_name", "折旧");
				infoMap.put("producer_name", userName);
				infoMap.put("supply_name", (String)faDepreciationEntry.get("supply_name"));
				for(int colIndex = 0; colIndex<ExcelTitleUtil.voucherExcelDataMap.size(); colIndex++){
					HSSFCell cell = row.createCell(colIndex);
					cell.setCellType(HSSFCell.CELL_TYPE_STRING);
					String colValue = "";
					if(infoMap.containsKey(ExcelTitleUtil.voucherExcelDataMap.get(colIndex))){
						colValue = String.valueOf(infoMap.get(ExcelTitleUtil.voucherExcelDataMap.get(colIndex)));
					}
					if(!isDetail && ("supply_name").equals(ExcelTitleUtil.voucherExcelDataMap.get(colIndex))) colValue = "";
					cell.setCellValue(colValue);
					invoiceSheet.setColumnWidth(colIndex, colValue.getBytes().length*256);
				}
				infoMap.remove("debit_amount");
				infoMap.put("lender_amount", String.valueOf(faDepreciationEntry.get("monthly_depreciation")));
				infoMap.put("account_title_code", "1602");
				infoMap.put("account_title_name", "累计折旧");
				rowIndex++;
				for(int colIndex = 0; colIndex<ExcelTitleUtil.voucherExcelDataMap.size(); colIndex++){
					HSSFCell cell = row.createCell(colIndex);
					cell.setCellType(HSSFCell.CELL_TYPE_STRING);
					String colValue = "";
					if(infoMap.containsKey(ExcelTitleUtil.voucherExcelDataMap.get(colIndex))){
						colValue = String.valueOf(infoMap.get(ExcelTitleUtil.voucherExcelDataMap.get(colIndex)));
					}
					if(!isDetail && ("supply_name").equals(ExcelTitleUtil.voucherExcelDataMap.get(colIndex))) colValue = "";
					cell.setCellValue(colValue);
					invoiceSheet.setColumnWidth(colIndex, colValue.getBytes().length*256);
				}
				rowIndex++;
				voucherNumMax++;
			}
			
			//将生成的excel保存至硬盘
			FileOutputStream fos = new FileOutputStream("/home/"+userId+".xls");
			workbook.write(fos);
			fos.flush();
			fos.close();
			workbook.close();
			retMap.put("error_code", "000000");
			retMap.put("error_msg", "");
			retMap.put("xls_path", "/home/"+userId+".xls");
		}catch(Exception e) {
			logger.error("genVoucher error : {}", e.toString());
			retMap.put("error_code", "200101");
			retMap.put("error_msg", "获取凭证失败。");
		}
		return retMap;
	}
}