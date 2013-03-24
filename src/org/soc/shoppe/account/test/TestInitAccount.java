package org.soc.shoppe.account.test;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

import org.soc.common.exception.DaoException;
import org.soc.shoppe.account.dao.AccountDao;
import org.soc.shoppe.account.dao.gae.entity.Privilege;
import org.soc.shoppe.account.dao.gae.entity.Role;
import org.soc.shoppe.account.vo.AccountVO;
import org.soc.shoppe.account.vo.PrivilegeVO;
import org.soc.shoppe.account.vo.RoleVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class TestInitAccount {

	
	@Inject
	AccountDao accountDao;
	
	@Autowired
	private EntityManagerFactory entityManagerFactory;
	
	@RequestMapping(value="/testinit",method=RequestMethod.GET)
	public String initAccount() {
		
		RoleVO roleVO = new RoleVO();
		roleVO.setRole("ADMIN");
		
	    AccountVO accountVO = new AccountVO();
		accountVO.setUsername("girish");
		accountVO.setPassword("girish");
		accountVO.setEmail("girish@test.com");
		accountVO.setFirstName("girish");
		accountVO.setLastName("gh");
		accountVO.getRoles().add(roleVO);
		
		try {
			accountDao.addAccount(accountVO);
		}catch(DaoException daoException) {
			
		}catch(Exception exception) {
			
		}
		
/*		Privilege privilege = new Privilege(true,true,true,true);
		EntityManager entityManager = entityManagerFactory.createEntityManager();
		entityManager.getTransaction().begin();
		entityManager.persist(privilege);
		entityManager.getTransaction().commit();
		return "login";*/
		
	/*	Role role = new Role(roleVO);
		EntityManager entityManager = entityManagerFactory.createEntityManager();
		entityManager.getTransaction().begin();
		entityManager.persist(role);
		entityManager.getTransaction().commit(); */
		return "login";
	}
}
