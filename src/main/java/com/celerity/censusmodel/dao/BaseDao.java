package com.celerity.censusmodel.dao;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import com.celerity.censusmodel.util.ReflectionUtility;

public abstract class BaseDao<T> {

	@PersistenceContext
	private EntityManager entityManager;

	public T findById(final Class<T> type, Long id) {
		T data = entityManager.find(type, id);
		return data;
	}

	public List<T> getByExample(final T example) {
		List<Object> parameters = new ArrayList<Object>();
		String queryText = this.buildTypedExampleQueryString(example, parameters);
		TypedQuery<T> typedQuery = (TypedQuery<T>) entityManager.createQuery(queryText, example.getClass());

		int index = 1;
		for (Object obj : parameters) {
			typedQuery.setParameter(index++, obj);
		}

		return typedQuery.getResultList();
	}

	private String buildTypedExampleQueryString(final T example, final List<Object> list) {
		String query = "";
		if (example.getClass().isAnnotationPresent(Entity.class)) {
			query = "from " + example.getClass().getCanonicalName();
			boolean isWhere = false;
			boolean needAnd = false;

			PropertyDescriptor[] descriptors = ReflectionUtility.getPropertyDescriptors(example.getClass());
			for (PropertyDescriptor descriptor : descriptors) {

				if (!descriptor.getName().equals("version") && !descriptor.getName().equals("uniqueIdentifier")) {
					Method read = descriptor.getReadMethod();

					String name = ReflectionUtility.getFieldName(descriptor);
					Object value = ReflectionUtility.safelyInvoke(read, example, (Object[]) null);

					if (value != null) {
						list.add(value);
						if (!isWhere) {
							isWhere = true;
							query += " where ";
						} else if (needAnd) {
							query += " and ";
						}

						query = query + name + " = ?";

						needAnd = true;
					}
				}
			}
		}
		return query;
	}
}
