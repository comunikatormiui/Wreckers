package ru.maklas.wreckers.server;

import ru.maklas.libs.game_looper.LoopedApplication;
import ru.maklas.libs.game_looper.LooperAccessor;
import ru.maklas.mnet.ServerAuthenticator;
import ru.maklas.mnet.ServerSocket;
import ru.maklas.mnet.Socket;
import ru.maklas.mrudp.ConnectionResponsePackage;
import ru.maklas.wreckers.assets.DCAssets;
import ru.maklas.wreckers.libs.Log;
import ru.maklas.wreckers.network.events.ConnectionRequest;
import ru.maklas.wreckers.network.events.ConnectionResponse;

import java.net.InetAddress;
import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;

public class Server implements LoopedApplication, ServerAuthenticator {

    private final ServerSocket serverSocket;
    private ClientBase clients;
    private LooperAccessor fps;
    private final LoopedApplication loginPage;


    public Server(ServerSocket serverSocket, ClientBase clients) {
        this.serverSocket = serverSocket;
        this.clients = clients;
        loginPage = null;
    }

    @Override
    public void onStart(LooperAccessor fps) {
        this.fps = fps;
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
    public ConnectionResponsePackage<?> validateNewConnection(InetAddress address, int port, Object request) {
        if (!(request instanceof ConnectionRequest)){
            return ConnectionResponsePackage.refuse(new ConnectionResponse(false, "Wrong request"));
        }

        ConnectionRequest req = (ConnectionRequest) request;


        FutureTask<Boolean> clientSizeCheck = new FutureTask<Boolean>(new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                if (clients.size() < 2) {
                    return true;
                }
                return false;
            }
        });
        fps.postRunnable(clientSizeCheck);

        boolean success;
        try {
            success = clientSizeCheck.get(500, TimeUnit.MILLISECONDS);
        } catch (Exception e){
            return ConnectionResponsePackage.refuse(new ConnectionResponse(false, "Fatal server error"));
        }

        if (success){
            return ConnectionResponsePackage.accept(new ConnectionResponse(true, "Success"));
        }

        return ConnectionResponsePackage.refuse(new ConnectionResponse(false, "Server is busy"));
    }

    @Override
    public void registerNewConnection(final Socket socket, ConnectionResponsePackage<?> sentResponsePackage, Object request) {
        ConnectionRequest req = (ConnectionRequest) request;
        final Client client = new Client();
        socket.setUserData(client);

        client.setSocket(socket);
        client.setHealth(100);
        client.setName(req.getName());
        client.setAdmin(false);
        FutureTask<Boolean> additionTask = new FutureTask<Boolean>(new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                if (clients.size() < 2) {
                    clients.add(client);
                    return true;
                }
                return false;
            }
        });

        Boolean result;
        try {
            result = additionTask.get();
        } catch (Exception e) {
            e.printStackTrace();
            socket.disconnect();
            socket.setUserData(null);
            return;
        }

        if (result){
            Log.SERVER.info("Client added " + client);
        } else {
            Log.SERVER.info("failed to add client " + client);
        }
    }

    @Override
    public void onSocketDisconnected(Socket socket, String msg) {
        clients.removeBySocket(socket);
    }

}
