/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Servers.Concordia;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;

/**
 *
 * @author daksh
 */
public class testClass {

    static ConcurrentHashMap<String, BookDetails> c = new ConcurrentHashMap();
    static ConcurrentHashMap<String, List<String>> c1 = new ConcurrentHashMap();
    static ConcurrentHashMap<String, String> c2 = new ConcurrentHashMap();

    static void populate() {
        c.put("1", new BookDetails("fa", 4));
        c.put("2", new BookDetails("fa2", 6));
        c.put("3", new BookDetails("fa3", 7));
        c.put("4", new BookDetails("fa4", 8));

    }

    public static void main(String arg[]) {
//        populate();
//        System.out.println(Arrays.asList(c)); 
//        for(String key : c.keySet())
//        {
//            BookDetails bd = c.get(key);
//            System.out.println(key  + " " + bd.name + " " + bd.quantity + " ");
//        }
     
        Queue<String> q = new LinkedList();
        q.add("1");
        q.add("2");
        q.add("3");
        q.add("4");
        q.add("5");
        
        String a = q.remove();
        System.out.println(a);
        System.out.println(q);
        q.remove("3");
        q.remove("9");
       // System.out.println(b);
        System.out.println(q);
    }
}
