package com.szymonharabasz.grocerylistmanager.service;

import com.szymonharabasz.grocerylistmanager.Utils;
import com.szymonharabasz.grocerylistmanager.domain.Salt;
import com.szymonharabasz.grocerylistmanager.domain.SaltRepository;

import org.jnosql.artemis.Database;
import org.jnosql.artemis.DatabaseType;
import org.jnosql.diana.api.document.Document;
import org.jnosql.diana.api.document.DocumentCollectionManager;
import org.jnosql.diana.api.document.DocumentQuery;
import org.jnosql.diana.api.document.DocumentEntity;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.Map;
import java.util.Optional;
import org.bson.types.Binary;

import static org.jnosql.diana.api.document.query.DocumentQueryBuilder.select;

@ApplicationScoped
public class HashingService {
    @Inject
    @Database(DatabaseType.DOCUMENT)
    private SaltRepository saltRepository;
    @Inject
    DocumentCollectionManager collectionManager;
    private final SecureRandom random = new SecureRandom();
    private SecretKeyFactory factory;

    public HashingService(SaltRepository saltRepository) {
        try {
            this.factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        this.saltRepository = saltRepository;
    }

    public HashingService() { this(null); }

    public void save(Salt salt) {
        DocumentEntity documentEntity = DocumentEntity.of("Salt");
        documentEntity.add(Document.of("_id", salt.getUserId()));
        documentEntity.add(Document.of("salt", new Binary(salt.getSalt())));
        collectionManager.update(documentEntity);
       // saltRepository.save(salt); 
    }
    public Optional<Salt> findSaltByUserId(String userId) {
       // return saltRepository.findById(userId);
        DocumentQuery query = select().from("Salt").where("_id").eq(userId).build();
        return collectionManager.singleResult(query).map(entity -> {
            Map<String, Object> map = entity.toMap();
            Binary b = (Binary)map.get("salt");
            return new Salt((String)map.get("_id"), b.getData());
        });
    }
    public Salt createSalt() {
        byte[] saltBytes = new byte[16];
        random.nextBytes(saltBytes);
        return new Salt(Utils.generateID(), saltBytes);
    }
    public String createHash(String string, byte[] salt) {
        KeySpec spec = new PBEKeySpec(string.toCharArray(), salt, 65536, 128);
        String hash = null;
        try {
            hash = new String(factory.generateSecret(spec).getEncoded(), StandardCharsets.UTF_8);
        } catch (InvalidKeySpecException e) {
            e.printStackTrace();
        }
        return hash;
    }
    public String createHash(String string, Salt salt) {
        return createHash(string, salt.getSalt());
    }
}
