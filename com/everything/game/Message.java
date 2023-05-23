package com.everything.game;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
/*
 * class Message
 * save packet send/receive
 */
public class Message {
    private ByteArrayOutputStream os;
    private DataOutputStream dos;
    private ByteArrayInputStream is;
    private DataInputStream dis;

    public Message() {
        // outcoming
        os = new ByteArrayOutputStream();
        dos = new DataOutputStream(os);
    }

    public Message(byte[] data) {
        // incoming
        is = new ByteArrayInputStream(data);
        dis = new DataInputStream(is);
    }

    public byte[] getData() {
    	return os.toByteArray();
    }

    public DataInputStream reader() {
        return dis;
    }

    public DataOutputStream writer() {
        return dos;
    }

    public void cleanup() {
        try {
            if (dis != null)
                dis.close();
            if (dos != null)
                dos.close();
        } catch (IOException e) {
        }
    }
}