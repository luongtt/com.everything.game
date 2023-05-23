package com.everything.game;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/*
 * class Model
 * parent class support ORM Object–relational mapping
 */
public class Model {
    // TODO: check sql injection
    private ArrayList<String> whereQuery;
    private ArrayList<String> orderQuery;
    Map<String, ArrayList<String>> whereInQuery;
    private String selectQuery;
    private int limit;
    protected String table;

    public Model() {
        this.cleanQuery();
    }
    
    public Model where(String query) {
        this.whereQuery.add(query);
        return this;
    }

    public Model whereIn(String field, ArrayList<String> values) {
        this.whereInQuery.put(field, values);
        return this;
    }

    public Model select(String query) {
        this.selectQuery = query;
        return this;
    }

    public Model sortByDesc(String field) {
        this.orderQuery.add(field + " DESC");
        return this;
    }

    public Model sortByAsc(String field) {
        this.orderQuery.add(field + " ASC");
        return this;
    }

    public Model limit(int limit) {
        this.limit = limit;
        return this;
    }

    public Model join(String table) {
        // TODO: impl join into other table
        return this;
    }

    public ArrayList<Map<String, Object>> find() {
        try {
            String query = this.makeQuery(String.format("SELECT %s FROM `%s`", this.selectQuery, this.table));
            if (this.limit > -1)
                query += " LIMIT " + this.limit;
            this.cleanQuery();
            ResultSet resultSet = MySQLManager.stat.executeQuery(query);
            ArrayList<Map<String, Object>> rList = new ArrayList<>();
            while (resultSet.next())
                rList.add(this.makeResult(resultSet));
            return rList;
        } catch(Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public Map<String, Object> findOne() {
        try {
            String query = this.makeQuery(String.format("SELECT %s FROM `%s`", this.selectQuery, this.table));
            query += " LIMIT 1";
            this.cleanQuery();
            ResultSet resultSet = MySQLManager.stat.executeQuery(query);
            if (resultSet.next())
                return this.makeResult(resultSet);
            return null;
        } catch(Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public int update(Map<String, Object> data) {
        try {
            String updateQuery = String.format("UPDATE %s SET ", this.table);
            ArrayList<String> dataUpdate = new ArrayList<String>();
            for (Map.Entry<String, Object> entry : data.entrySet()) {
                String key = entry.getKey();
                Object values = entry.getValue();
                dataUpdate.add(key + "=" + values);
            }
            updateQuery += String.join(",", dataUpdate);
            String query = this.makeQuery(updateQuery);
            this.cleanQuery();
            return MySQLManager.stat.executeUpdate(query);
        } catch(Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    public int delete() {
        try {
            String query = this.makeQuery(String.format("DELETE FROM `%s`", this.table));
            this.cleanQuery();
            return MySQLManager.stat.executeUpdate(query);
        } catch(Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    private void cleanQuery() {
        this.whereQuery = new ArrayList<String>();
        this.whereInQuery = new HashMap<>();
        this.orderQuery = new ArrayList<String>();
        this.selectQuery = "*";
        this.limit = -1;
    }

    private String makeQuery(String query) {
        if (this.whereQuery.size() > 0)
            query +=  " WHERE " + String.join(" AND ", this.whereQuery);
        if (this.whereInQuery.size() > 0)
            for (Map.Entry<String, ArrayList<String>> entry : this.whereInQuery.entrySet()) {
                String key = entry.getKey();
                ArrayList<String> values = entry.getValue();
                query += String.format(" WHERE %s IN (%s)", key, String.join(",", values));
            }
        if (this.orderQuery.size() > 0)
            for (String entry : this.orderQuery) {
                query += " ORDER BY " + entry;
            }
        return query;
    }

    private Map<String, Object> makeResult(ResultSet resultSet) throws Exception {
        ResultSetMetaData metaData = resultSet.getMetaData();
        int columnCount = metaData.getColumnCount();
        Map<String, Object> resultMap = new HashMap<>();
        for (int i = 1; i <= columnCount; i++) {
            String columnName = metaData.getColumnName(i);
            Object value = resultSet.getObject(i);
            resultMap.put(columnName, value);
        }
        return resultMap;
    }
}
