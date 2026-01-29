package classes;

public class Table {
    private Integer id;
    private Integer number;

    // Constructeur avec paramètres
    public Table(Integer id, Integer number) {
        this.id = id;
        this.number = number;
    }

    // Constructeur par défaut
    public Table() {
    }

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

    @Override
    public String toString() {
        return "Table{id=" + id + ", number=" + number + "}";
    }
}