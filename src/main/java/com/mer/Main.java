package com.mer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mer.constants.DBConstants;
import io.lettuce.core.RedisClient;
import io.lettuce.core.RedisURI;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.sync.RedisStringCommands;
import com.mer.dao.CityDAO;
import com.mer.dao.CountryDAO;
import com.mer.domain.City;
import com.mer.domain.Country;
import com.mer.domain.CountryLanguage;
import com.mer.redis.CityCountry;
import com.mer.redis.Language;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.cfg.Environment;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.stream.Collectors;

import static java.util.Objects.nonNull;

public class Main {
    private SessionFactory sessionFactory;
    private final RedisClient redisClient;
    private final ObjectMapper objectMapper;
    private final CityDAO cityDAO;
    private final CountryDAO countryDAO;

    public Main() {
        sessionFactory = prepareRelationalDb();
        redisClient = prepareRedisClient();
        objectMapper = new ObjectMapper();
        cityDAO = new CityDAO(sessionFactory);
        countryDAO = new CountryDAO(sessionFactory);
    }

        private SessionFactory prepareRelationalDb() {
            final SessionFactory sessionFactory;
            Properties properties = new Properties();
            properties.put(Environment.DIALECT, DBConstants.DB_DIALECT);
            properties.put(Environment.DRIVER, DBConstants.DB_DRIVER);
            properties.put(Environment.URL, DBConstants.DB_URL);
            properties.put(Environment.USER, DBConstants.DB_USER);
            properties.put(Environment.PASS, DBConstants.DB_PASS);
            properties.put(Environment.CURRENT_SESSION_CONTEXT_CLASS, DBConstants.CURRENT_SESSION_CONTEXT_CLASS);
            properties.put(Environment.HBM2DDL_AUTO, DBConstants.DB_HBM2DDL_AUTO);
            properties.put(Environment.STATEMENT_BATCH_SIZE, DBConstants.STATEMENT_BATCH_SIZE);

            sessionFactory = new Configuration()
                    .addAnnotatedClass(City.class)
                    .addAnnotatedClass(Country.class)
                    .addAnnotatedClass(CountryLanguage.class)
                    .addProperties(properties)
                    .buildSessionFactory();
            return sessionFactory;
        }

        private RedisClient prepareRedisClient() {
            RedisClient redisClient = RedisClient.create(RedisURI.create(DBConstants.REDIS_HOST, RedisURI.DEFAULT_REDIS_PORT));
            try (StatefulRedisConnection<String, String> connection = redisClient.connect()) {
                System.out.println("\nConnected to Redis\n");
            }
            return redisClient;
        }

        private void shutdown() {
            if (nonNull(sessionFactory)) {
                sessionFactory.close();
            }
            if (nonNull(redisClient)) {
                redisClient.shutdown();
            }
        }

        private List<City> fetchData(Main main) {
            try (Session session = main.sessionFactory.getCurrentSession()) {
                List<City> allCities = new ArrayList<>();
                session.beginTransaction();

                main.countryDAO.getAll();

                int totalCount = main.cityDAO.getTotalCount();
                int step = 500;
                for (int i = 0; i < totalCount; i += step) {
                    allCities.addAll(main.cityDAO.getItems(i, step));
                }

                session.getTransaction().commit();
                return allCities;
            }
        }

        private List<CityCountry> transformData(List<City> cities) {
            return cities.stream().map(city -> {
                CityCountry result = new CityCountry();
                result.setId(city.getId());
                result.setName(city.getName());
                result.setPopulation(city.getPopulation());
                result.setDistrict(city.getDistrict());

                Country country = city.getCountry();
                result.setCode2(country.getAlternativeCode());
                result.setContinent(country.getContinent());
                result.setCode(country.getCode());
                result.setCountryName(country.getName());
                result.setCountryPopulation(country.getPopulation());
                result.setCountryRegion(country.getRegion());
                result.setCountrySurfaceArea(country.getSurfaceArea());
                Set<CountryLanguage> countryLanguages = country.getLanguages();
                Set<Language> languages = countryLanguages.stream().map(cl -> {
                    Language language = new Language();
                    language.setLanguage(cl.getLanguageName());
                    language.setIsOfficial(cl.isOfficial());
                    language.setPercentage(cl.getPercentage());
                    return language;
                }).collect(Collectors.toSet());
                result.setLanguages(languages);

                return result;
            }).toList();
        }

        private void pushToRedis(List<CityCountry> data) {
            try (StatefulRedisConnection<String, String> connection = redisClient.connect()) {
                RedisStringCommands<String, String> sync = connection.sync();
                for (CityCountry cityCountry : data) {
                    try {
                        sync.set(String.valueOf(cityCountry.getId()), objectMapper.writeValueAsString(cityCountry));
                    } catch (JsonProcessingException e) {
                        e.printStackTrace();
                    }
                }

            }
        }

        private void testRedisData(List<Integer> ids) {
            try (StatefulRedisConnection<String, String> connection = redisClient.connect()) {
                RedisStringCommands<String, String> sync = connection.sync();
                for (Integer id : ids) {
                    String value = sync.get(String.valueOf(id));
                    try {
                        objectMapper.readValue(value, CityCountry.class);
                    } catch (JsonProcessingException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        private void testMysqlData(List<Integer> ids) {
            try (Session session = sessionFactory.getCurrentSession()) {
                session.beginTransaction();
                for (Integer id : ids) {
                    City city = cityDAO.getById(id);
                    city.getCountry().getLanguages();
                }
                session.getTransaction().commit();
            }
        }

        public static void main(String[] args) {
            Main main = new Main();
            List<City> allCities = main.fetchData(main);
            List<CityCountry> preparedData = main.transformData(allCities);
            main.pushToRedis(preparedData);

            main.sessionFactory.getCurrentSession().close();

            List<Integer> ids = List.of(3, 2545, 123, 4, 189, 89, 3458, 1189, 10, 102);

            long startRedis = System.currentTimeMillis();
            main.testRedisData(ids);
            long stopRedis = System.currentTimeMillis();

            long startMysql = System.currentTimeMillis();
            main.testMysqlData(ids);
            long stopMysql = System.currentTimeMillis();

            System.out.printf("%s:\t%d ms\n", "Redis", (stopRedis - startRedis));
            System.out.printf("%s:\t%d ms\n", "MySQL", (stopMysql - startMysql));

            main.shutdown();
        }
    }