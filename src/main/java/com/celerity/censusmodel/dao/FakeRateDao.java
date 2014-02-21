package com.celerity.censusmodel.dao;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import com.celerity.censusmodel.dao.api.RateDao;
import com.celerity.censusmodel.model.Rate;

public class FakeRateDao implements RateDao {

	public List<Rate> findRatesByProductId(Integer productId) {
		List<Rate> rates = new ArrayList<Rate>();
		rates.add(new Rate(0, 50, BigDecimal.valueOf(3333)));
		rates.add(new Rate(0, 99, BigDecimal.valueOf(4444)));
		return rates;
	}
}
