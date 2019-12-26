package ru.maklas.wreckers.utils.net_dispatcher;

import ru.maklas.mnet2.Socket;


public interface NetEventProcessor<T extends NetEvent> {

	void process(Socket s, T e);

}
