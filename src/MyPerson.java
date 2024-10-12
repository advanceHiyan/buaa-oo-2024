import com.oocourse.spec3.main.Message;
import com.oocourse.spec3.main.NoticeMessage;
import com.oocourse.spec3.main.Person;
import com.oocourse.spec3.main.Tag;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MyPerson implements Person {
    private int id;
    private String name;
    private int age;
    private HashMap<Integer,Person> acquaintance = new HashMap<>();
    private HashMap<Person,Integer> value = new HashMap<>();
    private HashMap<Integer,Tag> tags = new HashMap<>();
    private int money = 0;
    private int socialValue = 0;
    private HashMap<Integer,Message> mapMessages = new HashMap<>();
    private ArrayList<Message> messages = new ArrayList<>();
    private long bestID = Long.MAX_VALUE;
    private long maxVa = Long.MIN_VALUE;

    public MyPerson(int id, String name, int age) {
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
        TagFlag.isNewValuel((MyTag) tag,false);
        if (containsTag(tag.getId()) == false) {
            tags.put(tag.getId(),tag);
        }
    }

    @Override
    public void delTag(int id) {
        TagFlag.isNewValuel((MyTag) tags.get(id),false);
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
        if (acquaintance.get(person.getId()) != null) {
            return true;
        }
        return false;
    }

    @Override
    public int queryValue(Person person) {
        if (value.get(person) != null) {
            return value.get(person);
        }
        return 0;
    }

    @Override
    public void addSocialValue(int num) {
        this.socialValue += num;
    }

    @Override
    public int getSocialValue() {
        return socialValue;
    }

    @Override
    public List<Message> getMessages() {
        ArrayList<Message> bg = (ArrayList<Message>) messages.clone();
        return bg;
    }

    @Override
    public List<Message> getReceivedMessages() {
        if (messages.size() < 5) {
            return (List<Message>) messages.clone();
        }
        List<Message> bg = messages.subList(0,5);
        return bg;
    }

    @Override
    public void addMoney(int num) {
        this.money += num;
    }

    @Override
    public int getMoney() {
        return money;
    }

    public void insertMessage(Message message) {
        messages.add(0,message);
    }

    public void clearNotices() {
        int i = 0;
        while (i < messages.size()) {
            if (messages.get(i) instanceof NoticeMessage) {
                messages.remove(i);
            } else {
                i++;
            }
        }
    }

    public void buildLink(Person person, int value) {
        this.acquaintance.put(person.getId(),person);
        this.value.put(person,value);
        if (value > maxVa) {
            maxVa = value;
            bestID = person.getId();
        } else if (value == maxVa && person.getId() < bestID) {
            bestID = person.getId();
        }
    }

    public void removeLink(Person person) {
        int i;
        if (acquaintance.get(person.getId()) != null) {
            acquaintance.remove(person.getId());
            this.value.remove(person.getId());
        }
        if (person.getId() == bestID) {
            maxVa = Long.MIN_VALUE;
            bestID = Long.MAX_VALUE;
            if (acquaintance.size() != 0) {
                for (Integer key:acquaintance.keySet()) {
                    Person p = acquaintance.get(key);
                    if (maxVa < value.get(p)) {
                        maxVa = value.get(p);
                        bestID = key;
                    } else if (maxVa == value.get(p) && acquaintance.get(key).getId() < bestID) {
                        bestID = acquaintance.get(key).getId();
                    }
                }
            }
        }
    }

    public void addPerValue(Person person,int va) {
        if (acquaintance.get(person.getId()) != null) {
            int k = value.get(person) + va;
            value.put(person,k);
            if (bestID == person.getId() && va < 0) {
                maxVa = Long.MIN_VALUE;
                bestID = Long.MAX_VALUE;
                if (acquaintance.size() != 0) {
                    for (Integer key:acquaintance.keySet()) {
                        Person p = acquaintance.get(key);
                        if (maxVa < value.get(p)) {
                            maxVa = value.get(p);
                            bestID = key;
                        } else if (maxVa == value.get(p) &&
                                acquaintance.get(key).getId() < bestID) {
                            bestID = acquaintance.get(key).getId();
                        }
                    }
                }
                return;
            }
            if (value.get(person) > maxVa) {
                maxVa = value.get(person);
                bestID = person.getId();
            } else if (value.get(person) == maxVa && person.getId() < bestID) {
                bestID = person.getId();
            }
        }
    }

    public HashMap<Integer,Person> getAcquaintance() {
        return acquaintance;
    }

    public boolean strictEquals(Person person) { return true; }

    public void modTagRemove(Person person) {
        if (tags.size() > 0) {
            for (Integer key:tags.keySet()) {
                tags.get(key).delPerson(person);
                TagFlag.isNewValuel((MyTag) tags.get(key),false);
            }
        }
    }

    public int findBestID() {
        return ((int) bestID);
    }
}