package Servers.Montreal;

import java.io.IOException;
import java.net.SocketException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Queue;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

/**
 *
 * @author daksh
 */
public class MONOperationsImplementation implements MONOperations {

    Logger logr;
    static FileHandler fh;

    public MONOperationsImplementation() throws IOException {
        logr = Logger.getLogger(this.getClass().getSimpleName());
        fh = new FileHandler("d:/logs/server_logs/Montreal.log", true);
        fh.setFormatter(new SimpleFormatter());
        logr.addHandler(fh);
    }

    @Override
    public int addItem(String managerID, String itemID, String itemName, int quantity) {
        if (Data.managerIDs.containsKey(managerID)) {
            try {
                synchronized (this) {
                    System.out.println("Before Adding");
                    printDatabase1();
                    BookDetails bd = Data.availableBookInformation.get(itemID);
                    if (bd == null) {
                        Data.availableBookInformation.put(itemID, new BookDetails(itemName, quantity));
                        Data.bookInformation.put(itemID, itemName);
                        // System.out.println("added");
                    } else {
                        bd.name = itemName;
                        bd.quantity = bd.quantity + quantity;
                        Data.availableBookInformation.put(itemID, bd);
                        //System.out.println("updated");
                    }
                }
                
                logr.log(Level.INFO, "Additem" + " {0},{1},{2},{3}" + " " + "Success" + " " + "Return value:{4}", new Object[]{managerID, itemID, itemName, quantity, 1});
                synchronized (this) {
                    while (checkWaitList(itemID)) {
                        removeFromWaitListAndBorrow(itemID);
                    }
                }
                System.out.println("After adding");
                printDatabase1();
                return 1;                                                               //return 1 if everything perfect
            } catch (Exception e) {
                logr.log(Level.INFO, "Additem" + " {0},{1},{2},{3}" + " " + "Fail" + " " + "Reason: {4}" + " " + "Return value:{5}", new Object[]{managerID, itemID, itemName, quantity, e.toString(), 0});
                return 0;                                                           //return 0 if something got wrong
            }
        } else {
            logr.log(Level.INFO, "Additem" + " {0},{1},{2},{3}" + " " + "Fail" + " " + "Reason: Invalid Manager ID" + " " + "Return value:{4}", new Object[]{managerID, itemID, itemName, quantity, -1});
            return -1;                                                          //return -1 if not manager
        }
    }

    @Override
    public int removeItem(String managerID, String itemID, int quantity) {
        if (Data.managerIDs.containsKey(managerID)) {
            try {
                synchronized (this) {
                    BookDetails bd = Data.availableBookInformation.get(itemID);
                    if (bd == null) {
                        return 3;                                                       //bookid not available
                    } else {
                        if (quantity == -1) {
                            if (removeItemCompletely(managerID, itemID)) {
                                return 2;
                            } else {
                                return 0;
                            }
                        } else {
                            bd.quantity = bd.quantity - quantity;
                            if (bd.quantity <= 0) {
                                if (removeItemCompletely(managerID, itemID)) {
                                    return 2;
                                } else {
                                    return 0;
                                }
                            } else {
                                Data.availableBookInformation.put(itemID, bd);
                                logr.info(itemID + " Quantity decrease to " + bd.quantity);
                                return 1;                                                   //return 1 item quantity decreased 
                            }
                        }
                    }
                }
            } catch (Exception e) {
                System.out.println(e.toString());
                return 0;                                                           //return 0 if something went wrong
            }
        } else {
            logr.info("Remove item " + managerID + " " + itemID + "fail.Invalid user id");
            return -1;                                                          //invalid manager id
        }
    }

    boolean removeItemCompletely(String managerID, String itemID) {
        Data.availableBookInformation.remove(itemID);
        logr.info(itemID + " removed from Available Book database");

        Data.bookInformation.remove(itemID);
        logr.info(itemID + " removed from Book Information!");

        Data.waitList.remove(itemID);
        logr.info("Waitlist of " + itemID + " remove");

        for (String key : Data.borrowDetailsWithoutDays.keySet()) {
            List<String> l = Data.borrowDetailsWithoutDays.get(key);
            if (l.remove(itemID)) {
                logr.info("Item id:" + itemID + " Remove from borrow details(without days) of : " + key);
            }
        }

        for (String key : Data.borrowDetails.keySet()) {
            List<BorrowDetails> l = Data.borrowDetails.get(key);
            for (int i = 0; i < l.size(); i++) {
                BorrowDetails bdtemp = l.get(i);
                if (bdtemp.id.compareTo(itemID) == 0) {
                    l.remove(i);
                    logr.info("Item id:" + itemID + " Remove from borrow details of : " + key);
                }
            }
        }

        return true;                                                   //return 2 if item completely deleted

    }

    @Override
    public List listItemAvailability(String managerID) {
        if (Data.managerIDs.containsKey(managerID)) {
            List b = new ArrayList<String>();
            for (String id : Data.availableBookInformation.keySet()) {
                String c;
                c = id + " " + Data.availableBookInformation.get(id).name + " " + Data.availableBookInformation.get(id).quantity;
                b.add(c);
            }
            printDatabase1();
            logr.log(Level.INFO, "List items" + " {0}" + " " + "Success" + "Return value:{4}", new Object[]{managerID, null});
            return b;
        } else {
            logr.log(Level.INFO, "List items" + " {0}" + " " + "Fail" + " " + "Return value:{4}" + " " + "Invalid Manager ID", new Object[]{managerID, null});

            return null;
        }
    }

    @Override
    public int borrowItem(String userID, String itemID, int numberOfDays, int waitWish) {
        if (Data.userIDs.containsKey(userID)) {
            String a[], b[], c[];
            if (waitWish == 0) {
                String result = "";
                int destPort = getDestPort(itemID);
                switch (destPort) {
                    case Communication.MYPORT: {
                        synchronized (this) {
                            if (checkAvail(itemID)) {
                                System.out.println("Before borrow");
                                printDatabase1();
                                if (doBorrow(userID, itemID, numberOfDays)) {
                                    System.out.println("success borrow");
                                }

                                System.out.println("After borrow");
                                printDatabase1();
                                //printWaitList();
                                return 1;
                            } else if (Data.bookInformation.get(itemID) != null) {
                                logr.info("Borrow Item" + " " + userID + " " + itemID + " " + numberOfDays + " " + "Fail" + "Return value:" + 2 + "Item not available.Waitlist can be granted");
                                return 2;
                            } else {
                                return 11;
                            }
                        }
                    }
                    case Communication.CONPORT: {
                        String raw_msg = "";
                        String msgType = "" + Communication.REQUEST;
                        String requestType = "" + Communication.DO_BORROW;
                        raw_msg = raw_msg + msgType + "," + requestType + "," + userID + "," + itemID + "," + numberOfDays;
                        String messageID;
                        synchronized (this) {
                            messageID = "" + (++Communication.MESSAGE_ID);
                        }
                        String msg = "" + messageID + "," + raw_msg;
                        try {
                            Montreal.sendMessage(msg, Communication.CONPORT);
                            logr.info("Request : Montreal Server -> Concordia Server :Borrow Item request " + "Packet:" + "[" + msg + "]");
                        } catch (IOException ex) {
                            Logger.getLogger(MONOperationsImplementation.class.getName()).log(Level.SEVERE, null, ex);
                        }
                        boolean loop = true;
                        while (loop) {
                            if (Communication.responseMessages.get(messageID) != null) {
                                result = result + Communication.responseMessages.get(messageID);
                                Communication.responseMessages.remove(messageID);
                                loop = false;
                                String a1[] = result.split("-");
                                a = a1;
                                Boolean result1 = Boolean.parseBoolean(a[0]);
                                Boolean result2 = Boolean.parseBoolean(a[1]);
                                Boolean result3 = Boolean.parseBoolean(a[2]);
                                Boolean additionalchecker = Boolean.parseBoolean(a[3]);
                                if (result3) {
                                    logr.info("Provider : Concordia" + " " + "Borrow Item" + " " + userID + " " + itemID + " " + numberOfDays + " " + "Success" + "Return value:" + 1);
                                    return 1;
                                } else if (!result1 && result2 && additionalchecker) {
                                    logr.info("Borrow Item" + " " + userID + " " + itemID + " " + numberOfDays + " " + "Fail" + "Return value:" + 2 + "Item not available.Could be granted Waitlist!");
                                    return 2;
                                } else {
                                    return 4;
                                }
                            }
                        }
                        break;
                    }

                    case Communication.MCGPORT: {
                        String raw_msg = "";
                        String msgType = "" + Communication.REQUEST;
                        String requestType = "" + Communication.DO_BORROW;
                        raw_msg = raw_msg + msgType + "," + requestType + "," + userID + "," + itemID + "," + numberOfDays;
                        String messageID;
                        synchronized (this) {
                            messageID = "" + (++Communication.MESSAGE_ID);
                        }
                        String msg = "" + messageID + "," + raw_msg;
                        try {
                            Montreal.sendMessage(msg, Communication.MCGPORT);
                            logr.info("Request:Montreal Server->McGill Server :Borrow Item request " + "Packet:" + "[" + msg + "]");
                        } catch (IOException ex) {
                            Logger.getLogger(MONOperationsImplementation.class.getName()).log(Level.SEVERE, null, ex);
                        }
                        boolean loop = true;
                        while (loop) {
                            if (Communication.responseMessages.get(messageID) != null) {
                                result = result + Communication.responseMessages.get(messageID);
                                Communication.responseMessages.remove(messageID);
                                loop = false;
                                String b1[] = result.split("-");
                                b = b1;
                                Boolean result1 = Boolean.parseBoolean(b[0]);
                                Boolean result2 = Boolean.parseBoolean(b[1]);
                                Boolean result3 = Boolean.parseBoolean(b[2]);
                                Boolean additionalchecker = Boolean.parseBoolean(b[3]);
                                if (result3) {
                                    logr.info("Provider : Concordia" + " " + "Borrow Item" + " " + userID + " " + itemID + " " + numberOfDays + " " + "Success" + "Return value:" + 1);
                                    return 1;
                                } else if (!result1 && result2 && additionalchecker) {
                                    logr.info("Borrow Item" + " " + userID + " " + itemID + " " + numberOfDays + " " + "Fail" + "Return value:" + 2 + "Item not available.Could be granted Waitlist!");
                                    return 2;
                                } else {
                                    return 4;
                                }
                            }
                        }
                        break;
                    }
                }

            } else if (waitWish == 1) {
                String result = "";
                int destPort = getDestPort(itemID);
                switch (destPort) {
                    case Communication.MYPORT: {
                        boolean r = addWait(userID, itemID);
                        if (r) {
                            return 3;
                        } else {
                            return 5;
                        }

                    }
                    case Communication.CONPORT: {
                        String raw_msg = "";
                        String msgType = "" + Communication.REQUEST;
                        String requestType = "" + Communication.ADD_WAIT;
                        raw_msg = raw_msg + msgType + "," + requestType + "," + userID + "," + itemID;
                        String messageID;
                        synchronized (this) {
                            messageID = "" + (++Communication.MESSAGE_ID);
                        }
                        String msg = "" + messageID + "," + raw_msg;
                        try {
                            Montreal.sendMessage(msg, Communication.CONPORT);
                            logr.info("Request:Montreal Server->Concordia Server :Add into Waitlist request " + "Packet:" + "[" + msg + "]");
                        } catch (IOException ex) {
                            Logger.getLogger(MONOperationsImplementation.class.getName()).log(Level.SEVERE, null, ex);
                        }
                        boolean loop = true;
                        while (loop) {
                            if (Communication.responseMessages.get(messageID) != null) {
                                result = result + Communication.responseMessages.get(messageID);
                                Communication.responseMessages.remove(messageID);
                                if (Boolean.parseBoolean(result)) {
                                    logr.info("Concordia Response : Add wait " + userID + " " + itemID + " " + "Success " + "Return value: 3");
                                    return 3;
                                } else {
                                    logr.info("Concordia Response : Add wait " + userID + " " + itemID + " " + "Fail " + "Return value: 5");
                                    return 5;
                                }
                            }
                        }
                    }
                    case Communication.MCGPORT: {
                        String raw_msg = "";
                        String msgType = "" + Communication.REQUEST;
                        String requestType = "" + Communication.ADD_WAIT;
                        raw_msg = raw_msg + msgType + "," + requestType + "," + userID + "," + itemID;
                        String messageID;
                        synchronized (this) {
                            messageID = "" + (++Communication.MESSAGE_ID);
                        }
                        String msg = "" + messageID + "," + raw_msg;
                        try {
                            Montreal.sendMessage(msg, Communication.MCGPORT);
                            logr.info("Request:Montreal Server->McGill Server :Add into Waitlist request " + "Packet:" + "[" + msg + "]");
                        } catch (IOException ex) {
                            Logger.getLogger(MONOperationsImplementation.class.getName()).log(Level.SEVERE, null, ex);
                        }
                        boolean loop = true;
                        while (loop) {
                            if (Communication.responseMessages.get(messageID) != null) {
                                result = result + Communication.responseMessages.get(messageID);
                                Communication.responseMessages.remove(messageID);
                                if (Boolean.parseBoolean(result)) {
                                    logr.info("MCGILL Response : Add wait " + userID + " " + itemID + " " + "Success " + "Return value: 3");
                                    return 3;
                                } else {
                                    logr.info("MCGILL Response : Add wait " + userID + " " + itemID + " " + "Fail " + "Return value: 5");
                                    return 5;
                                }
                            }
                        }
                    }
                }
            }
        }
        logr.info("Borrow item " + userID + " " + itemID + " " + "Fail " + "Return value: 0 Invalid userID");
        return 0;
    }

    public boolean addWait(String userID, String itemID) {
        try {
            synchronized (this) {
                if (Data.availableBookInformation.get(itemID) == null) {
                    if (Data.waitList.get(itemID) == null) {
                        Queue q = new LinkedList();
                        q.add(userID);
                        Data.waitList.put(itemID, q);

                    } else {
                        Queue q = Data.waitList.get(itemID);
                        q.add(userID);
                        Data.waitList.put(itemID, q);
                    }
                    logr.info("Add wait " + userID + " " + itemID + " " + "Success " + "Return value: 3");
                    return true;
                } else {
                    logr.info("Add wait " + userID + " " + itemID + " " + "Fail " + "Return value: 5");
                    return false;
                }
            }
        } catch (Exception e) {
            System.out.println(e.toString());
            logr.info("Add wait " + userID + " " + itemID + " " + "Fail " + "Exception:" + e.toString());
            return false;
        }
    }

    boolean checkWaitList(String itemID) {
        if (Data.waitList.get(itemID) != null) {
            return true;
        } else {
            return false;
        }
    }

    void removeFromWaitListAndBorrow(String itemID) {
        Queue<String> q = Data.waitList.get(itemID);
        String userID = q.remove();
        if (q.size() == 0) {
            Data.waitList.remove(itemID);
            if (userID.substring(0, 3).equals("MCG") || userID.substring(0, 3).equals("CON")) {
                checkInWaitlistAndRemove(userID);
            }
        }
        logr.info("User id :" + userID + "Removed from waitlist of : " + itemID + " success");
        doBorrow(userID, itemID, 30);
    }

    boolean checkInWaitlistAndRemove(String userID) {
        for (String bookID : Data.waitList.keySet()) {
            Queue q = Data.waitList.get(bookID);
            q.remove(userID);
        }
        logr.info("User id :" + userID + "Removed from waitlist database " + " success");
        return true;
    }

    public boolean doBorrow(String userID, String itemID, int numberOfDays) {
        try {
            synchronized (this) {
                BookDetails bd = Data.availableBookInformation.get(itemID);
                bd.quantity = bd.quantity - 1;
                if (bd.quantity > 0) {
                    Data.availableBookInformation.put(itemID, bd);
                } else {
                    Data.availableBookInformation.remove(itemID);
                }
                if (Data.borrowDetails.get(userID) == null) {
                    List<BorrowDetails> l = new ArrayList();
                    l.add(new BorrowDetails(itemID, numberOfDays));
                    Data.borrowDetails.put(userID, l);

                    List<String> l2 = new ArrayList();
                    l2.add(itemID);
                    Data.borrowDetailsWithoutDays.put(userID, l2);
                } else {
                    List<BorrowDetails> l = Data.borrowDetails.get(userID);
                    l.add(new BorrowDetails(itemID, numberOfDays));
                    Data.borrowDetails.put(userID, l);

                    List<String> l2 = Data.borrowDetailsWithoutDays.get(userID);
                    l2.add(itemID);
                    Data.borrowDetailsWithoutDays.put(userID, l2);
                }
                logr.info("Borrow Item" + " " + userID + " " + itemID + " " + numberOfDays + " " + "Success" + "Return value:" + 1);
                return true;
            }
        } catch (Exception e) {
            System.out.println(e.toString());
            logr.info("Borrow Item" + " " + userID + " " + itemID + " " + numberOfDays + " " + "Fail" + "Return value:" + 10 + "Something Unexpected");
            return false;
        }
    }

    public boolean checkAvail(String itemID) {
        if (Data.availableBookInformation.containsKey(itemID)) {
            return true;
        } else {
            return false;
        }
    }

    public String checkAvailAndValidate(String userID, String itemID) {
        boolean result1, result2;
        synchronized (this) {
            result1 = checkAvail(itemID);
            result2 = Data.borrowDetails.containsKey(userID);
            String result = Boolean.toString(result1) + "-" + Boolean.toString(result2);
            return result;
        }
    }

    public String checkAvailAndValidateAndDoBorrow(String userID, String itemID, int noOfDays) {
        boolean result1, result2, result3, additionalchecker;
        synchronized (this) {
            additionalchecker = Data.bookInformation.containsKey(itemID);
            result1 = checkAvail(itemID);
            result2 = !(Data.borrowDetails.containsKey(userID));
            if (result1 && result2) {
                result3 = doBorrow(userID, itemID, noOfDays);
            } else {
                result3 = false;
            }
            if (result3 == true) {
                checkInWaitlistAndRemove(userID);
            }
            String result = Boolean.toString(result1) + "-" + Boolean.toString(result2) + "-" + Boolean.toString(result3) + "-" + Boolean.toString(additionalchecker);
            return result;
        }
    }

    @Override
    public String findItem(String userID, String itemName) {
        if (Data.userIDs.containsKey(userID)) {
            String result = "";
            result = findItemLocal(userID, itemName) + "\n";
            String raw_msg = "";
            String msgType = "" + Communication.REQUEST;
            String requestType = "" + Communication.FIND_ITEM;
            raw_msg = raw_msg + msgType + "," + requestType + "," + userID + "," + itemName;
            String messageID = "";
            String messageID2 = "";

            try {
                synchronized (this) {
                    messageID = "" + (++Communication.MESSAGE_ID);
                }
                String msg = "" + messageID + "," + raw_msg;
                Montreal.sendMessage(msg, Communication.CONPORT);
                logr.info("Request:Montreal Server->Concordia Server :Find Item request " + "Packet:" + "[" + msg + "]");
            } catch (IOException ex) {
                Logger.getLogger(MONOperationsImplementation.class.getName()).log(Level.SEVERE, null, ex);
            }
            try {
                synchronized (this) {
                    messageID2 = "" + (++Communication.MESSAGE_ID);
                }
                String msg = "" + messageID2 + "," + raw_msg;
                Montreal.sendMessage(msg, Communication.MCGPORT);
                logr.info("Request:Montreal Server->McGill Server :Find Item request " + "Packet:" + "[" + msg + "]");
            } catch (IOException ex) {
                Logger.getLogger(MONOperationsImplementation.class.getName()).log(Level.SEVERE, null, ex);
            }

            boolean loop1 = true;
            boolean loop2 = true;
            while (loop1 || loop2) {
                if (Communication.responseMessages.get(messageID) != null) {
                    result = result + Communication.responseMessages.get(messageID) + "\n";
                    loop1 = false;
                    logr.info("Concordia Response : Find item " + userID + " " + itemName + " " + "Success " + result);
                    Communication.responseMessages.remove(messageID);
                }
                if (Communication.responseMessages.get(messageID2) != null) {
                    result = result + Communication.responseMessages.get(messageID2) + "\n";
                    loop2 = false;
                    logr.info("McGill Response : Find item " + userID + " " + itemName + " " + "Success " + result);
                    Communication.responseMessages.remove(messageID2);
                }
            }

            return result;
        } else {
            return "Invalid userID";
        }
    }

    String findItemLocal(String userID, String itemName) {
        try {
            synchronized (this) {
                String result = "";
                String itemID = "";
                if (Data.bookInformation.contains(itemName)) {
                    for (String entry : Data.bookInformation.keySet()) {
                        if (Objects.equals(itemName, Data.bookInformation.get(entry))) {
                            itemID = entry;
                            break;
                        }
                    }
                }
                BookDetails bd = Data.availableBookInformation.get(itemID);
                result = result + itemID + " " + bd.name + " " + bd.quantity;
                logr.info("Find item:" + userID + " " + itemName + " " + "Success ");
                return result;
            }
        } catch (Exception e) {
            System.out.println(e.toString());
            logr.info("Find item:" + userID + " " + itemName + " " + "Fail ");
            return " ";
        }
    }

    @Override
    public int returnItem(String userID, String itemID) {
        if (Data.userIDs.containsKey(userID)) {
            int destPort = getDestPort(itemID);
            switch (destPort) {
                case Communication.MYPORT: {
                    boolean r = returnItemLocal(userID, itemID);
                    if (r) {
                        return 1;
                    } else {
                        return 2;
                    }
                }
                case Communication.CONPORT: {
                    String messageID;
                    synchronized (this) {
                        messageID = "" + (++Communication.MESSAGE_ID);
                    }
                    String raw_msg = "";
                    String msgType = "" + Communication.REQUEST;
                    String requestType = "" + Communication.RETURN_ITEM;
                    raw_msg = raw_msg + msgType + "," + requestType + "," + userID + "," + itemID;

                    String msg = "" + messageID + "," + raw_msg;
                    try {
                        Montreal.sendMessage(msg, Communication.CONPORT);
                        logr.info("Request:Montreal Server->Concordia Server :Return Item request " + "Packet:" + "[" + msg + "]");
                    } catch (IOException ex) {
                        Logger.getLogger(MONOperationsImplementation.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    boolean loop = true;
                    while (loop) {
                        if (Communication.responseMessages.get(messageID) != null) {
                            String response = Communication.responseMessages.get(messageID);
                            Boolean b = Boolean.parseBoolean(response);
                            if (b) {
                                logr.info("Concordia Response : Return item " + userID + " " + itemID + " " + "Success ");
                                return 1;
                            } else {
                                logr.info("Concordia Response : Return item " + userID + " " + itemID + " " + "fail");
                                return 2;
                            }
                        }
                    }
                    break;
                }
                case Communication.MCGPORT: {
                    String messageID;
                    synchronized (this) {
                        messageID = "" + (++Communication.MESSAGE_ID);
                    }
                    String raw_msg = "";
                    String msgType = "" + Communication.REQUEST;
                    String requestType = "" + Communication.RETURN_ITEM;
                    raw_msg = raw_msg + msgType + "," + requestType + "," + userID + "," + itemID;

                    String msg = "" + messageID + "," + raw_msg;
                    try {
                        Montreal.sendMessage(msg, Communication.MCGPORT);
                        logr.info("Request:Montreal Server->MCGILL Server :Return Item request " + "Packet:" + "[" + msg + "]");
                    } catch (IOException ex) {
                        Logger.getLogger(MONOperationsImplementation.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    boolean loop = true;
                    while (loop) {
                        if (Communication.responseMessages.get(messageID) != null) {
                            String response = Communication.responseMessages.get(messageID);
                            Boolean b = Boolean.parseBoolean(response);
                            if (b) {
                                logr.info("McGill Response : Return item " + userID + " " + itemID + " " + "Success ");
                                return 1;
                            } else {
                                logr.info("McGill Response : Return item " + userID + " " + itemID + " " + "fail");
                                return 2;
                            }
                        }
                    }
                    break;
                }
            }
            return 3;
        } else {
            logr.info("Return item " + userID + " " + itemID + " " + "fail-invalid user id");
            return 3;
        }
    }

    public int getDestPort(String itemID) {
        if (itemID.substring(0, 3).equals("MON")) {
            return Communication.MYPORT;
        } else if (itemID.substring(0, 3).equals("CON")) {
            return Communication.CONPORT;
        } else {
            return Communication.MCGPORT;
        }
    }

    boolean returnItemLocal(String userID, String itemID) {
        synchronized (this) {
            List<String> lbd = Data.borrowDetailsWithoutDays.get(userID);
            List<BorrowDetails> lbd2 = Data.borrowDetails.get(userID);
            System.out.println("Before Return");
            printDatabase1();
            if (lbd == null || !(lbd.contains(itemID))) {
                logr.info("Return item:" + userID + " " + itemID + " " + "Fail: User hasn't issued any book");
                return false;
            } else {
                int temp = lbd.indexOf(itemID);
                lbd.remove(itemID);
                lbd2.remove(temp);
                Data.borrowDetailsWithoutDays.put(userID, lbd);
                Data.borrowDetails.put(userID, lbd2);
                if (lbd.size() == 0) {
                    Data.borrowDetails.remove(userID);
                    Data.borrowDetailsWithoutDays.remove(userID);
                }

                BookDetails bd = Data.availableBookInformation.get(itemID);
                if (bd == null) {
                    String itemName = Data.bookInformation.get(itemID);
                    Data.availableBookInformation.put(itemID, new BookDetails(itemName, 1));
                    // System.out.println("added");
                } else {
                    bd.quantity = bd.quantity + 1;
                    Data.availableBookInformation.put(itemID, bd);
                    //System.out.println("updated");
                }

                if (checkWaitList(itemID)) {
                    //write log for specific user
                    removeFromWaitListAndBorrow(itemID);
                }
                System.out.println("After Return");
                printDatabase1();
                logr.info("Return item:" + userID + " " + itemID + " " + "Success ");
                return true;
            }
        }
    }

    @Override
    public int authuser(String id) {
        String pass = Data.up.get(id);
        // System.out.println(pass + " " + passcode);
        String passcode = "1111";
        if (pass == null) {
            logr.info("Authentication request : " + id + "Fail no userid found");
            return 0;                                   //return 0 for no userid found
        } else if (pass.equals(passcode)) {
            logr.info("Authentication request : " + id + "Success");
            if (id.charAt(3) == 'M') {
                return 1;                               //returns 1 for manager
            } else if (id.charAt(3) == 'U') //returns 2 for users
            {
                return 2;
            } else {
                logr.info("Authentication request : " + id + "Fail for different user id other than m and u at 4th place");
                return 0;                               // for different user id other than m and u at 4th place
            }
        } else {
            logr.info("Authentication request : " + id + "Fail returns -1 for password mistype ");
            return -1;                                  //returns -1 for password mistype             
        }
    }

    void printBookInformation() {
        System.out.println("Book Information");
        for (String s : Data.bookInformation.keySet()) {
            System.out.println(s + " :" + Data.bookInformation.get(s));
        }
        System.out.println();
    }

    void printAvailableBookInformation() {
        System.out.println("Available Book Information");
        for (String key : Data.availableBookInformation.keySet()) {
            BookDetails b = Data.availableBookInformation.get(key);
            System.out.println(key + " " + b.name + " " + b.quantity);
        }
        System.out.println("");
    }

    void printBorrowDetails() {
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

    void printBorrowDetailsWithoutDays() {
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

    void printWaitList() {
        System.out.println("WaitList");
        for (String key : Data.waitList.keySet()) {
            Queue q = Data.waitList.get(key);
            System.out.println(key + " " + q);
        }
        System.out.println();
    }

    void printUserDetails() {
        System.out.println("User Details");
        for (String s : Data.up.keySet()) {
            System.out.println(s + " :" + Data.up.get(s));
        }
        System.out.println();
    }

    void printDatabase() {
        printAvailableBookInformation();
        printBorrowDetailsWithoutDays();
        printBorrowDetails();
        printWaitList();
        printBookInformation();
        printUserDetails();
    }

    void printDatabase1() {
        printAvailableBookInformation();
        printBorrowDetailsWithoutDays();
        printBorrowDetails();
        printWaitList();
    }

}
