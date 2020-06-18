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
        Log.e("AWebSocketClient", "onOpen()");
    }

    @Override
    public void onMessage(String message) {
        Log.e("AWebSocketClient", "onMessage()");
    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
        Log.e("AWebSocketClient", "onClose()");
    }

    @Override
    public void onError(Exception ex) {
        Log.e("AWebSocketClient", "onError()");
    }
}
