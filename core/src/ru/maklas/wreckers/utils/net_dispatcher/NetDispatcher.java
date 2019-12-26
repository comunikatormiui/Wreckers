package ru.maklas.wreckers.utils.net_dispatcher;

import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.Pool;
import com.badlogic.gdx.utils.Queue;
import ru.maklas.mnet2.Socket;
import ru.maklas.mnet2.SocketProcessor;
import ru.maklas.wreckers.utils.Log;
import ru.maklas.wreckers.utils.Utils;

public class NetDispatcher implements SocketProcessor {

	private final ObjectMap<Class, NetSignal> map = new ObjectMap<>();
	private final Queue<NetData> memoryQueue;
	private final int memorySize;
	private final Pool<NetData> netDataPool = new Pool<NetData>() {
		@Override
		protected NetData newObject() {
			return new NetData();
		}
	};

	public NetDispatcher() {
		this(16);
	}

	public NetDispatcher(int memorySize) {
		this.memorySize = memorySize;
		this.memoryQueue = new Queue<>(memorySize);
	}

	@SuppressWarnings("all")
	public <T extends NetEvent> void subscribe(Class<T> subscriptionClass, NetEventProcessor<T> processor){
		NetSignal<T> signal = map.get(subscriptionClass);
		if (signal == null){
			signal = new NetSignal<>();
			map.put(subscriptionClass, signal);
		}
		signal.add(processor);
	}

	@SuppressWarnings("all")
	public <T extends NetEvent> void unsubscrive(Class<T> subscriptionClass, NetEventProcessor<T> processor){
		NetSignal<T> signal = map.get(subscriptionClass);
		if (signal != null){
			signal.remove(processor);
		}
	}

	@SuppressWarnings("all")
	@Override
	public void process(Socket socket, Object o) {
		NetSignal signal = map.get(o.getClass());
		if (signal != null){
			signal.dispatch(socket, (NetEvent) o);
		} else {
			Log.error("NetDispatcher", "Event of class " + o.getClass().getSimpleName() + " was not handled");
		}
		if (o instanceof NetEvent) {
			if (memoryQueue.size >= memorySize) {
				memoryQueue.removeLast();
				memoryQueue.addFirst(netDataPool.obtain().init(System.currentTimeMillis(), (NetEvent) o));
			}
		} else {
			Log.error("NetDispatcher", "Received not NetEvent: " + o.getClass() + ":" + Utils.codePoint());
		}
	}

	/**
	 * Queue which hold most recent events.
	 * First element - most recent
	 * Last element - least recent.
	 */
	public Queue<NetData> getMemoryQueue() {
		return memoryQueue;
	}

	public void clear() {
		map.clear();
	}

}
