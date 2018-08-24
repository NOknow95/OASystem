package com.wjw.ems.dao;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Projection;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.metadata.ClassMetadata;
import org.springframework.beans.factory.annotation.Autowired;

import com.wjw.ems.entities.Employee;
import com.wjw.ems.entities.Role;
import com.wjw.ems.orm.Page;
import com.wjw.ems.orm.PropertyFilter;
import com.wjw.ems.utils.ReflectionUtils;

public class BaseDao<T> {

	private Class<T> entityClass;

	public BaseDao() {
		this.entityClass = ReflectionUtils.getSuperGenericType(getClass());
	}

	@Autowired
	private SessionFactory sessionFactory;

	protected Session getSession() {
		return sessionFactory.getCurrentSession();
	}

	public void save(T employee) {
		getSession().saveOrUpdate(employee);
	}

	public void batch(List<T> entities) {
		for (int i = 0; i < entities.size(); i++) {
			getSession().save(entities.get(i));
			if ((i + 1) % 50 == 0) {
				getSession().flush();
				getSession().clear();
			}
		}
	}

	public Page<T> getPage(Page<T> page) {
		// 1. 查询总的记录数
		int totalElements = getTotalElements();

		// 2. 为 Page 的 totalElements 属性赋值
		page.setTotalElements(totalElements);

		// 3. 查询当前页面的 content
		List<T> content = getContent(page);

		// 4. 为 content 属性赋值
		page.setContent(content);

		// 5. 返回 Page 对象.
		return page;
	}

	public List<T> getContent(Page<T> page) {
		int firstResult = (page.getPageNo() - 1) * page.getPageSize();
		int maxResult = page.getPageSize();
		Criteria criteria = getCriteria()
				// .setFetchMode("department", FetchMode.JOIN)
				// .setFetchMode("roles", FetchMode.JOIN)
				// .setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY)
				.setFirstResult(firstResult).setMaxResults(maxResult);

		return criteria.list();
	}

	/**
	 * 查询总的记录数
	 * 
	 * @return
	 */
	public int getTotalElements() {
		Criteria criteria = getCriteria();
		String idName = getIdName();
		Projection projection = Projections.count(idName);
		criteria.setProjection(projection);
		int count = (int) ((long) criteria.uniqueResult());
		return count;
	}

	public String getIdName() {
		ClassMetadata classMetadata = this.sessionFactory.getClassMetadata(Employee.class);
		return classMetadata.getIdentifierPropertyName();
	}

	protected Criteria getCriteria() {
		return this.getSession().createCriteria(entityClass);
	}

	public T getBy(String propertyName, Object propertyVal) {
		Criteria criteria = getCriteria();
		// 调用Restrictions工厂方法获取Criterion对象
		Criterion criterion = Restrictions.eq(propertyName, propertyVal);
		// 添加查询条件
		criteria.add(criterion);
		// 返回唯一结果
		return (T) criteria.uniqueResult();
	}

	public T get(Integer id) {
		return (T) getSession().get(entityClass, id);
	}

	public List<T> getByIsNotNull(String propertyName) {
		Criterion criterion = Restrictions.isNotNull(propertyName);
		return getCriteria().add(criterion).list();
	}

	public List<T> getByIsNull(String propertyName) {
		Criterion criterion = Restrictions.isNull(propertyName);
		return getCriteria().add(criterion).list();
	}

	public List<T> getAll(boolean... cachable) {
		if (cachable != null && cachable.length == 1) {
			return getCriteria().setCacheable(cachable[0]).list();
		}
		return getCriteria().list();
	}

	public List<Role> getListByIn(String propertyName, List<Object> asList) {
		Criteria criteria = getCriteria();
		Criterion criterion = Restrictions.in(propertyName, asList);
		criteria.add(criterion);

		return criteria.list();
	}

	public Page<T> getPage(Page<T> page, List<PropertyFilter> filters) {
		List<Criterion> criterions = parsePropertyFiltersToCriterions(filters);
		System.out.println("criterions=" + criterions);
		// 1. 查询总的记录数
		int totalElements = getTotalElements(criterions);

		// 2. 为 Page 的 totalElements 属性赋值
		page.setTotalElements(totalElements);

		// 3. 查询当前页面的 content
		List<T> content = getContent(page, criterions);

		// 4. 为 content 属性赋值
		page.setContent(content);

		// 5. 返回 Page 对象.
		return page;
	}

	private List<T> getContent(Page<T> page, List<Criterion> criterions) {
		int firstResult = (page.getPageNo() - 1) * page.getPageSize();
		int maxResult = page.getPageSize();
		Criteria criteria = getCriteria();
		for (Criterion criterion : criterions) {
			criteria.add(criterion);
		}
		criteria.setFirstResult(firstResult).setMaxResults(maxResult);
		return criteria.list();
	}

	private int getTotalElements(List<Criterion> criterions) {
		Criteria criteria = getCriteria();
		for (Criterion criterion : criterions) {
			criteria.add(criterion);
		}
		String idName = getIdName();
		Projection projection = Projections.count(idName);
		criteria.setProjection(projection);
		int count = (int) ((long) criteria.uniqueResult());
		return count;
	}

	private List<Criterion> parsePropertyFiltersToCriterions(List<PropertyFilter> filters) {
		List<Criterion> criterions = new ArrayList<>();
		for (PropertyFilter filter : filters) {
			Object propertyValue = ReflectionUtils.convertValue(filter.getPropertyVal(), filter.getPropertyType());
			String propertyName = filter.getPropertyName();

			Criterion criterion = null;
			switch (filter.getMatchType()) {
			case EQ:
				criterion = Restrictions.eqOrIsNull(propertyName, propertyValue);
				break;
			case GE:
				criterion = Restrictions.ge(propertyName, propertyValue);
				break;
			case GT:
				criterion = Restrictions.gt(propertyName, propertyValue);
				break;
			case LE:
				criterion = Restrictions.le(propertyName, propertyValue);
				break;
			case LIKE:
				criterion = Restrictions.like(propertyName, propertyValue.toString(), MatchMode.ANYWHERE);
				break;
			case LT:
				criterion = Restrictions.lt(propertyName, propertyValue);
				break;
			}

			if (criterion != null) {
				criterions.add(criterion);
			}
		}

		return criterions;
	}
}
