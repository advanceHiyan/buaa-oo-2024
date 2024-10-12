import com.oocourse.spec2.main.Person;
import com.oocourse.spec2.main.Tag;

import java.util.HashMap;

public class MyTag implements Tag {
    private int id;
    private HashMap<Integer,Person> persons;
    private int qtvs = 0;
    private boolean iamNew = true;

    public MyTag(int id) {
        this.id = id;
        this.persons = new HashMap<>();
        TagFlag.isNewValuel(this,false);
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
            TagFlag.isNewValuel(this,false);
        }
    }

    @Override
    public boolean hasPerson(Person person) {
        return persons.containsValue(person);
    }

    @Override
    public int getValueSum() {
        int ret = 0;
        if (TagFlag.find(this) && iamNew == false) {
            return qtvs;
        }
        if (persons.size() != 0) {
            for (Integer ieKey:persons.keySet()) {
                for (Integer jeKey:((MyPerson) persons.get(ieKey)).getAcquaintance().keySet()) {
                    if (persons.get(jeKey) != null) {
                        ret += persons.get(ieKey).queryValue(persons.get(jeKey));
                    }
                }
            }
        }
        TagFlag.isNewValuel(this,true);
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
            TagFlag.isNewValuel(this,false);
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
