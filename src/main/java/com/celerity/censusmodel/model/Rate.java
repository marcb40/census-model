package com.celerity.censusmodel.model;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "RATE")
public class Rate extends BaseModel{
	
	@Column(name = "MIN_AGE", precision = 3, scale = 0)
	private Integer minAge;
	
	@Column(name = "MAX_AGE", precision = 3, scale = 0)
	private Integer maxAge;
	
	@Column(name = "RATE", precision = 10, scale = 2)
	private BigDecimal rate;
	

	public Rate(int minAge, int maxAge, BigDecimal rate) {
		super();
		this.minAge = minAge;
		this.maxAge = maxAge;
		this.rate = rate;
	}

	public Rate() {
		super();
	}

	public int getMinAge() {
		return minAge;
	}

	public void setMinAge(int minAge) {
		this.minAge = minAge;
	}

	public int getMaxAge() {
		return maxAge;
	}

	public void setMaxAge(int maxAge) {
		this.maxAge = maxAge;
	}

	public BigDecimal getRate() {
		return rate;
	}

	public void setRate(BigDecimal rate) {
		this.rate = rate;
	}
}
