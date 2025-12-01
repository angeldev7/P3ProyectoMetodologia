package Persistencia;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

/**
 * Generador de IDs legibles usando colección counters en Mongo.
 * Documento ejemplo:
 * { _id: "ADM", seq: 3 }
 */
public class IdGenerator {
    private final MongoCollection<Document> counters;

    public IdGenerator(MongoDatabase db) {
        this.counters = db.getCollection("counters");
    }

    public String nextUsuarioId(String rol) {
        String prefix;
        if (rol.equalsIgnoreCase("Admin Master")) {
            prefix = "ADM";
        } else if (rol.equalsIgnoreCase("Jefatura Financiera")) {
            prefix = "JEF";
        } else {
            prefix = "ASIS";
        }
        long seq = nextSequence(prefix);
        return prefix + "-" + String.format("%03d", seq);
    }

    private long nextSequence(String key) {
        Document result = counters.findOneAndUpdate(
                new Document("_id", key),
                new Document("$inc", new Document("seq", 1)),
                new com.mongodb.client.model.FindOneAndUpdateOptions()
                        .upsert(true)
                        .returnDocument(com.mongodb.client.model.ReturnDocument.AFTER)
        );
        // Manejar tanto Integer como Long
        Object seqValue = result.get("seq");
        if (seqValue instanceof Integer) {
            return ((Integer) seqValue).longValue();
        } else if (seqValue instanceof Long) {
            return (Long) seqValue;
        } else {
            throw new IllegalStateException("Tipo inesperado para seq: " + seqValue.getClass());
        }
    }

    public String nextTransaccionId() {
        long seq = nextSequence("TRX");
        return "TRX-" + String.format("%06d", seq);
    }

    public String nextBitacoraId() {
        long seq = nextSequence("BIT");
        return "BIT-" + String.format("%06d", seq);
    }
    
    public String nextRolId() {
        long seq = nextSequence("ROL");
        return "ROL-" + String.format("%03d", seq);
    }
}
