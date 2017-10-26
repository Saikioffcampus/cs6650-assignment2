package com.cs6650.assignment2.server;

import org.json.simple.JSONObject;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;

import bsdsass2testdata.RFIDLiftData;

/**
 * Created by saikikwok on 22/10/2017.
 */
public class RecordDAO {

    private static final String INSERT_ST =
            "INSERT INTO daily_record (id, resort_id, day_num, time, skier_id, lift_id) VALUES (DEFAULT, ?, ?, ?, ?, ?)";
    private static final String JOIN_TABLE_ST =
            "SELECT tmp.skier_id, COUNT (tmp.lift_id) AS lift_num, SUM (tmp.vertical) AS vertical_sum " +
            "FROM (SELECT daily_record.skier_id, daily_record.lift_id, lift_vertical.vertical " +
            "FROM daily_record " +
            "JOIN lift_vertical " +
            "ON daily_record.lift_id = lift_vertical.lift_id " +
            "WHERE daily_record.day_num = ?) AS tmp GROUP BY tmp.skier_id";

    public Boolean createInfoTableByDay(int dayNum) throws SQLException {
        final String sql = "CREATE TABLE info_day_" + dayNum + " AS (" + JOIN_TABLE_ST + ")";
        Connection conn = null;
        try {
            ConnectionFactory.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, dayNum);
            int i = ps.executeUpdate();
            if (i == 1) {
                return true;
            }
        } finally {
            if (conn != null) conn.close();
        }
        return false;
    }

    public Boolean insertRecord(LinkedList<RFIDLiftData> dataset) throws SQLException {
        Connection conn = null;
        try {
            conn = ConnectionFactory.getConnection();
            conn.setAutoCommit(false);
            PreparedStatement ps = conn.prepareStatement(INSERT_ST);
            for (int i = 0; i < dataset.size(); i++) {
                RFIDLiftData data = dataset.get(i);
                ps.setInt(1, data.getResortID());
                ps.setInt(2, data.getDayNum());
                ps.setInt(3, data.getTime());
                ps.setInt(4, data.getSkierID());
                ps.setInt(5, data.getLiftID());
                ps.addBatch();
            }
            int[] updateCounts = ps.executeBatch();
            conn.commit();
        } finally {
            if (conn != null) conn.close();
        }
        return true;
    }

    public JSONObject getSkierDataBySkierIDAndDayNum(int skierId, int dayNum) throws SQLException {
        JSONObject ret = new JSONObject();
        Connection conn = null;
        try{
            conn = ConnectionFactory.getConnection();
            String retrieve_sql =
                    "SELECT lift_num, vertical_sum " +
                    "FROM info_day_" + dayNum + " WHERE skier_id = ?";
            PreparedStatement ps = conn.prepareStatement(retrieve_sql);
            ps.setInt(1, skierId);
//            ps.setInt(2, dayNum);
            ResultSet resultSet = ps.executeQuery();
            if (resultSet.next()) {
                ret.put("num_lift", resultSet.getInt(1));
                ret.put("total_vertical", resultSet.getInt(2));
            }
        } finally {
            if (conn != null) conn.close();
        }
        return ret;
    }

}
