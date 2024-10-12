import com.oocourse.library2.LibraryBookId;
import com.oocourse.library2.LibraryMoveInfo;
import com.oocourse.library2.LibraryRequest;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;

import static com.oocourse.library2.LibrarySystem.PRINTER;

public class Library {
    private final BookShelf bookShelf;
    private final AppOffice appOffice;
    private final BroReOffice broReOffice;
    private final StudReqProcess frontDesk;
    private final DriftCorner driftCorner;
    private final BroCorner broCorner;
    private HashMap<String,LibraryRequest> needToDo;
    private ArrayList<LibraryMoveInfo> waitPrints;

    public Library(HashMap<LibraryBookId,Integer> inva) {
        this.bookShelf = new BookShelf(inva);
        this.appOffice = new AppOffice();
        this.broReOffice = new BroReOffice();
        this.frontDesk = new StudReqProcess(this);
        this.needToDo = new HashMap<>();
        this.waitPrints = new ArrayList<>();
        this.driftCorner = new DriftCorner();
        this.broCorner = new BroCorner();
    }

    public void tidy(boolean isOpen,LocalDate localDate) {
        waitPrints.clear();
        long newTime = localDate.toEpochDay();
        moveBroToShlf(broReOffice.tidyAndReturn());
        moveAppToShlf(appOffice.tidyAndReturn(isOpen,newTime));
        moveCornerBro();
        moveShlfToApp(localDate,isOpen);
        PRINTER.move(localDate,waitPrints);
        needToDo.clear();
    }

    public void querie(LocalDate date, LibraryBookId bookId) {
        if (bookId.isFormal()) {
            PRINTER.info(date,bookId, bookShelf.querie(bookId));
        } else {
            PRINTER.info(date,bookId,driftCorner.queryCount(bookId));
        }
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

    public void moveCornerBro() {
        ArrayList<CornerBook> cb = broCorner.getBooks();
        for (int i = 0;i < cb.size();i++) {
            if (cb.get(i).getLentCount() >= 2) {
                LibraryMoveInfo moveInfo = new LibraryMoveInfo(cb.get(i).getBookId(),"bro","bs");
                waitPrints.add(moveInfo);
                LibraryBookId bsBookId = cb.get(i).getBookId().toFormal();
                bookShelf.addBook(bsBookId);
            } else {
                LibraryMoveInfo moveInfo = new LibraryMoveInfo(cb.get(i).getBookId(),"bro","bdc");
                waitPrints.add(moveInfo);
                driftCorner.addBook(cb.get(i).getBookId(),cb.get(i));
            }
        }
        cb.clear();
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

    public CornerBook moveCornerToBro(LibraryBookId bookId) {
        CornerBook temp = driftCorner.getAndRemoveBook(bookId);
        if (temp == null) {
            return null; // reject
        }
        broCorner.addBook(temp);
        return temp;
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

    public DriftCorner getDriftCorner() {
        return driftCorner;
    }

    public BookShelf getBookShelf() {
        return bookShelf;
    }

    public BroCorner getBroCorner() {
        return broCorner;
    }
}
