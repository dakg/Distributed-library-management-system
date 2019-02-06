/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Servers.Concordia;

import java.util.List;
import java.util.Queue;
import Servers.Concordia.Data;
import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Logger;

/**
 *
 * @author daksh
 */
public class printDatabase {

    private final static Logger LOGGER = Logger.getLogger(printDatabase.class.getName());
    static FileHandler fh;

    public printDatabase() throws IOException {
    }

    public static void main(String args[]) throws IOException {
        FileHandler fh = new FileHandler("d:/logs/concordia.logs");

        PopulateData.populate();
        printBookInformation();
        printAvailableBookInformation();
        printBorrowDetailsWithoutDays();
        printWaitList();
        printBorrowDetails();
        printUserDetails();

    }

    static void printBookInformation() {
        System.out.println("Book Information");
        for (String s : Data.bookInformation.keySet()) {
            System.out.println(s + " :" + Data.bookInformation.get(s));
        }
        System.out.println();
        LOGGER.addHandler(fh);
        LOGGER.info("dsadsad");
    }

    static void printAvailableBookInformation() {
        System.out.println("Available Book Information");
        for (String key : Data.availableBookInformation.keySet()) {
            BookDetails b = Data.availableBookInformation.get(key);
            System.out.println(key + " " + b.name + " " + b.quantity);
        }
        System.out.println("");
    }

    static void printBorrowDetails() {
        System.out.println("Borrow Details");
        for (String key : Data.borrowDetails.keySet()) {
            List<BorrowDetails> l = Data.borrowDetails.get(key);
            System.out.print(key + " :");
            for (int i = 0; i < l.size(); i++) {
                BorrowDetails b = l.get(i);
                System.out.print(b.id + "-" + b.noOfDays + "   ");
            }
            System.out.println("");
        }

        System.out.println("");
    }

    static void printBorrowDetailsWithoutDays() {
        System.out.println("Borrow Details Without Days");
        for (String key : Data.borrowDetailsWithoutDays.keySet()) {
            List<String> l = Data.borrowDetailsWithoutDays.get(key);
            System.out.print(key + " : ");
            for (int i = 0; i < l.size(); i++) {
                String b = l.get(i);
                System.out.print(b + " ");
            }
            System.out.println();
        }
        System.out.println();

    }

    static void printWaitList() {
        System.out.println("WaitList");
        for (String key : Data.waitList.keySet()) {
            Queue q = Data.waitList.get(key);
            System.out.println(key + " " + q);
        }
        System.out.println();
    }

    static void printUserDetails() {
        System.out.println("User Details");
        for (String s : Data.up.keySet()) {
            System.out.println(s + " :" + Data.up.get(s));
        }
        System.out.println();
    }
}
