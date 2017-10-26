package com.cs6650.assignment2.server;


import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import java.sql.SQLException;


/**
 * Created by saikikwok on 23/10/2017.
 */
public class BackgroundJobManager implements Job {

    private static int dayNum = 1;

    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        System.out.println("creating caching table for query");
        RecordDAO dao = new RecordDAO();
        try {
            dao.createInfoTableByDay(dayNum);
            dayNum++;
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}

