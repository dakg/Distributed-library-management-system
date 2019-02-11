package Users;

import Servers.Concordia.CONOperations;
import Servers.McGill.MCGOperations;
import Servers.Montreal.MONOperations;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

/**
 *
 * @author daksh
 */
public class libraryManager {

    static private Logger logr;
    static FileHandler fh;
    static final int rmiRegistry = 1099;

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

    static boolean validBookId(String itemID) {
        if (itemID.length() == 7) {
            if (itemID.substring(0, 3).equals("CON") || itemID.substring(0, 3).equals("MCG") || itemID.substring(0, 3).equals("MON")) {
                if (isNumeric(itemID.substring(3, 7))) {
                    return true;
                } else {
                    return false;
                }

            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    public static boolean isNumeric(String str) {
        return str.matches("-?\\d+(\\.\\d+)?");  //match a number with optional '-' and decimal.
    }

    static void configurelogr(String userID) {
        try {
            logr = Logger.getLogger(libraryUser.class.getSimpleName());
            fh = new FileHandler("d:/logs/manager_logs/" + userID + ".log", true);
            fh.setFormatter(new SimpleFormatter());
            logr.addHandler(fh);
        } catch (IOException ex) {
        } catch (SecurityException ex) {
        }
    }

    public static void main(String arg[]) throws IOException {
        try {
            System.out.print("ID No :");
            BufferedReader buf = new BufferedReader(new InputStreamReader(System.in));
            String id = buf.readLine();
            id = id.toUpperCase();
//            System.out.print("Password :");
//            String pass = buf.readLine();
            String Token = tokenGenerator(id);

            if (Token.equals("CU") || Token.equals("CM")) {
                concordia(id);
            } else if ((Token.equals("GU") || Token.equals("GM"))) {
                mcgill(id);
            } else if ((Token.equals("MU") || Token.equals("MM"))) {
                montreal(id);
            }
        } catch (Exception e) {
            System.err.println("Client exception: " + e.toString());
            e.printStackTrace();
        }
    }

    static void testThread() {

    }

    static void concordia(String id) {
        try {
            Registry registry = LocateRegistry.getRegistry(rmiRegistry);
            CONOperations stub = (CONOperations) registry.lookup("//localhost:" + rmiRegistry + "/CONImp");
            int authCode = stub.authuser(id);
            Scanner sc = new Scanner(System.in);
            switch (authCode) {
                case 1: //for manager
                {   configurelogr(id);
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
                                System.out.print("Enter Book id :");
                                String bookID = sc.next();
                                bookID = bookID.toUpperCase();
                                if (validBookId(bookID)) {
                                    System.out.print("Enter Book name :");
                                    sc.nextLine();
                                    String bookName = sc.nextLine();
                                    System.out.print("Enter quantity :");
                                    int quant = sc.nextInt();
                                    System.out.println("Adding item...");
                                    int replyFromAdd = stub.addItem(id, bookID, bookName, quant);
                                    if (replyFromAdd == 1) {
                                        logr.info(bookID + "Quantity: " + " " + quant + " " + "Added Successfully");
                                        System.out.println("Item Added Successfully!");
                                    } else {
                                        logr.info(bookID + "Access Denied or Something went wrong.");
                                        System.out.println("Access Denied or Something went wrong.");
                                    }
                                } else {
                                    logr.info(bookID + "Invalid BookID");
                                    System.out.println("Invalid BookID");
                                }
                                break;
                            }
                            case 2: {
                                System.out.print("Enter Book id :");
                                String bookID = sc.next();
                                bookID = bookID.toUpperCase();
                                if (validBookId(bookID)) {
                                    System.out.println("1) Decrease Quantity");
                                    System.out.println("2) Remove completely");
                                    int quant = 0;
                                    int tq = sc.nextInt();
                                    if (tq == 1) {
                                        System.out.print("Enter quantity :");
                                        quant = sc.nextInt();
                                    } else if (tq == 2) {
                                        quant = -1;
                                    }
                                    System.out.println("Removing item...");
                                    int replyFromRemove = stub.removeItem(id, bookID, quant);
                                    if (replyFromRemove == 3) {
                                        logr.info(bookID + "No such book found");
                                        System.out.println("No such book found");
                                    } else if (replyFromRemove == 2) {
                                        logr.info(bookID + "Item completely deleted");
                                        System.out.println("Item completely deleted");
                                    } else if (replyFromRemove == 1) {
                                        logr.info(bookID + "Item quantity decreased");
                                        System.out.println("Item Qunatity decreased");
                                    } else {
                                        logr.info(id + " " + bookID + " " + "Access denied or Something went wrong");
                                        System.out.println("Access Denied or Something went wrong");
                                    }
                                } else {
                                    logr.info(bookID + "Invalid BookID");
                                    System.out.println("Invalid BookID");
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
                                fh.close();
                                loop = false;
                            }
                        }
                    }
                    break;
                }

                case 2: //for user
                {
                    break;
                }
                case 0: {
                    System.out.println("Invalid UserID or Password");
                    break;
                }
                case -1: {
                    System.out.println("Invalid UserID or Password!");
                    break;
                }

            }
        } catch (Exception e) {
            System.err.println("Client exception: " + e.toString());
            e.printStackTrace();
        }
    }

    static void mcgill(String id) {
        try {
            Registry registry = LocateRegistry.getRegistry(rmiRegistry);
            MCGOperations stub = (MCGOperations) registry.lookup("//localhost:" + rmiRegistry + "/MCGImp");
            int authCode = stub.authuser(id);
            Scanner sc = new Scanner(System.in);
            switch (authCode) {
                case 1: //for manager
                {
                    configurelogr(id);
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
                                System.out.print("Enter Book id :");
                                String bookID = sc.next();
                                bookID = bookID.toUpperCase();
                                if (validBookId(bookID)) {
                                    System.out.print("Enter Book name :");
                                    sc.nextLine();
                                    String bookName = sc.nextLine();
                                    System.out.print("Enter quantity :");
                                    int quant = sc.nextInt();
                                    System.out.println("Adding item...");
                                    int replyFromAdd = stub.addItem(id, bookID, bookName, quant);
                                    if (replyFromAdd == 1) {
                                        logr.info(bookID + "Quantity: " + " " + quant + " " + "Added Successfully");
                                        System.out.println("Item Added Successfully!");
                                    } else {
                                        logr.info(bookID + "Access Denied or Something went wrong.");
                                        System.out.println("Access Denied or Something went wrong.");
                                    }
                                } else {
                                    logr.info(bookID + "Invalid BookID");
                                    System.out.println("Invalid BookID");
                                }
                                break;
                            }
                            case 2: {
                                System.out.print("Enter Book id :");
                                String bookID = sc.next();
                                bookID = bookID.toUpperCase();
                                if (validBookId(bookID)) {
                                    System.out.println("1) Decrease Quantity");
                                    System.out.println("2) Remove completely");
                                    int quant = 0;
                                    int tq = sc.nextInt();
                                    if (tq == 1) {
                                        System.out.print("Enter quantity :");
                                        quant = sc.nextInt();
                                    } else if (tq == 2) {
                                        quant = -1;
                                    }
                                    System.out.println("Removing item...");
                                    int replyFromRemove = stub.removeItem(id, bookID, quant);
                                    if (replyFromRemove == 3) {
                                        logr.info(bookID + "No such book found");
                                        System.out.println("No such book found");
                                    } else if (replyFromRemove == 2) {
                                        logr.info(bookID + "Item completely deleted");
                                        System.out.println("Item completely deleted");
                                    } else if (replyFromRemove == 1) {
                                        logr.info(bookID + "Item quantity decreased");
                                        System.out.println("Item Qunatity decreased");
                                    } else {
                                        logr.info(id + " " + bookID + " " + "Access denied or Something went wrong");
                                        System.out.println("Access Denied or Something went wrong");
                                    }
                                } else {
                                    logr.info(bookID + "Invalid BookID");
                                    System.out.println("Invalid BookID");
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
                                fh.close();
                            }
                        }
                    }
                    break;
                }

                case 2: //for user
                {
                    break;
                }
                case 0: {
                    System.out.println("Invalid UserID or Password");
                    break;
                }
                case -1: {
                    System.out.println("Invalid UserID or Password!");
                    break;
                }

            }
        } catch (Exception e) {
            System.err.println("Client exception: " + e.toString());
            e.printStackTrace();
        }
    }

    static void montreal(String id) {
        try {
            Registry registry = LocateRegistry.getRegistry(rmiRegistry);
            MONOperations stub = (MONOperations) registry.lookup("//localhost:" + rmiRegistry + "/MONImp");
            int authCode = stub.authuser(id);
            Scanner sc = new Scanner(System.in);
            switch (authCode) {
                case 1: //for manager
                {
                    configurelogr(id);
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
                                System.out.print("Enter Book id :");
                                String bookID = sc.next();
                                bookID = bookID.toUpperCase();
                                if (validBookId(bookID)) {
                                    System.out.print("Enter Book name :");
                                    sc.nextLine();
                                    String bookName = sc.nextLine();
                                    System.out.print("Enter quantity :");
                                    int quant = sc.nextInt();
                                    System.out.println("Adding item...");
                                    int replyFromAdd = stub.addItem(id, bookID, bookName, quant);
                                    if (replyFromAdd == 1) {
                                        logr.info(bookID + "Quantity: " + " " + quant + " " + "Added Successfully");
                                        System.out.println("Item Added Successfully!");
                                    } else {
                                        logr.info(bookID + "Access Denied or Something went wrong.");
                                        System.out.println("Access Denied or Something went wrong.");
                                    }
                                } else {
                                    logr.info(bookID + "Invalid BookID");
                                    System.out.println("Invalid BookID");
                                }
                                break;
                            }
                            case 2: {
                                System.out.print("Enter Book id :");
                                String bookID = sc.next();
                                bookID = bookID.toUpperCase();
                                if (validBookId(bookID)) {
                                    System.out.println("1) Decrease Quantity");
                                    System.out.println("2) Remove completely");
                                    int quant = 0;
                                    int tq = sc.nextInt();
                                    if (tq == 1) {
                                        System.out.print("Enter quantity :");
                                        quant = sc.nextInt();
                                    } else if (tq == 2) {
                                        quant = -1;
                                    }
                                    System.out.println("Removing item...");
                                    int replyFromRemove = stub.removeItem(id, bookID, quant);
                                    if (replyFromRemove == 3) {
                                        logr.info(bookID + "No such book found");
                                        System.out.println("No such book found");
                                    } else if (replyFromRemove == 2) {
                                        logr.info(bookID + "Item completely deleted");
                                        System.out.println("Item completely deleted");
                                    } else if (replyFromRemove == 1) {
                                        logr.info(bookID + "Item quantity decreased");
                                        System.out.println("Item Qunatity decreased");
                                    } else {
                                        logr.info(id + " " + bookID + " " + "Access denied or Something went wrong");
                                        System.out.println("Access Denied or Something went wrong");
                                    }
                                } else {
                                    logr.info(bookID + "Invalid BookID");
                                    System.out.println("Invalid BookID");
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
                                fh.close();
                            }
                        }
                    }
                    break;
                }

                case 2: //for user
                {
                    break;
                }
                case 0: {
                    System.out.println("Invalid UserID or Password");
                    break;
                }
                case -1: {
                    System.out.println("Invalid UserID or Password!");
                    break;
                }

            }
        } catch (Exception e) {
            System.err.println("Client exception: " + e.toString());
            e.printStackTrace();
        }
    }
}
