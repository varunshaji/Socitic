package org.soc.shoppe.busdata.service.impl;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.apache.log4j.Logger;
import org.apache.log4j.Priority;
import org.soc.common.SocContentType;
import org.soc.common.exception.DaoException;
import org.soc.common.gaeBlobstore.BlobStoreManager;
import org.soc.shoppe.busdata.dao.BusinessDao;
import org.soc.shoppe.busdata.exception.BusinessException;
import org.soc.shoppe.busdata.service.BusinessDataService;
import org.soc.shoppe.busdata.vo.BusinessVO;
import org.soc.shoppe.busdata.vo.ResourceVO;
import org.soc.shoppe.site.service.ShopService;
import org.springframework.web.multipart.MultipartFile;

public class BusinessDataServiceImpl implements BusinessDataService {
	
	Logger log = Logger.getLogger(BusinessDataServiceImpl.class);

	@Inject
	public BusinessDao businessDao;
	
	@Inject
	public ShopService shopService;
	
	@Override
	public List<String> listResourceNames(String domaincode) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ResourceVO loadResource(String resourceName, String domaincode) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Boolean addResource(ResourceVO resourceVO) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * this is for add bussiness details
	 * 
	 * @param {@link {@link BusinessVO}
	 * @return {@literal Boolean}
	 */
	@Override
	public Boolean addBusiness(BusinessVO businessVO) throws BusinessException {
		log.debug("ADD Business Data");
		boolean result=false;
		MultipartFile fileData = businessVO.getFile();
		String file= fileData.getOriginalFilename();

		try{	
			byte [] bytes= fileData.getBytes();
			String contenttype=SocContentType.OWL;
			BlobStoreManager blobStoreManager = new BlobStoreManager();
			String businesskey = blobStoreManager.saveFile(file, contenttype, bytes);
			if(businesskey != null){
				businessVO.setbusinessKey(businesskey);
				result = businessDao.saveBusiness(businessVO);
			}
		}
		catch (Exception exception) {
			exception.printStackTrace();
		}
		return result;
	}

	/**
	 * this is for read all business
	 * 
	 * @return {@literal List of BusinessVO}
	 * @throws {@link BusinessException}
	 */
	@Override
	public List<BusinessVO> loadAllBusiness() throws BusinessException {
		List<BusinessVO> result = new ArrayList<BusinessVO>();
		try{
		result=businessDao.getAllBusinesses();
		log.log(Priority.DEBUG, "successfully load all businesses");
		}catch (DaoException daoException) {
			log.log(Priority.DEBUG, "unable to load all businesses");
			throw new BusinessException(BusinessException.ERROR_RETRIEVE, daoException);
		}
		catch (Exception exception) {
			log.log(Priority.DEBUG, "an unknown exception while loading all businesses");
			throw new BusinessException(BusinessException.ERROR_UNKNOWN, exception);
		}
		return result;
	}
	
	/**
	 * this is for read a business
	 * 
	 * @param {@literal String}
	 * @return {@link BusinessVO}
	 * @throws {@link BusinessException}
	 */
	@Override
	public BusinessVO loadBusiness(String businesscode)throws BusinessException{
	BusinessVO businessVO=null;
	try{
		businessVO = businessDao.getBusiness(businesscode);
	}catch(DaoException daoException){
		log.log(Priority.DEBUG,"DaoException while loading business:"+businesscode,daoException);
		throw new BusinessException(BusinessException.ERROR_RETRIEVE, daoException);
	}catch(Exception exception){
		log.log(Priority.DEBUG,"Unknown exeption while loading business:"+businesscode,exception);
		throw new BusinessException(BusinessException.ERROR_UNKNOWN, exception);
	}
	
	return businessVO;
	}
	public BusinessDao getBusinessDao() {
		return businessDao;
	}
	public void setBusinessDao(BusinessDao businessDao) {
		this.businessDao = businessDao;
	}
	public ShopService getShopService() {
		return shopService;
	}
	public void setShopService(ShopService shopService) {
		this.shopService = shopService;
	}

}
