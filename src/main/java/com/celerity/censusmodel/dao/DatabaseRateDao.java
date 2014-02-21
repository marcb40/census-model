package com.celerity.censusmodel.dao;

import java.util.List;

import com.celerity.censusmodel.dao.api.RateDao;
import com.celerity.censusmodel.model.Rate;

public class DatabaseRateDao extends BaseDao<Rate> implements RateDao {

	public List<Rate> findRatesByProductId(Integer productId) {
		Rate example = new Rate();
		example.setProductId(productId);
		List<Rate> rates = getByExample(example);
		return rates;
	}

}
