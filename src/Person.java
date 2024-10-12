import com.oocourse.library2.LibraryBookId;

import java.time.LocalDate;
import java.util.HashMap;

public class Person {
    private final String id;
    private HashMap<LibraryBookId, BookOfPer> booksCtype;
    private BookOfPer bookBtype;
    private BookOfPer bookBu;

    public Person(String id) {
        this.id = id;
        this.booksCtype = new HashMap<>();
        this.bookBtype = null;
    }

    public void addBook(LibraryBookId bookId,BookOfPer book) {
        if (bookId.getType() == LibraryBookId.Type.B) {
            if (isHaveB()) {
                System.out.println("have B alwady");
            }
            bookBtype = book;
        } else if (bookId.getType() == LibraryBookId.Type.BU) {
            if (isHaveBu()) {
                System.out.println("have BU alwady");
            }
            bookBu = book;
        } else if (booksCtype.get(bookId) == null) {
            if (this.haveBook(bookId)) {
                System.out.println("have C alwady");
            }
            booksCtype.put(bookId,book);
        } else {
            System.out.println("error A");
        }
    }

    public boolean removeBook(LibraryBookId bookId) {
        if (haveBandIsB(bookId)) {
            this.bookBtype = null;
            return true;
        }
        if (havebUanndIsBu(bookId)) {
            this.bookBu = null;
            return true;
        }
        boolean ret = false;
        if (booksCtype.containsKey(bookId)) {
            ret = true;
        } else {
            System.out.println("error not this book");
        }
        this.booksCtype.remove(bookId);
        return ret;
    }

    public String getId() {
        return id;
    }

    public boolean haveBook(LibraryBookId bookId) {
        return (this.bookBtype != null && this.bookBtype.getBookId().equals(bookId))
                || (bookBu != null && bookBu.getBookId().equals(bookId))
                || (booksCtype.containsKey(bookId));
    }

    public boolean haveBandIsB(LibraryBookId bookId) {
        return isHaveB() && bookBtype.getBookId().equals(bookId);
    }

    public boolean havebUanndIsBu(LibraryBookId bookId) {
        return isHaveBu() && bookBu.getBookId().equals(bookId);
    }

    public boolean isHaveB() {
        return (bookBtype != null);
    }

    public boolean isHaveBu() {
        return (bookBu != null);
    }

    public boolean tryContinue(LibraryBookId bookId,LocalDate date,int count) {
        if (havebUanndIsBu(bookId)) {
            return bookBu.tryContinueOwn(date,count);
        }
        if (haveBandIsB(bookId)) {
            return bookBtype.tryContinueOwn(date,count);
        }
        if (booksCtype.containsKey(bookId)) {
            return booksCtype.get(bookId).tryContinueOwn(date,count);
        }
        System.out.println("no known");
        return false;
    }

    public boolean isOkOwn(LibraryBookId bookId, LocalDate date) {
        if (havebUanndIsBu(bookId)) {
            return bookBu.isOkOwn(date);
        }
        if (haveBandIsB(bookId)) {
            return bookBtype.isOkOwn(date);
        }
        if (booksCtype.containsKey(bookId)) {
            return booksCtype.get(bookId).isOkOwn(date);
        }
        System.out.println("no known");
        return false;
    }

    public int getUcount(LibraryBookId bookId) {
        if (havebUanndIsBu(bookId)) {
            return ((CornerBook) bookBu).getLentCount();
        } else if (booksCtype.containsKey(bookId)) {
            return ((CornerBook) booksCtype.get(bookId)).getLentCount();
        } else {
            System.out.println("no,can't get count because book is not u");
            return 0;
        }
    }
}
