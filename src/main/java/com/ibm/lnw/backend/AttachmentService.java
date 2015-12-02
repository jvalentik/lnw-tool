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

	public List<Attachment> findAllByFilters(long filter1, String filter2) {
		return entityManager.createNamedQuery("Attachment.findAllByFilter", Attachment.class).setParameter
				("filter1", filter1).setParameter("filter2", filter2).getResultList();
	}
}
