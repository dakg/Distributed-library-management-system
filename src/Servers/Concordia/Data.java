/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Servers.Concordia;

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
        Data.up.put("CONU1111", "1111");
        Data.up.put("CONM1111", "1111");
        Data.up.put("CONU2222", "1111");
        Data.up.put("CONM2222", "1111");
        Data.up.put("CONU3333", "1111");
        Data.up.put("CONM3333", "1111");
        Data.up.put("CONU4444", "1111");
        Data.up.put("CONM4444", "1111");
        Data.up.put("CONU5555", "1111");
        Data.up.put("CONM5555", "1111");

    }

    static void populate_managerIDs() {
        Data.managerIDs.put("CONM1111", "1111");
        Data.managerIDs.put("CONM2222", "1111");
        Data.managerIDs.put("CONM3333", "1111");
        Data.managerIDs.put("CONM4444", "1111");
        Data.managerIDs.put("CONM5555", "1111");
    }

    static void populate_userIDs() {
        Data.userIDs.put("CONU1111", "1111");
        Data.userIDs.put("CONU2222", "1111");
        Data.userIDs.put("CONU3333", "1111");
        Data.userIDs.put("CONU4444", "1111");
        Data.userIDs.put("CONU5555", "1111");
    }

    static void populate_availableBookInformation() {
        Data.availableBookInformation.put("CON0001", new BookDetails("ds1", 1));
        Data.availableBookInformation.put("CON0002", new BookDetails("ds2", 2));
        Data.availableBookInformation.put("CON0003", new BookDetails("ds3", 1));
    }

    static void populate_bookInformation() {
        Data.bookInformation.put("CON0001", "ds1");
        Data.bookInformation.put("CON0002", "ds2");
        Data.bookInformation.put("CON0003", "ds3");
        Data.bookInformation.put("CON1000", "ds4");
        Data.bookInformation.put("CON1001", "ds5");
    }

    static void populate_borrowDetails() {
        List l1 = new ArrayList();
        l1.add(new BorrowDetails("CON1000", 5));
        l1.add(new BorrowDetails("CON1001", 6));
        List l2 = new ArrayList();
        l2.add(new BorrowDetails("CON1000", 5));
        l2.add(new BorrowDetails("CON0002", 6));
        Data.borrowDetails.put("CONU1111", l1);
        Data.borrowDetails.put("CONU2222", l2);

    }

    static void populate_borrowDetailsWithoutDays() {
        List l1 = new ArrayList();
        l1.add("CON1000");
        l1.add("CON1001");
        List l2 = new ArrayList();
        l2.add("CON1000");
        l2.add("CON0002");
        Data.borrowDetailsWithoutDays.put("CONU1111", l1);
        Data.borrowDetailsWithoutDays.put("CONU2222", l2);
    }

    static void populate_waitList() {
        Queue q = new LinkedList();
        q.add("CONU4444");
        q.add("CONU3333");
        Data.waitList.put("CON1000", q);

        Queue q2 = new LinkedList();
        q2.add("CONU3333");
        q2.add("CONU4444");
        Data.waitList.put("CON1001", q2);

    }
}
