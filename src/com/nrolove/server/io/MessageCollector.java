package com.nrolove.server.io;

import com.nrolove.server.Client;
import com.nrolove.server.Maintenance;
import com.nrolove.server.ServerManager;
import com.nrolove.utils.Logger;
import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;

/**
 *
 * @author 💖 Trần Lại 💖
 * @copyright 💖 GirlkuN 💖
 *
 */
public class MessageCollector implements Runnable {

    private DataInputStream dis;
    private Session session;

    public MessageCollector(Session session, Socket socket) {
        try {
            this.session = session;
            this.dis = new DataInputStream(socket.getInputStream());
        } catch (Exception e) {
        }
    }

    @Override
    public void run() {
        try {
            while (true) {
                if (!Maintenance.isRuning) {
                    Message msg = readMessage();
                    session.lastTimeReadMessage = System.currentTimeMillis();
                    session.controller.onMessage(session, msg);
                    msg.cleanup();
                }
                Thread.sleep(1);
            }
        } catch (Exception ex) {
        }
        if (!Maintenance.isRuning) {
            Client.gI().kickSession(session);
        }
    }

    private Message readMessage() throws Exception {
        long st = System.currentTimeMillis();
        byte cmd = dis.readByte();
        if (session.connected) {
            cmd = readKey(cmd);
        }
        int size;
        if (session.connected) {
            byte b1 = dis.readByte();
            byte b2 = dis.readByte();
            size = (readKey(b1) & 255) << 8 | readKey(b2) & 255;
        } else {
            size = dis.readUnsignedShort();
        }
        byte data[] = new byte[size];
        int len = 0;
        int byteRead = 0;
        while (len != -1 && byteRead < size) {
            len = dis.read(data, byteRead, size - byteRead);
            if (len > 0) {
                byteRead += len;
            }
        }
        if (session.connected) {
            for (int i = 0; i < data.length; i++) {
                data[i] = readKey(data[i]);
            }
        }
        if (session.logCheck) {
            System.out.println("Time read message: " + (System.currentTimeMillis() - st) + " ms");
        }
        return new Message(cmd, data);
    }

    private byte readKey(byte b) {
        byte i = (byte) ((Session.KEYS[session.curR++] & 255) ^ (b & 255));
        if (session.curR >= Session.KEYS.length) {
            session.curR %= Session.KEYS.length;
        }
        return i;
    }

    void close() throws IOException {
        if (this.dis != null) {
            this.dis.close();
        }
        this.dis = null;
        this.session = null;
    }
}
