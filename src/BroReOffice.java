import com.oocourse.library2.LibraryBookId;

import java.util.ArrayList;
import java.util.HashMap;

public class BroReOffice {
    private HashMap<LibraryBookId,Integer> books;

    public BroReOffice() {
        this.books = new HashMap<>();
    }

    public void addBook(LibraryBookId bookId) {
        if (books.get(bookId) != null) {
            books.put(bookId,books.get(bookId) + 1);
        } else {
            books.put(bookId,1);
        }
    }

    public LibraryBookId removeBook(LibraryBookId bookId) {
        if (books.get(bookId) != null && books.get(bookId) > 0) {
            int num = books.get(bookId);
            books.put(bookId,num - 1);
            return bookId;
        }
        return null;
    }

    public ArrayList<LibraryBookId> tidyAndReturn() {
        ArrayList<LibraryBookId> ret = new ArrayList<>();
        if (books.size() != 0) {
            for (LibraryBookId bookId: books.keySet()) {
                for (int i = 0;i < books.get(bookId);i++) {
                    ret.add(bookId);
                }
            }
        }
        books.clear();
        return ret;
    }
}
