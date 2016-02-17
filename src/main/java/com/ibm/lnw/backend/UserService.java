package com.ibm.lnw.backend;

import com.ibm.lnw.backend.domain.User;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaQuery;
import java.util.List;

/**
 * Created by Jan Valentik on 11/28/2015.
 */
@Stateless
public class UserService {

	@PersistenceContext(unitName = "application-pu")
	private EntityManager entityManager;

	public void saveOrPersist(User entity) {
		if (entity.getId() > 0) {
			entityManager.merge(entity);
		} else {
			entityManager.persist(entity);
		}
	}

	public void deleteEntity(User entity) {
		if (entity.getId() > 0) {
			entity = entityManager.merge(entity);
			entityManager.remove(entity);
		}
	}

	public List<User> findAll() {
		CriteriaQuery<User> cq = entityManager.getCriteriaBuilder().
				createQuery(User.class);
		cq.select(cq.from(User.class));
		return entityManager.createQuery(cq).getResultList();
	}

	public List<User> findByName(String filter) {
		return entityManager.createNamedQuery("User.findByName",
				User.class)
				.setParameter("filter", filter).getResultList();
	}

    public List<User> findByUserName(String userName) {
        return entityManager.createNamedQuery("User.findByUserName", User.class).setParameter("filter", userName)
                .getResultList();
    }
}





