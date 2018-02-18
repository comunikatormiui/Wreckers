package ru.maklas.wreckers.tests;

import ru.maklas.mnet.ServerAuthenticator;
import ru.maklas.mnet.Socket;
import ru.maklas.mrudp.ConnectionResponsePackage;
import ru.maklas.wreckers.libs.game_looper.LooperAccessor;
import ru.maklas.wreckers.server.Client;
import ru.maklas.wreckers.server.ClientBase;
import ru.maklas.wreckers.server.events.ConnectionResponse;

import java.net.InetAddress;

public class AllAllowedAuthenticator implements ServerAuthenticator{

    private final ClientBase clients;
    private final LooperAccessor looper;
    private volatile int counter = 0;

    public AllAllowedAuthenticator(ClientBase clients, LooperAccessor looper) {
        this.clients = clients;
        this.looper = looper;
    }

    @Override
    public ConnectionResponsePackage<?> validateNewConnection(InetAddress address, int port, Object request) {
        return ConnectionResponsePackage.accept(new ConnectionResponse(true, ""));
    }

    @Override
    public void registerNewConnection(Socket socket, ConnectionResponsePackage<?> sentResponsePackage, Object request) {
        final Client client = new Client();
        socket.setUserData(client);
        client.setAdmin(true);
        client.setHealth(100);
        client.setName("Player " + counter++);
        client.setSocket(socket);

        looper.postRunnable(new Runnable() {
            @Override
            public void run() {
                clients.add(client);
            }
        });
    }

    @Override
    public void handleUnknownSourceMsg(Object o) {

    }

    @Override
    public void onSocketDisconnected(Socket socket, String msg) {
        final Client client = (Client) socket.setUserData(null);
        looper.postRunnable(new Runnable() {
            @Override
            public void run() {
                clients.remove(client);
            }
        });
    }
}
