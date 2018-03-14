package ru.maklas.wreckers.tests;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import ru.maklas.mnet.ServerSocket;
import ru.maklas.mnet.Socket;
import ru.maklas.mnet.impl.AllInAuthenticator;
import ru.maklas.mnet.impl.ServerSocketImpl;
import ru.maklas.mrudp.ConnectionResponsePackage;
import ru.maklas.wreckers.assets.InetAssets;
import ru.maklas.wreckers.libs.gsm_lib.State;

import java.net.InetAddress;

public class HostState extends State {

    ServerSocket serverSocket;

    @Override
    protected void onCreate() {
        try {
            serverSocket = new ServerSocketImpl(InetAssets.defaultPort, InetAssets.defaultBufferSize, new AllInAuthenticator() {
                @Override
                public void registerNewConnection(Socket socket, ConnectionResponsePackage<?> sentResponsePackage, Object request) {

                }

                @Override
                public void onSocketDisconnected(Socket socket, String msg) {

                }

                @Override
                public Object responseFor(InetAddress address, int port, Object request) {
                    return null;
                }
            }, InetAssets.serializerProvider());
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
