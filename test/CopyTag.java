import com.oocourse.spec2.main.Person;
import com.oocourse.spec2.main.Tag;

import java.util.HashMap;

public class CopyTag implements Tag {
    private int id;
    private HashMap<Integer,Person> persons;

    public CopyTag(int id) {
        this.id = id;
        this.persons = new HashMap<>();
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj != null && obj instanceof Tag) {
            return (((Tag) obj).getId() == id);
        }
        return false;
    }

    @Override
    public void addPerson(Person person) {
        if (!hasPerson(person)) {
            persons.put(person.getId(),person);
        }
    }

    @Override
    public boolean hasPerson(Person person) {
        return persons.containsValue(person);
    }

    @Override
    public int getValueSum() {
        int ret = 0;
        if (persons.size() != 0) {
            for (Integer ieKey:persons.keySet()) {
                for (Integer jeKey:persons.keySet()) {
                    if (persons.get(ieKey).isLinked(persons.get(jeKey))) {
                        ret += persons.get(ieKey).queryValue(persons.get(jeKey));
                    }
                }
            }
        }
        return ret;
    }

    @Override
    public int getAgeMean() {
        int ret = 0;
        if (persons.size() > 0) {
            for (Integer key:persons.keySet()) {
                ret += persons.get(key).getAge();
            }
            ret /= persons.size();
        }
        return ret;
    }

    @Override
    public int getAgeVar() {
        int ret = 0;
        if (persons.size() > 0) {
            for (Integer key:persons.keySet()) {
                ret += ((persons.get(key).getAge() - getAgeMean()) *
                        (persons.get(key).getAge() - getAgeMean()));
            }
            ret /= persons.size();
        }
        return ret;
    }

    @Override
    public void delPerson(Person person) {
        if (hasPerson(person)) {
            persons.remove(person.getId());
        }
    }

    @Override
    public int getSize() {
        return persons.size();
    }

    public HashMap<Integer, Person> getTagPersons() {
        return persons;
    }
}
