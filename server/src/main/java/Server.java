import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.CopyOnWriteArrayList;

public class Server {
    private ServerSocket serverSocket = null;
    CopyOnWriteArrayList<ClientHandler> clients;
    public Server() {
        try {
            SQLHandler.connect();
            serverSocket = new ServerSocket(9000);
            clients = new CopyOnWriteArrayList<>();
            System.out.println("Server start");
            while (true) {
                Socket socket = serverSocket.accept();
                System.out.println("Client connected");
                new ClientHandler(this, socket);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                serverSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            SQLHandler.disconnect();
        }
    }
    public void subscribe(ClientHandler clientHandler) {
        clients.add(clientHandler);
    }

    public void unsubscribe(ClientHandler clientHandler) {
        clients.remove(clientHandler);
    }


    public boolean isNickBusy(String nick) {
        for (ClientHandler o : clients) {
            if (o.getLogin().equals(nick)) {
                return true;
            }
        }
        return false;
    }
}
