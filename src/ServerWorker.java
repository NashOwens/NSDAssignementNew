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

    private static Clock clock = new Clock();

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
                        handleLogoff(clock.tick());
                        break;
                    } else if ("login".equalsIgnoreCase(cmd)) {
                        handleLogin(outputStream, tokens);
                    } else if ("msg".equalsIgnoreCase(cmd)) {
                        String[] tokensMsg = line.split(" ", 3);
                        handleMsg(tokensMsg, clock.tick());
                    } else if("getTopic".equalsIgnoreCase(cmd)){
                        handleGetTopics();
                    } else if ("getTopicHistory".equalsIgnoreCase(cmd)){
                        handleTopicHistory(tokens[1]);
                    } else if("msgTopic".equalsIgnoreCase(cmd)){
                        String[] tokensMsg = line.split(" ",4);
                        handleTopicMsg(tokensMsg, clock.tick());
                    }
                    else {
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
            String outMsg = "getTopicHistory "+l[0]+" "+l[1]+" "+l[2]+"\n";
            send(outMsg);
        }
    }



    //format: "msg" "login" msg...
    private void handleMsg(String[] tokens, String time) throws IOException {

        String sendTo = tokens[1];
        String bodyMsg = tokens[2];
        tokens[0] = login;

        SQLdatabase db = new SQLdatabase();
        db.ArchiveMsg(tokens, time);
        List<ServerWorker> workerList = server.getWorkerList();
        for (ServerWorker worker : workerList){
            if (sendTo.equalsIgnoreCase(worker.getLogin())) {
                    String outMsg = "msg " + login + " " + time + " " + bodyMsg + "\n";
                    worker.send(outMsg);
            }
        }
    }

    private void handleTopicMsg(String[] tokens, String time) throws IOException {
        String topic = tokens[1];
        String fromLogin = tokens[2];
        String bodyMsg = tokens[3];
        String[] Data = new String[3];
        Data[0] = topic;
        Data[1] = fromLogin;
        Data[2] = bodyMsg;
        SQLdatabase db = new SQLdatabase();
        db.ArchiveTopicMsg(Data, time);
        List<ServerWorker> workerList = server.getWorkerList();
        for (ServerWorker worker : workerList){
            String outMsg = "msgTopic "+ fromLogin + " " + time + " "+ bodyMsg + "\n";
            worker.send(outMsg);
        }
    }


    private void handleLogoff(String time) throws IOException {
        server.removeWorker(this);

        List<ServerWorker> workerList = server.getWorkerList();
        // Send other online  users current status
        String offlineMsg = "Offline "+login+" "+time+"\n";
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
                            String onlineMsg2 = "Online "+worker.getLogin()+"\n";
                            send(onlineMsg2);
                        }
                        // Send other online users current status
                        String onlineMsg = "Online "+login+"\n";
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
