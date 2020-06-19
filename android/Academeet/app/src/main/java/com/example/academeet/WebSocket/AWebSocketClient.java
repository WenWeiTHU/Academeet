package com.example.academeet.WebSocket;

import android.util.Log;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.drafts.Draft_6455;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;

public class AWebSocketClient extends WebSocketClient {
    public AWebSocketClient(URI serverUri) {
        super(serverUri, new Draft_6455());
    }

    @Override
    public void onOpen(ServerHandshake handshakedata) {
        Log.i("AWebSocketClient", "onOpen()");
    }

    @Override
    public void onMessage(String message) {
        Log.i("AWebSocketClient", "onMessage()");
    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
        Log.i("AWebSocketClient", "onClose()");
    }

    @Override
    public void onError(Exception ex) {
        Log.i("AWebSocketClient", "onError()");
    }
}
