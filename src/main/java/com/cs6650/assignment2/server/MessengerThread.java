package com.cs6650.assignment2.server;

/**
 * Created by saikikwok on 18/11/2017.
 */
public abstract class MessengerThread extends Thread{
    protected Boolean isActive = true;
    protected final long INTERVAL = 1000;

    public void setDone() {
        this.isActive = false;
    }
}
