package com.cs6650.assignment2.server;

import java.sql.SQLException;
import java.util.LinkedList;

import bsdsass2testdata.RFIDLiftData;

/**
 * Created by saikikwok on 23/10/2017.
 */
public class BackgroundMessenger extends Thread{

    private static final long INTERVAL = 1000;
    private static final int BATCH_SIZE = 20;
    private Boolean isActive = true;

    public void run() {
        while (this.isActive) {
            LinkedList<RFIDLiftData> dataset = gatherDataSet();
            if (dataset.size() > 0) {
                RecordDAO dao = new RecordDAO();
                try {
                    dao.insertRecord(dataset);
                    Thread.sleep(100);
                } catch (SQLException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void setDone() {
        this.isActive = false;
    }

    private LinkedList<RFIDLiftData> gatherDataSet() {
        LinkedList<RFIDLiftData> dataset = new LinkedList<RFIDLiftData>();
        long t = System.currentTimeMillis();
        long end = t + INTERVAL;
        while (System.currentTimeMillis() < end && dataset.size() < BATCH_SIZE) {
            RFIDLiftData data = BackgroundMessengerManager.messageQueue.poll();
            if (data == null) {
                try {
                    Thread.sleep(INTERVAL);
                    continue;
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            dataset.add(data);
        }
        return dataset;
    }
}
