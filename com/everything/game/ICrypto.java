package com.everything.game;

/*
 * interface ICrypto
 * encrypt/decrypt packet
 */
public interface ICrypto {
    public byte decrypt(byte b);
    public byte encrypt(byte b);
    public void refresh();
}