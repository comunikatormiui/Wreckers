package ru.maklas.wreckers.states;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Batch;
import ru.maklas.mnet2.*;
import ru.maklas.wreckers.Wreckers;
import ru.maklas.wreckers.assets.InetAssets;
import ru.maklas.wreckers.net_events.NetConnectionRequest;
import ru.maklas.wreckers.utils.gsm_lib.State;

import java.net.InetAddress;

public class JoinState extends State{

	Socket socket;

	public JoinState() {
	}

	@Override
	protected void onCreate() {
		try {
			UDPSocket sock = InetAssets.wrapSocket(new JavaUDPSocket(), 0, 5);
			InetAddress address = InetAddress.getLocalHost();
			int port = InetAssets.defaultPort;

			socket = new SocketImpl(sock, address, port, InetAssets.defaultBufferSize, 7_000, 1_000, 100, InetAssets.serializerProvider().get());
			socket.connectAsync(new NetConnectionRequest("Client", Wreckers.VERSION), 5_000, resp -> Gdx.app.postRunnable(() -> {
				System.err.println(resp);
				switch (resp.getType()){
					case ACCEPTED:
						pushState(new JoinGameState(socket), false, false);
						break;
				}
			}));
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	@Override
	protected void update(float dt) {
	}

	@Override
	protected void render(Batch batch) {

	}

	@Override
	protected void dispose() {
		if (socket != null) {
			socket.close();
		}
	}
}
