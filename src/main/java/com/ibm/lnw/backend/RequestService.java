package com.ibm.lnw.backend;

import com.ibm.lnw.backend.domain.Request;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaQuery;
import java.util.List;

/**
 * Created by Jan Valentik on 11/29/2015.
 */

@Stateless
public class RequestService {

	@PersistenceContext(unitName = "application-pu")
	private EntityManager entityManager;

	public int persist(Request entity) {
		entityManager.persist(entity);
		entityManager.flush();
		return entity.getId();
	}

	public List<Request> findAll() {
		CriteriaQuery<Request> cq = entityManager.getCriteriaBuilder().
				createQuery(Request.class);
		cq.select(cq.from(Request.class));
		return entityManager.createQuery(cq).getResultList();
	}

	public List<Request> findAllByUser(String filter) {
		return entityManager.createNamedQuery("Request.findAllByUser",Request.class)
				.setParameter("filter", filter).getResultList();
	}

	public List<Request> findAllByUserAndFilter(String filter1, String filter2) {
		return entityManager.createNamedQuery("Request.findAllByUserAndFilter", Request.class)
				.setParameter("filter1", filter1).setParameter("filter2", filter2).getResultList();

	}
}
