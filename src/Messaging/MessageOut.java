package Messaging;

import Agent.Agent;
import AuctionHouse.AuctionHouse;
import Messaging.MessageInfo;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class MessageOut implements Runnable{
    
    public BlockingQueue<MessageInfo> outQueue;

    public int bankPort;

    private int portNum;

    private String hostName;
    
}
