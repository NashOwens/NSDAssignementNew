import java.io.*;
import java.net.Socket;
import java.util.ArrayList;

public class Client {
    private final String hostName;
    private final int Port;
    private Socket socket;
    private OutputStream serverOut;
    private InputStream serverIn;
    private BufferedReader BufferedIn;

    private  ArrayList<UserStatusListener> userStatusListeners = new ArrayList<>();
    private  ArrayList<MessageListener> messageListeners = new ArrayList<>();

    public Client(String hostName, int Port){
        this.hostName = hostName;
        this.Port = Port;
    }
    public static void main(String[] args) {
        Client client = new Client("localhost", 12345);
        client.addUserStatusListener(new UserStatusListener() {
            @Override
            public void online(String login) {
                System.out.println("Online "+ login);
            }

            @Override
            public void offline(String login) {
                System.out.println("Offline "+login);
            }
        });

        client.addMessageListener((fromLogin, msgBody) ->
                System.out.println("you have a message from "+fromLogin + ": " + msgBody + "\n"));

    }

    public void msg(String sendTo, String msgBody) throws IOException {
        String cmd = "msg " + sendTo + " " + msgBody + "\n";
        serverOut.write(cmd.getBytes());
    }

    public boolean login(String user, String pass) throws IOException {
        String cmd = "login " + user + " " + pass + "\n";
        serverOut.write(cmd.getBytes());

        String response = BufferedIn.readLine();
        System.out.println("Response Msg: " + response);

        if ("ok login".equalsIgnoreCase(response)){
            startMessageReader();
            return true;
        } else {
            return false;
        }
    }

    public void handleLogOff() throws IOException {
        String cmd= "logoff";
        serverOut.write(cmd.getBytes());
    }

    private void startMessageReader() {
        Thread t = new Thread() {
            @Override
            public void run() {
                readMessageLoop();
            }
        };
        t.start();
    }

    private void readMessageLoop() {
        try {
            String line;
            while ((line = BufferedIn.readLine()) != null) {
                String[] tokens = line.split(" ");
                if (tokens.length > 0) {
                    String cmd = tokens[0];
                    if("online".equalsIgnoreCase(cmd)){
                        handleOnline(tokens);
                    } else if("offline".equalsIgnoreCase(cmd)){
                        handleOffline(tokens);
                    } else if("msg".equalsIgnoreCase(cmd)){
                        String[] tokensMsg = line.split(" ",3);
                        handleMsg(tokensMsg);
                    } else if("logoff".equalsIgnoreCase(cmd)){
                        handleLogOff();
                    }
                }
            }
        }
        catch(IOException e) {
            e.printStackTrace();
                try {
                    socket.close();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        }

    private void handleMsg(String[] tokensMsg) {
        String login = tokensMsg[1];
        String msgBody = tokensMsg[2];

        for (MessageListener listener : messageListeners){
            listener.onMessage(login, msgBody);
        }
    }

    private void handleOffline(String[] tokens) {
        String login = tokens[1];
        for (UserStatusListener listener : userStatusListeners){
            listener.offline(login);
        }
    }

    private void handleOnline(String[] tokens) {
        String login = tokens[1];
        for (UserStatusListener listener : userStatusListeners){
            listener.online(login);
        }
    }


    public boolean connect() {
        try {
            this.socket = new Socket(hostName, Port);
            this.serverOut = socket.getOutputStream();
            this.serverIn = socket.getInputStream();
            this.BufferedIn = new BufferedReader(new InputStreamReader(serverIn));
            return true;
        } catch (IOException e){
            e.printStackTrace();
        }
        return false;
    }

    public void addUserStatusListener(UserStatusListener listener){
        userStatusListeners.add(listener);
    }
    public void removeUserStatusListener(UserStatusListener listener){
        userStatusListeners.remove(listener);
    }
    public void addMessageListener(MessageListener listener){
        messageListeners.add(listener);
    }
    public void removeMessageStatusListener(MessageListener listener){
        messageListeners.remove(listener);
    }
}
