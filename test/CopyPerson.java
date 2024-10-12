import com.oocourse.spec1.main.Person;

import java.util.ArrayList;

public class CopyPerson implements Person {
    private int id;
    private String name;
    private int age;
    private ArrayList<Person> acquaintance = new ArrayList<>();
    private ArrayList<Integer> value = new ArrayList<>();

    public CopyPerson(int id, String name, int age) {
        this.id = id;
        this.name = name;
        this.age = age;
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public int getAge() {
        return age;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj != null && obj instanceof Person) {
            return (((Person) obj).getId() == id);
        } else {
            return false;
        }
    }

    @Override
    public boolean isLinked(Person person) {
        if (person.getId() == id) {
            return true;
        }
        for (int i = 0; i < acquaintance.size(); i++) {
            if (acquaintance.get(i).getId() == person.getId()) {
                return true;
            }
        }
        return false;
    }

    @Override
    public int queryValue(Person person) {
        for (int i = 0; i < acquaintance.size(); i++) {
            if (acquaintance.get(i).getId() == person.getId()) {
                return value.get(i);
            }
        }
        return 0;
    }

    public void buildLink(Person person, int value) {
        this.acquaintance.add(person);
        this.value.add(value);
    }

    public void removeLink(Person person) {
        int i;
        for (i = 0;i < acquaintance.size();i++) {
            if (acquaintance.get(i).getId() == person.getId()) {
                break;
            }
        }
        this.acquaintance.remove(person);
        this.value.remove(i);
    }

    public void addPerValue(Person person,int va) {
        for (int i = 0; i < acquaintance.size(); i++) {
            if (acquaintance.get(i).getId() == person.getId()) {
                int k = value.get(i) + va;
                value.set(i,k);
            }
        }
    }

    public ArrayList<Person> getAcquaintance() {
        return acquaintance;
    }
}