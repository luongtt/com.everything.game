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
    private byte command;

    public Message(int command) {
        this((byte)command);
    }

    public Message(byte command) {
        // outcoming
        os = new ByteArrayOutputStream();
        dos = new DataOutputStream(os);
        this.command = command;
    }

    public Message(byte command, byte[] data) {
        // incoming
        is = new ByteArrayInputStream(data);
        dis = new DataInputStream(is);
        this.command = command;
    }

    public byte getCommand() {
        return command;
    }

    public void setCommand(int cmd) {
        setCommand((byte)cmd);
    }
    
    public void setCommand(byte cmd) {
        command = cmd;
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