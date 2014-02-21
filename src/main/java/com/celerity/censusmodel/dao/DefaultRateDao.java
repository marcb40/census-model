package com.celerity.censusmodel.dao;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import com.celerity.censusmodel.dao.api.RateDao;
import com.celerity.censusmodel.model.Rate;

public class DefaultRateDao implements RateDao {

	@PersistenceContext
	private EntityManager entityManager;

	public List<Rate> findRates(Long productId) {
		EntityManager m = this.entityManager.getEntityManagerFactory().createEntityManager();
		Rate obj = m.find(Rate.class, 3L);

		List<Rate> rates = new ArrayList<Rate>();
		rates.add(obj);
		rates.add(new Rate(0, 99, BigDecimal.valueOf(55)));
		return rates;
	}

}
