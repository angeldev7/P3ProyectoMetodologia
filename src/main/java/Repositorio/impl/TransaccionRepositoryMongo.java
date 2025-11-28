package Repositorio.impl;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Sorts;
import com.mongodb.client.model.Updates;

import Modelo.*;
import Persistencia.IdGenerator;
import Persistencia.MongoConnection;
import Repositorio.TransaccionRepository;

import org.bson.Document;
import org.bson.conversions.Bson;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class TransaccionRepositoryMongo implements TransaccionRepository {
    private final MongoCollection<Document> collection;
    private final IdGenerator idGenerator;
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public TransaccionRepositoryMongo() {
        MongoDatabase database = MongoConnection.getInstancia().getDatabase();
        this.collection = database.getCollection("transacciones");
        this.idGenerator = new IdGenerator(database);
    }

    @Override
    public Factura saveFactura(Factura factura) {
        Document doc = new Document("idTransaccion", factura.getIdTransaccion())
                .append("fecha", factura.getFecha().toString())
                .append("proveedorCliente", factura.getProveedorCliente())
                .append("subtotal", factura.getMonto())
                .append("cuentaContable", factura.getCuentaContable())
                .append("numeroFactura", factura.getNumeroDocumento());
        collection.insertOne(doc);
        return factura;
    }

    @Override
    public Gasto saveGasto(Gasto gasto) {
        String idTransaccion = idGenerator.nextTransaccionId();
        
        Document doc = new Document("idTransaccion", idTransaccion)
                .append("tipoTransaccion", "Gasto")
                .append("fecha", gasto.getFecha().format(FORMATTER))
                .append("tipoDocumento", gasto.getTipoDocumento())
                .append("proveedorCliente", gasto.getProveedorCliente())
                .append("monto", gasto.getMonto())
                .append("cuentaContable", gasto.getCuentaContable())
                .append("numeroDocumento", gasto.getNumeroDocumento())
                .append("numeroComprobante", gasto.getNumeroComprobante())
                .append("deducible", gasto.isDeducible())
                .append("ivaCompra", gasto.getIvaCompra())
                .append("estado", gasto.getEstado())
                .append("idUsuarioRegistro", gasto.getUsuarioRegistro().getIdUsuario())
                .append("eliminado", false);

        collection.insertOne(doc);
        
        return new Gasto(idTransaccion, gasto.getFecha(), gasto.getProveedorCliente(),
                gasto.getMonto(), gasto.getCuentaContable(), gasto.getNumeroComprobante(),
                gasto.isDeducible(), gasto.getUsuarioRegistro());
    }

    @Override
    public boolean updateEstado(String idTransaccion, String nuevoEstado) {
        Bson filter = Filters.eq("idTransaccion", idTransaccion);
        Bson update = Updates.set("estado", nuevoEstado);
        return collection.updateOne(filter, update).getModifiedCount() > 0;
    }

    @Override
    public boolean eliminarLogico(String idTransaccion) {
        Bson filter = Filters.eq("idTransaccion", idTransaccion);
        Bson update = Updates.set("eliminado", true);
        return collection.updateOne(filter, update).getModifiedCount() > 0;
    }

    @Override
    public List<Transaccion> findAll() {
        List<Transaccion> transacciones = new ArrayList<>();
        for (Document doc : collection.find().sort(Sorts.descending("fecha"))) {
            Transaccion t = documentToTransaccion(doc);
            if (t != null) transacciones.add(t);
        }
        return transacciones;
    }

    @Override
    public List<Transaccion> findActive() {
        List<Transaccion> transacciones = new ArrayList<>();
        for (Document doc : collection.find(Filters.eq("eliminado", false))
                .sort(Sorts.descending("fecha"))) {
            Transaccion t = documentToTransaccion(doc);
            if (t != null) transacciones.add(t);
        }
        return transacciones;
    }

    @Override
    public long count() {
        return collection.countDocuments(Filters.eq("eliminado", false));
    }

    private Transaccion documentToTransaccion(Document doc) {
        try {
            String idUsuario = doc.getString("idUsuarioRegistro");
            Usuario usuario = new Usuario(idUsuario, idUsuario, "", "Usuario Sistema", "");
            
            String tipoTransaccion = doc.getString("tipoTransaccion");
            LocalDate fecha = LocalDate.parse(doc.getString("fecha"), FORMATTER);
            String proveedorCliente = doc.getString("proveedorCliente");
            String cuentaContable = doc.getString("cuentaContable");
            String numeroDoc = doc.getString("numeroDocumento");
            
            if ("Factura".equals(tipoTransaccion)) {
                double subtotal = doc.getDouble("subtotal");
                String numeroFactura = doc.getString("numeroFactura");
                return new Factura(doc.getString("idTransaccion"), fecha, proveedorCliente,
                        subtotal, cuentaContable, numeroFactura, usuario);
            } else if ("Gasto".equals(tipoTransaccion)) {
                double monto = doc.getDouble("monto");
                String numeroComprobante = doc.getString("numeroComprobante");
                boolean deducible = doc.getBoolean("deducible", false);
                return new Gasto(doc.getString("idTransaccion"), fecha, proveedorCliente,
                        monto, cuentaContable, numeroComprobante, deducible, usuario);
            }
            
            return null;
        } catch (Exception e) {
            System.err.println("Error al convertir documento a transacción: " + e.getMessage());
            return null;
        }
    }
}
