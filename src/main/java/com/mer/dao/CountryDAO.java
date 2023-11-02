package com.mer.dao;

import com.mer.domain.Country;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.hibernate.SessionFactory;

import java.util.List;

@Data
@AllArgsConstructor
public class CountryDAO {
    private final SessionFactory sessionFactory;

    public List<Country> getAll() {
        return sessionFactory.getCurrentSession()
                .createQuery("select c from Country c", Country.class)
                .list();
    }
}
