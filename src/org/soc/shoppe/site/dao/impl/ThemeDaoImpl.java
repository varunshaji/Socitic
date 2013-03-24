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
import org.soc.shoppe.site.dao.ThemeDao;
import org.soc.shoppe.site.entity.Theme;
import org.soc.shoppe.site.vo.ThemeVO;

public class ThemeDaoImpl implements ThemeDao {
	
	Logger log = Logger.getLogger(ThemeDaoImpl.class);
	
	private EntityManagerFactory entityManagerFactory;
	
	public void setEntityManagerFactory(EntityManagerFactory entityManagerFactory) {
		this.entityManagerFactory = entityManagerFactory;
	}

	@Override
	public Boolean saveTheme(ThemeVO themeVO) throws DaoException {
		boolean result = false;
		EntityManager entityManager = entityManagerFactory.createEntityManager();
		try {
			entityManager.getTransaction().begin();
			Theme theme = new Theme(themeVO);
			entityManager.persist(theme);
			entityManager.getTransaction().commit();
			result = true;
			log.log(Priority.DEBUG, "theme saved successfully");
		}catch(Exception exception) {
			entityManager.getTransaction().rollback();
			log.log(Priority.DEBUG, "failed to save Theme", exception);
			throw new DaoException(DaoException.ADD_ERROR, exception);
		}finally {
			entityManager.close();
		}
		return result;
	}
	
	@Override
	public ThemeVO getTheme(String themename) throws DaoException {
		ThemeVO themeVO = null;
		EntityManager entityManager = entityManagerFactory.createEntityManager();
		try {
			log.log(Priority.DEBUG,"getting theme:"+themename);
			Query query = entityManager.createQuery("SELECT t FROM "+Theme.class.getSimpleName()+" t WHERE themename = :themename");
			query.setParameter("themename", themename);
			Theme theme = (Theme) query.getSingleResult();
			themeVO = theme.getThemeVO();
		}catch(NoResultException noResultException) {
			log.log(Priority.DEBUG, "No themes found with name:"+themename,noResultException);
		}catch(Exception exception) {
			log.log(Priority.DEBUG, "problem occured during theme retrievel: ",exception);
			throw new DaoException(DaoException.RETRIEVE_ERROR, exception);
		}finally {
			entityManager.close();
		}
		return themeVO;
	}
	
	@Override
	public Boolean deleteTheme(String themename) throws DaoException {
		boolean result= false;
		EntityManager entityManager = entityManagerFactory.createEntityManager();
		try {
			entityManager.getTransaction().begin();
			Query query = entityManager.createQuery("DELETE t FROM "+Theme.class.getSimpleName()+" t WHERE themename = :themename");
			query.setParameter("themename", themename);
			entityManager.getTransaction().commit();
			result = true;
			log.log(Priority.DEBUG,"theme:"+themename+" deleted successfully");
		}catch(Exception exception) {
			entityManager.getTransaction().rollback();
			log.log(Priority.DEBUG,"problem occured during deleting theme: "+themename,exception);
			throw new DaoException(DaoException.DELETE_ERROR,exception);
		}finally {
			entityManager.close();
		}
		return result;
	}
	
	@Override
	public List<ThemeVO> getAllThemes() throws DaoException {
		EntityManager entityManager = entityManagerFactory.createEntityManager();
		List<ThemeVO> result = new ArrayList<ThemeVO>();
		try{
			Query query = entityManager.createQuery("SELECT t FROM "+Theme.class.getSimpleName()+" t");
			List<Theme> themes = (List<Theme>) query.getResultList();
			result = convertToThemeVOList(themes);
			log.log(Priority.DEBUG,"successfully retrieve all themes");
		}catch(NoResultException noResultException){
			result = null;
			log.log(Priority.DEBUG,"No themes available",noResultException);
		}catch(Exception exception){
			log.log(Priority.DEBUG,"problem on retrieving themes",exception);
			throw new DaoException(DaoException.RETRIEVE_ERROR, exception);
		}finally{
			entityManager.close();
		}
		return result;
	}
	
	@Override
	public ThemeVO getThemeByImageUrl(String imageurl) throws DaoException {
		ThemeVO themeVO = null;
		EntityManager entityManager = entityManagerFactory.createEntityManager();
		try {
			log.log(Priority.DEBUG,"getting theme with imageurl:"+imageurl);
			Query query = entityManager.createQuery("SELECT t FROM "+Theme.class.getSimpleName()+" t WHERE imageurl = :imageurl");
			query.setParameter("imageurl", imageurl.trim());
			Theme theme = (Theme) query.getSingleResult();
			themeVO = theme.getThemeVO();
		}catch(NoResultException noResultException) {
			log.log(Priority.DEBUG, "No themes found  with imageurl:"+imageurl,noResultException);
		}catch(Exception exception) {
			log.log(Priority.DEBUG, "problem occured during theme retrievel  with imageurl: "+imageurl,exception);
			throw new DaoException(DaoException.RETRIEVE_ERROR, exception);
		}finally {
			entityManager.close();
		}
		return themeVO;
	}
	
/*	public void getThemeFile(String themename) {
		com.google.appengine.api.datastore.Query query = new com.google.appengine.api.datastore.Query("__BlobInfo__"); 
		query.addFilter("filename", FilterOperator.EQUAL, themename+".vz"); 

		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService(); 
		PreparedQuery pq = datastore.prepare(query); 
		List<com.google.appengine.api.datastore.Entity> entList = pq.asList(FetchOptions.Builder.withLimit(1)); 
	}
	*/
	private List<ThemeVO> convertToThemeVOList(List<Theme> themes) {
		List<ThemeVO> themeVOList = new ArrayList<ThemeVO>();
		for(Theme theme : themes)
			themeVOList.add(theme.getThemeVO());
		return themeVOList;
	}
}
