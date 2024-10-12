import com.oocourse.library2.LibraryBookId;
import com.oocourse.library2.LibraryCommand;
import com.oocourse.library2.LibraryReqCmd;
import com.oocourse.library2.LibraryRequest;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;

import static com.oocourse.library2.LibrarySystem.PRINTER;

public class StudReqProcess {
    private HashMap<String,Person> idToPersons;
    private final Library library;
    private HashMap<LibraryBookId, ArrayList<String>> orderIng;

    public StudReqProcess(Library library) {
        idToPersons = new HashMap<>();
        this.library = library;
        this.orderIng = new HashMap<>();
    }

    public void processStd(LibraryCommand command) {
        LibraryRequest request = ((LibraryReqCmd) command).getRequest();
        LocalDate localDate = command.getDate();
        String perId = request.getStudentId();
        LibraryBookId bookId = request.getBookId();
        if (!idToPersons.containsKey(perId)) {
            Person person = new Person(perId);
            idToPersons.put(perId,person);
        }
        switch (request.getType()) {
            case QUERIED: {
                library.querie(localDate,bookId);
                break;
            } case ORDERED: {
                tryOrder(command);
                break;
            } case PICKED: {
                tryPicked(command);
                break;
            } case BORROWED: {
                tryBorrow(command);
                break;
            } case RETURNED: {
                tryReturn(command);
                break;
            } case DONATED: {
                tryDonate(command);
                break;
            } case RENEWED: {
                tryRenew(command);
                break;
            } default: {
                System.out.println("error cmd");
            }
        }
    }

    public void tryDonate(LibraryCommand command) {
        LibraryRequest request = ((LibraryReqCmd) command).getRequest();
        LocalDate localDate = command.getDate();
        String pid = (request.getStudentId());
        LibraryBookId bookId = request.getBookId();
        if (idToPersons.get(pid) != null && idToPersons.get(pid).haveBook(bookId)) {
            PRINTER.reject(localDate,request);
            return;
        }
        if (library.getBookShelf().getBooks().containsKey(bookId)) {
            PRINTER.reject(localDate,request);
            return;
        }
        PRINTER.accept(localDate,request);
        CornerBook cornerBook = new CornerBook(bookId,0);
        library.getDriftCorner().addBook(bookId,cornerBook);
    }

    public void tryRenew(LibraryCommand command) {
        LibraryRequest request = ((LibraryReqCmd) command).getRequest();
        LocalDate localDate = command.getDate();
        Person person = idToPersons.get(request.getStudentId());
        LibraryBookId bookId = request.getBookId();
        if (!bookId.isFormal()) {
            PRINTER.reject(localDate,request);
            return;
        }
        if (orderIng.containsKey(bookId) && orderIng.get(bookId).size() > 0) {
            if (library.getBookShelf().getBooks().containsKey(bookId) == false ||
                library.getBookShelf().getBooks().get(bookId).equals(0)) {
                PRINTER.reject(command);
                return;
            }
        }
        if (person.tryContinue(bookId,localDate,30)) {
            PRINTER.accept(command);
        } else {
            PRINTER.reject(command);
        }
    }

    public void tryOrder(LibraryCommand command) {
        LibraryRequest request = ((LibraryReqCmd) command).getRequest();
        LocalDate localDate = command.getDate();
        Person person = idToPersons.get(request.getStudentId());
        LibraryBookId bookId = request.getBookId();
        if (bookId.isTypeBU() || bookId.isTypeAU() || bookId.isTypeCU()) {
            PRINTER.reject(command);
            return;
        }
        if (bookId.isTypeA() || (bookId.isTypeB() && person.isHaveB())
                || (bookId.isTypeC() && person.haveBook(bookId))) {
            PRINTER.reject(localDate,request);
            return;
        }
        PRINTER.accept(localDate,request);
        library.addNeedApp(request);
        if (orderIng.containsKey(bookId)) {
            orderIng.get(bookId).add(person.getId());
        } else {
            ArrayList<String> list = new ArrayList<>();
            list.add(person.getId());
            orderIng.put(bookId,list);
        }
    }

    public void tryPicked(LibraryCommand command) {
        LibraryRequest request = ((LibraryReqCmd) command).getRequest();
        LocalDate localDate = command.getDate();
        Person person = idToPersons.get(request.getStudentId());
        LibraryBookId bookId = request.getBookId();
        if (bookId.isTypeA() || (bookId.isTypeB() && person.isHaveB())
                || (bookId.isTypeC() && person.haveBook(bookId))) {
            PRINTER.reject(localDate,request);
            return;
        }
        if  (!library.getAppOffice().pickOneBook(person.getId(), bookId)) {
            PRINTER.reject(localDate,request);
            return;
        }
        BookOfPer book = new BookOfPer(bookId,localDate);
        person.addBook(bookId,book);
        PRINTER.accept(localDate,request);
        if (orderIng.containsKey(bookId)) {
            orderIng.get(bookId).remove(person.getId());
        } else {
            System.out.println("why not have order");
        }
    }

    public void tryReturn(LibraryCommand command) {
        LibraryRequest request = ((LibraryReqCmd) command).getRequest();
        LocalDate localDate = command.getDate();
        Person person = idToPersons.get(request.getStudentId());
        LibraryBookId bookId = request.getBookId();
        if (!person.haveBook(bookId)) {
            System.out.println("flase");
            return;
        }
        boolean isOk = person.isOkOwn(bookId,localDate);
        if (isOk) {
            PRINTER.accept(command,"not overdue");
        } else {
            PRINTER.accept(command,"overdue");
        }
        if (bookId.isFormal()) {
            library.getBroReOffice().addBook(bookId);
        } else {
            int count = person.getUcount(bookId);
            CornerBook book = new CornerBook(bookId,count);
            library.getBroCorner().addBook(book);
        }
        person.removeBook(bookId);
    }

    public void tryBorrow(LibraryCommand command) {
        LibraryRequest request = ((LibraryReqCmd) command).getRequest();
        LocalDate localDate = command.getDate();
        String perId = request.getStudentId();
        LibraryBookId bookId = request.getBookId();
        if (bookId.isTypeA() || bookId.isTypeAU()) {
            PRINTER.reject(localDate,request);
            return;
        }
        if (bookId.isTypeB() || bookId.isTypeC()) {
            LibraryBookId temp = library.moveShlfToBro(localDate,bookId);
            if (temp == null) {
                PRINTER.reject(localDate,request);
            } else if (bookId.isTypeB()) {
                if (idToPersons.get(perId).isHaveB()) {
                    PRINTER.reject(localDate,request);
                } else {
                    givePerBook(bookId,request,localDate,perId);
                }
            } else {
                if (idToPersons.get(perId).haveBook(bookId)) {
                    PRINTER.reject(localDate,request);
                } else {
                    givePerBook(bookId,request,localDate,perId);
                }
            }
        } else { //Bu Cu
            CornerBook cb = library.moveCornerToBro(bookId);
            if (cb == null) {
                PRINTER.reject(command);
                return;
            }
            LibraryBookId temp = cb.getBookId();
            int c = cb.getLentCount() + 1;
            if (temp == null) {
                PRINTER.reject(localDate,request);
            } else if (temp.isTypeBU()) {
                if (idToPersons.get(perId).isHaveBu()) {
                    PRINTER.reject(localDate,request);
                } else {
                    library.getBroCorner().removeBook(cb);
                    giveUbook(bookId,request,localDate,perId,c);
                }
            } else {
                if (idToPersons.get(perId).haveBook(bookId)) {
                    PRINTER.reject(localDate,request);
                } else {
                    library.getBroCorner().removeBook(cb);
                    giveUbook(bookId,request,localDate,perId,c);
                }
            }
        }
    }

    public void givePerBook(LibraryBookId bookId,LibraryRequest request,
                            LocalDate localDate,String perId) {
        PRINTER.accept(localDate,request);
        BookOfPer book = new BookOfPer(bookId,localDate);
        library.getBroReOffice().removeBook(bookId);
        idToPersons.get(perId).addBook(bookId,book);
    }

    public void giveUbook(LibraryBookId bookId,LibraryRequest request,
                          LocalDate localDate,String perId,int count) {
        PRINTER.accept(localDate,request);
        CornerBook book = new CornerBook(bookId,localDate,count);
        idToPersons.get(perId).addBook(bookId,book);
    }
}
