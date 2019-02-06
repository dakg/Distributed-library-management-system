/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Users;

import Servers.Concordia.CONOperations;
import Servers.McGill.MCGOperations;
import Servers.Montreal.MONOperations;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Scanner;

/**
 *
 * @author daksh
 */
public class libraryUser {

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

    public static void main(String arg[]) throws IOException {
        try {
            System.out.print("ID No :");
            BufferedReader buf = new BufferedReader(new InputStreamReader(System.in));
            String id = buf.readLine().toUpperCase();
//            System.out.print("Password :");
//            String pass = buf.readLine();
            String Token = tokenGenerator(id);
            //   System.out.println(tokenGenerator(id));

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

    static void concordia(String id) {
        try {
            Registry registry = LocateRegistry.getRegistry(rmiRegistry);
            CONOperations stub = (CONOperations) registry.lookup("//localhost:" + rmiRegistry + "/CONImp");
            int authCode = stub.authuser(id);
            Scanner sc = new Scanner(System.in);
            switch (authCode) {
                case 2: //for user
                {
                    boolean loop = true;
                    while (loop) {
                        System.out.println("1). Borrow Item");
                        System.out.println("2). Return Item");
                        System.out.println("3). Find Item");
                        System.out.println("4). Exit");
                        int c = sc.nextInt();
                        switch (c) {
                            case 1: {
                                System.out.println("Enter Book ID");
                                String itemID = sc.next();
                                int waitWish = 0;
                                System.out.println("Enter Number of Days you want to borrow for:");
                                int noOfDays = sc.nextInt();
                                int reply = stub.borrowItem(id, itemID, noOfDays, waitWish);
                                switch (reply) {
                                    case 1: {
                                        System.out.println("Book Issued!!!");
                                        break;
                                    }
                                    case 2: {
                                        System.out.println("Book not available. Do you want to be added in waitlist?1(yes)/2(no)");
                                        int t = sc.nextInt();
                                        if (t == 1) {
                                            waitWish = 1;
                                            int temp = stub.borrowItem(id, itemID, noOfDays, waitWish);
                                            if (temp == 3) {
                                                System.out.println("Added in waitlist");
                                            } else if (temp == 5) {
                                                System.out.println("Not able to add in waitlist! Either book available or something wrong try again!");
                                            }
                                        } else {
                                            System.out.println("Operation cancelled!");
                                        }
                                        break;
                                    }
                                    case 4: {
                                        System.out.println("You cannot be added in waitlist! Your limit is 1");
                                        break;
                                    }
                                }
                                break;
                            }
                            case 2: {
                                System.out.println("Enter Book ID");
                                String itemID = sc.next();
                                int r = stub.returnItem(id, itemID);
                                if (r == 1) {
                                    System.out.println("Book Successfully Returned!!");
                                } else if (r == 2) {
                                    System.out.println("You have not issued that book!");
                                } else {
                                    System.out.println("Invalid Userid");
                                }
                                break;
                            }
                            case 3: {
                                System.out.println("Enter Item Name");
                                String itemName = sc.next();
                                String r = stub.findItem(id, itemName);
                                System.out.println(r);
                                break;
                            }
                            case 4: {
                                loop = false;
                                break;
                            }
                            case 0: {
                                System.out.println("Something wrong!!!");
                            }
                        }
                    }
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
                case 2: //for user
                {
                    boolean loop = true;
                    while (loop) {
                        System.out.println("1). Borrow Item");
                        System.out.println("2). Return Item");
                        System.out.println("3). Find Item");
                        System.out.println("4). Exit");
                        int c = sc.nextInt();
                        switch (c) {
                            case 1: {
                                System.out.println("Enter Book ID");
                                String itemID = sc.next();
                                int waitWish = 0;
                                System.out.println("Enter Number of Days you want to borrow for:");
                                int noOfDays = sc.nextInt();
                                int reply = stub.borrowItem(id, itemID, noOfDays, waitWish);
                                switch (reply) {
                                    case 1: {
                                        System.out.println("Book Issued!!!");
                                        break;
                                    }
                                    case 2: {
                                        System.out.println("Book not available. Do you want to be added in waitlist?1(yes)/2(no)");
                                        int t = sc.nextInt();
                                        if (t == 1) {
                                            waitWish = 1;
                                            int temp = stub.borrowItem(id, itemID, noOfDays, waitWish);
                                            if (temp == 3) {
                                                System.out.println("Added in waitlist");
                                            } else if (temp == 5) {
                                                System.out.println("Not able to add in waitlist! Either book available or something wrong try again!");
                                            }
                                        } else {
                                            System.out.println("Operation cancelled!");
                                        }
                                        break;
                                    }
                                    case 4: {
                                        System.out.println("You cannot be added in waitlist! Your limit is 1");
                                        break;
                                    }
                                }
                                break;
                            }
                            case 2: {
                                System.out.println("Enter Book ID");
                                String itemID = sc.next();
                                int r = stub.returnItem(id, itemID);
                                if (r == 1) {
                                    System.out.println("Book Successfully Returned!!");
                                } else if (r == 2) {
                                    System.out.println("You have not issued that book!");
                                } else {
                                    System.out.println("Invalid Userid");
                                }
                                break;
                            }
                            case 3: {
                                System.out.println("Enter Item Name");
                                String itemName = sc.next();
                                String r = stub.findItem(id, itemName);
                                System.out.println(r);
                                break;
                            }
                            case 4: {
                                loop = false;
                                break;
                            }
                            case 0: {
                                System.out.println("Something wrong!!!");
                            }
                        }
                    }
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
                case 2: //for user
                {
                    boolean loop = true;
                    while (loop) {
                        System.out.println("1). Borrow Item");
                        System.out.println("2). Return Item");
                        System.out.println("3). Find Item");
                        System.out.println("4). Exit");
                        int c = sc.nextInt();
                        switch (c) {
                            case 1: {
                                System.out.println("Enter Book ID");
                                String itemID = sc.next();
                                int waitWish = 0;
                                System.out.println("Enter Number of Days you want to borrow for:");
                                int noOfDays = sc.nextInt();
                                int reply = stub.borrowItem(id, itemID, noOfDays, waitWish);
                                switch (reply) {
                                    case 1: {
                                        System.out.println("Book Issued!!!");
                                        break;
                                    }
                                    case 2: {
                                        System.out.println("Book not available. Do you want to be added in waitlist?1(yes)/2(no)");
                                        int t = sc.nextInt();
                                        if (t == 1) {
                                            waitWish = 1;
                                            int temp = stub.borrowItem(id, itemID, noOfDays, waitWish);
                                            if (temp == 3) {
                                                System.out.println("Added in waitlist");
                                            } else if (temp == 5) {
                                                System.out.println("Not able to add in waitlist! Either book available or something wrong try again!");
                                            }
                                        } else {
                                            System.out.println("Operation cancelled!");
                                        }
                                        break;
                                    }
                                    case 4: {
                                        System.out.println("You cannot be added in waitlist! Your limit is 1");
                                        break;
                                    }
                                }
                                break;
                            }
                            case 2: {
                                System.out.println("Enter Book ID");
                                String itemID = sc.next();
                                int r = stub.returnItem(id, itemID);
                                if (r == 1) {
                                    System.out.println("Book Successfully Returned!!");
                                } else if (r == 2) {
                                    System.out.println("You have not issued that book!");
                                } else {
                                    System.out.println("Invalid Userid");
                                }
                                break;
                            }
                            case 3: {
                                System.out.println("Enter Item Name");
                                String itemName = sc.next();
                                String r = stub.findItem(id, itemName);
                                System.out.println(r);
                                break;
                            }
                            case 4: {
                                loop = false;
                                break;
                            }
                            case 0: {
                                System.out.println("Something wrong!!!");
                            }
                        }
                    }
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
