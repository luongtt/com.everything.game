package com.everything.game;

/*
 * interface IMessageListener
 * message listener handler, route each packet to handle 
 * implement by other class
 */
public interface IMessageListener {
    public void onMessage(Message message);
    public void onConnectionFail();
    public void onDisconnected();
    public void onConnectOK();
}