package org.soc.shoppe.site.dao.impl;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import org.apache.log4j.Logger;
import org.apache.log4j.Priority;
import org.soc.common.exception.DaoException;
import org.soc.shoppe.site.dao.ShopDao;
import org.soc.shoppe.site.entity.Deal;
import org.soc.shoppe.site.entity.Shop;
import org.soc.shoppe.site.vo.DealVO;
import org.soc.shoppe.site.vo.ShopVO;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;

public class ShopDaoImpl implements ShopDao{

	private EntityManagerFactory entityManagerFactory;
	
	public void setEntityManagerFactory(EntityManagerFactory entityManagerFactory) {
		this.entityManagerFactory = entityManagerFactory;
	}

	Logger log = Logger.getLogger(ShopDaoImpl.class);
	
	@Override
	public String addShop(ShopVO shopVO) throws DaoException 
	{
        String result = null;
        EntityManager entityManager = entityManagerFactory.createEntityManager();
		try {
			
			Shop shop = new Shop(shopVO);
			entityManager.getTransaction().begin();
			entityManager.persist(shop);
			entityManager.getTransaction().commit();
  		    result = shop.getShopId();
		}
		catch(Exception exception) {
			entityManager.getTransaction().rollback();
			log.log(Priority.DEBUG,"Error: add shop",exception);
			throw new DaoException(DaoException.ADD_ERROR, exception);
		}
		finally {
			entityManager.close();
		}
		
		return result;
	}
	
	@Override
	public boolean validateShopId(String shopId) {
		
		boolean result = false;
		EntityManager entityManager = entityManagerFactory.createEntityManager();
		try {
			
			Query query = entityManager.createQuery("SELECT s FROM "+Shop.class.getSimpleName()+" s WHERE shopId = :shopId" );
			query.setParameter("shopId", shopId);
			Shop shop =  (Shop) query.getSingleResult();
			if(shop==null)
				result =  true;
		}
		catch(NoResultException noResultException) {
			result =  true;
		}
		catch(Exception exception) {
			log.log(Priority.DEBUG,"Error: validate shopId:"+shopId,exception);
			result = false;
		}
		finally {
			entityManager.close();
		}
		return result;
	}
	
	@Override
	public ShopVO getShop(String shopId) throws DaoException {
		ShopVO shopVO = new ShopVO();
		EntityManager entityManager = entityManagerFactory.createEntityManager();
		try {
			Query query = entityManager.createQuery("SELECT s FROM "+Shop.class.getSimpleName()+" s WHERE shopId = :shopId" );
			query.setParameter("shopId", shopId);
			Shop shop =  (Shop) query.getSingleResult();
			shopVO = shop.getShopVO();
		}
		catch(Exception exception) {
			log.log(Priority.DEBUG,"Error: get shop:"+shopId,exception);
			throw new DaoException(DaoException.RETRIEVE_ERROR, exception);
		}
		finally {
			entityManager.close();
		}
		
		return shopVO;
	}
	
	@Override
	public List<ShopVO> getAllShops() throws DaoException {
		EntityManager entityManager = entityManagerFactory.createEntityManager();
		List<ShopVO> result = new ArrayList<ShopVO>(); 
		try {
			Query q = entityManager.createQuery("SELECT s FROM "+Shop.class.getSimpleName()+" s");
			List<Shop> shops = (List<Shop>)q.getResultList();
			result = convertTOShopVOList(shops);
		}catch(Exception exception) {
			log.log(Priority.DEBUG,"Error: get all shops",exception);
			throw new DaoException(DaoException.RETRIEVE_ERROR, exception);
		}
		return result;
	}
	 
	@Override
	public ShopVO updateShop(ShopVO shopVO) throws DaoException {
		EntityManager entityManager = entityManagerFactory.createEntityManager();
		ShopVO newShopVO = null;
		try {
			
			entityManager.getTransaction().begin();
			Query query = entityManager.createQuery("SELECT s FROM "+Shop.class.getSimpleName()+" s WHERE shopId = :shopId" );
			query.setParameter("shopId", shopVO.getShopId());
			Shop shop =  (Shop) query.getSingleResult();
			shop.update(shopVO);
			newShopVO = shop.getShopVO();
			entityManager.persist(shop);
			entityManager.getTransaction().commit();
		}
		catch (Exception exception) {
			log.log(Priority.DEBUG,"Error: update shop",exception);
			throw new DaoException(DaoException.UPDATE_ERROR, exception);
		}
		finally {
			entityManager.close();
		}
		return newShopVO;
	}

	@Override
	public boolean saveDeal(DealVO dealVO,String shopId) throws DaoException {
		boolean result = false;
		EntityManager entityManager = entityManagerFactory.createEntityManager();
		try {
			entityManager.getTransaction().begin();
			Deal deal = new Deal(dealVO);
			Query query = entityManager.createQuery("SELECT s FROM "+Shop.class.getSimpleName()+" s WHERE shopId = :shopId" );
			query.setParameter("shopId", shopId);
			Shop shop =  (Shop) query.getSingleResult();
			shop.getDeals().add(deal);
			entityManager.persist(shop);
			entityManager.getTransaction().commit();
			result = true;
		}
		catch(Exception exception) {
			log.log(Priority.DEBUG,"Error: save deal of shop:"+shopId,exception);
			throw new DaoException(DaoException.ADD_ERROR, exception);
		}
		finally {
			entityManager.close();
		}
		
		return result;
	}
	
	@Override
	public DealVO updateDeal(DealVO dealVO,String shopId) throws DaoException {
		
		EntityManager entityManager = entityManagerFactory.createEntityManager();
		DealVO newDealVO = null;
		try {
			entityManager.getTransaction().begin();
			long dealKey = Long.parseLong(dealVO.getDealId());
			Query query = entityManager.createQuery("SELECT s FROM "+Shop.class.getSimpleName()+" s WHERE shopId = :shopId" );
			query.setParameter("shopId", shopId);
			Shop shop =  (Shop) query.getSingleResult();
			Key newDealKey = KeyFactory.createKey(shop.getShopKey(), "Deal", dealKey);
			Deal deal = (Deal) entityManager.find(Deal.class, newDealKey);
			deal.update(dealVO);
			newDealVO = deal.getDealVO();
			entityManager.persist(deal);
			entityManager.getTransaction().commit();
		}
		catch(Exception exception) {
			log.log(Priority.DEBUG,"Error: update deal of shop:"+shopId,exception);
			throw new DaoException(DaoException.UPDATE_ERROR, exception);
		}
		finally {
			entityManager.close();
		}
		return newDealVO;
	}
	
	@Override
	public DealVO getDeal(String dealId,String shopId) throws DaoException {
		DealVO dealVO = null;
		EntityManager entityManager = entityManagerFactory.createEntityManager();
		try {
			
			long dealKey = Long.parseLong(dealId);
			Query query = entityManager.createQuery("SELECT s FROM "+Shop.class.getSimpleName()+" s WHERE shopId = :shopId" );
			query.setParameter("shopId", shopId);
			Shop shop =  (Shop) query.getSingleResult();
			Key newDealKey = KeyFactory.createKey(shop.getShopKey(), "Deal", dealKey); 
			Deal deal = (Deal) entityManager.find(Deal.class, newDealKey);
			dealVO = deal.getDealVO();
		}
		catch(Exception exception) {
			log.log(Priority.DEBUG,"Error: get deal",exception);
			throw new DaoException(DaoException.RETRIEVE_ERROR, exception);
		}
		finally {
			entityManager.close();
		}
		
		return dealVO;
	}
	
	@Override
	public List<DealVO> getDeals(String shopId) throws DaoException{
		List<DealVO> result = null;
		EntityManager entityManager = entityManagerFactory.createEntityManager();
		try {
			Query query = entityManager.createQuery("SELECT s FROM "+Shop.class.getSimpleName()+" s WHERE shopId = :shopId");
			query.setParameter("shopId",shopId);
			Shop shop =  (Shop) query.getSingleResult(); 
			result = convertToDealVOList(shop.getDeals());
		}
		catch(NoResultException noResultException) {
			result = null;               //#####temp
		}
		catch(Exception exception) {
			log.log(Priority.DEBUG,"Error: get all deals of shop:"+shopId,exception);
			result = null;//throw new DaoException(DaoException.RETRIEVE_ERROR, ae);
		}
		finally {
			entityManager.close();
		}
		
		
		return result;
		
	}
	
	@Override
	public List<DealVO> getAllDeals() throws DaoException {
		EntityManager entityManager = entityManagerFactory.createEntityManager();
		List<DealVO> result = new ArrayList<DealVO>();
		try {
			Query query = entityManager.createQuery("SELECT d FROM "+Deal.class.getSimpleName()+" d");
			List<Deal> deals = (List<Deal>) query.getResultList();
			result =  convertToDealVOList(deals);
		}
		catch(Exception exception) {
			log.log(Priority.DEBUG,"Error: get all deals",exception);
			throw new DaoException(DaoException.RETRIEVE_ERROR, exception);
		}
		finally {
			entityManager.close();
		}
		return result;
	}
	
	@Override
	public Boolean deleteShopDeal(String dealId,String shopId) throws DaoException {
		Boolean result = false;
		EntityManager entityManager = entityManagerFactory.createEntityManager();
		try {
			entityManager.getTransaction().begin();
				Query q = entityManager.createQuery("SELECT s FROM "+Shop.class.getSimpleName()+" s WHERE shopId = :shopId" );
				q.setParameter("shopId", shopId);
				Shop shop =  (Shop) q.getSingleResult();
				long dealKey = Long.parseLong(dealId);
				Key newDealKey = KeyFactory.createKey(shop.getShopKey(), "Deal", dealKey); 
				Query query = entityManager.createQuery("DELETE FROM "+Deal.class.getSimpleName()+" WHERE dealKey = :dealKey");
				query.setParameter("dealKey", newDealKey);
				query.executeUpdate();
			entityManager.getTransaction().commit();
			result = true;
		}
		catch(Exception exception) {
			entityManager.getTransaction().rollback();
			log.log(Priority.DEBUG,"Error: delete deal",exception);
			throw new DaoException(DaoException.DELETE_ERROR, exception);
		}
		finally {
			entityManager.close();
		}
		return result;
	}
	
	private List<DealVO> convertToDealVOList(List<Deal> deals){
		List<DealVO> dealVOList = new ArrayList();
		for(Deal deal:deals){
			dealVOList.add(deal.getDealVO());
		}
		return dealVOList;
	}
	
	private List<ShopVO> convertTOShopVOList(List<Shop> shops) {
		List<ShopVO> shopVOList = new ArrayList<ShopVO>();
		for(Shop shop:shops) {
			shopVOList.add(shop.getShopVO());
		}
		return shopVOList;
	}
	
	@Override
	public ShopVO addShopContacts(ShopVO shopVO) throws DaoException {
		EntityManager entityManager = entityManagerFactory.createEntityManager();
		ShopVO newShopVO = null;
		try {
			
			entityManager.getTransaction().begin();
			Query query = entityManager.createQuery("SELECT s FROM "+Shop.class.getSimpleName()+" s WHERE shopId = :shopId" );
			query.setParameter("shopId", shopVO.getShopId());
			Shop shop =  (Shop) query.getSingleResult();
			shop.updateContacts(shopVO);
			newShopVO = shop.getShopVO();
			entityManager.persist(shop);
			entityManager.getTransaction().commit();
		}
		catch (Exception exception) {
			entityManager.getTransaction().rollback();
			log.log(Priority.DEBUG,"Error: add shop contact details",exception);
			throw new DaoException(DaoException.UPDATE_ERROR, exception);
		}
		finally {
			entityManager.close();
		}
		return newShopVO;
	}
	
	@Override
	public Boolean saveBusinessKey(ShopVO shopVO) throws DaoException {
		EntityManager entityManager = entityManagerFactory.createEntityManager();
	    boolean result = false;
	    try {
	    	entityManager.getTransaction().begin();
	    	Query query = entityManager.createQuery("SELECT s FROM "+Shop.class.getSimpleName()+" s WHERE shopId = :shopId");
	    	query.setParameter("shopId", shopVO.getShopId());
	    	Shop shop = (Shop) query.getSingleResult();
	    	shop.setBusinesskey(shopVO.getBusinesskey());
	    	entityManager.persist(shop);
	    	entityManager.getTransaction().commit();
	    	result = true;
	    }catch(Exception exception) {
	    	entityManager.getTransaction().rollback();
	    	log.log(Priority.DEBUG,"Error: add businesskey",exception);
			throw new DaoException(DaoException.UPDATE_ERROR, exception);
	    }finally{
	    	entityManager.close();
	    }
	    return result;
	}
	
	@Override
	public String getBusinessKey(String shopId) throws DaoException {
		EntityManager entityManager = entityManagerFactory.createEntityManager();
		String result = null;
		try {
			Query query = entityManager.createQuery("SELECT s FROM "+Shop.class.getSimpleName()+" s WHERE shopId = :shopId");
	    	query.setParameter("shopId", shopId);
	    	Shop shop = (Shop) query.getSingleResult();
	    	result = shop.getBusinesskey();
		}catch(Exception exception) {
			log.log(Priority.DEBUG,"Error: retrieving businesskey",exception);
			throw new DaoException(DaoException.RETRIEVE_ERROR, exception);
		}finally{
			entityManager.close();
		}
		return result;
	}
}
