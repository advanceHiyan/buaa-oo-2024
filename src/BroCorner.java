import java.util.ArrayList;

public class BroCorner {
    private ArrayList<CornerBook> books;

    public BroCorner() {
        books = new ArrayList<CornerBook>();
    }

    public void addBook(CornerBook book) {
        books.add(book);
    }

    public void removeBook(CornerBook book) {
        books.remove(book);
    }

    public ArrayList<CornerBook> getBooks() {
        return books;
    }
}
