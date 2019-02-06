package Servers.McGill;

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

/**
 *
 * @author daksh
 */
public class MCGOperationsImplementation implements MCGOperations {

    Logger LOGGER;
    static FileHandler fh;

    public MCGOperationsImplementation(Logger LOGGER, FileHandler fh) {
        this.LOGGER = LOGGER;
        this.fh = fh;
        LOGGER.addHandler(fh);
    }

    public MCGOperationsImplementation() {

    }

    public void dummy() {
        System.out.println("dummy called");
        LOGGER.info("asdsadas");
    }

    @Override
    public int addItem(String managerID, String itemID, String itemName, int quantity) {
        if (Data.up.containsKey(managerID)) {
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
                while (checkWaitList(itemID)) {
                    removeFromWaitListAndBorrow(itemID);
                }
                System.out.println("After adding");
                printDatabase1();
                return 1;                                                               //return 1 if everything perfect
            } catch (Exception e) {
                return 0;                                                           //return 0 if something got wrong
            }
        } else {
            return -1;                                                          //return -1 if not manager
        }
    }

    @Override
    public int removeItem(String managerID, String itemID, int quantity) {
        if (Data.up.containsKey(managerID)) {
            try {
                BookDetails bd = Data.availableBookInformation.get(itemID);
                if (bd == null) {
                    return 3;                                                       //bookid not available
                } else {
                    if (quantity == -1) {
                        Data.availableBookInformation.remove(itemID);
                        return 2;                                                   //return 2 if item completely deleted
                    } else {
                        bd.quantity = bd.quantity - quantity;
                        Data.availableBookInformation.put(itemID, bd);
                        return 1;                                                   //return 1 item quantity decreased 
                    }
                }

            } catch (Exception e) {
                System.out.println(e.toString());
                return 0;                                                           //return 0 if something went wrong
            }
        } else {
            return -1;                                                          //invalid manager id
        }
    }

    @Override
    public List listItemAvailability(String managerID) {
        List b = new ArrayList<String>();
        for (String id : Data.availableBookInformation.keySet()) {
            String c;
            c = id + " " + Data.availableBookInformation.get(id).name + " " + Data.availableBookInformation.get(id).quantity;
            b.add(c);
        }
        printDatabase1();
        return b;
    }

    @Override
    public int borrowItem(String userID, String itemID, int numberOfDays, int waitWish) {
        if (Data.up.containsKey(userID)) {
            String a[], b[], c[];
            if (waitWish == 0) {
                String result = "";
                int destPort = getDestPort(itemID);
                switch (destPort) {
                    case Communication.MYPORT: {
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
                        } else {
                            return 2;
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
                            McGill.sendMessage(msg, Communication.CONPORT);
                        } catch (IOException ex) {
                            Logger.getLogger(MCGOperationsImplementation.class.getName()).log(Level.SEVERE, null, ex);
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
                                if (result3) {
                                    return 1;
                                } else if (!result1 && result2) {
                                    return 2;
                                } else {
                                    return 4;
                                }
                            }
                        }
                        break;
                    }

                    case Communication.MONPORT: {
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
                            McGill.sendMessage(msg, Communication.MONPORT);
                        } catch (IOException ex) {
                            Logger.getLogger(MCGOperationsImplementation.class.getName()).log(Level.SEVERE, null, ex);
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
                                if (result3) {
                                    return 1;
                                } else if (!result1 && result2) {
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
                            McGill.sendMessage(msg, Communication.CONPORT);
                        } catch (IOException ex) {
                            Logger.getLogger(MCGOperationsImplementation.class.getName()).log(Level.SEVERE, null, ex);
                        }
                        boolean loop = true;
                        while (loop) {
                            if (Communication.responseMessages.get(messageID) != null) {
                                result = result + Communication.responseMessages.get(messageID);
                                Communication.responseMessages.remove(messageID);
                                if (Boolean.parseBoolean(result)) {
                                    return 3;
                                } else {
                                    return 5;
                                }
                            }
                        }
                    }
                    case Communication.MONPORT: {
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
                            McGill.sendMessage(msg, Communication.MONPORT);
                        } catch (IOException ex) {
                            Logger.getLogger(MCGOperationsImplementation.class.getName()).log(Level.SEVERE, null, ex);
                        }
                        boolean loop = true;
                        while (loop) {
                            if (Communication.responseMessages.get(messageID) != null) {
                                result = result + Communication.responseMessages.get(messageID);
                                Communication.responseMessages.remove(messageID);
                                if (Boolean.parseBoolean(result)) {
                                    return 3;
                                } else {
                                    return 5;
                                }
                            }
                        }
                    }
                }
            }
        }
        return 0;
    }

    public boolean addWait(String userID, String itemID) {
        try {
            if (Data.availableBookInformation.get(itemID) == null) {
                if (Data.waitList.get(itemID) == null) {
                    Queue q = new LinkedList();
                    q.add(userID);
                    Data.waitList.put(itemID, q);
                    return true;
                } else {
                    Queue q = Data.waitList.get(itemID);
                    q.add(userID);
                    Data.waitList.put(itemID, q);
                    return true;
                }
            } else {
                return false;
            }
        } catch (Exception e) {
            System.out.println(e.toString());
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
        }
        doBorrow(userID, itemID, 30);
    }

    boolean checkInWaitlistAndRemove(String userID) {
        for (String bookID : Data.waitList.keySet()) {
            Queue q = Data.waitList.get(bookID);
            q.remove(userID);
        }
        return true;
    }

    public boolean doBorrow(String userID, String itemID, int numberOfDays) {
        try {
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
            return true;
        } catch (Exception e) {
            System.out.println(e.toString());
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
        result1 = checkAvail(itemID);
        result2 = Data.borrowDetails.containsKey(userID);
        String result = Boolean.toString(result1) + "-" + Boolean.toString(result2);
        return result;
    }

    public String checkAvailAndValidateAndDoBorrow(String userID, String itemID, int noOfDays) {
        boolean result1, result2, result3;
        result1 = checkAvail(itemID);
        result2 = !(Data.borrowDetails.containsKey(userID));
        if (result1 && result2) {
            result3 = doBorrow(userID, itemID, noOfDays);
        } else {
            result3 = false;
        }
        if(result3 == true)
        {
         checkInWaitlistAndRemove(userID);   
        }
        String result = Boolean.toString(result1) + "-" + Boolean.toString(result2) + "-" + Boolean.toString(result3);
        return result;
    }

    @Override
    public String findItem(String userID, String itemName) {
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
            McGill.sendMessage(msg, Communication.CONPORT);
        } catch (IOException ex) {
            Logger.getLogger(MCGOperationsImplementation.class.getName()).log(Level.SEVERE, null, ex);
        }
        try {
            synchronized (this) {
                messageID2 = "" + (++Communication.MESSAGE_ID);
            }
            String msg = "" + messageID + "," + raw_msg;
            McGill.sendMessage(msg, Communication.MONPORT);
        } catch (IOException ex) {
            Logger.getLogger(MCGOperationsImplementation.class.getName()).log(Level.SEVERE, null, ex);
        }

        boolean loop1 = true;
        boolean loop2 = true;
        while (loop1 || loop2) {
            if (Communication.responseMessages.get(messageID) != null) {
                result = result + Communication.responseMessages.get(messageID) + "\n";
                loop1 = false;
                Communication.responseMessages.remove(messageID);
            }
            if (Communication.responseMessages.get(messageID2) != null) {
                result = result + Communication.responseMessages.get(messageID2) + "\n";
                loop2 = false;
                Communication.responseMessages.remove(messageID2);
            }
        }

        return result;
    }

    String findItemLocal(String userID, String itemName) {
        try {
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
            return result;

        } catch (Exception e) {
            System.out.println(e.toString());
            return " ";
        }
    }

    @Override
    public int returnItem(String userID, String itemID) {
        if (Data.up.containsKey(userID)) {
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
                        McGill.sendMessage(msg, Communication.CONPORT);
                    } catch (IOException ex) {
                        Logger.getLogger(MCGOperationsImplementation.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    boolean loop = true;
                    while (loop) {
                        if (Communication.responseMessages.get(messageID) != null) {
                            String response = Communication.responseMessages.get(messageID);
                            Boolean b = Boolean.parseBoolean(response);
                            if (b) {
                                return 1;
                            } else {
                                return 2;
                            }
                        }
                    }
                    break;
                }
                case Communication.MONPORT: {
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
                        McGill.sendMessage(msg, Communication.MONPORT);
                    } catch (IOException ex) {
                        Logger.getLogger(MCGOperationsImplementation.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    boolean loop = true;
                    while (loop) {
                        if (Communication.responseMessages.get(messageID) != null) {
                            String response = Communication.responseMessages.get(messageID);
                            Boolean b = Boolean.parseBoolean(response);
                            if (b) {
                                return 1;
                            } else {
                                return 2;
                            }
                        }
                    }
                    break;
                }
            }
            return 3;
        } else {
            return 3;
        }
    }

    public int getDestPort(String itemID) {
        if (itemID.substring(0, 3).equals("MCG")) {
            return Communication.MYPORT;
        } else if (itemID.substring(0, 3).equals("CON")) {
            return Communication.CONPORT;
        } else {
            return Communication.MONPORT;
        }
    }

    boolean returnItemLocal(String userID, String itemID) {
        List<String> lbd = Data.borrowDetailsWithoutDays.get(userID);
        List<BorrowDetails> lbd2 = Data.borrowDetails.get(userID);
        System.out.println("Before Return");
        printDatabase1();
        if (lbd == null || !(lbd.contains(itemID))) {
            return false;
        } else {
            int temp = lbd.indexOf(itemID);
            lbd.remove(itemID);
            lbd2.remove(temp);
            if (lbd.size() == 0) {
                Data.borrowDetails.remove(userID);
                Data.borrowDetailsWithoutDays.remove(userID);
            }
            Data.borrowDetailsWithoutDays.put(userID, lbd);
            Data.borrowDetails.put(userID, lbd2);

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
            return true;
        }
    }

    @Override
    public int authuser(String id) {
        String pass = Data.up.get(id);
        // System.out.println(pass + " " + passcode);
        String passcode = "1111";
        if (pass == null) {
            return 0;                                   //return 0 for no userid found
        } else if (pass.equals(passcode)) {
            if (id.charAt(3) == 'M') {
                return 1;                               //returns 1 for manager
            } else if (id.charAt(3) == 'U') //returns 2 for users
            {
                return 2;
            } else {
                return 0;                               // for different user id other than m and u at 4th place
            }
        } else {
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
        printBookInformation();
        printAvailableBookInformation();
        printBorrowDetailsWithoutDays();
        printWaitList();
        printBorrowDetails();
        printUserDetails();
    }

    void printDatabase1() {
        printBookInformation();
        printAvailableBookInformation();
        printBorrowDetailsWithoutDays();
        printWaitList();
        printBorrowDetails();
    }

}
