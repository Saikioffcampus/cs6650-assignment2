package com.cs6650.assignment2.client;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentLinkedQueue;

import bsdsass2testdata.RFIDLiftData;

/**
 * Created by saikikwok on 21/10/2017.
 */
public class ClientContentProducer {
    private ConcurrentLinkedQueue<RFIDLiftData> queue;
    final private String contentSource = "resource/BSDSAssignment2Day2.ser";

    public ClientContentProducer() {
        populate();
    }

    public RFIDLiftData consume() {
        return this.queue.poll();
    }

    public Boolean isEmpty() {
        return this.queue.isEmpty();
    }

    private void populate() {
        try {
            FileInputStream fis = new FileInputStream(this.contentSource);
            ObjectInputStream ois = new ObjectInputStream(fis);
            ArrayList<RFIDLiftData> RFIDDataIn = (ArrayList) ois.readObject();
            this.queue = new ConcurrentLinkedQueue<RFIDLiftData>(RFIDDataIn);
            ois.close();
            fis.close();
        }catch(IOException ioe){
            ioe.printStackTrace();
            return;
        }catch(ClassNotFoundException c){
            System.out.println("Class not found");
            c.printStackTrace();
            return;
        }
    }

}
