package com.ibm.lnw.backend;

import com.ibm.lnw.backend.domain.Attachment;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

/**
 * Created by Jan Valentik on 11/30/2015.
 */


@Stateless
public class AttachmentService {
	@PersistenceContext(name = "application-pu")
	private EntityManager entityManager;

	public void persist(Attachment entity) {
		entityManager.persist(entity);
	}

	public List<Attachment> findAllByMainRequest(long requestId) {
		return entityManager.createNamedQuery("Attachment.findByMainRequest", Attachment.class).setParameter
				("filter", requestId).getResultList();
	}

    public long saveOrPersist(Attachment entity) {
        if (entity.getId() > 0) {
            entityManager.merge(entity);
        }
        else {
            entityManager.persist(entity);
        }
        return entity.getId();
    }
}
