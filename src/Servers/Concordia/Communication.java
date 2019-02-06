/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Servers.Concordia;

import java.util.Hashtable;

/**
 *
 * @author daksh
 */
class Communication {

    static int MESSAGE_ID = 1000;
    
    static final int REQUEST = 8;
    static final int RESPONSE = 9;

    static final int FIND_ITEM = 10;
    static final int CHECK_AVIAL_AND_VALIDATE = 11;
    static final int DO_BORROW = 12;
    static final int ADD_WAIT = 13;
    static final int RETURN_ITEM = 14;
    
    final static int MCGPORT = 7000;
    final static int MONPORT = 8000;
    final static int MYPORT=6000;
    
    static Hashtable<String, String> responseMessages = new Hashtable();
}
