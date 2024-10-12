import com.oocourse.spec3.main.Person;
import com.oocourse.spec3.main.Tag;

import java.util.HashMap;

public class CopyTag implements Tag {
    private int id;
    private HashMap<Integer,Person> persons;
    private int qtvs = 0;
    private boolean iamNew = true;

    public CopyTag(int id) {
        this.id = id;
        this.persons = new HashMap<>();
        CopyTagFlag.isNewValuel(this,false);
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
            CopyTagFlag.isNewValuel(this,false);
        }
    }

    @Override
    public boolean hasPerson(Person person) {
        return persons.containsValue(person);
    }

    @Override
    public int getValueSum() {
        int ret = 0;
        if (CopyTagFlag.find(this) && iamNew == false) {
            return qtvs;
        }
        if (persons.size() != 0) {
            for (Integer ieKey:persons.keySet()) {
                for (Integer jeKey:((CopyPerson) persons.get(ieKey)).getAcquaintance().keySet()) {
                    if (persons.get(jeKey) != null) {
                        ret += persons.get(ieKey).queryValue(persons.get(jeKey));
                    }
                }
            }
        }
        CopyTagFlag.isNewValuel(this,true);
        this.qtvs = ret;
        iamNew = false;
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
            CopyTagFlag.isNewValuel(this,false);
        }
    }

    @Override
    public int getSize() {
        return persons.size();
    }

    public void allPersAddSoc(int va) {
        if (persons.size() != 0) {
            for (Person person:persons.values()) {
                person.addSocialValue(va);
            }
        }
    }

    public void allPerAddMoney(int sum) {
        if (persons.size() != 0) {
            for (Person person:persons.values()) {
                person.addMoney(sum);
            }
        }
    }

    public HashMap<Integer, Person> getTagPersons() {
        return persons;
    }
}
