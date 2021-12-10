import java.io.IOException;
import java.io.OutputStream;

import java.net.Socket;

import java.util.Date;

public class ServerMain {
    public static void main(String args[])
    {
        int port= 12345;
        Server server = new Server(port);
        server.start();

    }

    private static void handleClientSocket(Socket clientSocket) throws IOException, InterruptedException {
        OutputStream outputStream = clientSocket.getOutputStream();
        for (int i=0;i<10;i++){
            outputStream.write(("Time now is " + new Date()+ "\n").getBytes());
            Thread.sleep(1000);
        }
        clientSocket.close();
    }
}
