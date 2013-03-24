package org.soc.shoppe.site.dao.impl;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Query;
import org.soc.shoppe.site.dao.TestDao;
import org.soc.shoppe.site.entity.Deal;
import org.soc.shoppe.site.vo.DealVO;
import org.soc.shoppe.site.vo.ShopVO;

public class TestDaoImpl implements TestDao {
	
	private EntityManagerFactory entityManagerFactory;
	
	public void setEntityManagerFactory(EntityManagerFactory entityManagerFactory) {
		this.entityManagerFactory = entityManagerFactory;
	}

	@Override
	public DealVO getDeal() {
		EntityManager entityManager = entityManagerFactory.createEntityManager();
		Query query = entityManager.createQuery(
		"SELECT d FROM Deal d WHERE description = :description");
		query.setParameter("description","hello1");
		Deal deal = (Deal) query.getSingleResult();
		return deal.getDealVO();
	}

	@Override
	public DealVO getDeal(ShopVO shopVO) {
		EntityManager entityManager = entityManagerFactory.createEntityManager();
		ShopVO shop = new ShopVO();
		shop.setName("shop1");
		Query query = entityManager.createQuery("SELECT d FROM Deal d WHERE description = :description");
		return null;
	}

}
