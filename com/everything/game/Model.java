package com.everything.game;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/*
 * class Model
 * parent class support ORM Object–relational mapping
 */
public class Model {
    private ArrayList<String> whereQuery;
    private ArrayList<String> orderQuery;
    private String selectQuery;
    private int limit;
    protected String table;
    private ArrayList<Object> parameters;

    public Model() {
        this.cleanQuery();
    }
    
    public Model where(String field, Object value) {
        this.whereQuery.add(field + " = ?");
        this.parameters.add(value);
        return this;
    }

    public Model whereIn(String field, List<String> values) {
        ArrayList<String> inClause = new ArrayList<String>();
        for (int i = 0; i < values.size(); i++) {
            inClause.add("?");
            this.parameters.add(values.get(i));
        }
        this.whereQuery.add(field + " IN (" + String.join(",", inClause) + ")");
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
            PreparedStatement statement = MySQLManager.conn.prepareStatement(query);
            this.setParameter(statement);
            this.cleanQuery();
            ResultSet resultSet = statement.executeQuery();
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
            PreparedStatement statement = MySQLManager.conn.prepareStatement(query);
            this.setParameter(statement);
            this.cleanQuery();
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next())
                return this.makeResult(resultSet);
            return null;
        } catch(Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Updates records in the database table with the provided data.
     *
     * @param data A Map representing the data to be updated.
     *             The keys are column names, and the values are the new values for those columns.
     * @return The number of rows affected by the update operation.
     */
    public int update(Map<String, Object> data) {
        try {
            String updateQuery = String.format("UPDATE %s SET ", this.table);
            ArrayList<String> keysUpdate = new ArrayList<String>();
            int updatePos = 0;
            for (Map.Entry<String, Object> entry : data.entrySet()) {
                keysUpdate.add(entry.getKey() + " = ?");
                Object value = entry.getValue();
                this.parameters.add(updatePos++, value);
            }
            updateQuery += String.join(",", keysUpdate);
            String query = this.makeQuery(updateQuery);
            PreparedStatement statement = MySQLManager.conn.prepareStatement(query);
            this.setParameter(statement);
            this.cleanQuery();
            return statement.executeUpdate();
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
        this.orderQuery = new ArrayList<String>();
        this.selectQuery = "*";
        this.limit = -1;
        this.parameters = new ArrayList<Object>();
    }

    private String makeQuery(String query) {
        if (this.whereQuery.size() > 0)
            query +=  " WHERE " + String.join(" AND ", this.whereQuery);
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
        resultSet.close();
        return resultMap;
    }

    private void setParameter(PreparedStatement statement) throws Exception {
        for (int i = 0; i < this.parameters.size(); i++) {
            Object value = this.parameters.get(i);
            statement.setObject(i + 1, value);
        }
    }
}
