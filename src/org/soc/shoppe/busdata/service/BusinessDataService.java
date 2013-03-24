package org.soc.shoppe.busdata.service;

import java.util.List;

import org.soc.shoppe.busdata.exception.BusinessException;
import org.soc.shoppe.busdata.vo.BusinessVO;
import org.soc.shoppe.busdata.vo.ResourceVO;

public interface BusinessDataService {
	
	List<String> listResourceNames (String domaincode);
	
	ResourceVO loadResource(String resourceName,String domaincode);

	Boolean addResource(ResourceVO resourceVO);
	
	Boolean addBusiness(BusinessVO businessVO)throws BusinessException;

	public List<BusinessVO> loadAllBusiness()throws BusinessException;

	public BusinessVO loadBusiness(String businesscode)throws BusinessException;
}
