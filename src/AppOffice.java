import com.oocourse.library1.LibraryBookId;

import java.util.ArrayList;
import java.util.HashMap;

public class AppOffice {
    private HashMap<String, ArrayList<BookOfPer>> idToBooks;

    public AppOffice() {
        this.idToBooks = new HashMap<>();
    }

    public void addOneBook(String perid,BookOfPer book) {
        if (idToBooks.containsKey(perid)) {
            idToBooks.get(perid).add(book);
        } else {
            ArrayList<BookOfPer> t = new ArrayList<>();
            t.add(book);
            idToBooks.put(perid,t);
        }
    }

    public ArrayList<LibraryBookId> tidyAndReturn(boolean isOpen, long newDayOfYear) {
        ArrayList<LibraryBookId> ret = new ArrayList<>();
        if (idToBooks.size() != 0) {
            for (String prid: idToBooks.keySet()) {
                ArrayList<Integer> needRemoveIndex = new ArrayList<>();
                for (int i = 0;i < idToBooks.get(prid).size();i++) {
                    long oldDay = idToBooks.get(prid).get(i).getStartInApp().toEpochDay();
                    if (isOpen) {
                        if ((newDayOfYear - oldDay) > 5) {
                            ret.add(idToBooks.get(prid).get(i).getBookId());
                            needRemoveIndex.add(i);
                        }
                    } else {
                        if ((newDayOfYear - oldDay) >= 5) {
                            ret.add(idToBooks.get(prid).get(i).getBookId());
                            needRemoveIndex.add(i);
                        }
                    }
                }
                for (int i = 0;i < needRemoveIndex.size();i++) {
                    idToBooks.get(prid).remove((int) needRemoveIndex.get(i));
                }
            }
        }
        return ret;
    }

    public boolean pickOneBook(String perid,LibraryBookId bookId) {
        int index = haveBookId(perid,bookId);
        if (index != -1) {
            idToBooks.get(perid).remove(index);
            if (idToBooks.get(perid).size() == 0) {
                idToBooks.remove(perid);
            }
            return true;
        }
        return false;
    }

    public int haveBookId(String perid,LibraryBookId bookId) {
        if (idToBooks.containsKey(perid) && idToBooks.get(perid).size() > 0) {
            for (int i = 0;i < idToBooks.get(perid).size();i++) {
                if (idToBooks.get(perid).get(i).getBookId().equals(bookId)) {
                    return i;
                }
            }
        }
        return -1;
    }
}
