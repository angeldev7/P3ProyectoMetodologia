// DAO/ProductoDAO.java
package DAO;

import com.mongodb.client.MongoCollection;
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
            Document docProducto = new Document()
                .append("codigo", producto.getCodigo())
                .append("nombre", producto.getNombre())
                .append("descripcion", producto.getDescripcion())
                .append("stock", producto.getStock())
                .append("precio", producto.getPrecio())
                .append("stockMinimo", producto.getStockMinimo());
            
            coleccionProductos.insertOne(docProducto);
            logger.info("Producto guardado: " + producto.getCodigo());
        } catch (Exception e) {
            logger.error("Error al guardar producto: " + e.getMessage());
        }
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
            );
            
            coleccionProductos.updateOne(filtro, actualizacion);
            logger.info("Producto actualizado: " + producto.getCodigo());
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
        try {
            for (Document doc : coleccionProductos.find()) {
                productos.add(convertirDocumentAProducto(doc));
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
            for (Document doc : coleccionProductos.find(filtro)) {
                productos.add(convertirDocumentAProducto(doc));
            }
            logger.info("✅ Productos con stock obtenidos: " + productos.size());
        } catch (Exception e) {
            logger.error("❌ Error al obtener productos con stock: " + e.getMessage());
         }
        return productos;
    }
    
    public void actualizarStock(String codigo, int nuevoStock) {
        try {
            Bson filtro = Filters.eq("codigo", codigo);
            Document actualizacion = new Document("$set", 
                new Document("stock", nuevoStock));
            
            coleccionProductos.updateOne(filtro, actualizacion);
            logger.info("✅ Stock actualizado en MongoDB: " + codigo + " -> " + nuevoStock);
        } catch (Exception e) {
            logger.error("❌ Error al actualizar stock en MongoDB: " + e.getMessage());
        }
    }
    
    public boolean existeProducto(String codigo) {
        try {
            Bson filtro = Filters.eq("codigo", codigo);
            return coleccionProductos.countDocuments(filtro) > 0;
        } catch (Exception e) {
            logger.error("❌ Error al verificar existencia de producto: " + e.getMessage());
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
            doc.getInteger("stockMinimo")
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
}