package com.celerity.censusmodel.dao.api;

import java.util.List;

import com.celerity.censusmodel.model.Rate;

public interface RateDao {

	List<Rate> findRates(Long productId);
}
