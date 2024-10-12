import com.oocourse.library1.LibraryBookId;

import java.time.LocalDate;

public class BookOfPer {
    private final LibraryBookId bookId;
    private final LibraryBookId.Type type;
    private final String uid;
    private final LocalDate startInApp;

    public BookOfPer(LibraryBookId bookId) {
        this.bookId = bookId;
        this.type = bookId.getType();
        this.uid = bookId.getUid();
        this.startInApp = null;
    }

    public BookOfPer(LibraryBookId bookId,LocalDate localDate,boolean isOpen) {
        this.bookId = bookId;
        this.type = bookId.getType();
        this.uid = bookId.getUid();
        if (isOpen) {
            this.startInApp = localDate.minusDays(1);
        } else {
            this.startInApp = localDate;
        }
    }

    public LibraryBookId getBookId() {
        return bookId;
    }

    public LocalDate getStartInApp() {
        return startInApp;
    }
}
