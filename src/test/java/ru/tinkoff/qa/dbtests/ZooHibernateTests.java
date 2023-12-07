package ru.tinkoff.qa.dbtests;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import ru.tinkoff.qa.hibernate.BeforeCreator;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static ru.tinkoff.qa.hibernate.service.DbService.*;

public class ZooHibernateTests {

    @BeforeAll
    static void init() {
        BeforeCreator.createData();
    }

    /**
     * В таблице public.animal ровно 10 записей
     */
    @Test
    public void countRowAnimal() {
        long countOfRows = countRows("Animal");
        assertEquals(10, countOfRows, "Count rows in table 'Animal'");
    }

    /**
     * В таблицу public.animal нельзя добавить строку с индексом от 1 до 10 включительно
     */
    @Test
    public void insertIndexAnimal() {
        boolean canInsertInRange = canInsertInRange(1, 10);
        assertFalse(canInsertInRange, "Unable to insert rows into the Animal table with indices from 1 to 10");
    }

    /**
     * В таблицу public.workman нельзя добавить строку с name = null
     */
    @Test
    public void insertNullToWorkman() {
        boolean canInsertNullName = insertIntoWorkmanFieldWithNull(null, 1,5,142);
        assertFalse(canInsertNullName, "Unable to insert a row with name = null into the Workman table");
    }

    /**
     * Если в таблицу public.places добавить еще одну строку, то в ней будет 6 строк
     */
    @Test
    public void insertPlacesCountRow() {
        insertInPlace("New-York", 53, 6);
        long countOfRows = countRows("Place");
        assertEquals(6, countOfRows, "Count rows in table 'Place'");
    }

    /**
     * В таблице public.zoo всего три записи с name 'Центральный', 'Северный', 'Западный'
     */
    @Test
    public void countRowZoo() {
        List<String> zooNames = getZooNames();
        List<String> expectedZooNames =  List.of("Центральный", "Северный", "Западный");
        assertEquals(expectedZooNames, zooNames);
    }
}
