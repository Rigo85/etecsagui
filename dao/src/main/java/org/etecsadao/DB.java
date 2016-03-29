package org.etecsadao;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Author Rigoberto Leander Salgado Reyes <rlsalgado2006@gmail.com>
 * <p>
 * Copyright 2016 by Rigoberto Leander Salgado Reyes.
 * <p>
 * This program is licensed to you under the terms of version 3 of the
 * GNU Affero General Public License. This program is distributed WITHOUT
 * ANY EXPRESS OR IMPLIED WARRANTY, INCLUDING THOSE OF NON-INFRINGEMENT,
 * MERCHANTABILITY OR FITNESS FOR A PARTICULAR PURPOSE. Please refer to the
 * AGPL (http:www.gnu.org/licenses/agpl-3.0.txt) for more details.
 */
public class DB {

    Connection connection;
    List<String> queries;

    public DB(String url) throws SQLException {
        setUrl(url);
    }

    public void setUrl(String url) throws SQLException {
        if (connection != null) connection.close();
        connection = DriverManager.getConnection(String.format("jdbc:sqlite:%s", url));
        queries = Queries(Tables(connection));
    }

    public List<List<Map<String, String>>> execute(String partialQuery) {
        return queries.parallelStream().map(q -> q.replaceAll("\\$1\\$", partialQuery)).map(q -> {
            List<Map<String, String>> objs = new ArrayList<>();
            try {
                try (ResultSet resultSet = connection.prepareStatement(q).executeQuery()) {

                    ResultSetMetaData rsmd = resultSet.getMetaData();
                    int count = rsmd.getColumnCount();

                    while (resultSet.next()) {
                        Map<String, String> obj = new HashMap<>();
                        IntStream.range(1, count + 1).boxed().forEach(i -> {
                            try {
                                obj.put(rsmd.getColumnName(i), resultSet.getObject(i).toString());
                            } catch (SQLException e) {
                            }
                        });
                        if (!obj.isEmpty()) objs.add(obj);
                    }
                }
            } finally {
                return objs;
            }
        }).filter(l -> !l.isEmpty()).collect(Collectors.toList());
    }

    List<String> SchemasOrCatalog(Connection conn, boolean schema) throws SQLException {
        List<String> result = new ArrayList<>();

        DatabaseMetaData metaData = conn.getMetaData();
        try (ResultSet resultSet = (schema ? metaData.getSchemas() : metaData.getCatalogs())) {
            while (resultSet.next()) {
                result.add(resultSet.getString(1));
            }
        }

        return result;
    }

    Map<String, List<String>> Tables(Connection conn) throws SQLException {
        Map<String, List<String>> result = new HashMap<>();

        String[] types = {"TABLE"};
        final List<String> schemas = SchemasOrCatalog(conn, true);
        final List<String> catalogs = SchemasOrCatalog(conn, false);
        if (schemas.isEmpty()) schemas.add("");
        if (catalogs.isEmpty()) catalogs.add("");

        schemas.stream().forEach(schema -> catalogs.stream().forEach(catalog -> {
            try {
                DatabaseMetaData metaData = conn.getMetaData();
                try (ResultSet resultSet = metaData.getTables(catalog, schema, null, types)) {
                    while (resultSet.next()) {
                        String table = resultSet.getString(3);
                        result.put(table, Columns(conn, table, schema));
                    }
                }
            } catch (SQLException e) {
            }
        }));

        return result;
    }

    List<String> Columns(Connection conn, String table, String schema) throws SQLException {
        List<String> result = new ArrayList<>();

        try (ResultSet resultSet = conn.prepareStatement("PRAGMA " + (schema.isEmpty() ? "" : schema + ".") +
                "table_info(" + table + ");").executeQuery()) {
            while (resultSet.next()) {
                result.add(resultSet.getString(2));
            }
        }

        return result;
    }

    List<String> Queries(Map<String, List<String>> tables) {
        return tables.keySet().stream()
                .map(k -> String.format("SELECT * FROM %s WHERE %s", k, tables.get(k)
                        .stream()
                        .map(c -> String.format("%s LIKE '%%$1$%%' ", c))
                        .collect(Collectors.joining("or ")))).collect(Collectors.toList());
    }
}
