import com.oocourse.library1.LibraryBookId;
import com.oocourse.library1.LibraryCommand;
import com.oocourse.library1.LibraryRequest;

import java.time.LocalDate;
import java.util.HashMap;

import static com.oocourse.library1.LibrarySystem.PRINTER;

public class StudReqProcess {
    private HashMap<String,Person> idToPersons;
    private final Library library;

    public StudReqProcess(Library library) {
        idToPersons = new HashMap<>();
        this.library = library;
    }

    public void processStd(LibraryCommand command) {
        LibraryRequest request = (LibraryRequest) command.getCmd();
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
            } default: {
                System.out.println("error cmd");
            }
        }
    }

    public void tryOrder(LibraryCommand command) {
        LibraryRequest request = (LibraryRequest) command.getCmd();
        LocalDate localDate = command.getDate();
        Person person = idToPersons.get(request.getStudentId());
        LibraryBookId bookId = request.getBookId();
        if (bookId.isTypeA() || (bookId.isTypeB() && person.isHaveB())
                || (bookId.isTypeC() && person.haveBook(bookId))) {
            PRINTER.reject(localDate,request);
            return;
        }
        PRINTER.accept(localDate,request);
        library.addNeedApp(request);
    }

    public void tryPicked(LibraryCommand command) {
        LibraryRequest request = (LibraryRequest) command.getCmd();
        LocalDate localDate = command.getDate();
        Person person = idToPersons.get(request.getStudentId());
        LibraryBookId bookId = request.getBookId();
        if (bookId.isTypeA() || (bookId.isTypeB() && person.isHaveB())
                || (bookId.isTypeC() && person.haveBook(bookId))) {
            PRINTER.reject(localDate,request);
            return;
        }
        if  (library.getAppOffice().pickOneBook(person.getId(),bookId) == false) {
            PRINTER.reject(localDate,request);
            return;
        }
        BookOfPer book = new BookOfPer(bookId);
        person.addBook(bookId,book);
        PRINTER.accept(localDate,request);
    }

    public void tryReturn(LibraryCommand command) {
        LibraryRequest request = (LibraryRequest) command.getCmd();
        LocalDate localDate = command.getDate();
        Person person = idToPersons.get(request.getStudentId());
        LibraryBookId bookId = request.getBookId();
        if (!person.haveBook(bookId)) {
            System.out.println("flase");
            return;
        }
        person.removeBook(bookId);
        PRINTER.accept(localDate,request);
        library.getBroReOffice().addBook(bookId);
    }

    public void tryBorrow(LibraryCommand command) {
        LibraryRequest request = (LibraryRequest) command.getCmd();
        LocalDate localDate = command.getDate();
        String perId = request.getStudentId();
        LibraryBookId bookId = request.getBookId();
        if (bookId.isTypeA()) {
            PRINTER.reject(localDate,request);
            return;
        }
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
    }

    public void givePerBook(LibraryBookId bookId,LibraryRequest request,
                            LocalDate localDate,String perId) {
        PRINTER.accept(localDate,request);
        BookOfPer book = new BookOfPer(bookId);
        library.getBroReOffice().removeBook(bookId);
        idToPersons.get(perId).addBook(bookId,book);
    }

}
