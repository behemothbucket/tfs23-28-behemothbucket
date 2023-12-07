package ru.tinkoff.qa.hibernate;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import ru.tinkoff.qa.hibernate.models.Animal;
import ru.tinkoff.qa.hibernate.models.Place;
import ru.tinkoff.qa.hibernate.models.Workman;
import ru.tinkoff.qa.hibernate.models.Zoo;

public class HibernateSessionFactoryCreator {


    public static SessionFactory createSessionFactory() {
        return new Configuration().
                configure().
                addAnnotatedClass(Animal.class).
                addAnnotatedClass(Zoo.class).
                addAnnotatedClass(Workman.class).
                addAnnotatedClass(Place.class).
                buildSessionFactory();
    }

    public static Session openSession() {
        return HibernateSessionFactoryCreator.createSessionFactory().openSession();
    }
}