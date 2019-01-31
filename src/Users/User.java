/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Users;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.StringTokenizer;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

//Interface Implementation
import Servers.Concordia.CONOperations;
import Servers.McGill.MCGOperations;
import Servers.Montreal.MONOperations;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 *
 * @author daksh
 */
public class User {

    static String tokenGenerator(String ID) {

        String id = ID.toUpperCase();
        String token = "";
        if (id.length() == 8) {
            if (id.substring(0, 4).equals("CONU")) {
                token = token + "CU";
            } else if (id.substring(0, 4).equals("MCGU")) {
                token = token + "GU";
            } else if (id.substring(0, 4).equals("MONU")) {
                token = token + "MU";
            } else if (id.substring(0, 4).equals("CONM")) {
                token = token + "CM";
            } else if (id.substring(0, 4).equals("MCGM")) {
                token = token + "GM";
            } else if (id.substring(0, 4).equals("MONM")) {
                token = token + "MM";
            }
            return token;
        }
        return "Invalid ID";

    }

    public static void main(String arg[]) throws IOException {
        try {
//            System.out.print("ID No :");
//            BufferedReader buf = new BufferedReader(new InputStreamReader(System.in));
//            String id = buf.readLine();
//            System.out.print("Password :");
//            String pass = buf.readLine();
//            String Token = tokenGenerator(id);
//            //   System.out.println(tokenGenerator(id));
//
//            if (Token.equals("CU") || Token.equals("CM")) {
//                concordia(id, pass);
//            } else if ((Token.equals("GU") || Token.equals("GM"))) {
//                mcgill(id, pass);
//            } else if ((Token.equals("MU") || Token.equals("MM"))) {
//                montreal(id, pass);
//            }
            testThread();
        } catch (Exception e) {
            System.err.println("Client exception: " + e.toString());
            e.printStackTrace();
        }
    }

    static void concordia(String id, String pass) {
        try {
            Registry registry = LocateRegistry.getRegistry(1111);
            CONOperations stub = (CONOperations) registry.lookup("//localhost:1111/CONImp");
            int authCode = stub.authuser(id, pass);
            Scanner sc = new Scanner(System.in);
            switch (authCode) {
                case 1: //for manager
                {
                    boolean loop = true;
                    while (loop) {
                        System.out.println("1 : Add item ");
                        System.out.println("2 : Remove item");
                        System.out.println("3 : List Availability");
                        System.out.println("4 : Exit");
                        System.out.print("Enter your choice: ");
                        int c = sc.nextInt();
                        switch (c) {
                            case 1: { //for manager
                                System.out.print("Enter Book id:");
                                String boodID = sc.next();
                                System.out.println("Enter Book name:");
                                String bookName = sc.next();
                                System.out.println("Enter quantity:");
                                int quant = sc.nextInt();
                                System.out.println("Adding item...");
                                int replyFromAdd = stub.addItem(id, boodID, bookName, quant);
                                if (replyFromAdd == 1) {
                                    System.out.println("Item Added Successfully!");
                                } else {
                                    System.out.println("Access Denied or Something went wrong.");
                                }
                                break;
                            }
                            case 2: {
                                System.out.println("Enter Book id:");
                                String boodID = sc.next();
                                System.out.println("Enter quantity:");
                                int quant = sc.nextInt();
                                System.out.println("Removing item...");
                                int replyFromRemove = stub.removeItem(id, boodID, quant);
                                if (replyFromRemove == 3) {
                                    System.out.println("No such book found");
                                } else if (replyFromRemove == 2) {
                                    System.out.println("Item completely deleted");
                                } else if (replyFromRemove == 1) {
                                    System.out.println("Item Qunatity decreased");
                                } else {
                                    System.out.println("Access Denied or Something went wrong");
                                }
                                break;
                            }
                            case 3: {
                                System.out.println("Available items and their quantity : ");
                                List l = stub.listItemAvailability(id);
                                System.out.println(Arrays.toString(l.toArray()));
                                break;
                            }
                            case 4: {
                                loop = false;
                            }
                        }
                    }
                    break;
                }

                case 2: //for user
                {
                }

            }
        } catch (Exception e) {
            System.err.println("Client exception: " + e.toString());
            e.printStackTrace();
        }
    }

    static void mcgill(String id, String pass) {
        try {
            Registry registry = LocateRegistry.getRegistry(2222);

            MCGOperations stub = (MCGOperations) registry.lookup("//localhost:2222/MCGImp");
            int a = stub.authuser(id, pass);
            System.out.println(a);
        } catch (Exception e) {
            System.err.println("Client exception: " + e.toString());
            e.printStackTrace();
        }
    }

    static void montreal(String id, String pass) {
        try {
            Registry registry = LocateRegistry.getRegistry(3333);
            MONOperations stub = (MONOperations) registry.lookup("//localhost:3333/MONImp");
            int a = stub.authuser(id, pass);
            System.out.println(a);
        } catch (Exception e) {
            System.err.println("Client exception: " + e.toString());
            e.printStackTrace();
        }
    }

    static void testThread() {
        try {
            Registry registry = LocateRegistry.getRegistry(1111);
            CONOperations stub = (CONOperations) registry.lookup("//localhost:1111/CONImp");
            List l = stub.listItemAvailability("CONM1111");
            System.out.println(Arrays.toString(l.toArray()));
            ThreadClass obj = new ThreadClass();
  //          ExecutorService executorService = Executors.newFixedThreadPool(10);

            //   ThreadClass2 obj2 = new ThreadClass2();
            for (int i = 0; i < 1000; i++) {
                new Thread(obj).start();
                //     new Thread(obj2).start();
            }
            Thread.sleep(1000);
            l = stub.listItemAvailability("CONM1111");
            System.out.println(Arrays.toString(l.toArray()));
        } catch (Exception e) {

        }
    }
}

class ThreadClass implements Runnable {

    @Override
    public void run() {
        try {
            Registry registry = LocateRegistry.getRegistry(1111);
            CONOperations stub = (CONOperations) registry.lookup("//localhost:1111/CONImp");

            stub.addItem("CONM1111", "CON0001", "ds1", 1);
            //    System.out.println("updated");
        } catch (Exception e) {
            System.out.println(e.toString());
        }

    }
}

class ThreadClass2 implements Runnable {

    @Override
    public void run() {
        try {
            Registry registry = LocateRegistry.getRegistry(1111);
            CONOperations stub = (CONOperations) registry.lookup("//localhost:1111/CONImp");

            List l = stub.listItemAvailability("CONM1111");
            System.out.println(Arrays.toString(l.toArray()));

        } catch (Exception e) {
            System.out.println(e.toString());
        }

    }
}
