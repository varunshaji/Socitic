package org.soc.setup.dao;

import java.sql.Date;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.NoResultException;
import javax.persistence.Query;

import org.apache.log4j.Logger;
import org.apache.log4j.Priority;
import org.soc.setup.entity.SetupSoc;

public class SetupDao {
	
	Logger log = Logger.getLogger(SetupDao.class);
	
	private EntityManagerFactory entityManagerFactory;
	
	public void setEntityManagerFactory(EntityManagerFactory entityManagerFactory) {
		this.entityManagerFactory = entityManagerFactory;
	}

	public Boolean saveInit(boolean initStatus,java.util.Date initDate) {
		
		EntityManager entityManager = entityManagerFactory.createEntityManager();
		boolean result = false;
		try {
			entityManager.getTransaction().begin();
			SetupSoc setupSoc = new SetupSoc(initStatus, initDate);
			entityManager.persist(setupSoc);
			entityManager.getTransaction().commit();
			result = true;
		}catch(Exception exception) {
			log.log(Priority.DEBUG,"Error: saveInit setup status",exception);
		}finally {
			entityManager.close();
		}
		return result;
	}
	
	public Boolean validateInitStatus() {
		
		EntityManager entityManager = entityManagerFactory.createEntityManager();
		boolean result = false;
		try {
			Query query = entityManager.createQuery("SELECT s FROM "+SetupSoc.class.getSimpleName()+" s");
			SetupSoc setupSoc = (SetupSoc) query.getSingleResult();
			if(setupSoc==null)
				result = true; 
		}catch(NoResultException noResultException) {
			result = true;
		}catch(Exception exception) {
			log.log(Priority.DEBUG,"Error: validating init setup status.",exception);
		}finally {
			entityManager.close();
		}
		return result;
	}
}
