import com.oocourse.library3.LibraryBookId;

import java.util.HashMap;

public class BookShelf {
    private HashMap<LibraryBookId,Integer> books;

    public BookShelf(HashMap<LibraryBookId,Integer> inva) {
        this.books = inva;
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

    public int querie(LibraryBookId bookId) {
        if (books.get(bookId) == null) {
            return 0;
        }
        return books.get(bookId);
    }

    public boolean haveBook(LibraryBookId bookId) {
        return books.containsKey(bookId) && books.get(bookId) > 0;
    }

    public HashMap<LibraryBookId,Integer> getBooks() {
        return books;
    }
}
