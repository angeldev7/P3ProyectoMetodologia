package DAO;

import Database.ConexionBaseDatos;
import model.AccesoSistema;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Sorts;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class AccesoSistemaDAO {
    private MongoCollection<Document> coleccionAccesos;
    private static final Logger logger = LoggerFactory.getLogger(AccesoSistemaDAO.class);
    
    // Constructor para testing con inyección de dependencias
    public AccesoSistemaDAO(MongoCollection<Document> coleccion) {
        this.coleccionAccesos = coleccion;
    }
    
    public AccesoSistemaDAO() {
        this.coleccionAccesos = ConexionBaseDatos.getColeccion("historial_accesos");
        crearIndices();
    }
    
    private void crearIndices() {
        try {
            // Crear índice para búsqueda por usuario
            Document indexUsuario = new Document("usuario", 1);
            coleccionAccesos.createIndex(indexUsuario);
            
            // Crear índice para búsqueda por fecha
            Document indexFecha = new Document("fechaHora", -1);
            coleccionAccesos.createIndex(indexFecha);
            
            logger.info("Índices creados para historial de accesos");
        } catch (Exception e) {
<<<<<<< HEAD
            logger.error("❌ Error al crear índices de accesos: " + e.getMessage());
=======
            System.err.println("Error al crear índices de accesos: " + e.getMessage());
>>>>>>> origin/Test
        }
    }
    
    public void registrarAcceso(AccesoSistema acceso) {
        try {
            String usuario = sanitizarTexto(acceso.getUsuario());
            String rol = sanitizarTexto(acceso.getRol());
            String mensaje = sanitizarTexto(acceso.getMensaje());
            
            Document docAcceso = new Document()
                .append("usuario", usuario)
                .append("rol", rol)
                .append("fechaHora", acceso.getFechaHora())
                .append("tipoAcceso", acceso.getTipoAcceso())
                .append("ip", acceso.getIp())
                .append("mensaje", mensaje);
            
            coleccionAccesos.insertOne(docAcceso);
            logger.info("Registro de acceso guardado: " + usuario + " - " + acceso.getTipoAcceso());
        } catch (Exception e) {
<<<<<<< HEAD
            logger.error("❌ Error al registrar acceso: " + e.getMessage());
=======
            System.err.println("Error al registrar acceso: " + e.getMessage());
>>>>>>> origin/Test
        }
    }

    // Añadir método:
    private String sanitizarTexto(String texto) {
        if (texto == null) return "";
        return texto.replaceAll("[<>\"'&;]", "");
    }

    public List<AccesoSistema> obtenerTodosAccesos() {
        List<AccesoSistema> accesos = new ArrayList<>();
        try (MongoCursor<Document> cursor = coleccionAccesos.find().sort(Sorts.descending("fechaHora")).iterator()) {
            while (cursor.hasNext()) {
                accesos.add(convertirDocumentAAcceso(cursor.next()));
            }
<<<<<<< HEAD
            logger.info("✅ Historial de accesos obtenido: " + accesos.size() + " registros");
        } catch (Exception e) {
            logger.error("❌ Error al obtener historial de accesos: " + e.getMessage());
=======
            System.out.println("Historial de accesos obtenido: " + accesos.size() + " registros");
        } catch (Exception e) {
            System.err.println("Error al obtener historial de accesos: " + e.getMessage());
            e.printStackTrace();
>>>>>>> origin/Test
        }
        return accesos;
    }
    
    public List<AccesoSistema> obtenerAccesosPorUsuario(String usuario) {
        List<AccesoSistema> accesos = new ArrayList<>();
        try (MongoCursor<Document> cursor = coleccionAccesos.find(Filters.eq("usuario", usuario))
                                                           .sort(Sorts.descending("fechaHora"))
                                                           .iterator()) {
            while (cursor.hasNext()) {
                Document doc = cursor.next();
                accesos.add(convertirDocumentAAcceso(doc));
            }
        } catch (Exception e) {
<<<<<<< HEAD
            logger.error("❌ Error al obtener accesos por usuario: " + e.getMessage());
=======
            System.err.println("Error al obtener accesos por usuario: " + e.getMessage());
>>>>>>> origin/Test
        }
        return accesos;
    }
    
    public List<AccesoSistema> obtenerAccesosExitosos() {
        List<AccesoSistema> accesos = new ArrayList<>();
        try (MongoCursor<Document> cursor = coleccionAccesos.find(Filters.eq("tipoAcceso", "EXITOSO"))
                                                           .sort(Sorts.descending("fechaHora"))
                                                           .iterator()) {
            while (cursor.hasNext()) {
                Document doc = cursor.next();
                accesos.add(convertirDocumentAAcceso(doc));
            }
        } catch (Exception e) {
<<<<<<< HEAD
            logger.error("❌ Error al obtener accesos exitosos: " + e.getMessage());
=======
            System.err.println("Error al obtener accesos exitosos: " + e.getMessage());
>>>>>>> origin/Test
        }
        return accesos;
    }
    
    public List<AccesoSistema> obtenerAccesosFallidos() {
        List<AccesoSistema> accesos = new ArrayList<>();
        try (MongoCursor<Document> cursor = coleccionAccesos.find(Filters.eq("tipoAcceso", "FALLIDO"))
                                                           .sort(Sorts.descending("fechaHora"))
                                                           .iterator()) {
            while (cursor.hasNext()) {
                Document doc = cursor.next();
                accesos.add(convertirDocumentAAcceso(doc));
            }
        } catch (Exception e) {
<<<<<<< HEAD
            logger.error("❌ Error al obtener accesos fallidos: " + e.getMessage());
=======
            System.err.println("Error al obtener accesos fallidos: " + e.getMessage());
>>>>>>> origin/Test
        }
        return accesos;
    }
    
    public List<AccesoSistema> obtenerAccesosPorFecha(String fecha) {
        List<AccesoSistema> accesos = new ArrayList<>();
        
        // Validar formato de fecha (yyyy-MM-dd)
        if (fecha == null || !fecha.matches("\\d{4}-\\d{2}-\\d{2}")) {
            logger.error("❌ Formato de fecha inválido: " + fecha);
            return accesos;
        }
        
        try (MongoCursor<Document> cursor = coleccionAccesos.find(Filters.regex("fechaHora", "^" + fecha))
                                                           .sort(Sorts.descending("fechaHora"))
                                                           .iterator()) {
            while (cursor.hasNext()) {
                Document doc = cursor.next();
                accesos.add(convertirDocumentAAcceso(doc));
            }
        } catch (Exception e) {
<<<<<<< HEAD
            logger.error("❌ Error al obtener accesos por fecha: " + e.getMessage());
=======
            System.err.println("Error al obtener accesos por fecha: " + e.getMessage());
>>>>>>> origin/Test
        }
        return accesos;
    }
    
    public long contarAccesosTotales() {
        try {
            return coleccionAccesos.countDocuments();
        } catch (Exception e) {
<<<<<<< HEAD
            logger.error("❌ Error al contar accesos: " + e.getMessage());
=======
            System.err.println("Error al contar accesos: " + e.getMessage());
>>>>>>> origin/Test
            return 0;
        }
    }
    
    public long contarAccesosExitosos() {
        try {
            return coleccionAccesos.countDocuments(Filters.eq("tipoAcceso", "EXITOSO"));
        } catch (Exception e) {
<<<<<<< HEAD
            logger.error("❌ Error al contar accesos exitosos: " + e.getMessage());
=======
            System.err.println("Error al contar accesos exitosos: " + e.getMessage());
>>>>>>> origin/Test
            return 0;
        }
    }
    
    public long contarAccesosFallidos() {
        try {
            return coleccionAccesos.countDocuments(Filters.eq("tipoAcceso", "FALLIDO"));
        } catch (Exception e) {
<<<<<<< HEAD
            logger.error("❌ Error al contar accesos fallidos: " + e.getMessage());
=======
            System.err.println("Error al contar accesos fallidos: " + e.getMessage());
>>>>>>> origin/Test
            return 0;
        }
    }
    
    private AccesoSistema convertirDocumentAAcceso(Document doc) {
        return new AccesoSistema(
            doc.getString("usuario"),
            doc.getString("rol"),
            doc.getString("fechaHora"),
            doc.getString("tipoAcceso"),
            doc.getString("ip"),
            doc.getString("mensaje")
        );
    }
    
    // Método para limpiar registros antiguos
    public boolean eliminarRegistrosAntiguos(int dias) {
        try {
            return true;
        } catch (Exception e) {
<<<<<<< HEAD
            logger.error("❌ Error al eliminar registros antiguos: " + e.getMessage());
=======
            System.err.println("Error al eliminar registros antiguos: " + e.getMessage());
>>>>>>> origin/Test
            return false;
        }
    }
}