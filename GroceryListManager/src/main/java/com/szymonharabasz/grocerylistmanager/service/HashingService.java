package com.szymonharabasz.grocerylistmanager.service;

import com.szymonharabasz.grocerylistmanager.Utils;
import com.szymonharabasz.grocerylistmanager.domain.Salt;
import com.szymonharabasz.grocerylistmanager.domain.SaltRepository;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.Optional;

@Named
@ApplicationScoped
public class HashingService {
    private final SaltRepository saltRepository;
    private final SecureRandom random = new SecureRandom();
    private SecretKeyFactory factory;

    @Inject
    public HashingService(SaltRepository saltRepository) {
        try {
            this.factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        this.saltRepository = saltRepository;
    }

    public HashingService() { this(null); }

    public void save(Salt salt) { saltRepository.save(salt); }
    public Optional<Salt> findSaltByUserId(String userId) {
        return saltRepository.findById(userId);
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
