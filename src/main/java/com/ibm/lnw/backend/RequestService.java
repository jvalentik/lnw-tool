package com.ibm.lnw.backend;

import com.ibm.lnw.backend.domain.Request;
import com.ibm.lnw.presentation.model.CustomAccessControl;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaQuery;
import java.util.List;

/**
 * Created by Jan Valentik on 11/29/2015.
 */

@Stateless
public class RequestService {

    @Inject
    private CustomAccessControl accessControl;

	@PersistenceContext(unitName = "application-pu")
	private EntityManager entityManager;

	public long saveOrPersist(Request entity) {
        if (entity.getId() > 0) {
            entityManager.merge(entity);
        }
        else {
            entityManager.persist(entity);
        }
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
				.setParameter("filter1", filter1.toLowerCase()).setParameter("filter2", filter2.toLowerCase()).getResultList();

	}

	public List<Request> findByID(int id) {
		return entityManager.createNamedQuery("Request.findByID", Request.class).setParameter("filter", id)
				.getResultList();
	}

    public List<Request> findByFilter(String filter) {
        return entityManager.createNamedQuery("Request.findByFilter", Request.class).setParameter("filter", filter)
                .getResultList();
    }

    public List<Request> findAssigned(String name) {
        return entityManager.createNamedQuery("Request.findAssigned", Request.class).setParameter("filter", name).getResultList();
    }
}
