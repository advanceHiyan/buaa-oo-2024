import com.oocourse.spec3.main.Message;
import com.oocourse.spec3.main.NoticeMessage;
import com.oocourse.spec3.main.Person;
import com.oocourse.spec3.main.Tag;

public class MyNoticeMessage implements NoticeMessage {
    private int id;
    private int socialValue;
    private int type;
    private Person person1;
    private Person person2;
    private Tag tag;
    private String string;

    public MyNoticeMessage(int messageId, String noticeString,
                           Person messagePerson1, Person messagePerson2) {
        this.type = 0;
        this.tag = null;
        this.id = messageId;
        this.person1 = messagePerson1;
        this.person2 = messagePerson2;
        this.string = noticeString;
        this.socialValue = noticeString.length();
    }

    public MyNoticeMessage(int messageId, String noticeString
            , Person messagePerson1, Tag messageTag) {
        this.type = 1;
        this.person2 = null;
        this.id = messageId;
        this.person1 = messagePerson1;
        this.tag = messageTag;
        this.string = noticeString;
        this.socialValue = noticeString.length();
    }

    public String getString() {
        return string;
    }

    public int getType() {
        return type;
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public int getSocialValue() {
        return socialValue;
    }

    @Override
    public Person getPerson1() {
        return person1;
    }

    @Override
    public Person getPerson2() {
        return person2;
    }

    @Override
    public Tag getTag() {
        return tag;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj != null && obj instanceof Message) {
            return (((Message) obj).getId() == id);
        }
        return false;
    }
}
