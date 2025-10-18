// Archivo único para el proyecto: ModuloVentasApp.java
// Contiene las clases Modelo, Conexión y DAO para el módulo de ventas.

// SQL Requerido para la Base de Datos:
/*
CREATE DATABASE software_ventas;
USE software_ventas;

CREATE TABLE Ventas (
    id_venta INT PRIMARY KEY AUTO_INCREMENT,
    fecha_venta DATE NOT NULL,
    cliente_nombre VARCHAR(100) NOT NULL,
    total_venta DOUBLE NOT NULL,
    estado VARCHAR(50) NOT NULL
);
*/

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

// Nombramiento de Clase: PascalCase
/**
 * Clase que representa el modelo de datos para una Venta.
 * Cumple con el estándar de nombramiento de clases (PascalCase).
 */
class Venta {
    // Nombramiento de Variables: camelCase
    private int idVenta;
    private Date fechaVenta;
    private String clienteNombre;
    private double totalVenta;
    private String estado;

    // Constructor completo
    public Venta(int idVenta, Date fechaVenta, String clienteNombre, double totalVenta, String estado) {
        this.idVenta = idVenta;
        this.fechaVenta = fechaVenta;
        this.clienteNombre = clienteNombre;
        this.totalVenta = totalVenta;
        this.estado = estado;
    }

    // Constructor para inserción (sin ID)
    public Venta(Date fechaVenta, String clienteNombre, double totalVenta, String estado) {
        this.fechaVenta = fechaVenta;
        this.clienteNombre = clienteNombre;
        this.totalVenta = totalVenta;
        this.estado = estado;
    }

    // Getters y Setters (Nombramiento de Métodos: camelCase)
    public int getIdVenta() {
        return idVenta;
    }

    public void setIdVenta(int idVenta) {
        this.idVenta = idVenta;
    }

    public Date getFechaVenta() {
        return fechaVenta;
    }

    public void setFechaVenta(Date fechaVenta) {
        this.fechaVenta = fechaVenta;
    }

    public String getClienteNombre() {
        return clienteNombre;
    }

    public void setClienteNombre(String clienteNombre) {
        this.clienteNombre = clienteNombre;
    }

    public double getTotalVenta() {
        return totalVenta;
    }

    public void setTotalVenta(double totalVenta) {
        this.totalVenta = totalVenta;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    @Override
    public String toString() {
        return "Venta [ID=" + idVenta + ", Fecha=" + fechaVenta + ", Cliente='" + clienteNombre + "', Total="
                + totalVenta + ", Estado=" + estado + "]";
    }
}

// Nombramiento de Clase: PascalCase
/**
 * Clase encargada de gestionar la conexión JDBC con la base de datos MySQL.
 */
class ConexionDB {
    // Configuración de la Base de Datos (ajustar según el entorno)
    private static final String URL = "jdbc:mysql://localhost:3306/software_ventas";
    private static final String USUARIO = "root"; //
    private static final String CLAVE = ""; //

    // Nombramiento de Método: camelCase
    /**
     * Establece y retorna la conexión a la base de datos.
     * 
     * @return Objeto Connection.
     * @throws SQLException Si ocurre un error de conexión.
     */
    public static Connection obtenerConexion() throws SQLException {
        try {
            // Cargar el driver de MySQL
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.err.println(
                    "Error: Driver JDBC de MySQL no encontrado. Asegúrese de tener el conector en el classpath.");
            throw new SQLException("Driver de DB no encontrado.", e);
        }
        return DriverManager.getConnection(URL, USUARIO, CLAVE);
    }
}

// Nombramiento de Clase: PascalCase
/**
 * Clase DAO (Data Access Object) para gestionar las operaciones CRUD
 * del módulo de ventas (Inserción, Consulta, Actualización y Eliminación).
 */
class ModuloVentas {
    // Nombramiento de Método: camelCase
    /**
     * Inserta un nuevo registro de venta en la base de datos.
     * (Funcionalidad: INSERCIÓN/CREATE)
     * 
     * @param venta El objeto Venta a insertar.
     * @return true si la inserción fue exitosa, false en caso contrario.
     */
    public boolean insertarVenta(Venta venta) {
        String sql = "INSERT INTO Ventas (fecha_venta, cliente_nombre, total_venta, estado) VALUES (?, ?, ?, ?)";
        try (Connection conn = ConexionDB.obtenerConexion();
                PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setDate(1, venta.getFechaVenta());
            stmt.setString(2, venta.getClienteNombre());
            stmt.setDouble(3, venta.getTotalVenta());
            stmt.setString(4, venta.getEstado());

            int filasAfectadas = stmt.executeUpdate();

            if (filasAfectadas > 0) {
                // Obtener la ID generada para actualizar el objeto Venta
                try (ResultSet rs = stmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        venta.setIdVenta(rs.getInt(1));
                    }
                }
                System.out.println("✅ Venta insertada correctamente. ID Asignado: " + venta.getIdVenta());
                return true;
            }
        } catch (SQLException e) {
            System.err.println("❌ Error al insertar la venta: " + e.getMessage());
        }
        return false;
    }

    // Nombramiento de Método: camelCase
    /**
     * Consulta una venta por su ID.
     * (Funcionalidad: CONSULTA/READ)
     * 
     * @param id ID de la venta a buscar.
     * @return Objeto Venta si se encuentra, o null.
     */
    public Venta consultarVenta(int id) {
        String sql = "SELECT * FROM Ventas WHERE id_venta = ?";
        try (Connection conn = ConexionDB.obtenerConexion();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    // Mapear el resultado al objeto Venta
                    return new Venta(
                            rs.getInt("id_venta"),
                            rs.getDate("fecha_venta"),
                            rs.getString("cliente_nombre"),
                            rs.getDouble("total_venta"),
                            rs.getString("estado"));
                }
            }
        } catch (SQLException e) {
            System.err.println("❌ Error al consultar la venta (ID: " + id + "): " + e.getMessage());
        }
        return null;
    }

    // Nombramiento de Método: camelCase
    /**
     * Actualiza los datos de una venta existente.
     * (Funcionalidad: ACTUALIZACIÓN/UPDATE)
     * 
     * @param venta Objeto Venta con los datos actualizados.
     * @return true si la actualización fue exitosa, false en caso contrario.
     */
    public boolean actualizarVenta(Venta venta) {
        String sql = "UPDATE Ventas SET cliente_nombre = ?, total_venta = ?, estado = ? WHERE id_venta = ?";
        try (Connection conn = ConexionDB.obtenerConexion();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, venta.getClienteNombre());
            stmt.setDouble(2, venta.getTotalVenta());
            stmt.setString(3, venta.getEstado());
            stmt.setInt(4, venta.getIdVenta());

            int filasAfectadas = stmt.executeUpdate();

            if (filasAfectadas > 0) {
                System.out.println("✅ Venta ID " + venta.getIdVenta() + " actualizada correctamente.");
                return true;
            }
        } catch (SQLException e) {
            System.err.println("❌ Error al actualizar la venta (ID: " + venta.getIdVenta() + "): " + e.getMessage());
        }
        return false;
    }

    // Nombramiento de Método: camelCase
    /**
     * Elimina una venta por su ID.
     * (Funcionalidad: ELIMINACIÓN/DELETE)
     * 
     * @param id ID de la venta a eliminar.
     * @return true si la eliminación fue exitosa, false en caso contrario.
     */
    public boolean eliminarVenta(int id) {
        String sql = "DELETE FROM Ventas WHERE id_venta = ?";
        try (Connection conn = ConexionDB.obtenerConexion();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);

            int filasAfectadas = stmt.executeUpdate();

            if (filasAfectadas > 0) {
                System.out.println("✅ Venta ID " + id + " eliminada correctamente.");
                return true;
            } else {
                System.out.println("❌ No se encontró la Venta ID " + id + " para eliminar.");
            }
        } catch (SQLException e) {
            System.err.println("❌ Error al eliminar la venta (ID: " + id + "): " + e.getMessage());
        }
        return false;
    }

    // Nombramiento de Método: camelCase
    /**
     * Lista todas las ventas existentes en la base de datos.
     * (Funcionalidad: CONSULTA de todas las ventas)
     * 
     * @return Lista de objetos Venta.
     */
    public List<Venta> listarVentas() {
        List<Venta> ventas = new ArrayList<>();
        String sql = "SELECT * FROM Ventas ORDER BY fecha_venta DESC";

        try (Connection conn = ConexionDB.obtenerConexion();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {

            System.out.println("\n--- LISTADO COMPLETO DE VENTAS ---");
            while (rs.next()) {
                Venta venta = new Venta(
                        rs.getInt("id_venta"),
                        rs.getDate("fecha_venta"),
                        rs.getString("cliente_nombre"),
                        rs.getDouble("total_venta"),
                        rs.getString("estado"));
                ventas.add(venta);
                System.out.println(venta); // Imprimir en el proceso de iteración
            }
            System.out.println("---------------------------------");

        } catch (SQLException e) {
            System.err.println("❌ Error al listar las ventas: " + e.getMessage());
        }
        return ventas;
    }
}

// Nombramiento de Clase: PascalCase
/**
 * Clase principal para la ejecución y demostración del Módulo de Ventas.
 */
public class ModuloVentasApp {
    public static void main(String[] args) {
        System.out.println("--- DEMOSTRACIÓN DEL MÓDULO DE VENTAS (CRUD JDBC) ---");

        // Instancia del DAO
        ModuloVentas dao = new ModuloVentas();

        // --- 1. FUNCIÓN DE INSERCIÓN (CREATE) ---
        System.out.println("\n--- 1. INSERCIÓN DE NUEVOS REGISTROS ---");
        // Crear nuevas ventas
        Venta venta1 = new Venta(Date.valueOf("2025-10-15"), "Sofía Rodríguez", 150.50, "Pendiente");
        Venta venta2 = new Venta(Date.valueOf("2025-10-16"), "Carlos Martínez", 300.00, "Pagada");

        // Insertar y capturar la ID generada
        dao.insertarVenta(venta1);
        dao.insertarVenta(venta2);

        // --- 2. FUNCIÓN DE CONSULTA (READ) ---
        System.out.println("\n--- 2. CONSULTA DE UN REGISTRO ---");
        int idVentaConsultar = venta1.getIdVenta();
        Venta ventaConsultada = dao.consultarVenta(idVentaConsultar);

        if (ventaConsultada != null) {
            System.out.println("Venta encontrada (ID " + idVentaConsultar + "): " + ventaConsultada);
        } else {
            System.out.println("Venta ID " + idVentaConsultar + " no encontrada.");
        }

        // --- 3. FUNCIÓN DE ACTUALIZACIÓN (UPDATE) ---
        System.out.println("\n--- 3. ACTUALIZACIÓN DE UN REGISTRO ---");
        // Cambiar el estado y total de la venta 1
        if (ventaConsultada != null) {
            ventaConsultada.setEstado("Enviada");
            ventaConsultada.setTotalVenta(155.50); // Ajuste de precio
            dao.actualizarVenta(ventaConsultada);
        }

        // Verificar la actualización
        Venta ventaActualizada = dao.consultarVenta(idVentaConsultar);
        if (ventaActualizada != null) {
            System.out.println("Estado después de la actualización: " + ventaActualizada);
        }

        // --- 4. FUNCIÓN DE LISTADO (READ ALL) ---
        dao.listarVentas();

        // --- 5. FUNCIÓN DE ELIMINACIÓN (DELETE) ---
        System.out.println("\n--- 5. ELIMINACIÓN DE UN REGISTRO ---");
        // Eliminar la venta 2
        dao.eliminarVenta(venta2.getIdVenta());

        // --- 6. FUNCIÓN DE LISTADO FINAL (VERIFICACIÓN) ---
        System.out.println("\n--- 6. VERIFICACIÓN DESPUÉS DE LA ELIMINACIÓN ---");
        dao.listarVentas();

        System.out.println("\n--- DEMOSTRACIÓN FINALIZADA ---");
    }
}