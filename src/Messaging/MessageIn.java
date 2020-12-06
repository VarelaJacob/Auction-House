package Messaging;

import AuctionHouse.AuctionHouse;
import Bank.Bank;
import Messaging.MessageInfo;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class MessageIn {

    private Socket socket;
    private Bank bank;
    private AuctionHouse auction;
    private String source;
    private String returnMessage;
    private ObjectOutputStream outStream;
    private ObjectInputStream  inputStream;
 
    /*********** */
    public MessageIn(Socket socket, Object reference) throws IOException {
        
        this.socket = socket;
        setVar(reference);

        outStream   = new ObjectOutputStream(socket.getOutputStream());
        inputStream = new ObjectInputStream(socket.getInputStream()) ;
    }
    
}
