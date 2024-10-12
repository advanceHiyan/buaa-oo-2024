import com.oocourse.spec2.exceptions.EqualPersonIdException;
import com.oocourse.spec2.exceptions.EqualRelationException;
import com.oocourse.spec2.exceptions.PersonIdNotFoundException;
import com.oocourse.spec2.exceptions.RelationNotFoundException;
import com.oocourse.spec2.main.Person;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.Random;

import static org.junit.Assert.*;

public class MyNetworkTest {
    public MyNetwork myNetwork = null;
    private OneNetwork oneNetwork = null;
    private MyNetwork yingZi = null;

    @Before
    public void setUp() throws Exception {
        assert (1 == 1);
    }

    @After
    public void tearDown() throws Exception {
        assert (2 == 2);
    }

    @Test
    public void queryCoupleSum() throws EqualPersonIdException, PersonIdNotFoundException,
            EqualRelationException, RelationNotFoundException {
        this.myNetwork = new MyNetwork();
        this.oneNetwork = new OneNetwork();
        this.yingZi = new MyNetwork();
        for (int i = 0;i <= 100;i++) {
            MyPerson person = new MyPerson(i,"傻逼",100);
            addPerson(person);
        }
        AssErt();
        Random random = new Random();
        for (int i = 0;i < 100;i++) {
            addRelation(random.nextInt(100),random.nextInt(100),random.nextInt());
            modifyRelation(random.nextInt(100),random.nextInt(100),random.nextInt());
        }
        for (int i = 0;i < 18;i++) {
            for (int j = 0;j < 18;j++) {
                addRelation(i,j,100);
            }
        }
        for (int i = 0;i < 100;i++) {
            AssErt();
        }
    }

    public void addPerson(Person person) throws EqualPersonIdException {
        CopyPerson cp = new CopyPerson(person.getId(),person.getName(),person.getAge());
        try {
            myNetwork.addPerson(person);
            oneNetwork.addPerson(cp);
            yingZi.addPerson(person);
        } catch (EqualPersonIdException e) {
            throw new RuntimeException(e);
        }
    }

    public void addRelation(int id1, int id2, int value) throws
            PersonIdNotFoundException, EqualRelationException, RelationNotFoundException {
        try {
            myNetwork.addRelation(id1,id2,value);
            oneNetwork.addRelation(id1,id2,value);
            yingZi.addRelation(id1,id2,value);
        } catch (Exception e) {
            return;
        }
        AssErt();
    }

    public void modifyRelation(int id1, int id2, int value) throws PersonIdNotFoundException,
            RelationNotFoundException {
        try {
            myNetwork.modifyRelation(id1,id2,value);
            oneNetwork.modifyRelation(id1,id2,value);
            yingZi.modifyRelation(id1,id2,value);
        } catch (Exception e) {
            return;
        }
        AssErt();
    }

    public void AssErt() throws PersonIdNotFoundException, RelationNotFoundException {
        Person [] olds = yingZi.getPersons();
        assertEquals(myNetwork.queryCoupleSum(),oneNetwork.queryCoupleSum());
        Person [] news = myNetwork.getPersons();
        assertEquals(olds.length,news.length);
        for (int i = 0;i < olds.length;i++) {
            boolean f = ((MyPerson) olds[i]).strictEquals(news[i]);
            assertEquals(f,true);
        }
    }


}