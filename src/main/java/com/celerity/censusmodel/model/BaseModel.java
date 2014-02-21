package com.celerity.censusmodel.model;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

@MappedSuperclass
public abstract class BaseModel {
	
	 public BaseModel() {
		super();
	}

	public BaseModel(Long id) {
		super();
		this.id = id;
	}

		@Id
	    @Column(name = "ID")
	    private Long id;

		public Long getId() {
			return id;
		}

		public void setId(Long id) {
			this.id = id;
		}
	 	
}
