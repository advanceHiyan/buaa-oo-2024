import com.oocourse.library3.LibraryBookId;
import com.oocourse.library3.LibraryCommand;
import com.oocourse.library3.LibraryQcsCmd;
import com.oocourse.library3.LibraryReqCmd;
import com.oocourse.library3.LibraryRequest;

import java.time.LocalDate;
import java.util.HashMap;

import static com.oocourse.library3.LibrarySystem.SCANNER;

public class MainClass {
    public static void main(String[] args) {
        HashMap<LibraryBookId, Integer> inventory =
                (HashMap<LibraryBookId, Integer>) SCANNER.getInventory();
        Library library = new Library(inventory);
        StudReqProcess stdPro = library.getFrontDesk();
        while (true) {
            LibraryCommand command = SCANNER.nextCommand();
            if (command == null) { break; }
            LocalDate localDate = command.getDate();
            if (command.getCmd().equals("OPEN")) {
                library.tidy(true,localDate);
            } else if (command.getCmd().equals("CLOSE")) {
                library.tidy(false,localDate);
            } else if (command instanceof LibraryQcsCmd) {
                // 信用积分查询
                // 这个 if 如果实在要用和字符串比较的方式的话可以
                // cmd.getCommandString().endsWith("queried credit score")
                // 不过这样确实显得有点奇怪
                stdPro.tryQcs((LibraryQcsCmd) command);
            }
            else {
                LibraryRequest request = ((LibraryReqCmd) command).getRequest();
                stdPro.processStd(command);
            }
        }
    }
}
