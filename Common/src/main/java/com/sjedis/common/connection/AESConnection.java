package com.sjedis.common.connection;

import com.sjedis.common.util.AESUtil;

import javax.crypto.*;
import java.io.IOException;
import java.io.Serializable;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

public interface AESConnection extends Connection {

    SecretKey getAESKey();

    @Override
    default Object readObject() throws Exception {
        try {
            return AESUtil.decryptObject("AES/CBC/PKCS5Padding" , (SealedObject) getInputStream().readObject(), getAESKey());
        } catch (IOException | ClassNotFoundException | InvalidKeyException | NoSuchPaddingException | NoSuchAlgorithmException
                 | InvalidAlgorithmParameterException | BadPaddingException | IllegalBlockSizeException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    default void writeObject(Object object) {
        try {
            getOutputStream().writeObject(AESUtil.encryptObject("AES/CBC/PKCS5Padding", (Serializable) object, getAESKey()));
            getOutputStream().flush();
        } catch (IOException | InvalidKeyException | NoSuchPaddingException | NoSuchAlgorithmException
                 | InvalidAlgorithmParameterException | IllegalBlockSizeException e) {
            e.printStackTrace();
        }
    }
}
