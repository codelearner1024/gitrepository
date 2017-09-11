package cn.itcast.jk.action.cargo;

import java.io.ByteArrayOutputStream;
import java.util.List;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.struts2.ServletActionContext;

import cn.itcast.jk.action.BaseAction;
import cn.itcast.jk.domain.Contract;
import cn.itcast.jk.domain.ContractProduct;
import cn.itcast.jk.service.ContractProductService;
import cn.itcast.jk.util.DownloadUtil;
import cn.itcast.jk.util.MyWorkBookUtil;
import cn.itcast.jk.util.Page;

public class OutProductAction extends BaseAction {

	private ContractProductService contractProductService;

	public void setContractProductService(ContractProductService contractProductService) {
		this.contractProductService = contractProductService;
	}

	Page<Contract> page = new Page<Contract>();

	public Page<Contract> getPage() {
		return page;
	}

	public void setPage(Page<Contract> page) {
		this.page = page;
	}

	private String inputDate;

	public String getInputDate() {
		return inputDate;
	}

	public void setInputDate(String inputDate) {
		this.inputDate = inputDate;
	}

	public String toedit() throws Exception {
		return "toedit";
	}

	/**
	 * 打印页面传递过来的参数船期的所有出货单 以contract_product为中心表进行打印 打印出船期为某年某月的所有货物的详单
	 * 使用自己创建excel的方式
	 * 
	 * @return
	 * @throws Exception
	 */

	public String print() throws Exception {
		System.out.println(inputDate);
		// 查询货物对象中的合同对象中字段shiptime为参数值的所有货物对象
		// String hql = "select to_char(shipTime,'YYYY-MM') as '船期' from
		// Contract where '船期'= ?";
		String hql = "from ContractProduct where to_char(contract.shipTime,'yyyy-MM') ='" + inputDate + "'";
		// 获取货物对象集
		List<ContractProduct> list = contractProductService.find(hql, ContractProduct.class, null);
		System.out.println("----------------------------------制作大标题------------------------------------------");
		// 创建工作薄
		Workbook wk = new HSSFWorkbook();
		// 拼接工作表名2015-05
		String sheetName = inputDate.replace("-0", "年").replace("-", "年") + "月出货表";
		// 创建工作表
		Sheet sheet = wk.createSheet(sheetName);
		// 设置列块
		// 创建行
		Row row1 = sheet.createRow(0);
		// 设置行高
		row1.setHeightInPoints(36f);
		// 使用行创建列
		Cell bigTitlecell = row1.createCell(1);
		// 给单元格设置内容
		bigTitlecell.setCellValue(sheetName);
		// 使用工作表对象合并单元格
		sheet.addMergedRegion(new CellRangeAddress(0, 0, 1, 8));
		// 设置大标题的样式通过工作薄创建样式对象，字体对象，将字体对象设到样式中，样式再另外设置上下左右的居中
		CellStyle bigTitle = MyWorkBookUtil.bigTitle(wk);
		// 设置cell1样式为bigTitle
		bigTitlecell.setCellStyle(bigTitle);
		System.out.println("----------------------------------完成大标题------------------------------------------");
		System.out.println("----------------------------------制作小标题------------------------------------------");
		// 获取小标题的样式
		CellStyle title = MyWorkBookUtil.title(wk);
		// 客户 订单号 货号 数量 工厂 工厂交期 船期 贸易条款
		// 定位到第二（1）行的2（1）到9（8）列的单元格
		Row row2 = sheet.createRow(1);
		// 创建第二行的列及列的样式
		Cell Cell1 = row2.createCell(1);
		Cell1.setCellValue("客户");
		Cell1.setCellStyle(title);
		Cell Cell2 = row2.createCell(2);
		Cell2.setCellValue("订单号");
		Cell2.setCellStyle(title);
		Cell Cell3 = row2.createCell(3);
		Cell3.setCellValue("货号");
		Cell3.setCellStyle(title);
		Cell Cell4 = row2.createCell(4);
		Cell4.setCellValue("数量");
		Cell4.setCellStyle(title);
		Cell Cell5 = row2.createCell(5);
		Cell5.setCellValue("工厂");
		Cell5.setCellStyle(title);
		Cell Cell6 = row2.createCell(6);
		Cell6.setCellValue("工厂交期");
		Cell6.setCellStyle(title);
		Cell Cell7 = row2.createCell(7);
		Cell7.setCellValue("船期");
		Cell7.setCellStyle(title);
		Cell Cell8 = row2.createCell(8);
		Cell8.setCellValue("贸易条件");
		Cell8.setCellStyle(title);
		// 设置每一列的宽度
		sheet.setColumnWidth(0, 256 * 5);
		sheet.setColumnWidth(1, 256 * 26);
		sheet.setColumnWidth(2, 256 * 11);
		sheet.setColumnWidth(3, 256 * 29);
		sheet.setColumnWidth(4, 256 * 12);
		sheet.setColumnWidth(5, 256 * 15);
		sheet.setColumnWidth(6, 256 * 10);
		sheet.setColumnWidth(7, 256 * 10);
		sheet.setColumnWidth(8, 256 * 10);
		System.out.println("----------------------------------完成小标题------------------------------------------");
		System.out.println("----------------------------------导入------------------------------------------");
		// 获取货物表中的数据
		Row r = null;
		int rNo = 2;
		Cell c = null;
		int cNo = 1;
		// 客户 订单号 货号 数量 工厂 工厂交期 船期 贸易条款
		for (ContractProduct cp : list) {
			r = sheet.createRow(rNo++);
			r.setHeightInPoints(24f);
			c = r.createCell(cNo++);
			c.setCellValue(cp.getContract().getCustomName());
			c.setCellStyle(title);
			c = r.createCell(cNo++);// 2
			// c.setCellValue(cp.getOrderNo());
			c.setCellValue(cp.getContract().getContractNo());
			c.setCellStyle(title);

			c = r.createCell(cNo++);
			c.setCellValue(cp.getProductNo());
			c.setCellStyle(title);
			c = r.createCell(cNo++);
			c.setCellValue(cp.getCnumber());
			c.setCellStyle(title);
			c = r.createCell(cNo++);
			c.setCellValue(cp.getFactoryName());
			c.setCellStyle(title);
			c = r.createCell(cNo++);
			c.setCellValue(cp.getContract().getDeliveryPeriod());
			c.setCellStyle(title);
			c = r.createCell(cNo++);
			c.setCellValue(cp.getContract().getShipTime());
			c.setCellStyle(title);
			c = r.createCell(cNo++);
			c.setCellValue(cp.getContract().getTradeTerms());
			c.setCellStyle(title);
			// 将cNo设为1
			cNo = 1;
		}

		System.out.println("----------------------------------完成------------------------------------------");

		// 创建缓冲区输出流
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		// 向输出流中写入数据
		wk.write(os);
		// 准备下载按钮界面
		DownloadUtil du = new DownloadUtil();
		// 准备下载数据
		du.download(os, ServletActionContext.getResponse(), sheetName + ".xls");

		// 不返回任何数据
		return NONE;

		// 创建输出流 OutputStream os = new FileOutputStream("D://abc.xls");
		// 写入到本地 wk.write(os); // 关闭流 os.close();
	}

//	 读取模板的方式打印数据表2003版的excel

	 /*public String print2() throws IOException {
		// 根据页面的船期查询出所有的列表
		String date = inputDate.replace("-0", "年").replace("-", "月") + "月出货表";
		// 查询数据库数据
		List<ContractProduct> list = contractProductService.find(
				"from ContractProduct where to_char(contract.shipTime,'YYYY-MM')=?", ContractProduct.class,
				new Object[] { inputDate });
		// String hql = "from ContractProduct where
		// to_char(contract.shipTime,'yyyy-MM') ='" + inputDate + "'";
		// 加载模板
		FileInputStream fis = new FileInputStream("D:传智/就业班6之项目1/day01/资料/1-输入项-原始报表/出货表模板.xls");
		Workbook wb = new HSSFWorkbook(fis);
		// 获取第一行
		Sheet sheet1 = wb.getSheetAt(0);
		Row r = sheet1.getRow(0);
		Cell cell = r.getCell(1);
		cell.setCellValue(date);
		// 第二行不用动
		// 跳到第三行获取样式
		r = sheet1.getRow(2);
		// 获取2到9行的单元格样式
		int cNo = 1;

		cell = r.getCell(cNo++);
		CellStyle s1 = cell.getCellStyle();
		cell = r.getCell(cNo++);
		CellStyle s2 = cell.getCellStyle();
		cell = r.getCell(cNo++);
		CellStyle s3 = cell.getCellStyle();
		cell = r.getCell(cNo++);
		CellStyle s4 = cell.getCellStyle();
		cell = r.getCell(cNo++);
		CellStyle s5 = cell.getCellStyle();
		cell = r.getCell(cNo++);
		CellStyle s6 = cell.getCellStyle();
		cell = r.getCell(cNo++);
		CellStyle s7 = cell.getCellStyle();
		cell = r.getCell(cNo++);
		CellStyle s8 = cell.getCellStyle();

		cNo = 1;

		// 获取货物表中的数据
		int rNo = 2;
		Cell c = null;
		// 客户 订单号 货号 数量 工厂 工厂交期 船期 贸易条款
		for (ContractProduct cp : list) {
			r = sheet1.createRow(rNo++);
			r.setHeightInPoints(24f);
			c = r.createCell(cNo++);
			c.setCellValue(cp.getContract().getCustomName());
			c.setCellStyle(s1);
			c = r.createCell(cNo++);// 2
			// c.setCellValue(cp.getOrderNo());
			c.setCellValue(cp.getContract().getContractNo());
			c.setCellStyle(s2);

			c = r.createCell(cNo++);
			c.setCellValue(cp.getProductNo());
			c.setCellStyle(s3);
			c = r.createCell(cNo++);
			c.setCellValue(cp.getCnumber());
			c.setCellStyle(s4);
			c = r.createCell(cNo++);
			c.setCellValue(cp.getFactoryName());
			c.setCellStyle(s5);
			c = r.createCell(cNo++);
			c.setCellValue(cp.getContract().getDeliveryPeriod());
			c.setCellStyle(s6);
			c = r.createCell(cNo++);
			c.setCellValue(cp.getContract().getShipTime());
			c.setCellStyle(s7);
			c = r.createCell(cNo++);
			c.setCellValue(cp.getContract().getTradeTerms());
			c.setCellStyle(s8);
			// 将cNo设为1
			cNo = 1;
		}

		System.out.println("----------------------------------完成------------------------------------------");

		// 创建缓冲区输出流
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		// 向输出流中写入数据
		wb.write(os);
		// 准备下载按钮界面
		DownloadUtil du = new DownloadUtil();
		// 准备下载数据
		du.download(os, ServletActionContext.getResponse(), date + ".xls");
		os.close();

		return NONE;

	}

	
//	  使用模板导入07版的excel 大数据导出测试
	
	public String print1() throws IOException, ParseException {
		// 根据页面的船期查询出所有的列表
		String date = inputDate.replace("-0", "年").replace("-", "月") + "月出货表";
		// 查询数据库数据
		List<ContractProduct> list = contractProductService.find(
				"from ContractProduct where to_char(contract.shipTime,'YYYY-MM')=?", ContractProduct.class,
				new Object[] { inputDate });
		// 加载模板
		String path = ServletActionContext.getServletContext().getRealPath("/") + "make/xlsprint/tOUTPRODUCT.xlsx";
		FileInputStream fis = new FileInputStream(path);
		// 创建07版的XSSFWorkbook
		Workbook wb = new XSSFWorkbook(fis);

		// 获取第一行
		Sheet sheet1 = wb.getSheetAt(0);
		Row r = sheet1.getRow(0);
		Cell cell = r.getCell(1);
		cell.setCellValue(date);
		// 第二行不用动
		// 跳到第三行获取样式
		r = sheet1.getRow(2);
		int cNo = 1;
		// 获取2到9行的单元格样式
		cell = r.getCell(cNo++);
		CellStyle s1 = cell.getCellStyle();
		cell = r.getCell(cNo++);
		CellStyle s2 = cell.getCellStyle();
		cell = r.getCell(cNo++);
		CellStyle s3 = cell.getCellStyle();
		cell = r.getCell(cNo++);
		CellStyle s4 = cell.getCellStyle();
		cell = r.getCell(cNo++);
		CellStyle s5 = cell.getCellStyle();
		cell = r.getCell(cNo++);
		CellStyle s6 = cell.getCellStyle();
		cell = r.getCell(cNo++);
		CellStyle s7 = cell.getCellStyle();
		cell = r.getCell(cNo++);
		CellStyle s8 = cell.getCellStyle();

		cNo = 1;

		// 获取货物表中的数据
		int rNo = 2;
		Cell c = null;

		long currentTimeMillis = System.currentTimeMillis();
		// 客户 订单号 货号 数量 工厂 工厂交期 船期 贸易条款
		for (int i = 0; i < 3000; i++) {
			for (ContractProduct cp : list) {
				r = sheet1.createRow(rNo++);
				r.setHeightInPoints(24f);
				c = r.createCell(cNo++);
				c.setCellValue(cp.getContract().getCustomName());
				c.setCellStyle(s1);
				c = r.createCell(cNo++);// 2
				// c.setCellValue(cp.getOrderNo());
				c.setCellValue(cp.getContract().getContractNo());
				c.setCellStyle(s2);

				c = r.createCell(cNo++);
				c.setCellValue(cp.getProductNo());
				c.setCellStyle(s3);
				c = r.createCell(cNo++);
				c.setCellValue(cp.getCnumber());
				c.setCellStyle(s4);
				c = r.createCell(cNo++);
				// 设置工厂名称为自动换行
				c.setCellValue(cp.getFactoryName().replaceAll("[\\t\\n\\r]", ""));
				c.setCellStyle(s5);
				c = r.createCell(cNo++);
				c.setCellValue(new SimpleDateFormat("yyyy-MM-dd").format(cp.getContract().getDeliveryPeriod()));
				c.setCellStyle(s6);
				c = r.createCell(cNo++);
				// c.setCellValue(new
				// SimpleDateFormat("yyyy-MM-dd").format(cp.getContract().getShipTime()));
				c.setCellValue(UtilFuns.dateTimeFormat(cp.getContract().getShipTime()));
				c.setCellStyle(s7);
				c = r.createCell(cNo++);
				c.setCellValue(cp.getContract().getTradeTerms());
				c.setCellStyle(s8);
				// 将cNo设为1
				cNo = 1;
			}
		}
		long currentTimeMillis2 = System.currentTimeMillis();
		long seconds = (currentTimeMillis2 - currentTimeMillis) / 1000;
		System.out.println("耗时：" + seconds + "s");
		System.out.println("----------------------------------完成------------------------------------------");
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		// 向输出流中写入数据
		wb.write(os);
		// 准备下载按钮界面
		DownloadUtil du = new DownloadUtil();
		// 准备下载数据
		du.download(os, ServletActionContext.getResponse(), date + ".xlsx");
		os.close();

		long currentTimeMillis3 = System.currentTimeMillis();
		long seconds2 = (currentTimeMillis3 - currentTimeMillis2) / 1000;
		System.out.println("存储：" + seconds2 + "s");
		return NONE;
	}

	/**测试使用SXSSHWorkBook进行百万级大数据的导入
	 * @return
	 * @throws Exception
	 */
	public String print5() throws Exception {
		System.out.println(inputDate);
		// 查询货物对象中的合同对象中字段shiptime为参数值的所有货物对象
		// String hql = "select to_char(shipTime,'YYYY-MM') as '船期' from
		// Contract where '船期'= ?";
		String hql = "from ContractProduct where to_char(contract.shipTime,'yyyy-MM') ='" + inputDate + "'";
		// 获取货物对象集
		List<ContractProduct> list = contractProductService.find(hql, ContractProduct.class, null);
		System.out.println("----------------------------------制作大标题------------------------------------------");
		// 创建工作薄
		Workbook wk = new SXSSFWorkbook(500);
		// 拼接工作表名2015-05
		String sheetName = inputDate.replace("-0", "年").replace("-", "年") + "月出货表";
		// 创建工作表
		Sheet sheet = wk.createSheet(sheetName);
		// 设置列块
		// 创建行
		Row row1 = sheet.createRow(0);
		// 设置行高
		row1.setHeightInPoints(36f);
		// 使用行创建列
		Cell bigTitlecell = row1.createCell(1);
		// 给单元格设置内容
		bigTitlecell.setCellValue(sheetName);
		// 使用工作表对象合并单元格
		sheet.addMergedRegion(new CellRangeAddress(0, 0, 1, 8));
		// 设置大标题的样式通过工作薄创建样式对象，字体对象，将字体对象设到样式中，样式再另外设置上下左右的居中
		CellStyle bigTitle = MyWorkBookUtil.bigTitle(wk);
		// 设置cell1样式为bigTitle
		bigTitlecell.setCellStyle(bigTitle);
		System.out.println("----------------------------------完成大标题------------------------------------------");
		System.out.println("----------------------------------制作小标题------------------------------------------");
		// 获取小标题的样式
		CellStyle title = MyWorkBookUtil.title(wk);
		// 客户 订单号 货号 数量 工厂 工厂交期 船期 贸易条款
		// 定位到第二（1）行的2（1）到9（8）列的单元格
		Row row2 = sheet.createRow(1);
		// 创建第二行的列及列的样式
		Cell Cell1 = row2.createCell(1);
		Cell1.setCellValue("客户");
		Cell1.setCellStyle(title);
		Cell Cell2 = row2.createCell(2);
		Cell2.setCellValue("订单号");
		Cell2.setCellStyle(title);
		Cell Cell3 = row2.createCell(3);
		Cell3.setCellValue("货号");
		Cell3.setCellStyle(title);
		Cell Cell4 = row2.createCell(4);
		Cell4.setCellValue("数量");
		Cell4.setCellStyle(title);
		Cell Cell5 = row2.createCell(5);
		Cell5.setCellValue("工厂");
		Cell5.setCellStyle(title);
		Cell Cell6 = row2.createCell(6);
		Cell6.setCellValue("工厂交期");
		Cell6.setCellStyle(title);
		Cell Cell7 = row2.createCell(7);
		Cell7.setCellValue("船期");
		Cell7.setCellStyle(title);
		Cell Cell8 = row2.createCell(8);
		Cell8.setCellValue("贸易条件");
		Cell8.setCellStyle(title);
		// 设置每一列的宽度
		sheet.setColumnWidth(0, 256 * 5);
		sheet.setColumnWidth(1, 256 * 26);
		sheet.setColumnWidth(2, 256 * 11);
		sheet.setColumnWidth(3, 256 * 29);
		sheet.setColumnWidth(4, 256 * 12);
		sheet.setColumnWidth(5, 256 * 15);
		sheet.setColumnWidth(6, 256 * 10);
		sheet.setColumnWidth(7, 256 * 10);
		sheet.setColumnWidth(8, 256 * 10);
		System.out.println("----------------------------------完成小标题------------------------------------------");
		System.out.println("----------------------------------导入------------------------------------------");
		// 获取货物表中的数据
		Row r = null;
		int rNo = 2;
		Cell c = null;
		int cNo = 1;
		// 客户 订单号 货号 数量 工厂 工厂交期 船期 贸易条款
		long currentTimeMillis = System.currentTimeMillis();
		for (int i = 0; i < 10000; i++) {
			for (ContractProduct cp : list) {
				r = sheet.createRow(rNo++);
				r.setHeightInPoints(24f);
				c = r.createCell(cNo++);
				c.setCellValue(cp.getContract().getCustomName());
				c.setCellStyle(title);
				c = r.createCell(cNo++);// 2
				// c.setCellValue(cp.getOrderNo());
				c.setCellValue(cp.getContract().getContractNo());
				c.setCellStyle(title);

				c = r.createCell(cNo++);
				c.setCellValue(cp.getProductNo());
				c.setCellStyle(title);
				c = r.createCell(cNo++);
				c.setCellValue(cp.getCnumber());
				c.setCellStyle(title);
				c = r.createCell(cNo++);
				c.setCellValue(cp.getFactoryName());
				c.setCellStyle(title);
				c = r.createCell(cNo++);
				c.setCellValue(cp.getContract().getDeliveryPeriod());
				c.setCellStyle(title);
				c = r.createCell(cNo++);
				c.setCellValue(cp.getContract().getShipTime());
				c.setCellStyle(title);
				c = r.createCell(cNo++);
				c.setCellValue(cp.getContract().getTradeTerms());
				c.setCellStyle(title);
				// 将cNo设为1
				cNo = 1;
			}
		}

		long currentTimeMillis2 = System.currentTimeMillis();
		long seconds = (currentTimeMillis2 - currentTimeMillis) / 1000;
		System.out.println("耗时：" + seconds + "s");

		System.out.println("----------------------------------完成------------------------------------------");

		// 创建缓冲区输出流
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		// 向输出流中写入数据
		wk.write(os);
		// 准备下载按钮界面
		DownloadUtil du = new DownloadUtil();
		// 准备下载数据
		du.download(os, ServletActionContext.getResponse(), sheetName + ".xls");

		long currentTimeMillis3 = System.currentTimeMillis();
		long seconds2 = (currentTimeMillis3 - currentTimeMillis2) / 1000;
		System.out.println("存储：" + seconds2 + "s");
		// 不返回任何数据
		return NONE;

		// 创建输出流 OutputStream os = new FileOutputStream("D://abc.xls");
		// 写入到本地 wk.write(os); // 关闭流 os.close();
	}

}
