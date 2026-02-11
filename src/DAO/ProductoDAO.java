// DAO/ProductoDAO.java
package DAO;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import com.mongodb.client.model.IndexOptions;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import model.Producto;
import Database.ConexionBaseDatos;
import java.util.ArrayList;
import java.util.List;

public class ProductoDAO {
    private MongoCollection<Document> coleccionProductos;
    private static final Logger logger = LoggerFactory.getLogger(ProductoDAO.class);
    
    // Constructor para testing con inyección de dependencias
    public ProductoDAO(MongoCollection<Document> coleccion) {
        this.coleccionProductos = coleccion;
    }
    
    public ProductoDAO() {
        this.coleccionProductos = ConexionBaseDatos.getColeccion("productos");
        crearIndices();
    }
    
    private void crearIndices() {
        try {
            Document index = new Document("codigo", 1);
            IndexOptions indexOptions = new IndexOptions().unique(true);
            coleccionProductos.createIndex(index, indexOptions);
        } catch (Exception e) {
            // El índice ya existe o error de conexión
        }
    }
    
    public void guardarProducto(Producto producto) {
        try {
            // CORREGIR: Validar datos antes de insertar
            String codigo = sanitizarCodigo(producto.getCodigo());
            String nombre = sanitizarTexto(producto.getNombre());
            String descripcion = sanitizarTexto(producto.getDescripcion());
            
            Document docProducto = new Document()
                .append("codigo", codigo)
                .append("nombre", nombre)
                .append("descripcion", descripcion)
                .append("stock", producto.getStock())
                .append("precio", producto.getPrecio())
                .append("stockMinimo", producto.getStockMinimo())
                // NUEVO: Agregar campos de ubicación
                .append("pasillo", producto.getPasillo())
                .append("estante", producto.getEstante())
                .append("posicion", producto.getPosicion());
            
            coleccionProductos.insertOne(docProducto);
<<<<<<< HEAD
            logger.info("Producto guardado: " + codigo);
=======
            System.out.println("Producto guardado: " + producto.getCodigo() + " - Ubicación: " + producto.getUbicacionCompleta());
>>>>>>> origin/Test
        } catch (Exception e) {
            logger.error("Error al guardar producto: " + e.getMessage());
        }
    }
    
    private String sanitizarCodigo(String codigo) {
        return codigo.replaceAll("[^a-zA-Z0-9-_]", "");
    }

    private String sanitizarTexto(String texto) {
        if (texto == null) return "";
        return texto.replaceAll("[<>\"'&;]", "");
    }
    
    public void actualizarProducto(Producto producto) {
        try {
            Bson filtro = Filters.eq("codigo", producto.getCodigo());
            
            Document actualizacion = new Document("$set", 
                new Document()
                    .append("nombre", producto.getNombre())
                    .append("descripcion", producto.getDescripcion())
                    .append("stock", producto.getStock())
                    .append("precio", producto.getPrecio())
                    .append("stockMinimo", producto.getStockMinimo())
                    // NUEVO: Agregar campos de ubicación
                    .append("pasillo", producto.getPasillo())
                    .append("estante", producto.getEstante())
                    .append("posicion", producto.getPosicion())
            );
            
            coleccionProductos.updateOne(filtro, actualizacion);
<<<<<<< HEAD
            logger.info("Producto actualizado: " + producto.getCodigo());
=======
            System.out.println("Producto actualizado: " + producto.getCodigo() + " - Ubicación: " + producto.getUbicacionCompleta());
>>>>>>> origin/Test
        } catch (Exception e) {
            logger.error("Error al actualizar producto: " + e.getMessage());
        }
    }
    
    public void eliminarProducto(String codigo) {
        try {
            Bson filtro = Filters.eq("codigo", codigo);
            coleccionProductos.deleteOne(filtro);
            logger.info("Producto eliminado: " + codigo);
        } catch (Exception e) {
            logger.error("Error al eliminar producto: " + e.getMessage());
        }
    }
    
    public Producto buscarProductoPorCodigo(String codigo) {
        try {
            Bson filtro = Filters.eq("codigo", codigo);
            Document doc = coleccionProductos.find(filtro).first();
            
            if (doc != null) {
                return convertirDocumentAProducto(doc);
            }
        } catch (Exception e) {
            logger.error("Error al buscar producto: " + e.getMessage());
        }
        return null;
    }
    
    public List<Producto> obtenerTodosProductos() {
        List<Producto> productos = new ArrayList<>();
        try (MongoCursor<Document> cursor = coleccionProductos.find().iterator()) {
            while (cursor.hasNext()) {
                productos.add(convertirDocumentAProducto(cursor.next()));
            }
            logger.info("Productos obtenidos: " + productos.size());
        } catch (Exception e) {
            logger.error("Error al obtener productos: " + e.getMessage());
        }
        return productos;
    }
    
    public List<Producto> obtenerProductosConStock() {
        List<Producto> productos = new ArrayList<>();
        try {
            Bson filtro = Filters.gt("stock", 0);
            try (MongoCursor<Document> cursor = coleccionProductos.find(filtro).iterator()) {
                while (cursor.hasNext()) {
                    productos.add(convertirDocumentAProducto(cursor.next()));
                }
                logger.info("✅ Productos con stock obtenidos: " + productos.size());
            }
<<<<<<< HEAD
        } catch (Exception e) {
            logger.error("❌ Error al obtener productos con stock: " + e.getMessage());
=======
            System.out.println("Productos con stock obtenidos: " + productos.size());
        } catch (Exception e) {
            System.err.println("Error al obtener productos con stock: " + e.getMessage());
            e.printStackTrace();
>>>>>>> origin/Test
        }
        return productos;
    }
    
    public void actualizarStock(String codigo, int nuevoStock) {
        try {
            Bson filtro = Filters.eq("codigo", codigo);
            Document actualizacion = new Document("$set", 
                new Document("stock", nuevoStock));
            
            coleccionProductos.updateOne(filtro, actualizacion);
<<<<<<< HEAD
            logger.info("✅ Stock actualizado en MongoDB: " + codigo + " -> " + nuevoStock);
        } catch (Exception e) {
            logger.error("❌ Error al actualizar stock en MongoDB: " + e.getMessage());
=======
            System.out.println("Stock actualizado en MongoDB: " + codigo + " -> " + nuevoStock);
        } catch (Exception e) {
            System.err.println("Error al actualizar stock en MongoDB: " + e.getMessage());
            e.printStackTrace();
>>>>>>> origin/Test
        }
    }
    
    public boolean existeProducto(String codigo) {
        try {
            Bson filtro = Filters.eq("codigo", codigo);
            return coleccionProductos.countDocuments(filtro) > 0;
        } catch (Exception e) {
<<<<<<< HEAD
            logger.error("❌ Error al verificar existencia de producto: " + e.getMessage());
=======
            System.err.println("Error al verificar existencia de producto: " + e.getMessage());
            e.printStackTrace();
>>>>>>> origin/Test
            return false;
        }
    }
    
    public long contarProductos() {
        try {
            return coleccionProductos.countDocuments();
        } catch (Exception e) {
            logger.error("❌ Error al contar productos: " + e.getMessage());
            return 0;
        }
    }
    
    private Producto convertirDocumentAProducto(Document doc) {
        return new Producto(
            doc.getString("codigo"),
            doc.getString("nombre"),
            doc.getString("descripcion"),
            doc.getInteger("stock"),
            doc.getDouble("precio"),
            doc.getInteger("stockMinimo"),
            // NUEVO: Campos de ubicación (con valores por defecto si no existen)
            doc.getString("pasillo") != null ? doc.getString("pasillo") : "",
            doc.getString("estante") != null ? doc.getString("estante") : "",
            doc.getString("posicion") != null ? doc.getString("posicion") : ""
        );
    }
    
    // Método para verificar la conexión
    public boolean verificarConexion() {
        try {
            coleccionProductos.countDocuments();
            return true;
        } catch (Exception e) {
            return false;
        }
    }
    
    // NUEVO: Métodos para búsqueda por ubicación
    public List<Producto> buscarProductosPorUbicacion(String pasillo, String estante, String posicion) {
        List<Producto> productosEncontrados = new ArrayList<>();
        
        try {
            // Construir filtro dinámico
            List<org.bson.conversions.Bson> filtros = new ArrayList<>();
            
            if (pasillo != null && !pasillo.trim().isEmpty()) {
                filtros.add(Filters.regex("pasillo", ".*" + pasillo.trim() + ".*", "i"));
            }
            if (estante != null && !estante.trim().isEmpty()) {
                filtros.add(Filters.regex("estante", ".*" + estante.trim() + ".*", "i"));
            }
            if (posicion != null && !posicion.trim().isEmpty()) {
                filtros.add(Filters.regex("posicion", ".*" + posicion.trim() + ".*", "i"));
            }
            
            org.bson.conversions.Bson filtroFinal;
            if (filtros.isEmpty()) {
                // Si no hay filtros, buscar todos
                filtroFinal = new org.bson.Document();
            } else if (filtros.size() == 1) {
                filtroFinal = filtros.get(0);
            } else {
                filtroFinal = Filters.and(filtros);
            }
            
            for (org.bson.Document doc : coleccionProductos.find(filtroFinal)) {
                productosEncontrados.add(convertirDocumentAProducto(doc));
            }
            
        } catch (Exception e) {
            System.err.println("❌ Error al buscar productos por ubicación: " + e.getMessage());
            e.printStackTrace();
        }
        
        return productosEncontrados;
    }
    
    // NUEVO: Método para obtener productos sin ubicación
    public List<Producto> obtenerProductosSinUbicacion() {
        List<Producto> productosSinUbicacion = new ArrayList<>();
        
        try {
            // Buscar productos donde todos los campos de ubicación están vacíos o nulos
            org.bson.conversions.Bson filtro = Filters.or(
                Filters.and(
                    Filters.or(Filters.eq("pasillo", ""), Filters.eq("pasillo", null)),
                    Filters.or(Filters.eq("estante", ""), Filters.eq("estante", null)),
                    Filters.or(Filters.eq("posicion", ""), Filters.eq("posicion", null))
                ),
                Filters.and(
                    Filters.exists("pasillo", false),
                    Filters.exists("estante", false),
                    Filters.exists("posicion", false)
                )
            );
            
            for (org.bson.Document doc : coleccionProductos.find(filtro)) {
                productosSinUbicacion.add(convertirDocumentAProducto(doc));
            }
            
        } catch (Exception e) {
            System.err.println("❌ Error al obtener productos sin ubicación: " + e.getMessage());
            e.printStackTrace();
        }
        
        return productosSinUbicacion;
    }
}