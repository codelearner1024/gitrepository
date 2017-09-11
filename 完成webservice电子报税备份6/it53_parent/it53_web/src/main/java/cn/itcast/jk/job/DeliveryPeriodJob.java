package cn.itcast.jk.job;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import cn.itcast.jk.domain.Contract;
import cn.itcast.jk.service.ContractService;
import cn.itcast.jk.util.MailUtil;

public class DeliveryPeriodJob {
	//注入ContractService
	private ContractService contractService;
	public void setContractService(ContractService contractService) {
		this.contractService = contractService;
	}


	/**
	 * 以当前时间为标准，查询出交期到期的购销合同，并进行邮件发送，以提醒负责人
	 * @throws Exception
	 */
	public void execute() throws Exception{
		String hql = "from Contract where to_char(deliveryPeriod,'yyyy-MM-dd')=?";
		
		//获取当前时间
		String deteStr  = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
		
		List<Contract> list = contractService.find(hql, Contract.class, new String[]{deteStr});
		
		//判断集合，如果不为空，说明有购销合同今天到期
		if(list!=null && list.size()>0){
			for(final Contract c :list){
				Thread.sleep(3000);//让当前线程休眠  3秒
				
				Thread th = new Thread(new Runnable() {
					
					public void run() {
						//发送邮件的代码	
						try {
							MailUtil.sendMail("wuweigang11@Outlook.com", "提醒：交期到了", "主人您好，您有一个购销合同的交货日期于"+new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(c.getDeliveryPeriod())+"到期");
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				});
				
				th.start();
			}
		}else{
			System.out.println("您目前还没有购销合同交期到期的？？？");
		}
	}
}
