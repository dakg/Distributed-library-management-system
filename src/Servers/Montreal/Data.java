package Servers.Montreal;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;

/**
 *
 * @author daksh
 */
class BookDetails {

    String name;
    int quantity;

    BookDetails(String name, int quantity) {
        this.name = name;
        this.quantity = quantity;
    }

}

class BorrowDetails {

    String id;
    int noOfDays;

    BorrowDetails(String id, int noOfDays) {
        this.id = id;
        this.noOfDays = noOfDays;
    }
}

class Data {

    static ConcurrentHashMap<String, String> up = new ConcurrentHashMap();   //User id and Password Data
    static ConcurrentHashMap<String, BookDetails> availableBookInformation = new ConcurrentHashMap();
    static ConcurrentHashMap<String, String> bookInformation = new ConcurrentHashMap();
    static ConcurrentHashMap<String, List<BorrowDetails>> borrowDetails = new ConcurrentHashMap();
    static ConcurrentHashMap<String, List<String>> borrowDetailsWithoutDays = new ConcurrentHashMap();
    static ConcurrentHashMap<String, Queue<String>> waitList = new ConcurrentHashMap();

}

class PopulateData {

    static void populate() {
        populate_up();
        populate_availableBookInformation();
        populate_borrowDetails();
        populate_borrowDetailsWithoutDays();
        populate_bookInformation();
        populate_waitList();

    }

    static void populate_up() {
        Data.up.put("MONU1111", "1111");
        Data.up.put("MONM1111", "1111");
        Data.up.put("MONU2222", "1111");
        Data.up.put("MONM2222", "1111");
        Data.up.put("MONU3333", "1111");
        Data.up.put("MONM3333", "1111");
        Data.up.put("MONU4444", "1111");
        Data.up.put("MONM4444", "1111");
        Data.up.put("MONU5555", "1111");
        Data.up.put("MONM5555", "1111");

    }

    static void populate_availableBookInformation() {
        Data.availableBookInformation.put("MON0001", new BookDetails("ds1", 1));
        Data.availableBookInformation.put("MON0002", new BookDetails("ds2", 2));
        Data.availableBookInformation.put("MON0003", new BookDetails("ds3", 1));
    }

    static void populate_bookInformation() {
        Data.bookInformation.put("MON0001", "ds1");
        Data.bookInformation.put("MON0002", "ds2");
        Data.bookInformation.put("MON0003", "ds3");
        Data.bookInformation.put("MON1000", "ds4");
        Data.bookInformation.put("MON1001", "ds5");
    }

    static void populate_borrowDetails() {
        List l1 = new ArrayList();
        l1.add(new BorrowDetails("MON1000", 5));
        l1.add(new BorrowDetails("MON1001", 6));
        List l2 = new ArrayList();
        l2.add(new BorrowDetails("MON1000", 5));
        l2.add(new BorrowDetails("MON0002", 6));
        Data.borrowDetails.put("MONU1111", l1);
        Data.borrowDetails.put("MONU2222", l2);

    }

    static void populate_borrowDetailsWithoutDays() {
        List l1 = new ArrayList();
        l1.add("MON1000");
        l1.add("MON1001");
        List l2 = new ArrayList();
        l2.add("MON1000");
        l2.add("MON0002");
        Data.borrowDetailsWithoutDays.put("MONU1111", l1);
        Data.borrowDetailsWithoutDays.put("MONU2222", l2);
    }
    
    static void populate_waitList()
    {
        Queue q = new LinkedList();
        q.add("MONU4444");
        q.add("MONU3333");
        Data.waitList.put("MON1000",q);
        
        Queue q2 = new LinkedList();
        q2.add("MONU3333");
        q2.add("MONU4444");
        q2.add("MCGU1111");
        Data.waitList.put("MON1001",q2);
                
    }
}
