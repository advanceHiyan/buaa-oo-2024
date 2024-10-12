import com.oocourse.library3.LibraryBookId;

import java.time.LocalDate;

public class CornerBook extends BookOfPer {
    private int lentCount;

    public CornerBook(LibraryBookId bookId,LocalDate localDate,int lentCount) {
        super(bookId,localDate);
        this.lentCount = lentCount;
    }

    public CornerBook(LibraryBookId bookId,int lentCount) {
        super(bookId);
        this.lentCount = lentCount;
    }

    public CornerBook(LibraryBookId bookId, LocalDate localDate, boolean isOpen, int lentCount) {
        super(bookId, localDate, isOpen);
        this.lentCount = lentCount;
    }

    public int getLentCount() {
        return lentCount;
    }
}
