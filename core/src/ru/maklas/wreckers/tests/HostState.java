package ru.maklas.wreckers.tests;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import ru.maklas.mnet2.*;
import ru.maklas.wreckers.assets.InetAssets;
import ru.maklas.wreckers.libs.gsm_lib.GSMBackToFirst;
import ru.maklas.wreckers.libs.gsm_lib.State;
import ru.maklas.wreckers.network.events.ConnectionResponse;

public class HostState extends State implements ServerAuthenticator {

    private ServerSocket serverSocket;
    private Socket socket;

    @Override
    protected void onCreate() {

        try {
            UDPSocket sock = new JavaUDPSocket(InetAssets.defaultPort);
            serverSocket = new ServerSocket(sock, InetAssets.defaultBufferSize, 7_000, 1_000, 100, this, InetAssets.serializerProvider());
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
    }

    @Override
    public void acceptConnection(Connection conn) {
        if (HostState.this.socket == null) {
            HostState.this.socket = conn.accept(new ConnectionResponse(true, ""));
            if (getGsm().getCurrentState() == HostState.this) {
                pushState(new HostGameState(socket), true, false);
            }
            HostState.this.socket.addDcListener((socket, msg) -> {
                System.out.println("Socket disconnected with msg: " + msg);
                if (socket == HostState.this.socket) {
                    HostState.this.socket = null;
                    if (getGsm().getCurrentState() != HostState.this) {
                        getGsm().setCommand(new GSMBackToFirst());
                    }
                }
                ;
            });
        } else {
            conn.reject(new ConnectionResponse(false, "Busy"));
        }
    }

    @Override
    protected void update(float dt) {
        serverSocket.update();
        if (socket != null) {
        }
    }

    @Override
    protected void render(SpriteBatch batch) {

    }

    @Override
    protected void dispose() {
        serverSocket.close();
    }

}
