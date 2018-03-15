package ru.maklas.wreckers.tests;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import ru.maklas.mnet.Handler;
import ru.maklas.mnet.ServerResponse;
import ru.maklas.mnet.Socket;
import ru.maklas.mnet.impl.SocketImpl;
import ru.maklas.mnet.impl.udp.HighPingUDPSocket;
import ru.maklas.mrudp.JavaUDPSocket;
import ru.maklas.mrudp.UDPSocket;
import ru.maklas.wreckers.Wreckers;
import ru.maklas.wreckers.assets.InetAssets;
import ru.maklas.wreckers.libs.gsm_lib.State;
import ru.maklas.wreckers.network.events.ConnectionRequest;

import java.net.InetAddress;
import java.util.concurrent.TimeUnit;

public class JoinState extends State{

    Socket socket;

    @Override
    protected void onCreate() {
        try {
            UDPSocket sock = new JavaUDPSocket();
            socket = new SocketImpl("Client", sock, 7000, InetAssets.defaultBufferSize, InetAssets.serializerProvider().provide());
            socket.start(InetAssets.defaultClientSocketUpdate);
            socket.connectAsync((int) TimeUnit.SECONDS.toMillis(10), InetAddress.getLocalHost(), InetAssets.defaultPort, new ConnectionRequest("Client", Wreckers.VERSION), new Handler<ServerResponse>() {
                @Override
                public void handle(final ServerResponse resp) {
                    Gdx.app.postRunnable(new Runnable() {
                        @Override
                        public void run() {
                            System.err.println(resp);
                            switch (resp.getType()){
                                case ACCEPTED:
                                    pushState(new JoinGameState(socket), false, false);
                                    break;
                            }
                        }
                    });
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
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

    }
}
