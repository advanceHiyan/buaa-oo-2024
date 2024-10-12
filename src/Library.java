import com.oocourse.library1.LibraryBookId;
import com.oocourse.library1.LibraryMoveInfo;
import com.oocourse.library1.LibraryRequest;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;

import static com.oocourse.library1.LibrarySystem.PRINTER;

public class Library {
    private final BookShelf bookShelf;
    private final AppOffice appOffice;
    private final BroReOffice broReOffice;
    private final StudReqProcess frontDesk;
    private HashMap<String,LibraryRequest> needToDo;
    private ArrayList<LibraryMoveInfo> waitPrints;

    public Library(HashMap<LibraryBookId,Integer> inva) {
        this.bookShelf = new BookShelf(inva);
        this.appOffice = new AppOffice();
        this.broReOffice = new BroReOffice();
        this.frontDesk = new StudReqProcess(this);
        this.needToDo = new HashMap<>();
        this.waitPrints = new ArrayList<>();
    }

    public void tidy(boolean isOpen,LocalDate localDate) {
        waitPrints.clear();
        long newTime = localDate.toEpochDay();
        moveBroToShlf(broReOffice.tidyAndReturn());
        moveAppToShlf(appOffice.tidyAndReturn(isOpen,newTime));
        moveShlfToApp(localDate,isOpen);
        PRINTER.move(localDate,waitPrints);
        needToDo.clear();
    }

    public void querie(LocalDate date, LibraryBookId bookId) {
        PRINTER.info(date,bookId, bookShelf.querie(bookId));
    }

    public void moveShlfToApp(LocalDate nowDate,boolean isOpen) {
        if (needToDo.size() > 0) {
            for (String perid:needToDo.keySet()) {
                LibraryBookId bookId = needToDo.get(perid).getBookId();
                if (bookShelf.haveBook(bookId)) {
                    bookShelf.removeBook(bookId);
                    BookOfPer book = new BookOfPer(bookId,nowDate,isOpen);
                    appOffice.addOneBook(perid,book);
                    LibraryMoveInfo mo = new LibraryMoveInfo(bookId,"bs","ao",perid);
                    waitPrints.add(mo);
                }
            }
        }
    }

    public void moveBroToShlf(ArrayList<LibraryBookId> bookIds) {
        for (int i = 0;i < bookIds.size();i++) {
            bookShelf.addBook(bookIds.get(i));
            LibraryMoveInfo mo = new LibraryMoveInfo(bookIds.get(i),"bro","bs");
            waitPrints.add(mo);
        }
    }

    public void moveAppToShlf(ArrayList<LibraryBookId> bookIds) {
        for (int i = 0;i < bookIds.size();i++) {
            bookShelf.addBook(bookIds.get(i));
            LibraryMoveInfo mo = new LibraryMoveInfo(bookIds.get(i),"ao","bs");
            waitPrints.add(mo);
        }
    }

    public LibraryBookId moveShlfToBro(LocalDate date,LibraryBookId bookId) {
        LibraryBookId temp = bookShelf.removeBook(bookId);
        if (temp == null) {
            return null; // reject
        }
        broReOffice.addBook(temp);
        return bookId;
    }

    public void addNeedApp(LibraryRequest request) {
        String perId = request.getStudentId();
        if (needToDo.get(perId) == null) {
            needToDo.put(perId,request);
        }
    }

    public BroReOffice getBroReOffice() {
        return broReOffice;
    }

    public AppOffice getAppOffice() {
        return appOffice;
    }

    public StudReqProcess getFrontDesk() {
        return frontDesk;
    }
}
