import com.oocourse.spec2.main.Person;
import com.oocourse.spec2.main.Tag;

import java.util.ArrayList;
import java.util.HashMap;

public class CopyPerson implements Person {
    private int id;
    private String name;
    private int age;
    private ArrayList<Person> acquaintance = new ArrayList<>();
    private ArrayList<Integer> value = new ArrayList<>();
    private HashMap<Integer,Tag> tags = new HashMap<>();
    private long bestID = Long.MAX_VALUE;
    private long maxVa = Long.MIN_VALUE;

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
    public boolean containsTag(int id) {
        return (tags.get(id) != null);
    }

    @Override
    public Tag getTag(int id) {
        return tags.get(id); // 可能为null
    }

    @Override
    public void addTag(Tag tag) {
        if (containsTag(tag.getId()) == false) {
            tags.put(tag.getId(),tag);
        }
    }

    @Override
    public void delTag(int id) {
        if (containsTag(id)) {
            tags.remove(id);
        }
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
        if (value > maxVa) {
            maxVa = value;
            bestID = person.getId();
        } else if (value == maxVa && person.getId() < bestID) {
            bestID = person.getId();
        }
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
        if (person.getId() == bestID) {
            maxVa = Long.MIN_VALUE;
            bestID = Long.MAX_VALUE;
            for (i = 0;i < acquaintance.size();i++) {
                if (maxVa < value.get(i)) {
                    maxVa = value.get(i);
                    bestID = acquaintance.get(i).getId();
                } else if (maxVa == value.get(i) && acquaintance.get(i).getId() < bestID) {
                    bestID = acquaintance.get(i).getId();
                }
            }
        }
    }

    public void addPerValue(Person person,int va) {
        for (int i = 0; i < acquaintance.size(); i++) {
            if (acquaintance.get(i).getId() == person.getId()) {
                int k = value.get(i) + va;
                value.set(i,k);
                if (bestID == person.getId() && va < 0) {
                    maxVa = Long.MIN_VALUE;
                    bestID = Long.MAX_VALUE;
                    for (i = 0;i < acquaintance.size();i++) {
                        if (maxVa < value.get(i)) {
                            maxVa = value.get(i);
                            bestID = acquaintance.get(i).getId();
                        } else if (maxVa == value.get(i) && acquaintance.get(i).getId() < bestID) {
                            bestID = acquaintance.get(i).getId();
                        }
                    }
                    return;
                }
                if (value.get(i) > maxVa) {
                    maxVa = value.get(i);
                    bestID = person.getId();
                } else if (value.get(i) == maxVa && person.getId() < bestID) {
                    bestID = person.getId();
                }
            }
        }
    }

    public ArrayList<Person> getAcquaintance() {
        return acquaintance;
    }

    public boolean strictEquals(Person person) { return true; }

    public void modTagRemove(Person person) {
        if (tags.size() > 0) {
            for (Integer key:tags.keySet()) {
                tags.get(key).delPerson(person);
            }
        }
    }

    public int findBestID() {
        return ((int) bestID);
    }
}