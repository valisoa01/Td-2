package classes;

import Connection.DBRetriever;

import java.sql.Connection;
import java.time.Instant;

public class Table {
    private Integer id;
    private Integer number;

    public Table(Integer id) {}

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getNumber() {
        return number;
    }

    public void setNumber(Integer number) {
        this.number = number;
    }
}
