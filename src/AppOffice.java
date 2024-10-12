import com.oocourse.library3.LibraryBookId;

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

    public ArrayList<LibraryBookId> tidyAndReturn(boolean isOpen, long newDayOfYear,
                                                  HashMap<LibraryBookId,ArrayList<String>> map,
                                                  HashMap<String,Person> idto) {
        ArrayList<LibraryBookId> ret = new ArrayList<>();
        if (idToBooks.size() != 0) {
            for (String prid: idToBooks.keySet()) {
                ArrayList<BookOfPer> needRemoveIndex = new ArrayList<>();
                for (int i = 0;i < idToBooks.get(prid).size();i++) {
                    long oldDay = idToBooks.get(prid).get(i).getStartInApp().toEpochDay();
                    if (isOpen) {
                        if ((newDayOfYear - oldDay) > 5) {
                            ret.add(idToBooks.get(prid).get(i).getBookId());
                            needRemoveIndex.add(idToBooks.get(prid).get(i));
                            map.get(idToBooks.get(prid).get(i).getBookId()).remove(prid);
                            idto.get(prid).changePoint(-3);
                        }
                    } else {
                        if ((newDayOfYear - oldDay) >= 5) {
                            ret.add(idToBooks.get(prid).get(i).getBookId());
                            needRemoveIndex.add(idToBooks.get(prid).get(i));
                            map.get(idToBooks.get(prid).get(i).getBookId()).remove(prid);
                            idto.get(prid).changePoint(-3);
                        }
                    }
                } // 禁止存index，因为remove会改变list的size
                for (int i = 0;i < needRemoveIndex.size();i++) {
                    idToBooks.get(prid).remove(needRemoveIndex.get(i));
                }
            }
        }
        return ret;
    }

    public boolean pickOneBook(String perid,LibraryBookId bookId) {
        int index = haveBookId(perid,bookId);
        if (index != -1) {
            idToBooks.get(perid).remove(index);
            if (idToBooks.get(perid).isEmpty()) {
                idToBooks.remove(perid);
            }
            return true;
        }
        return false;
    }

    public int haveBookId(String perid,LibraryBookId bookId) {
        if (idToBooks.containsKey(perid) && !idToBooks.get(perid).isEmpty()) {
            for (int i = 0;i < idToBooks.get(perid).size();i++) {
                if (idToBooks.get(perid).get(i).getBookId().equals(bookId)) {
                    return i;
                }
            }
        }
        return -1;
    }

    public void getOrderedBook() {
        return;
    }

    public void fuckBook() {
        return;
    }
}
