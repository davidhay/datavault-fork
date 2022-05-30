package org.datavaultplatform.common.model.dao.custom;

import java.util.List;

import javax.persistence.EntityManager;
import org.datavaultplatform.common.model.BillingInfo;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BillingCustomDAOImpl extends BaseCustomDAOImpl implements BillingCustomDAO {

  private static final Logger LOGGER = LoggerFactory.getLogger(BillingCustomDAOImpl.class);

  public BillingCustomDAOImpl(EntityManager em) {
      super(em);
  }

  @Override
  public List<BillingInfo> list(String sort, String order, String offset, String maxResult) {
    Session session = this.getCurrentSession();
    Criteria criteria = session.createCriteria(BillingInfo.class);
    criteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);

    if("asc".equals(order)){
      criteria.addOrder(Order.asc(sort));
    } else {
      criteria.addOrder(Order.desc(sort));
    }
    if((offset != null && maxResult != null) || !maxResult.equals("0")) {
      criteria.setMaxResults(Integer.valueOf(maxResult));
      criteria.setFirstResult(Integer.valueOf(offset));
    }

    List<BillingInfo> vaults = criteria.list();
    session.close();
    return vaults;
  }


  @SuppressWarnings("unchecked")
  @Override
  public List<BillingInfo> search(String query, String sort, String order, String offset, String maxResult) {
    Session session = this.getCurrentSession();
    Criteria criteria = session.createCriteria(BillingInfo.class);
    criteria.add(Restrictions.or(Restrictions.ilike("id", "%" + query + "%"), Restrictions.ilike("name", "%" + query + "%"), Restrictions.ilike("description", "%" + query + "%")));
    criteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);

    if(order.equals("desc")){
      criteria.addOrder(Order.desc(sort));
    } else {
      criteria.addOrder(Order.asc(sort));
    }
    if((offset != null && maxResult != null) || !maxResult.equals("0")) {
      criteria.setMaxResults(Integer.valueOf(maxResult));
      criteria.setFirstResult(Integer.valueOf(offset));
    }

    List<BillingInfo> vaults = criteria.list();
    session.close();
    return vaults;
  }

  @Override
  public Long getTotalNumberOfVaults() {
    Session session = this.getCurrentSession();
    Criteria criteria = session.createCriteria(BillingInfo.class);
    criteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
    criteria.setProjection(Projections.rowCount());
    Long totalNoOfRows = (Long) criteria.uniqueResult();

    session.close();
    return totalNoOfRows;
  }
  /**
   * Retrieve Total NUmber of rows after applying the filter
   */
  public Long getTotalNumberOfVaults(String query) {
    Session session = this.getCurrentSession();
    Criteria criteria = session.createCriteria(BillingInfo.class);
    criteria.add(Restrictions.or(Restrictions.ilike("id", "%" + query + "%"), Restrictions.ilike("name", "%" + query + "%"), Restrictions.ilike("description", "%" + query + "%")));
    criteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
    criteria.setProjection(Projections.rowCount());
    Long totalNoOfRows = (Long) criteria.uniqueResult();

    session.close();
    return totalNoOfRows;
  }

}
