package ru.maklas.wreckers.tests;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import ru.maklas.mnet.ServerAuthenticator;
import ru.maklas.mnet.ServerSocket;
import ru.maklas.mnet.Socket;
import ru.maklas.mnet.impl.ServerSocketImpl;
import ru.maklas.mrudp.ConnectionResponsePackage;
import ru.maklas.mrudp.JavaUDPSocket;
import ru.maklas.mrudp.UDPSocket;
import ru.maklas.wreckers.assets.InetAssets;
import ru.maklas.wreckers.libs.gsm_lib.GSMBackToFirst;
import ru.maklas.wreckers.libs.gsm_lib.State;
import ru.maklas.wreckers.network.events.ConnectionResponse;

import java.net.InetAddress;
import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;

public class HostState extends State {

    ServerSocket serverSocket;
    Socket socket;

    @Override
    protected void onCreate() {
        ServerAuthenticator singleSocketAuthenticator = new ServerAuthenticator() {
            @Override
            public ConnectionResponsePackage<?> validateNewConnection(InetAddress address, int port, Object request) {
                FutureTask<Boolean> allow = new FutureTask<Boolean>(new Callable<Boolean>() {
                    @Override
                    public Boolean call() throws Exception {
                        return socket == null;
                    }
                });
                Gdx.app.postRunnable(allow);
                boolean b = false;
                try {
                    b = allow.get();
                } catch (Exception ignore) {
                }

                if (b) {
                    return ConnectionResponsePackage.accept(new ConnectionResponse(true, ""));
                } else {
                    return ConnectionResponsePackage.accept(new ConnectionResponse(false, "Busy"));
                }
            }

            @Override
            public void registerNewConnection(final Socket socket, ConnectionResponsePackage<?> sentResponsePackage, Object request) {
                Gdx.app.postRunnable(new Runnable() {
                    @Override
                    public void run() {
                        HostState.this.socket = socket;
                        socket.start(InetAssets.defaultServerSocketUpdate);
                        if (getGsm().getCurrentState() == HostState.this){
                            pushState(new HostGameState(socket), false, false);
                        }
                    }
                });
            }

            @Override
            public void onSocketDisconnected(final Socket socket, String msg) {
                System.out.println("Socket disconnected with msg: " + msg);
                Gdx.app.postRunnable(new Runnable() {
                    @Override
                    public void run() {
                        if (socket == HostState.this.socket) {
                            HostState.this.socket = null;
                            if (getGsm().getCurrentState() != HostState.this){
                                getGsm().setCommand(new GSMBackToFirst());
                            }
                        }
                    }
                });
            }
        };
        try {
            UDPSocket sock = new JavaUDPSocket(InetAssets.defaultPort);
            serverSocket = new ServerSocketImpl("Server", sock, InetAssets.defaultBufferSize, 7000, 2500, singleSocketAuthenticator, InetAssets.serializerProvider());
            serverSocket.start();
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
    }

    @Override
    protected void update(float dt) {

    }

    @Override
    protected void render(SpriteBatch batch) {

    }

    @Override
    protected void dispose() {
        serverSocket.close();
    }

}
