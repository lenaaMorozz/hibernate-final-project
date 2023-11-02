package com.mer.dao;

import com.mer.Main;
import com.mer.domain.City;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;

import java.util.List;

@Data
@AllArgsConstructor
public class CityDAO {
    private final SessionFactory sessionFactory;

    public List<City> getItems(int offset, int limit) {
        return sessionFactory.getCurrentSession()
                .createQuery("from City", City.class)
                .setFirstResult(offset)
                .setMaxResults(limit)
                .list();
    }

    public int getTotalCount() {
        Query<Long> query = sessionFactory.getCurrentSession()
                .createQuery("select count(c) from City c", Long.class);
        return Math.toIntExact(query.uniqueResult());
    }

}
