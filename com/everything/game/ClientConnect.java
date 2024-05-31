package com.everything.game;

import java.net.Socket;
import java.util.ArrayList;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public abstract class ClientConnect {
    public Socket sc;
    public DataInputStream dis;
    public DataOutputStream dos;
    public int id;
    protected boolean connected;
    private IMessageListener messageListenerHandle;
    private Thread msgListenThread;
    protected Thread msgSendThread;
    protected MessageSender msgSender;
    protected ICrypto crypto;

    protected class MessageSender implements Runnable {
        private final ArrayList<Message> sendingMessage;

        public MessageSender() {
            sendingMessage = new ArrayList<>();
        }

        public void AddMessage(Message message) {
            sendingMessage.add(message);
        }

        @Override
        public void run() {
            try {
                while (isConnected()) {
                    while (sendingMessage.size() > 0) {
                        Message m = sendingMessage.get(0);
                        doSendMessage(m);
                        sendingMessage.remove(0);
                    }
                    try {
                        Thread.sleep(10);
                    } catch (InterruptedException e) {}
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

       private void doSendMessage(Message m) throws IOException {
            byte msg[] = crypto.encrypt(m);
            dos.write(msg);
            dos.flush();
            m.cleanup();
       }
    }

    protected class MessageListener implements Runnable {
        @Override
        public void run() {
            try {
                while (true) {
                    Message message = doGetMessage();
                    if (message != null) {
                        messageListenerHandle.onMessage(message);
                        message.cleanup();
                    } else
                        break;
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            if (isConnected()) {
                if (messageListenerHandle != null) {
                    messageListenerHandle.onDisconnected();
                }
            }
            close();
        }

        private Message doGetMessage() {
            return crypto.decrypt(dis);
        }
    }

    public ClientConnect(Socket sc, int id, ICrypto crypto) throws IOException {
        this.sc = sc;
        this.id = id;
        this.crypto = crypto;
        this.dis = new DataInputStream(sc.getInputStream());
        this.dos = new DataOutputStream(sc.getOutputStream());
        msgListenThread = new Thread(new MessageListener());
        msgListenThread.start();
        msgSendThread = new Thread(msgSender = new MessageSender());
    }

    public boolean isConnected() {
        return connected;
    }

    public void setConnected(boolean connected) {
        this.connected = connected;
    }

    public void setMessageListenerHandle(IMessageListener messageListener) {
        this.messageListenerHandle = messageListener;
    }

    public void sendMessage(Message msg) {
        this.msgSender.AddMessage(msg);
    }

    public void startSender() {
        this.msgSendThread.start();
    }

    public void close() {
        cleanNetwork();
    }

    private void cleanNetwork() {
        try {
            connected = false;
            if (sc != null) {
                sc.close();
                sc = null;
            }
            if (dos != null) {
                dos.close();
                dos = null;
            }
            if (dis != null) {
                dis.close();
                dis = null;
            }
            crypto = null;
            msgSendThread = null;
            msgListenThread = null;
            System.gc();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void closeMessage() {
        if (isConnected()) {
            if (this.messageListenerHandle != null)
                this.messageListenerHandle.onDisconnected();
            close();
        }
    }
}