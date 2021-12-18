import java.io.*;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

public class ServerWorker extends Thread {
    private final Socket clientSocket;
    private String login = null;
    private final Server server;
    private OutputStream outputStream;

    public ServerWorker(Server server,Socket clientSocket) {
        this.server = server;
        this.clientSocket = clientSocket;
    }

    @Override
    public void run() {
        handleClientSocket();
    }

    //accepts server commands from client

    private void handleClientSocket()  {
        try {
            InputStream inputStream = clientSocket.getInputStream();
            this.outputStream = clientSocket.getOutputStream();

            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            while ((line = reader.readLine()) != null) {
                String[] tokens = line.split(" ");
                if (tokens.length > 0 && tokens != null) {
                    String cmd = tokens[0];
                    if ("logoff".equalsIgnoreCase(cmd)) {
                        handleLogoff();
                        break;
                    } else if ("login".equalsIgnoreCase(cmd)) {
                        handleLogin(outputStream, tokens);
                    } else if ("msg".equalsIgnoreCase(cmd)) {
                        String[] tokensMsg = line.split(" ", 3);
                        handleMsg(tokensMsg);
                    } else if("getTopic".equalsIgnoreCase(cmd)){
                        handleGetTopics();
                    } else if ("getTopicHistory".equalsIgnoreCase(cmd)){
                        handleTopicHistory(tokens[1]);
                    } else {
                        String msg = "unknown " + cmd + "\n";
                        outputStream.write(msg.getBytes());
                    }
                }
            }
            clientSocket.close();
        } catch (IOException e){
            e.printStackTrace();
        }
    }

    private void handleGetTopics() throws IOException {
        SQLdatabase db = new SQLdatabase();
        ArrayList<String> list = db.Topics();
        for (String l : list){
            String outMsg = "getTopic "+l+"\n";
            send(outMsg);
        }
    }

    private void handleTopicHistory(String topic) throws  IOException {
        SQLdatabase db = new SQLdatabase();
        ArrayList<String[]> list = db.GetTopicLog(topic);
        for (String[] l : list){
            String outMsg = "getTopicHistory "+l[1] +" "+l[2] + "\n";
            send(outMsg);
        }
    }



    //format: "msg" "login" msg...
    private void handleMsg(String[] tokens) throws IOException {

        String sendTo = tokens[1];
        String bodyMsg = tokens[2];

        String[] MsgA = new String[3];
        MsgA[0] = login;
        MsgA[1] = sendTo;
        MsgA[2] = bodyMsg;

        SQLdatabase db = new SQLdatabase();
        List<ServerWorker> workerList = server.getWorkerList();
        for (ServerWorker worker : workerList){
            if (sendTo.equalsIgnoreCase(worker.getLogin())) {
                    db.ArchiveMsg(MsgA);
                    String outMsg = "msg " + login + " " + bodyMsg + "\n";
                    worker.send(outMsg);
            }
        }
    }


    private void handleLogoff() throws IOException {
        server.removeWorker(this);

        List<ServerWorker> workerList = server.getWorkerList();
        // Send other online  users current status
        String offlineMsg = "Offline "+login+"\n";
        for (ServerWorker worker : workerList){
            if (!login.equals(worker.getLogin())) {
                worker.send(offlineMsg);
            }
        }
        clientSocket.close();
    }

    public String getLogin(){
        return login;
    }

    private void handleLogin(OutputStream outputStream, String[] tokens) throws IOException {
        if (tokens.length == 3) {
            String login = tokens[1];
            String password = tokens[2];
            SQLdatabase db = new SQLdatabase();
            ArrayList<String[]> Users = db.GetUsers();

            for (String[] user : Users) {
                if (user[0].equals(login) && user[1].equals(password)) {
                    String msg = "Ok login\n";
                    outputStream.write(msg.getBytes());
                    this.login = login;

                    List<ServerWorker> workerList = server.getWorkerList();
                    //Send current user all other online logins
                    for (ServerWorker worker : workerList) {
                        if (!login.equals(worker.getLogin()) && worker.getLogin() != null) {
                            String onlineMsg2 = "Online " + worker.getLogin() + "\n";
                            send(onlineMsg2);
                        }
                        // Send other online users current status
                        String onlineMsg = "Online " + login + "\n";
                        if (!login.equals(worker.getLogin())) {
                            worker.send(onlineMsg);
                        }
                    }
                }
            }
        }
    }

    private void send(String Msg) throws IOException{
        if (login!=null){
            outputStream.write(Msg.getBytes());
        }

    }
}
