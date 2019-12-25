package ru.maklas.wreckers.server;

import ru.maklas.libs.game_looper.LoopedApplication;
import ru.maklas.libs.game_looper.Looper;
import ru.maklas.mnet2.Connection;
import ru.maklas.mnet2.ServerAuthenticator;
import ru.maklas.mnet2.ServerSocket;
import ru.maklas.mnet2.Socket;
import ru.maklas.wreckers.assets.DCAssets;
import ru.maklas.wreckers.libs.Log;
import ru.maklas.wreckers.network.events.ConnectionRequest;
import ru.maklas.wreckers.network.events.ConnectionResponse;

public class Server implements LoopedApplication, ServerAuthenticator {

    private final ServerSocket serverSocket;
    private ClientBase clients;
    private Looper looper;
    private final LoopedApplication loginPage;


    public Server(ServerSocket serverSocket, ClientBase clients) {
        this.serverSocket = serverSocket;
        this.clients = clients;
        loginPage = null;
    }

    @Override
    public void onStart(Looper fps) {
        this.looper = fps;
        loginPage.onStart(fps);
    }

    @Override
    public void update(float dt) {
        loginPage.update(dt);
    }

    @Override
    public void dispose() {
        loginPage.dispose();
        clients.disconnectAll(DCAssets.SERVER_STOPPED);
    }

    @Override
    public void acceptConnection(Connection conn) {
        if (!(conn.getRequest() instanceof ConnectionRequest)){
            conn.reject(new ConnectionResponse(false, "Wrong request"));
            return;
        }
        ConnectionRequest request = (ConnectionRequest) conn.getRequest();

        if (clients.size() < 2) {
            Socket socket = conn.accept(new ConnectionResponse(true, "Success"));
            registerNewConnection(socket, request);
        } else {
            conn.reject(new ConnectionResponse(false, "Server is busy"));
        }
    }

    public void registerNewConnection(final Socket socket, ConnectionRequest request) {
        final Client client = new Client();
        socket.setUserData(client);

        client.setSocket(socket);
        client.setHealth(100);
        client.setName(request.getName());
        client.setAdmin(false);

        Log.SERVER.info("Client added " + client);
        socket.addDcListener((socket1, msg) -> clients.removeBySocket(socket));
    }

}
