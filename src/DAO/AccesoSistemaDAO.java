package DAO;

import Database.ConexionBaseDatos;
import model.AccesoSistema;
import com.mongodb.client.MongoCollection;
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
            logger.error("❌ Error al crear índices de accesos: " + e.getMessage());
        }
    }
    
    public void registrarAcceso(AccesoSistema acceso) {
        try {
            Document docAcceso = new Document()
                .append("usuario", acceso.getUsuario())
                .append("rol", acceso.getRol())
                .append("fechaHora", acceso.getFechaHora())
                .append("tipoAcceso", acceso.getTipoAcceso())
                .append("ip", acceso.getIp())
                .append("mensaje", acceso.getMensaje());
            
            coleccionAccesos.insertOne(docAcceso);
            logger.info("Registro de acceso guardado: " + acceso.getUsuario() + " - " + acceso.getTipoAcceso());
        } catch (Exception e) {
            logger.error("❌ Error al registrar acceso: " + e.getMessage());
        }
    }
    
    public List<AccesoSistema> obtenerTodosAccesos() {
        List<AccesoSistema> accesos = new ArrayList<>();
        try {
            for (Document doc : coleccionAccesos.find().sort(Sorts.descending("fechaHora"))) {
                accesos.add(convertirDocumentAAcceso(doc));
            }
            logger.info("✅ Historial de accesos obtenido: " + accesos.size() + " registros");
        } catch (Exception e) {
            logger.error("❌ Error al obtener historial de accesos: " + e.getMessage());
            e.printStackTrace();
        }
        return accesos;
    }
    
    public List<AccesoSistema> obtenerAccesosPorUsuario(String usuario) {
        List<AccesoSistema> accesos = new ArrayList<>();
        try {
            for (Document doc : coleccionAccesos.find(Filters.eq("usuario", usuario))
                                               .sort(Sorts.descending("fechaHora"))) {
                accesos.add(convertirDocumentAAcceso(doc));
            }
        } catch (Exception e) {
            logger.error("❌ Error al obtener accesos por usuario: " + e.getMessage());
        }
        return accesos;
    }
    
    public List<AccesoSistema> obtenerAccesosExitosos() {
        List<AccesoSistema> accesos = new ArrayList<>();
        try {
            for (Document doc : coleccionAccesos.find(Filters.eq("tipoAcceso", "EXITOSO"))
                                               .sort(Sorts.descending("fechaHora"))) {
                accesos.add(convertirDocumentAAcceso(doc));
            }
        } catch (Exception e) {
            logger.error("❌ Error al obtener accesos exitosos: " + e.getMessage());
        }
        return accesos;
    }
    
    public List<AccesoSistema> obtenerAccesosFallidos() {
        List<AccesoSistema> accesos = new ArrayList<>();
        try {
            for (Document doc : coleccionAccesos.find(Filters.eq("tipoAcceso", "FALLIDO"))
                                               .sort(Sorts.descending("fechaHora"))) {
                accesos.add(convertirDocumentAAcceso(doc));
            }
        } catch (Exception e) {
            logger.error("❌ Error al obtener accesos fallidos: " + e.getMessage());
        }
        return accesos;
    }
    
    public List<AccesoSistema> obtenerAccesosPorFecha(String fecha) {
        List<AccesoSistema> accesos = new ArrayList<>();
        try {
            // Buscar accesos que comiencen con la fecha especificada
            for (Document doc : coleccionAccesos.find(Filters.regex("fechaHora", "^" + fecha))
                                               .sort(Sorts.descending("fechaHora"))) {
                accesos.add(convertirDocumentAAcceso(doc));
            }
        } catch (Exception e) {
            logger.error("❌ Error al obtener accesos por fecha: " + e.getMessage());
        }
        return accesos;
    }
    
    public long contarAccesosTotales() {
        try {
            return coleccionAccesos.countDocuments();
        } catch (Exception e) {
            logger.error("❌ Error al contar accesos: " + e.getMessage());
            return 0;
        }
    }
    
    public long contarAccesosExitosos() {
        try {
            return coleccionAccesos.countDocuments(Filters.eq("tipoAcceso", "EXITOSO"));
        } catch (Exception e) {
            logger.error("❌ Error al contar accesos exitosos: " + e.getMessage());
            return 0;
        }
    }
    
    public long contarAccesosFallidos() {
        try {
            return coleccionAccesos.countDocuments(Filters.eq("tipoAcceso", "FALLIDO"));
        } catch (Exception e) {
            logger.error("❌ Error al contar accesos fallidos: " + e.getMessage());
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
    
    // Método para limpiar registros antiguos (opcional)
    public boolean eliminarRegistrosAntiguos(int dias) {
        try {
            // Implementar lógica para eliminar registros más antiguos que X días
            // Esto es para mantenimiento de la base de datos
            return true;
        } catch (Exception e) {
            logger.error("❌ Error al eliminar registros antiguos: " + e.getMessage());
            return false;
        }
    }
}