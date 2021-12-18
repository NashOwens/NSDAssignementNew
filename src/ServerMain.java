import java.io.IOException;
import java.io.OutputStream;

import java.net.Socket;

import java.util.Date;

public class ServerMain {

    public static void main(String args[])
    {
        int port = 12345;
        Server server = new Server(port);
        server.start();

    }
}
