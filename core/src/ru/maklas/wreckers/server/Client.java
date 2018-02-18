package ru.maklas.wreckers.server;

import ru.maklas.mnet.Socket;

public class Client {

    Socket socket;
    String name;
    int health;
    int roomId;
    boolean roomAdmin;

    public Client() {

    }

    public Socket getSocket() {
        return socket;
    }

    public void setSocket(Socket socket) {
        this.socket = socket;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isAdmin() {
        return roomAdmin;
    }

    public void setAdmin(boolean roomAdmin) {
        this.roomAdmin = roomAdmin;
    }

    public boolean isConnected(){
        return socket.isConnected();
    }

    public int getHealth() {
        return health;
    }

    public void setHealth(int health) {
        this.health = health;
    }

    @Override
    public String toString() {
        return "Client{" +
                "socket=" + socket +
                ", name='" + name + '\'' +
                ", health=" + health +
                ", roomAdmin=" + roomAdmin +
                '}';
    }
}
