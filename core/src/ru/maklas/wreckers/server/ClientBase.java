package ru.maklas.wreckers.server;

import com.badlogic.gdx.utils.Array;
import org.jetbrains.annotations.NotNull;
import ru.maklas.mnet2.Socket;
import ru.maklas.mnet2.SocketProcessor;
import ru.maklas.wreckers.statics.DCType;

import java.net.InetAddress;
import java.util.Iterator;
import java.util.concurrent.TimeUnit;

public class ClientBase implements Iterable<Client> {

	private final Array<Client> clients = new Array<>();
	private final Array.ArrayIterator<Client> processingIterator;
	private final Array.ArrayIterator<Client> sendAllIterator;
	private final Array<Listener> listeners = new Array<>();
	private final Array<Ban> bans = new Array<>();
	private volatile int size;

	public ClientBase() {
		processingIterator = new Array.ArrayIterator<>(clients);
		sendAllIterator = new Array.ArrayIterator<>(clients);
	}

	public void sendAll(@NotNull Object objectToSend) {
		Array.ArrayIterator<Client> iter = this.sendAllIterator;
		iter.reset();
		while (iter.hasNext()){
			iter.next().getSocket().send(objectToSend);
		}
	}

	public void add(Client client){
		clients.add(client);
		size = clients.size;
		for (Listener listener : listeners) {
			listener.onAdded(client);
		}

	}

	public void banAndRemove(Client client, int banDuration, TimeUnit timeUnit){
		ban(client.getSocket().getRemoteAddress(), banDuration, timeUnit);
	}

	public void ban(InetAddress address, int banDuration, TimeUnit timeUnit){
		for (Ban ban : bans) {
			if (ban.address.equals(address)){
				ban.banStartTime = System.currentTimeMillis();
				ban.banDurationMS = timeUnit.toMillis(banDuration);
				return;
			}
		}

		Ban ban = new Ban(address, System.currentTimeMillis(), timeUnit.toMillis(banDuration));
		bans.add(ban);
		Client bannedClient = get(address);
		if (bannedClient != null){
			bannedClient.getSocket().close(DCType.KICK);
			remove(bannedClient);
		}
	}

	public Client get(InetAddress address) {
		for (Client client : clients) {
			if (client.getSocket().getRemoteAddress().equals(address)){
				return client;
			}
		}

		return null;
	}

	public boolean isBanned(InetAddress address){
		long currentTime = System.currentTimeMillis();
		Iterator<Ban> iterator = bans.iterator();
		while (iterator.hasNext()){
			Ban ban = iterator.next();
			if (currentTime - ban.banStartTime > ban.banDurationMS){
				iterator.remove();
				continue;
			}
			if (ban.address.equals(address)){
				return true;
			}
		}

		return false;
	}

	public void remove(Client client){
		boolean removed = clients.removeValue(client, true);
		if (removed){
			size = clients.size;
			for (Listener listener : listeners) {
				listener.onRemoved(client);
			}

		}
	}

	public int size(){
		return clients.size;
	}

	public int volatileSize(){
		return size;
	}

	public void addListener(Listener listener){
		listeners.add(listener);
	}

	public void removeListener(Listener listener){
		listeners.removeValue(listener, true);
	}

	@Override
	public Iterator<Client> iterator() {
		return clients.iterator();
	}

	public void clear() {
		clients.clear();
	}

	public void disconnectAll(String msg) {
		Client[] clients = this.clients.toArray(Client.class);
		for (Client client : clients) {
			client.getSocket().close(msg);
		}
	}

	public Client getSafe(int i) {
		try {
			return clients.get(i);
		} catch (Exception e){
			return null;
		}
	}

	public void process(SocketProcessor processor) {
		Array.ArrayIterator<Client> iter = this.processingIterator;
		iter.reset();
		for (;iter.hasNext();){
			iter.next().getSocket().update(processor);
			//TODO
			//if (interrupted){
			//	break;
			//}
		}
	}

	public boolean hasAdmin() {
		for (Client client : clients) {
			if (client.isAdmin()){
				return true;
			}
		}
		return false;
	}

	public void sendAllExcept(Object o, Client notTo) {
		Array.ArrayIterator<Client> iter = this.sendAllIterator;
		iter.reset();
		while (iter.hasNext()){
			Client next = iter.next();
			if (next != notTo) {
				next.getSocket().send(o);
			}
		}
	}

	public void removeBySocket(Socket socket) {
		Iterator<Client> iterator = new Array.ArrayIterator<Client>(this.clients);

		while (iterator.hasNext()){
			if (iterator.next().getSocket() == socket){
				iterator.remove();
				return;
			}
		}

	}

	public interface Listener {


		void onAdded(Client client);

		void onRemoved(Client client);

	}
	private class Ban{

		InetAddress address;
		long banStartTime;
		long banDurationMS;

		public Ban(InetAddress address, long banStartTime, long banDurationMS) {
			this.address = address;
			this.banStartTime = banStartTime;
			this.banDurationMS = banDurationMS;
		}
	}

}
