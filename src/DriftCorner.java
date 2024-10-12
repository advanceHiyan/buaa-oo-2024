import com.oocourse.library3.LibraryBookId;

import java.util.ArrayList;
import java.util.HashMap;

public class DriftCorner {
    private HashMap<LibraryBookId, ArrayList<CornerBook>> books;

    public DriftCorner() {
        books = new HashMap<>();
    }

    public void addBook(LibraryBookId bookId, CornerBook book) {
        if (!books.containsKey(bookId)) {
            ArrayList<CornerBook> bookList = new ArrayList<>();
            bookList.add(book);
            books.put(bookId, bookList);
        } else {
            books.get(bookId).add(book);
        }
    }

    public CornerBook getAndRemoveBook(LibraryBookId bookId) {
        if (haveBook(bookId)) {
            CornerBook book = books.get(bookId).remove(0);
            if (books.get(bookId).isEmpty()) {
                books.remove(bookId);
            }
            return book;
        } else {
            return null;
        }
    }

    public boolean haveBook(LibraryBookId bookId) {
        return (books.containsKey(bookId) && books.get(bookId).size() > 0);
    }

    public int queryCount(LibraryBookId bookId) {
        if (haveBook(bookId)) {
            return books.get(bookId).size();
        }
        return 0;
    }
}
