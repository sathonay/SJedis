package com.sjedis.common.connection.implementations;

import com.sjedis.common.connection.AESConnection;
import com.sjedis.common.packet.handler.PacketHandlers;
import com.sjedis.common.util.AESUtil;

import javax.crypto.*;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.Socket;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

public class AESPacketConnection extends PacketConnection implements AESConnection {

    private final SecretKey secretKey;

    public AESPacketConnection(Socket socket, String password, PacketHandlers packetHandlers) {
        super(socket, packetHandlers);
        try {
            secretKey = AESUtil.getKeyFromPassword(password);
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public SecretKey getAESKey() {
        return secretKey;
    }
}
