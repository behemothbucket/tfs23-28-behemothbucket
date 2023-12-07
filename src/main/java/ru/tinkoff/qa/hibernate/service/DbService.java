package ru.tinkoff.qa.hibernate.service;

import jakarta.persistence.PersistenceException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.SelectionQuery;
import ru.tinkoff.qa.hibernate.HibernateSessionFactoryCreator;
import ru.tinkoff.qa.hibernate.models.Animal;
import ru.tinkoff.qa.hibernate.models.Place;
import ru.tinkoff.qa.hibernate.models.Workman;

import java.util.ArrayList;
import java.util.List;

public class DbService {
    public static Long countRows(String tableName) {
        try (Session session = HibernateSessionFactoryCreator.openSession()) {
            SelectionQuery<?> query = session.createSelectionQuery("SELECT COUNT (*) FROM " + tableName);
            return (long) query.getSingleResult();
        }
    }

    public static boolean canInsertInRange(int min, int max) {
        try (Session session = HibernateSessionFactoryCreator.openSession()) {
            Transaction transaction = session.beginTransaction();

            boolean isInsertable = true;
            for (int i = min; i <= max; i++) {
                Animal animal = new Animal();
                animal.setId(i);
                animal.setName("AnimalPlanet");
                session.persist(animal);

                isInsertable = !session.contains(animal);
            }

            transaction.rollback();

            return isInsertable;
        }
    }

    public static boolean insertIntoWorkmanFieldWithNull(String name, int id, int age, int position) {
        try (Session session = HibernateSessionFactoryCreator.openSession()) {
            Workman workman = new Workman(id, name, age, position);
            Transaction transaction = session.beginTransaction();

            boolean isInsertable = true;

            try {
                session.persist(workman);
                session.flush();
                transaction.commit();
            } catch (PersistenceException e) {
                transaction.rollback();
                isInsertable = false;
            }

            return isInsertable;
        }
    }

    public static void insertInPlace(String name, int placeNum, int row) {
        try (Session session = HibernateSessionFactoryCreator.openSession()) {
            int id = getMaxIdFromPlace() + 1;

            Place place = new Place(id, row, placeNum, name);
            Transaction transaction = session.beginTransaction();

            try {
                session.persist(place);
                session.flush();
                transaction.commit();
            } catch (PersistenceException e) {
                transaction.rollback();
            }
        }
    }

    private static int getMaxIdFromPlace() {
        int maxId;

        try (Session session = HibernateSessionFactoryCreator.openSession()) {
            Transaction transaction = session.beginTransaction();
            SelectionQuery<?> query = session.createSelectionQuery("SELECT MAX(id) FROM Place");
            maxId = (int) query.getSingleResult();

            transaction.commit();
        }

        return maxId;
    }

    public static List<String> getZooNames() {
        try (Session session = HibernateSessionFactoryCreator.openSession()) {
            SelectionQuery<?> query = session.createSelectionQuery("SELECT name FROM Zoo");
            List<?> resultList = query.list();

            List<String> zooNames = new ArrayList<>();
            for (Object result : resultList) {
                zooNames.add((String) result);
            }

            return zooNames;
        }
    }
}