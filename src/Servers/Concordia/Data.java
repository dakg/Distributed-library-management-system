/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Servers.Concordia;

import java.util.HashMap;
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


class Data {

    static HashMap<String, String> up = new HashMap();   //User id and Password Data
    static ConcurrentHashMap<String, BookDetails> bi = new ConcurrentHashMap();
}

class PopulateData {
    static void populate(){
        populate_up();
        populate_bi();
    }
    static void populate_up() {
        Data.up.put("CONU1111", "1111");
        Data.up.put("CONM1111", "1111");
        Data.up.put("CONU2222", "1111");
    }

    static void populate_bi() {
        Data.bi.put("CON0001", new BookDetails("ds1", 1));
        Data.bi.put("CON0002", new BookDetails("ds2", 2));
        Data.bi.put("CON0003", new BookDetails("ds3", 4));
    }
}
