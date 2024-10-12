import com.oocourse.spec2.exceptions.EqualRelationException;
import com.oocourse.spec2.exceptions.EqualTagIdException;
import com.oocourse.spec2.exceptions.EqualPersonIdException;
import com.oocourse.spec2.exceptions.RelationNotFoundException;
import com.oocourse.spec2.exceptions.AcquaintanceNotFoundException;
import com.oocourse.spec2.exceptions.PathNotFoundException;
import com.oocourse.spec2.exceptions.PersonIdNotFoundException;
import com.oocourse.spec2.exceptions.TagIdNotFoundException;
import com.oocourse.spec2.main.Network;
import com.oocourse.spec2.main.Person;
import com.oocourse.spec2.main.Tag;

import java.util.ArrayList;
import java.util.HashMap;

public class MyNetwork implements Network {
    private HashMap<Integer,Person> persons;
    private HashMap<Integer,Integer> toTreeEnd; //构造一个连通的树
    private int sumTriple;
    private int sumBlock;
    private HashMap<Integer,Integer> mark = new HashMap<>();

    public MyNetwork() {
        this.persons = new HashMap<>();
        this.toTreeEnd = new HashMap<>();
        this.sumTriple = 0;
        this.sumBlock = 0;
    }

    @Override
    public boolean containsPerson(int id) {
        return persons.containsKey(id);
    }

    @Override
    public Person getPerson(int id) {
        if (containsPerson(id)) {
            return persons.get(id);
        }
        return null;
    }

    @Override
    public void addPerson(Person person) throws EqualPersonIdException {
        if (!containsPerson(person.getId())) {
            persons.put(person.getId(),person);
            toTreeEnd.put(person.getId(),null);
            sumBlock++;
        } else {
            throw new MyEqualPersonIdException(person.getId());
        }
    }

    @Override
    public void addRelation(int id1, int id2, int value) throws
            PersonIdNotFoundException, EqualRelationException {
        if (containsPerson(id1) && containsPerson(id2) &&
                !getPerson(id1).isLinked(getPerson(id2))) {
            ((MyPerson) this.getPerson(id1)).buildLink(getPerson(id2),value);
            ((MyPerson) this.getPerson(id2)).buildLink(getPerson(id1),value);
            if ((getEndPoint(id1) == getEndPoint(id2)) == false) { //本来就不连通
                toTreeEnd.put(getEndPoint(id1),getEndPoint(id2));
                sumBlock--;
            }
            sumTriple += findCommon(id1,id2);
            for (MyTag key:TagFlag.getIsNewValue().keySet()) {
                TagFlag.isNewValuel(key,false);
            }
        } else {
            if (!containsPerson(id1)) {
                throw new MyPersonIdNotFoundException(id1);
            } else if (containsPerson(id1) && !containsPerson(id2)) {
                throw new MyPersonIdNotFoundException(id2);
            } else if (containsPerson(id1) && containsPerson(id2) &&
                    getPerson(id1).isLinked(getPerson(id2))) {
                throw new MyEqualRelationException(id1, id2);
            }
        }
    }

    @Override
    public void modifyRelation(int id1, int id2, int value) throws PersonIdNotFoundException,
            EqualPersonIdException, RelationNotFoundException {
        if (containsPerson(id1) && containsPerson(id2) && id1 != id2 &&
                getPerson(id1).isLinked(getPerson(id2)) &&
                (getPerson(id1).queryValue(getPerson(id2)) + value) > 0) {
            ((MyPerson) getPerson(id1)).addPerValue(getPerson(id2),value);
            ((MyPerson) getPerson(id2)).addPerValue(getPerson(id1),value);
            for (MyTag key:TagFlag.getIsNewValue().keySet()) {
                TagFlag.isNewValuel(key,false);
            }
        } else if (containsPerson(id1) && containsPerson(id2) && id1 != id2 &&
                getPerson(id1).isLinked(getPerson(id2)) &&
                (getPerson(id1).queryValue(getPerson(id2)) + value) <= 0) {
            ((MyPerson) getPerson(id1)).removeLink(getPerson(id2));
            ((MyPerson) getPerson(id2)).removeLink(getPerson(id1));
            modifyTree(id1,id2);
            sumTriple -= findCommon(id1,id2);
            ((MyPerson) getPerson(id1)).modTagRemove(getPerson(id2));
            ((MyPerson) getPerson(id2)).modTagRemove(getPerson(id1));
            for (MyTag key:TagFlag.getIsNewValue().keySet()) {
                TagFlag.isNewValuel(key,false);
            }
        } else if (!containsPerson(id1) || !containsPerson(id2) || id1 == id2 ||
                !getPerson(id1).isLinked(getPerson(id2))) {
            if (!containsPerson(id1)) {
                throw new MyPersonIdNotFoundException(id1);
            } else if (containsPerson(id1) && !containsPerson(id2)) {
                throw new MyPersonIdNotFoundException(id2);
            } else if (id1 == id2) {
                throw new MyEqualPersonIdException(id1);
            } else {
                throw new MyRelationNotFoundException(id1,id2);
            }
        }
    }

    @Override
    public int queryValue(int id1, int id2) throws PersonIdNotFoundException,
            RelationNotFoundException {
        if (containsPerson(id1) && containsPerson(id2) && getPerson(id1).isLinked(getPerson(id2))) {
            return getPerson(id1).queryValue(getPerson(id2));
        } else {
            if (!containsPerson(id1)) {
                throw new MyPersonIdNotFoundException(id1);
            } else if (containsPerson(id1) && !containsPerson(id2)) {
                throw new MyPersonIdNotFoundException(id2);
            } else {
                throw new MyRelationNotFoundException(id1,id2);
            }
        }
    }

    @Override
    public boolean isCircle(int id1, int id2) throws PersonIdNotFoundException {
        if (containsPerson(id1) && containsPerson(id2)) {
            return (onlyGetEndPoint(id1) == onlyGetEndPoint(id2));
        } else {
            if (!containsPerson(id1)) {
                throw new MyPersonIdNotFoundException(id1);
            } else {
                throw new MyPersonIdNotFoundException(id2);
            }
        }
    }

    @Override
    public int queryBlockSum() {
        return sumBlock;
    }

    @Override
    public int queryTripleSum() {
        return sumTriple;
    }

    @Override
    public void addTag(int personId, Tag tag) throws
            PersonIdNotFoundException, EqualTagIdException {
        if (containsPerson(personId) && !getPerson(personId).containsTag(tag.getId())) {
            getPerson(personId).addTag(tag);
            TagFlag.isNewValuel((MyTag) tag,false);
        } else {
            if (!containsPerson(personId)) {
                throw new MyPersonIdNotFoundException(personId);
            } else {
                throw new MyEqualTagIdException(tag.getId());
            }
        }
    }

    @Override
    public void addPersonToTag(int personId1, int personId2, int tagId) throws
            PersonIdNotFoundException, RelationNotFoundException,
            TagIdNotFoundException, EqualPersonIdException {
        if (containsPerson(personId1) && containsPerson(personId2) && personId1 != personId2 &&
                getPerson(personId2).isLinked(getPerson(personId1)) &&
                getPerson(personId2).containsTag(tagId) &&
                !getPerson(personId2).getTag(tagId).hasPerson(getPerson(personId1)) &&
                ((MyTag) getPerson(personId2).getTag(tagId)).getTagPersons().size() <= 1111) {
            getPerson(personId2).getTag(tagId).addPerson(getPerson(personId1));
        } else {
            if (!containsPerson(personId1)) {
                throw new MyPersonIdNotFoundException(personId1);
            } else if (!containsPerson(personId2)) {
                throw new MyPersonIdNotFoundException(personId2);
            } else if (personId1 == personId2) {
                throw new MyEqualPersonIdException(personId1);
            } else if (!getPerson(personId2).isLinked(getPerson(personId1))) {
                throw new MyRelationNotFoundException(personId1,personId2);
            } else if (!getPerson(personId2).containsTag(tagId)) {
                throw new MyTagIdNotFoundException(tagId);
            } else if (getPerson(personId2).getTag(tagId).hasPerson(getPerson(personId1))) {
                throw new MyEqualPersonIdException(personId1);
            }
        }
    }

    @Override
    public int queryTagValueSum(int personId, int tagId) throws
            PersonIdNotFoundException, TagIdNotFoundException {
        if (containsPerson(personId) && getPerson(personId).containsTag(tagId)) {
            return getPerson(personId).getTag(tagId).getValueSum();
        } else {
            if (!containsPerson(personId)) {
                throw new MyPersonIdNotFoundException(personId);
            } else {
                throw new MyTagIdNotFoundException(tagId);
            }
        }
    }

    @Override
    public int queryTagAgeVar(int personId, int tagId) throws
            PersonIdNotFoundException, TagIdNotFoundException {
        if (containsPerson(personId) && getPerson(personId).containsTag(tagId)) {
            return getPerson(personId).getTag(tagId).getAgeVar();
        } else {
            if (!containsPerson(personId)) {
                throw new MyPersonIdNotFoundException(personId);
            } else {
                throw new MyTagIdNotFoundException(tagId);
            }
        }
    }

    @Override
    public void delPersonFromTag(int personId1, int personId2, int tagId) throws
            PersonIdNotFoundException, TagIdNotFoundException {
        if (containsPerson(personId1) && containsPerson(personId2) &&
                getPerson(personId2).containsTag(tagId) &&
                getPerson(personId2).getTag(tagId).hasPerson(getPerson(personId1))) {
            getPerson(personId2).getTag(tagId).delPerson(getPerson(personId1));
        } else {
            if (!containsPerson(personId1)) {
                throw new MyPersonIdNotFoundException(personId1);
            } else if (!containsPerson(personId2)) {
                throw new MyPersonIdNotFoundException(personId2);
            } else if (!getPerson(personId2).containsTag(tagId)) {
                throw new MyTagIdNotFoundException(tagId);
            } else {
                throw new MyPersonIdNotFoundException(personId1);
            }
        }
    }

    @Override
    public void delTag(int personId, int tagId) throws
            PersonIdNotFoundException, TagIdNotFoundException {
        if (containsPerson(personId) && getPerson(personId).containsTag(tagId)) {
            getPerson(personId).delTag(tagId);
        } else {
            if (!containsPerson(personId)) {
                throw new MyPersonIdNotFoundException(personId);
            } else {
                throw new MyTagIdNotFoundException(tagId);
            }
        }
    }

    @Override
    public int queryBestAcquaintance(int id) throws
            PersonIdNotFoundException, AcquaintanceNotFoundException {
        if (containsPerson(id) && ((MyPerson) getPerson(id)).getAcquaintance().size() != 0) {
            return ((MyPerson) getPerson(id)).findBestID();
        } else {
            if (!containsPerson(id)) {
                throw new MyPersonIdNotFoundException(id);
            } else {
                throw new MyAcquaintanceNotFoundException(id);
            }
        }
    }

    @Override
    public int queryCoupleSum() {
        int ret = 0;
        if (persons.size() == 0) {
            return 0;
        }
        for (Integer ieKey:persons.keySet()) {
            MyPerson ieP = (MyPerson) persons.get(ieKey);
            if (ieP.getAcquaintance().size() != 0) {
                int j = ieP.findBestID();
                MyPerson jeP = ((MyPerson) persons.get(j));
                if (jeP.getAcquaintance().size() != 0) {
                    if (jeP.findBestID() == ieKey) {
                        ret++;
                    }
                }
            }
        }
        ret /= 2;
        return ret;
    }

    @Override
    public int queryShortestPath(int id1, int id2) throws
            PersonIdNotFoundException, PathNotFoundException {
        if (containsPerson(id1) && containsPerson(id2) &&
                (onlyGetEndPoint(id1) == onlyGetEndPoint(id2))) {
            mark.clear();
            if (id1 == id2) {
                return 0;
            }
            ArrayList<Person> cnm = new ArrayList<>();
            cnm.add(persons.get(id1));
            mark.put(persons.get(id1).getId(),1);
            return findNearPath(0,cnm,id2);
        } else {
            if (!containsPerson(id1)) {
                throw new MyPersonIdNotFoundException(id1);
            } else if (!containsPerson(id2)) {
                throw new MyPersonIdNotFoundException(id2);
            } else {
                throw new MyPathNotFoundException(id1,id2);
            }
        }
    }

    public int findNearPath(int floor,ArrayList<Person> floorPoople,int id2) {
        ArrayList<Person> newPeople = new ArrayList<>();
        for (int i = 0;i < floorPoople.size();i++) {
            if (((MyPerson) floorPoople.get(i)).getAcquaintance().size() != 0) {
                for (Integer jeKey:((MyPerson) floorPoople.get(i)).getAcquaintance().keySet()) {
                    Person p = ((MyPerson) floorPoople.get(i)).getAcquaintance().get(jeKey);
                    if (p.getId() == id2) {
                        return floor;
                    } else {
                        if (mark.get(p.getId()) == null) {
                            mark.put(p.getId(),1);
                            newPeople.add(p);
                        }
                    }
                }
            }
        }
        return findNearPath(floor + 1,newPeople,id2);
    }

    public int getEndPoint(int id) { //连通树
        int end = id;
        while (toTreeEnd.get(end) != null) {
            end = toTreeEnd.get(end);
        }
        if (end != id) { //路径优化,直达终点
            int temp = id;
            while (toTreeEnd.get(temp) != null) {
                int old = temp;
                temp = toTreeEnd.get(temp);
                toTreeEnd.put(old,end);
            }
        }
        return end;
    }

    public int onlyGetEndPoint(int id) {
        int end = id;
        while (toTreeEnd.get(end) != null) {
            end = toTreeEnd.get(end);
        }
        return end;
    }

    public void modifyTree(int id1,int id2) {
        HashMap<Integer,Person> queue1 = new HashMap<>();
        addQueueAndChange(queue1,(MyPerson) persons.get(id1),id1);
        toTreeEnd.put(id1,null);
        if (queue1.get(id2) == null) {
            sumBlock++;
            HashMap<Integer,Person> queue2 = new HashMap<>();
            addQueueAndChange(queue2,(MyPerson) persons.get(id2),id2);
            toTreeEnd.put(id2,null);
        }
    }

    public void addQueueAndChange(HashMap<Integer,Person> queue,MyPerson personHead,int newEnd) {
        if (personHead.getAcquaintance() != null && !personHead.getAcquaintance().isEmpty()) {
            for (Integer key:personHead.getAcquaintance().keySet()) {
                Person myPerson = personHead.getAcquaintance().get(key);
                if (queue.get(myPerson.getId()) == null) {
                    queue.put(myPerson.getId(),myPerson);
                    toTreeEnd.put(myPerson.getId(),newEnd);
                    addQueueAndChange(queue,(MyPerson) myPerson,newEnd);
                }
            }
        }
    }

    public int findCommon(int id1,int id2) {
        int ret = 0;
        HashMap<Integer,Person> cnm = ((MyPerson) persons.get(id1)).getAcquaintance();
        if (cnm != null && !cnm.isEmpty()) {
            for (Integer k:cnm.keySet()) {
                Person person = cnm.get(k);
                if (((MyPerson) persons.get(id2)).getAcquaintance().get(k) != null) {
                    ret++;
                }
            }
        }
        return ret;
    }

    public Person[] getPersons() { return null; }
}
