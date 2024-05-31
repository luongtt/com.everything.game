package com.everything.game;

import com.everything.game.Message;
import java.io.DataInputStream;

/*
 * interface ICrypto
 * encrypt/decrypt packet
 */
public interface ICrypto {
    public Message decrypt(DataInputStream dis);
    public byte[] encrypt(Message m);
    public void refresh();
}