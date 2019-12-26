package ru.maklas.wreckers.utils.net_dispatcher;

import com.badlogic.gdx.utils.SnapshotArray;
import ru.maklas.mnet2.Socket;

public class NetSignal<T extends NetEvent> {

	private SnapshotArray<NetEventProcessor<T>> processors;

	public NetSignal () {
		processors = new SnapshotArray<>(NetEventProcessor.class);
	}

	/**
	 * Add a Listener to this Signal
	 * @param listener The Listener to be added
	 */
	public void add (NetEventProcessor<T> listener) {
		processors.add(listener);
	}

	/**
	 * Remove a listener from this Signal
	 * @param listener The Listener to remove
	 */
	public void remove (NetEventProcessor<T> listener) {
		processors.removeValue(listener, true);
	}

	/** Removes all processors attached to this Signal. */
	public void removeAllListeners () {
		processors.clear();
	}

	/**
	 * Dispatches an event to all Listeners registered to this Signal
	 * @param object The object to send off
	 */
	@SuppressWarnings("all")
	public void dispatch (Socket socket, T object) {
		SnapshotArray<NetEventProcessor<T>> listeners = this.processors;

		final NetEventProcessor[] items = listeners.begin();
		for (int i = 0, n = listeners.size; i < n; i++) {
			NetEventProcessor<T> listener = items[i];
			listener.process(socket, object);
		}
		listeners.end();
	}
}
