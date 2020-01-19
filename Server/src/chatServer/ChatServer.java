package chatServer;

import chatNetwork.TCPConnection;
import chatNetwork.TCPConnectionListener;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;

public class ChatServer implements TCPConnectionListener
{
    public static void main(String[] args)
    {
        new ChatServer();
    }

    private final ArrayList<TCPConnection> connections = new ArrayList<>();

    private ChatServer()
    {
        System.out.println("Server Running...");
        try(ServerSocket serverSocket = new ServerSocket(8189))
        {
            while (true)
            {
                try {
                    new TCPConnection(this, serverSocket.accept());
                } catch (IOException e)
                {
                    System.out.println("TCPConnection exception: " + e);
                }
            }
        }catch (IOException e)
        {
            throw new RuntimeException(e);
        }
    }

    @Override
    public synchronized void onConnectionReady(TCPConnection tcpConnection)
    {
        connections.add(tcpConnection);
        sendToAllConnection("Client connected: " + tcpConnection);
    }

    @Override
    public synchronized void onReceiveString(TCPConnection tcpConnection, String value)
    {
        sendToAllConnection(value);
    }

    @Override
    public synchronized void onDisconnect(TCPConnection tcpConnection)
    {
        connections.remove(tcpConnection);
        sendToAllConnection("Client disconnected: " + tcpConnection);
    }

    @Override
    public synchronized void onException(TCPConnection tcpConnection, Exception e)
    {
        System.out.println("TCPConnection exception: " + e);
    }
    private void sendToAllConnection(String value)
    {
        final int cnt = connections.size();
        System.out.println(value);
        for (int i = 0; i < cnt; i++) connections.get(i).sendString(value);
    }
}
