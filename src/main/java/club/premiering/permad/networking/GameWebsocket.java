package club.premiering.permad.networking;

import club.premiering.permad.PermaGlobals;
import lombok.Getter;
import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

// Implementation of the websocket server, routes events to the impl of NetworkHandler
public class GameWebsocket extends WebSocketServer {
    private NetworkHandler networkHandler;
    @Getter
    private List<GameSession> sessions = new ArrayList<>();

    public GameWebsocket(NetworkHandler handler, int port) throws UnknownHostException {
        super(new InetSocketAddress(port));

        this.networkHandler = handler;
    }

    public GameSession getSession(WebSocket webSocket) {
        for (GameSession session : this.sessions) {
            if (session.handle == webSocket)
                return session;
        }
        throw new RuntimeException("Couldn't find session for websocket client.");
    }

    @Override
    public void onOpen(WebSocket webSocket, ClientHandshake clientHandshake) {
        var session = new GameSession(webSocket);
        this.sessions.add(session);

        this.networkHandler.onConnect(session);
    }

    @Override
    public void onClose(WebSocket webSocket, int i, String s, boolean b) {
        this.networkHandler.onDisconnect(this.getSession(webSocket));
    }

    @Override
    public void onMessage(WebSocket webSocket, String s) {
        this.networkHandler.onPacket(this.getSession(webSocket), s.getBytes());
    }

    @Override
    public void onMessage(WebSocket conn, ByteBuffer message) {
        this.networkHandler.onPacket(this.getSession(conn), message.array());
    }

    @Override
    public void onError(WebSocket webSocket, Exception e) {
        e.printStackTrace();
    }

    @Override
    public void onStart() {
        System.out.println("Game server websocket | started on :" + PermaGlobals.WS_PORT);
    }
}
