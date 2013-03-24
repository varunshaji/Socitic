package org.soc.shoppe.site.facade.dao;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.NoResultException;
import javax.persistence.Query;

import org.apache.log4j.Logger;
import org.apache.log4j.Priority;
import org.soc.common.exception.DaoException;
import org.soc.shoppe.account.dao.gae.entity.Account;
import org.soc.shoppe.account.dao.gae.entity.Role;
import org.soc.shoppe.account.dao.gae.entity.ValidateEmail;
import org.soc.shoppe.account.vo.AccountVO;
import org.soc.shoppe.account.vo.RoleVO;
import org.soc.shoppe.site.entity.Shop;
import org.soc.shoppe.site.vo.ShopVO;

public class ShopAccountUnifiedDao {
	
	private EntityManagerFactory entityManagerFactory;
	
	Logger log = Logger.getLogger(ShopAccountUnifiedDao.class);
	
	public void setEntityManagerFactory(EntityManagerFactory entityManagerFactory) {
		this.entityManagerFactory = entityManagerFactory;
	}

	public List<AccountVO> saveShop(ShopVO shopVO) throws DaoException {
		boolean adminStatus = false;
		boolean shop_status = false;
		List<AccountVO> accounts = new ArrayList<AccountVO>();
		Shop shop = new Shop(shopVO);
		List<String> users = shopVO.getUsers();
		EntityManager entityManager = entityManagerFactory.createEntityManager();
		try {
			entityManager.getTransaction().begin();
			entityManager.persist(shop);
			entityManager.getTransaction().commit();
			shop_status = true;
		}catch (Exception exception) {
			entityManager.getTransaction().rollback();
			log.log(Priority.DEBUG,"Error: save shop",exception);
			throw new DaoException(DaoException.ADD_ERROR, exception);
		}

		try {
			if(shop_status) {
				entityManager.getTransaction().begin();
				for (String user : users) {

					Query query = entityManager.createQuery("SELECT a FROM "
							+ Account.class.getSimpleName()
							+ " a WHERE username = :username");
					query.setParameter("username", user);
					Account account = (Account) query.getSingleResult();
					account.getShops().add(shop.getShopId());
					boolean siteAdmin = false;
					for (Role role : account.getRoles()) {
						if (role.getRole().equals("ROLE_SITE-ADMIN"))
							siteAdmin = true;
					}
					if (!siteAdmin) {
						Role role = new Role(new RoleVO("ROLE_SITE-ADMIN"));
						account.getRoles().add(role);
					}

					entityManager.persist(account);
					accounts.add(account.getAccountVO());
				}
				entityManager.getTransaction().commit();
				adminStatus = true;
			}
		}catch (Exception exception) {
			entityManager.getTransaction().rollback();
			log.log(Priority.DEBUG,"Error: adding shop to site admins account",exception);
			throw new DaoException(DaoException.ADD_ERROR, exception);
		}

		finally {
			if (!adminStatus && shop_status) {
				log.debug("Inside finally to delete shop");
				try {
					Query query = entityManager.createQuery("DELETE FROM "
							+ Shop.class.getSimpleName()
							+ " WHERE shopId = :shopId");
					query.setParameter("shopId", shopVO.getShopId());
					query.executeUpdate();
					entityManager.getTransaction().commit();
				}catch (Exception exception) {
					entityManager.getTransaction().rollback();
					log.log(Priority.DEBUG,"Error: occured during delete shop:(Manual Rollback inside finally)",exception);
				}
				accounts = null;
			}
			entityManager.close();
		}
		return accounts;
	}
	
	public Boolean saveAccountAndShop(ShopVO shopVO,AccountVO accountVO) throws DaoException {
		
		boolean result = false;
		boolean shop_status = false;
		Shop shop = new Shop(shopVO);
		EntityManager entityManager = entityManagerFactory.createEntityManager();
		try {
			entityManager.getTransaction().begin();
			entityManager.persist(shop);
			entityManager.getTransaction().commit();
			shop_status = true;
		}catch(Exception exception) {
			log.log(Priority.DEBUG,"Error: save shop",exception);
			entityManager.getTransaction().rollback();
			throw new DaoException(DaoException.ADD_ERROR, exception);
		}
		
		try {
			if(shop_status) {
				entityManager.getTransaction().begin();
				Account account = new Account(accountVO);
				account.getShops().add(shop.getShopId());
				entityManager.persist(account);
				entityManager.getTransaction().commit();
				result = true;
			}
		}catch(Exception exception) {
			log.log(Priority.DEBUG,"Error: save account after save shop",exception);
			throw new DaoException(DaoException.ADD_ERROR, exception);
		}finally {
			if(!result && shop_status) {
				log.debug("Inside finally to delete shop");
				try {
					entityManager.clear();
					entityManager.getTransaction().begin();
					Query query = entityManager.createQuery("DELETE FROM "+ Shop.class.getSimpleName()+ " WHERE shopId = :shopId");
					query.setParameter("shopId", shopVO.getShopId());
					query.executeUpdate();
					entityManager.getTransaction().commit();
				}
				catch (Exception exception) {
					entityManager.getTransaction().rollback();
					log.log(Priority.DEBUG,"Error: occured during delete shop:(Manual Rollback inside finally)",exception);
				}
			}
			if(result && shop_status) {
				try {
					entityManager.getTransaction().begin();
					Query query = entityManager.createQuery("DELETE FROM "+ ValidateEmail.class.getSimpleName()+ " WHERE email = :email");
					query.setParameter("email", accountVO.getEmail());
					query.executeUpdate();
					entityManager.getTransaction().commit();
				}catch(Exception exception) {
					entityManager.getTransaction().rollback();
					log.log(Priority.DEBUG,"Error: delete validationKey after successfully saving shop and account",exception);
				}
			}
			entityManager.close();
		}
		return result;
	}
	
	private boolean isNewShopId(String shopId) {
		
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
}
