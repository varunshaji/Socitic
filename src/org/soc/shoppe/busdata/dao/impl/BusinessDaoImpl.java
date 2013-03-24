package org.soc.shoppe.busdata.dao.impl;


import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.NoResultException;
import javax.persistence.Query;

import org.apache.log4j.Logger;
import org.apache.log4j.Priority;
import org.soc.common.exception.DaoException;
import org.soc.shoppe.busdata.dao.BusinessDao;
import org.soc.shoppe.busdata.entity.Business;
import org.soc.shoppe.busdata.vo.BusinessVO;

public class BusinessDaoImpl implements BusinessDao{
	
	Logger log=Logger.getLogger(BusinessDaoImpl.class);
	
	private EntityManagerFactory entityManagerFactory;
	
	public void setEntityManagerFactory(EntityManagerFactory entityManagerFactory){
		this.entityManagerFactory=entityManagerFactory;
	}
	/**
	 * this is for save business into entity
	 * 
	 * @param {@link BusinessVO}
	 * @return {@literal Boolean}
	 * @throws {@link DaoException}
	 */
	@Override
	public boolean saveBusiness(BusinessVO businessVO) throws DaoException{
		boolean result=false;
		EntityManager entityManager=entityManagerFactory.createEntityManager();
		try{
			entityManager.getTransaction().begin();
			Business business=new Business(businessVO);
			entityManager.persist(business);
			entityManager.getTransaction().commit();
			result=true;
			log.log(Priority.DEBUG, "business successfully saved");
		}
		catch (Exception exception) {
			entityManager.getTransaction().rollback();
			log.log(Priority.DEBUG,"business can't successfully saved" );
			throw new DaoException(DaoException.ADD_ERROR, exception);
		}
		
		entityManager.close();
		return result;
	}
	/**
	 * this is for get all businesses
	 * 
	 * @return {@literal List of BusinessVO}
	 * @throws {@link DaoException}
	 */
	@Override
	public List<BusinessVO> getAllBusinesses()throws DaoException{
		EntityManager entityManager=entityManagerFactory.createEntityManager();
		List<BusinessVO> result= new ArrayList<BusinessVO>();
		try{
			Query query = entityManager.createQuery("SELECT a FROM "+Business.class.getSimpleName()+" a");
			List<Business> businesses = (List<Business>) query.getResultList();
			result = convertToBusinessVoList(businesses);
			log.log(Priority.DEBUG, "successfully retrive all businesses");
		}
		catch (NoResultException noResultException) {
			result=null;
			log.log(Priority.DEBUG, "no businesses available");
			}catch (Exception exception) {
				log.log(Priority.DEBUG, "problems on retrieving businesses");
				throw new DaoException(DaoException.RETRIEVE_ERROR, exception);
			}		
		return result;
	}
	/**
	 * this is for convert all businesses into business list
	 * @param {@literal List of business}
	 * @return {@literal List of BusinessVO}
	 */
	private List<BusinessVO> convertToBusinessVoList(List<Business> businesses) {
		List<BusinessVO> businessVOList = new ArrayList<BusinessVO>();
		for(Business business : businesses)
			businessVOList.add(business.getBusinessVO());
		return businessVOList;
	}
	/**
	 * this is for get business
	 * 
	 * @param {@literal String}
	 * @return {@link BusinessVO}
	 * @throws {@link DaoException}
	 */
	@Override
	public BusinessVO getBusiness(String businesscode) throws DaoException{
		BusinessVO businessVO=null;
		EntityManager entityManager=entityManagerFactory.createEntityManager();
		try{
			log.log(Priority.DEBUG, "getting business"+businesscode);
			Query query=entityManager.createQuery(" SELECT b FROM " +Business.class.getSimpleName()+" b WHERE businessCode =:businesscode");
			query.setParameter("businesscode", businesscode);
			Business business = (Business) query.getSingleResult();
			businessVO = business.getBusinessVO();
		}catch(NoResultException noResultException) {
			log.log(Priority.DEBUG, "No business found with code:"+businesscode,noResultException);
		}catch(Exception exception) {
			log.log(Priority.DEBUG, "problem occured during business retrievel: ",exception);
			throw new DaoException(DaoException.RETRIEVE_ERROR, exception);
		}finally {
			entityManager.close();
		}
		return businessVO;
	}
	
	@Override
	public String getBusinessKey(String businesscode) throws DaoException {
		String result = null;
		EntityManager entityManager=entityManagerFactory.createEntityManager();
		try{
			log.log(Priority.DEBUG, "getting filekey of business"+businesscode);
			Query query=entityManager.createQuery(" SELECT businessKey FROM " +Business.class.getSimpleName()+" b WHERE businessCode =:businesscode");
			query.setParameter("businesscode", businesscode);
			result = (String) query.getSingleResult();
		}catch(NoResultException noResultException) {
			log.log(Priority.DEBUG, "No business found with code:"+businesscode,noResultException);
		}catch(Exception exception) {
			log.log(Priority.DEBUG, "problem occured during business retrievel: ",exception);
			throw new DaoException(DaoException.RETRIEVE_ERROR, exception);
		}finally {
			entityManager.close();
		}
		return result;
	}
}

