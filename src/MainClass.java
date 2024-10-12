import com.oocourse.library1.LibraryBookId;
import com.oocourse.library1.LibraryCommand;
import com.oocourse.library1.LibraryRequest;

import java.time.LocalDate;
import java.util.HashMap;

import static com.oocourse.library1.LibrarySystem.SCANNER;

public class MainClass {
    public static void main(String[] args) {
        HashMap<LibraryBookId, Integer> inventory =
                (HashMap<LibraryBookId, Integer>) SCANNER.getInventory();
        Library library = new Library(inventory);
        StudReqProcess stdPro = library.getFrontDesk();
        while (true) {
            LibraryCommand<?> command = SCANNER.nextCommand();
            if (command == null) { break; }
            LocalDate localDate = command.getDate();
            if (command.getCmd().equals("OPEN")) {
                library.tidy(true,localDate);
            } else if (command.getCmd().equals("CLOSE")) {
                library.tidy(false,localDate);
            } else {
                LibraryRequest request = (LibraryRequest) command.getCmd();
                stdPro.processStd(command);
            }
        }
    }
}
