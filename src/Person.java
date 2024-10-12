import com.oocourse.library1.LibraryBookId;

import java.util.HashMap;

public class Person {
    private final String id;
    private HashMap<LibraryBookId, BookOfPer> booksCtype;
    private BookOfPer bookBtype;

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
                || (booksCtype.containsKey(bookId));
    }

    public boolean haveBandIsB(LibraryBookId bookId) {
        return isHaveB() && bookBtype.getBookId().equals(bookId);
    }

    public boolean isHaveB() {
        return (bookBtype != null);
    }
}
