package Servers.McGill;

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
    static ConcurrentHashMap<String, String> managerIDs = new ConcurrentHashMap();
    static ConcurrentHashMap<String, String> userIDs = new ConcurrentHashMap();

}

class PopulateData {

    static void populate() {
        populate_up();
        populate_availableBookInformation();
        populate_borrowDetails();
        populate_borrowDetailsWithoutDays();
        populate_bookInformation();
        populate_waitList();
        populate_managerIDs();
        populate_userIDs();

    }

    static void populate_up() {
        Data.up.put("MCGU1111", "1111");
        Data.up.put("MCGM1111", "1111");
        Data.up.put("MCGU2222", "1111");
        Data.up.put("MCGM2222", "1111");
        Data.up.put("MCGU3333", "1111");
        Data.up.put("MCGM3333", "1111");
        Data.up.put("MCGU4444", "1111");
        Data.up.put("MCGM4444", "1111");
        Data.up.put("MCGU5555", "1111");
        Data.up.put("MCGM5555", "1111");

    }

    static void populate_managerIDs() {
        Data.managerIDs.put("MCGM1111", "1111");
        Data.managerIDs.put("MCGM2222", "1111");
        Data.managerIDs.put("MCGM3333", "1111");
        Data.managerIDs.put("MCGM4444", "1111");
        Data.managerIDs.put("MCGM5555", "1111");
    }

    static void populate_userIDs() {
        Data.userIDs.put("MCGU1111", "1111");
        Data.userIDs.put("MCGU2222", "1111");
        Data.userIDs.put("MCGU3333", "1111");
        Data.userIDs.put("MCGU4444", "1111");
        Data.userIDs.put("MCGU5555", "1111");
    }

    static void populate_availableBookInformation() {
        Data.availableBookInformation.put("MCG0001", new BookDetails("ds1", 1));
        Data.availableBookInformation.put("MCG0002", new BookDetails("ds2", 2));
        Data.availableBookInformation.put("MCG0003", new BookDetails("ds3", 1));
    }

    static void populate_bookInformation() {
        Data.bookInformation.put("MCG0001", "ds1");
        Data.bookInformation.put("MCG0002", "ds2");
        Data.bookInformation.put("MCG0003", "ds3");
        Data.bookInformation.put("MCG1000", "ds4");
        Data.bookInformation.put("MCG1001", "ds5");
    }

    static void populate_borrowDetails() {
        List l1 = new ArrayList();
        l1.add(new BorrowDetails("MCG1000", 5));
        l1.add(new BorrowDetails("MCG1001", 6));
        List l2 = new ArrayList();
        l2.add(new BorrowDetails("MCG1000", 5));
        l2.add(new BorrowDetails("MCG0002", 6));
        Data.borrowDetails.put("MCGU1111", l1);
        Data.borrowDetails.put("MCGU2222", l2);

    }

    static void populate_borrowDetailsWithoutDays() {
        List l1 = new ArrayList();
        l1.add("MCG1000");
        l1.add("MCG1001");
        List l2 = new ArrayList();
        l2.add("MCG1000");
        l2.add("MCG0002");
        Data.borrowDetailsWithoutDays.put("MCGU1111", l1);
        Data.borrowDetailsWithoutDays.put("MCGU2222", l2);
    }

    static void populate_waitList() {
        Queue q = new LinkedList();
        q.add("MCGU4444");
        q.add("MCGU3333");
        Data.waitList.put("MCG1000", q);

        Queue q2 = new LinkedList();
        q2.add("MCGU3333");
        q2.add("MCGU4444");
        Data.waitList.put("MCG1001", q2);

    }
}
