import com.oocourse.library3.LibraryBookId;
import com.oocourse.library3.LibraryCommand;
import com.oocourse.library3.LibraryQcsCmd;
import com.oocourse.library3.LibraryReqCmd;
import com.oocourse.library3.LibraryRequest;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;

import static com.oocourse.library3.LibrarySystem.PRINTER;

public class StudReqProcess { // 学生请求处理类
    private HashMap<String,Person> idToPersons;
    private final Library library;
    private HashMap<LibraryBookId, ArrayList<String>> orderIng;
    private HashMap<LibraryBookId,Person> donaters;
    private HashMap<String,Boolean> idOrderEdB;

    public StudReqProcess(Library library) {
        idToPersons = new HashMap<>();
        this.library = library;
        this.orderIng = new HashMap<>();
        this.donaters = new HashMap<>();
        this.idOrderEdB = new HashMap<>();
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

    public void tryQcs(LibraryQcsCmd cmd) {
        String perId = cmd.getStudentId();
        Person person = idToPersons.get(perId);
        int qsc = 10;
        if (person == null) {
            idToPersons.put(perId,new Person(perId));
        } else {
            qsc = person.getPoint(cmd.getDate());
        }
        PRINTER.info(cmd.getDate(),perId,qsc);
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
        idToPersons.get(pid).changePoint(2);
        donaters.put(bookId,idToPersons.get(pid));
        CornerBook cornerBook = new CornerBook(bookId,0);
        library.getDriftCorner().addBook(bookId,cornerBook);
    }

    public void tryRenew(LibraryCommand command) {
        LibraryRequest request = ((LibraryReqCmd) command).getRequest();
        LocalDate localDate = command.getDate();
        Person person = idToPersons.get(request.getStudentId());
        LibraryBookId bookId = request.getBookId();
        if (!bookId.isFormal() || !person.isEnoughPoint(localDate)) {
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
        if (!bookId.isFormal() || !person.isEnoughPoint(localDate)) {
            PRINTER.reject(command);
            return;
        }
        if (bookId.isTypeA() || (bookId.isTypeB() && person.isHaveB())
                || (bookId.isTypeC() && person.haveBook(bookId))) {
            PRINTER.reject(localDate,request);
            return;
        }
        if (bookId.isTypeB()) {
            if (idOrderEdB.get(person.getId()) != null) {
                if (!orderIng.isEmpty()) {
                    for (LibraryBookId key : orderIng.keySet()) {
                        if (key.isTypeB() && orderIng.get(key).contains(person.getId())) {
                            PRINTER.reject(localDate, request);
                            return;
                        }
                    }
                }
            }
        } else {
            if (orderIng.containsKey(bookId) && orderIng.get(bookId).contains(person.getId())) {
                PRINTER.reject(localDate, request);
                return;
            }
        }
        PRINTER.accept(localDate,request);
        library.addNeedApp(request);
        if (bookId.isTypeB()) {
            idOrderEdB.put(person.getId(),true);
        }
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
            person.changePoint(1);
        } else {
            PRINTER.accept(command,"overdue");
            person.changePoint(-2);
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
            if (temp == null || !idToPersons.get(perId).isEnoughPoint(localDate)) {
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
            if (cb == null || !idToPersons.get(perId).isEnoughPoint(localDate)) {
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

    public HashMap<LibraryBookId, ArrayList<String>> getOrderIng() {
        return orderIng;
    }

    public HashMap<String, Person> getIdToPersons() {
        return idToPersons;
    }

    public HashMap<LibraryBookId, Person> getDonaters() {
        return donaters;
    }

    public void orderNewBook() {
        return;
    }

    public void wantFuck() {
        return;
    }
}
