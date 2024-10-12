import com.oocourse.spec1.exceptions.EqualPersonIdException;
import com.oocourse.spec1.exceptions.EqualRelationException;
import com.oocourse.spec1.exceptions.RelationNotFoundException;
import com.oocourse.spec1.exceptions.PersonIdNotFoundException;
import com.oocourse.spec1.main.Network;
import com.oocourse.spec1.main.Person;

import java.util.ArrayList;
import java.util.HashMap;

public class OneNetwork implements Network {
    private HashMap<Integer,Person> persons;
    private HashMap<Integer,Integer> toTreeEnd; //构造一个连通的树
    private int sumTriple;
    private int sumBlock;

    public OneNetwork() {
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
            throw new MEP(person.getId());
        }
    }

    @Override
    public void addRelation(int id1, int id2, int value) throws
            PersonIdNotFoundException, EqualRelationException {
        if (containsPerson(id1) && containsPerson(id2) &&
                !getPerson(id1).isLinked(getPerson(id2))) {
            ((CopyPerson) this.getPerson(id1)).buildLink(getPerson(id2),value);
            ((CopyPerson) this.getPerson(id2)).buildLink(getPerson(id1),value);
            if ((getEndPoint(id1) == getEndPoint(id2)) == false) { //本来就不连通
                toTreeEnd.put(getEndPoint(id1),getEndPoint(id2));
                sumBlock--;
            }
            sumTriple += findCommon(id1,id2);
        } else {
            if (!containsPerson(id1)) {
                throw new MP(id1);
            } else if (containsPerson(id1) && !containsPerson(id2)) {
                throw new MP(id2);
            } else if (containsPerson(id1) && containsPerson(id2) &&
                    getPerson(id1).isLinked(getPerson(id2))) {
                throw new MER(id1, id2);
            }
        }
    }

    @Override
    public void modifyRelation(int id1, int id2, int value) throws PersonIdNotFoundException,
            EqualPersonIdException, RelationNotFoundException {
        if (containsPerson(id1) && containsPerson(id2) && id1 != id2 &&
                getPerson(id1).isLinked(getPerson(id2)) && getPerson(id1).
                queryValue(getPerson(id2)) + value > 0) {
            ((CopyPerson) getPerson(id1)).addPerValue(getPerson(id2),value);
            ((CopyPerson) getPerson(id2)).addPerValue(getPerson(id1),value);
        } else if (containsPerson(id1) && containsPerson(id2) && id1 != id2 &&
                getPerson(id1).isLinked(getPerson(id2)) &&
                getPerson(id1).queryValue(getPerson(id2)) + value <= 0) {
            ((CopyPerson) getPerson(id1)).removeLink(getPerson(id2));
            ((CopyPerson) getPerson(id2)).removeLink(getPerson(id1));
            modifyTree(id1,id2);
            sumTriple -= findCommon(id1,id2);
        } else if (!containsPerson(id1) || !containsPerson(id2) || id1 == id2 ||
                !getPerson(id1).isLinked(getPerson(id2))) {
            if (!containsPerson(id1)) {
                throw new MP(id1);
            } else if (containsPerson(id1) && !containsPerson(id2)) {
                throw new MP(id2);
            } else if (id1 == id2) {
                throw new MEP(id1);
            } else {
                throw new MR(id1,id2);
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
                throw new MP(id1);
            } else if (containsPerson(id1) && !containsPerson(id2)) {
                throw new MP(id2);
            } else {
                throw new MR(id1,id2);
            }
        }
    }

    @Override
    public boolean isCircle(int id1, int id2) throws PersonIdNotFoundException {
        if (containsPerson(id1) && containsPerson(id2)) {
            return (onlyGetEndPoint(id1) == onlyGetEndPoint(id2));
        } else {
            if (!containsPerson(id1)) {
                throw new MP(id1);
            } else {
                throw new MP(id2);
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
        addQueueAndChange(queue1,(CopyPerson) persons.get(id1),id1);
        toTreeEnd.put(id1,null);
        if (queue1.get(id2) == null) {
            sumBlock++;
            HashMap<Integer,Person> queue2 = new HashMap<>();
            addQueueAndChange(queue2,(CopyPerson) persons.get(id2),id2);
            toTreeEnd.put(id2,null);
        }
    }

    public void addQueueAndChange(HashMap<Integer,Person> queue,CopyPerson personHead,int newEnd) {
        if (personHead.getAcquaintance() != null && !personHead.getAcquaintance().isEmpty()) {
            for (Person CopyPerson:personHead.getAcquaintance()) {
                if (queue.get(CopyPerson.getId()) == null) {
                    queue.put(CopyPerson.getId(),CopyPerson);
                    toTreeEnd.put(CopyPerson.getId(),newEnd);
                    addQueueAndChange(queue,(CopyPerson) CopyPerson,newEnd);
                }
            }
        }
    }

    public int findCommon(int id1,int id2) {
        int ret = 0;
        ArrayList<Person> cnm = ((CopyPerson) persons.get(id1)).getAcquaintance();
        if (cnm != null && !cnm.isEmpty()) {
            for (Person person:cnm) {
                if (((CopyPerson) persons.get(id2)).getAcquaintance().contains(person)) {
                    ret++;
                }
            }
        }
        return ret;
    }

    public int getCopySize() {
        return persons.size();
    }

    public HashMap<Integer,Person> getCopyPs() {
        return persons;
    }

    public boolean strictEquals(Person person) { return true; }
}
