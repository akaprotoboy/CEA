import Clases.Clave;
import Clases.Rango;
import Clases.Respuesta;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MySQLDatabaseHandler {
    private static final String DB_URL = "jdbc:mysql://sql10.freesqldatabase.com:3306/sql10715507";
    private static final String USER = "sql10715507";
    private static final String PASSWORD = "bPXLXzGLEb";

    public static List<Respuesta> getListaRespuesta() {
        Connection conn = null;
        List<Respuesta> respuestas = new ArrayList<>();
        try {
            // Establish connection
            conn = DriverManager.getConnection(DB_URL, USER, PASSWORD);
            // Retrieving data into a List of Clases.Respuesta objects
            respuestas = getDataFromRespuesta(conn, "respuesta");

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            // Close connection
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
        return respuestas;
    }

    public static List<Clave> getListaClave() {
        Connection conn = null;
        List<Clave> claves = new ArrayList<>();
        try {
            // Establish connection
            conn = DriverManager.getConnection(DB_URL, USER, PASSWORD);
            // Retrieving data into a List of Clases.Respuesta objects
            claves = getDataFromClave(conn, "clave");

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            // Close connection
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
        return claves;
    }

    public static List<Rango> getListaRango() {
        Connection conn = null;
        List<Rango> rangos = new ArrayList<>();
        try {
            // Establish connection
            conn = DriverManager.getConnection(DB_URL, USER, PASSWORD);
            // Retrieving data into a List of Clases.Respuesta objects
            rangos = getDataFromRango(conn, "rango");

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            // Close connection
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
        return rangos;
    }

    public static List<Respuesta> getDataFromRespuesta(Connection conn, String tableName) throws SQLException {
        List<Respuesta> respuestas = new ArrayList<>();

        // SQL query to retrieve data from the specified table
        String sql = "SELECT ide_iIndice, res_vcRespuesta FROM " + tableName;

        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                int indice = rs.getInt("ide_iIndice");
                String respuesta = rs.getString("res_vcRespuesta");
                Respuesta respuestaObj = new Respuesta(indice, respuesta);
                respuestas.add(respuestaObj);
            }
        }
        return respuestas;
    }

    public static List<Clave> getDataFromClave(Connection conn, String tableName) throws SQLException {
        List<Clave> claves = new ArrayList<>();

        // SQL query to retrieve data from the specified table
        String sql = "SELECT cla_iIndice, cla_iPosicion, cla_vcRespuesta FROM " + tableName;

        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                int indice = rs.getInt("cla_iIndice");
                int posicion = rs.getInt("cla_iPosicion");
                String respuesta = rs.getString("cla_vcRespuesta");
                Clave claveObj = new Clave(indice, posicion, respuesta);
                claves.add(claveObj);
            }
        }
        return claves;
    }

    public static List<Rango> getDataFromRango(Connection conn, String tableName) throws SQLException {
        List<Rango> rangos = new ArrayList<>();

        // SQL query to retrieve data from the specified table
        String sql = "SELECT ran_iIndiceMinimo, ran_iIndiceMaximo, cla_iPosicion FROM " + tableName;

        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                int min = rs.getInt("ran_iIndiceMinimo");
                int max = rs.getInt("ran_iIndiceMaximo");
                int posicion = rs.getInt("cla_iPosicion");
                Rango rangoObj = new Rango(min, max, posicion);
                rangos.add(rangoObj);
            }
        }
        return rangos;
    }
}
