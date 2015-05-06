package pl.mrasoft.springboottest.jbmp.internal;

import org.jbpm.shared.services.impl.QueryManager;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.util.List;
import java.util.Map;

public class CustomJpaPersistenceContext {

    public final static String FIRST_RESULT = "firstResult";
    public final static String MAX_RESULTS = "maxResults";

    private EntityManager em;

    public CustomJpaPersistenceContext(EntityManager em) {
        this.em = em;
    }

    protected Query getQueryByName(String queryName, Map<String, Object> params) {
        String queryStr = QueryManager.get().getQuery(queryName, params);
        Query query = null;
        if (queryStr != null) {
            query = this.em.createQuery(queryStr);
        } else {
            query = this.em.createNamedQuery(queryName);
        }

        return query;
    }

    public <T> List<T> queryAndLockWithParametersInTransaction(String queryName,
                                                         Map<String, Object> params, Class<?> clazz) {
        check();
        Query query = getQueryByName(queryName, params);
        return queryStringWithParameters(params, clazz, query);
    }

    protected void check() {
        if (em == null || !em.isOpen()) {
            throw new IllegalStateException("Entity manager is null or is closed, exiting...");
        }
    }

    @SuppressWarnings("unchecked")
    private <T> List<T> queryStringWithParameters(Map<String, Object> params,
                                            Class<?> clazz, Query query) {
        if (params != null && !params.isEmpty()) {
            for (String name : params.keySet()) {
                if (FIRST_RESULT.equals(name)) {
                    query.setFirstResult((Integer) params.get(name));
                    continue;
                }
                else if (MAX_RESULTS.equals(name)) {
                    if (((Integer) params.get(name)) > -1) {
                        query.setMaxResults((Integer) params.get(name));
                    }
                    continue;
                }
                // skip control parameters
                else if (QueryManager.ASCENDING_KEY.equals(name)
                        || QueryManager.DESCENDING_KEY.equals(name)
                        || QueryManager.ORDER_BY_KEY.equals(name)
                        || QueryManager.FILTER.equals(name)) {
                    continue;
                }
                query.setParameter(name, params.get(name));
            }
        }
        return (List<T>) query.getResultList();
    }

    public <T> T merge(T entity) {
        check();
        return this.em.merge(entity);
    }
}
