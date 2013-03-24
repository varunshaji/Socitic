package org.soc.shoppe.busdata.dao;

import java.util.List;

import org.soc.common.exception.DaoException;
import org.soc.shoppe.busdata.vo.BusinessVO;

public interface BusinessDao {

	public boolean saveBusiness(BusinessVO businessVO) throws DaoException;

	public List<BusinessVO> getAllBusinesses()throws DaoException;

	public BusinessVO getBusiness(String businesscode)throws DaoException;

	public String getBusinessKey(String businesscode) throws DaoException;
}
