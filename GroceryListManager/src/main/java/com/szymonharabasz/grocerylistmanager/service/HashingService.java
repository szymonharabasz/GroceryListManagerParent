package com.szymonharabasz.grocerylistmanager.service;

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
    private static final SecureRandom random = new SecureRandom();
    private static SecretKeyFactory factory;

    @Inject
    public HashingService(SaltRepository saltRepository) {
        this.saltRepository = saltRepository;
    }

    public HashingService() { this(null); }

    static {
        try {
            factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

    public void save(Salt salt) { saltRepository.save(salt); }
    public Optional<Salt> findSaltByUserId(String userId) {
        return saltRepository.findById(userId);
    }
    public static byte[] createSalt() {
        byte[] salt = new byte[16];
        random.nextBytes(salt);
        return salt;
    }
    public static String createHash(String string, byte[] salt) {
        KeySpec spec = new PBEKeySpec(string.toCharArray(), salt, 65536, 128);
        String hash = null;
        try {
            hash = new String(factory.generateSecret(spec).getEncoded(), StandardCharsets.UTF_8);
        } catch (InvalidKeySpecException e) {
            e.printStackTrace();
        }
        return hash;
    }
    public static String createHash(String string, Salt salt) {
        return createHash(string, salt.getSalt());
    }
}
