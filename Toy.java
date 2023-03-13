import java.util.Objects;

public class Toy {
    private int id;
    private String name;
    private int count;

    public Toy(int id, String name, int count) {
        if (count <= 0) {
            throw new IllegalArgumentException("Toy count can not be zero or negative at the beginning.");
        }
        this.name = name;
        this.count = count;
        this.id = id;
    }

    public static Toy parseToy(String string) {
        int firstSpace = string.indexOf(' ');
        int lastSpace = string.lastIndexOf(' ');
        if (firstSpace == lastSpace) {
            throw new IllegalArgumentException("Not enough spaces in string for toy parse.");
        }
        int id = Integer.parseInt(string.substring(0, firstSpace));
        int count = Integer.parseInt(string.substring(lastSpace + 1));
        return new Toy(id, string.substring(firstSpace + 1, lastSpace), count);
    }

    public void changeCount(int amount) {
        if (-amount > count) {
            throw new IllegalArgumentException("There is not enough toys.");
        }
        count = count + amount;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getCount() {
        return count;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Toy toy = (Toy) o;
        return id == toy.id && name.equals(toy.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name);
    }

    @Override
    public String toString() {
        return String.format("ID: %d: \"%s\", Осталось: %d", id, name, count);
    }
}
