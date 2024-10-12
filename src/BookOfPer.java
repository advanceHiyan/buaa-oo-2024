import com.oocourse.library2.LibraryBookId;

import java.time.LocalDate;

public class BookOfPer {
    private final LibraryBookId bookId;
    private final LibraryBookId.Type type;
    private final String uid;
    private final LocalDate startInApp;
    private final LocalDate startOwner;
    private int canOwnTime;

    public BookOfPer(LibraryBookId bookId,LocalDate localDate) { // for book of per
        this.bookId = bookId;
        this.type = bookId.getType();
        this.uid = bookId.getUid();
        this.startInApp = null;
        this.startOwner = localDate;
        if (bookId.isTypeB()) {
            canOwnTime = 30;
        } else if (bookId.isTypeC()) {
            canOwnTime = 60;
        } else if (bookId.isTypeBU()) {
            canOwnTime = 7;
        } else if (bookId.isTypeCU()) {
            canOwnTime = 14;
        } else {
            System.out.println("Error: Book type not supported");
        }
    }

    public BookOfPer(LibraryBookId bookId) { // for corner
        this.bookId = bookId;
        this.type = bookId.getType();
        this.uid = bookId.getUid();
        this.startInApp = null;
        this.startOwner = null;
        this.canOwnTime = 0;
    }

    public BookOfPer(LibraryBookId bookId,LocalDate localDate,boolean isOpen) { //for app
        this.bookId = bookId;
        this.type = bookId.getType();
        this.uid = bookId.getUid();
        this.startOwner = null;
        this.canOwnTime = 0;
        if (isOpen) {
            this.startInApp = localDate.minusDays(1);
        } else {
            this.startInApp = localDate;
        }
    }

    public boolean isOkOwn(LocalDate localDate) {
        if (this.startOwner == null) {
            return true;
        } else {
            long days = localDate.toEpochDay() - this.startOwner.toEpochDay();
            return (days <= this.canOwnTime);
        }
    }

    public boolean tryContinueOwn(LocalDate localDate,int continueDays) {
        long owndays = localDate.toEpochDay() - this.startOwner.toEpochDay();
        if ((canOwnTime - owndays) >= 0 && (canOwnTime - owndays) <= 4) {
            this.canOwnTime += continueDays;
            return true;
        }
        return false;
    }

    public LibraryBookId getBookId() {
        return bookId;
    }

    public LocalDate getStartInApp() {
        return startInApp;
    }
}
