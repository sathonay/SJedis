package com.sjedis.common.util;

import java.io.IOException;
import java.io.Serializable;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.Arrays;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SealedObject;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

//ref https://www.baeldung.com/java-aes-encryption-decryption

public class AESUtil {
    
    public static SecretKey getKeyFromPassword(String password)
        throws NoSuchAlgorithmException, InvalidKeySpecException {
        
        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
        KeySpec spec = new PBEKeySpec(password.toCharArray(), password.getBytes(), 65536, 256);
        SecretKey secret = new SecretKeySpec(factory.generateSecret(spec)
            .getEncoded(), "AES");
        return secret;
    }

    public static SealedObject encryptObject(String algorithm, Serializable object,
        SecretKey key) throws NoSuchPaddingException,
        NoSuchAlgorithmException, InvalidAlgorithmParameterException, 
        InvalidKeyException, IOException, IllegalBlockSizeException {
        
        Cipher cipher = Cipher.getInstance(algorithm);
        cipher.init(Cipher.ENCRYPT_MODE, key, generateIv(key.getEncoded()));
        SealedObject sealedObject = new SealedObject(object, cipher);
        return sealedObject;
    }

public static IvParameterSpec generateIv() {
    byte[] iv = new byte[16];
    new SecureRandom().nextBytes(iv);
    return new IvParameterSpec(iv);
}

public static IvParameterSpec generateIv(byte[] b) {
    if (b.length > 16)
        b = Arrays.copyOf(b, 16);
    return new IvParameterSpec(b);
}

    public static Serializable decryptObject(String algorithm, SealedObject sealedObject,
        SecretKey key) throws NoSuchPaddingException,
        NoSuchAlgorithmException, InvalidAlgorithmParameterException,
        ClassNotFoundException, BadPaddingException, IllegalBlockSizeException,
        IOException, InvalidKeyException {
        
        Cipher cipher = Cipher.getInstance(algorithm);
        cipher.init(Cipher.DECRYPT_MODE, key, generateIv(key.getEncoded()));
        Serializable unsealObject = (Serializable) sealedObject.getObject(cipher);
        return unsealObject;
    }
}
