import java.io.*;
import java.net.Socket;
import java.util.HashSet;
import java.util.List;

public class ServerWorker extends Thread {
    private final Socket clientSocket;
    private String login = null;
    private final Server server;
    private OutputStream outputStream;
    private HashSet<String> topicSet = new HashSet<>();

    public ServerWorker(Server server,Socket clientSocket) {
        this.server = server;
        this.clientSocket = clientSocket;
    }

    @Override
    public void run() {
        try {
            handleClientSocket();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void handleClientSocket() throws IOException {
        InputStream inputStream = clientSocket.getInputStream();
        this.outputStream = clientSocket.getOutputStream();

        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        String line;
        while ((line = reader.readLine()) != null) {
            String[] tokens = line.split(" ");
            if (tokens.length > 0) {
                String cmd = tokens[0];
                if ("quit".equalsIgnoreCase(cmd) || "logoff".equalsIgnoreCase(cmd)) {
                    handleLogoff();
                    break;
                } else if ("login".equalsIgnoreCase(cmd)){
                    handleLogin(outputStream, tokens);
                }
                else if ("msg".equalsIgnoreCase(cmd))
                {
                    String[] tokensMsg = line.split(" ",3);
                    handleMsg(tokensMsg);
                }
                else if("join".equalsIgnoreCase(cmd)){
                    handleJoin(tokens);
                }
                else if ("leave".equalsIgnoreCase(cmd)){
                    handleLeave(tokens);
                }
                else {
                    String msg = "unknown " +cmd + "\n";
                    outputStream.write(msg.getBytes());
                }
               // String msg = "message:" + line + "\n";
               // outputStream.write(msg.getBytes());
            }
        }
        clientSocket.close();
    }

    private void handleLeave(String[] tokens) {
        if (tokens.length > 1){
            String Topic = tokens[1];
            topicSet.remove(Topic);
        }
    }

    public boolean isMemberOfTopic(String topic){
        return topicSet.contains(topic);
    }

    private void handleJoin(String[] tokens) {
        if (tokens.length > 1){
            String Topic = tokens[1];
            topicSet.add(Topic);
        }
    }

    //format: "msg" "login" msg...
    private void handleMsg(String[] tokens) throws IOException {
        String sendTo = tokens[1];
        String bodyMsg = tokens[2];

        boolean isTopic = sendTo.charAt(0) =='#';

        List<ServerWorker> workerList = server.getWorkerList();
        for (ServerWorker worker : workerList){
            if (isTopic){
                if (worker.isMemberOfTopic(sendTo)){
                    String outMsg = "msg " + sendTo + ": " + login + " " +bodyMsg + "\n";
                    worker.send(outMsg);
                }
            } else {
                if (sendTo.equalsIgnoreCase(worker.getLogin())) {
                    String outMsg = "msg " + login + " " + bodyMsg + "\n";
                    worker.send(outMsg);
                }
            }
        }
    }

    private void handleLogoff() throws IOException {
        server.removeWorker(this);
        List<ServerWorker> workerList = server.getWorkerList();
        // Send other online  users current status
        String onlineMsg = "Offline "+login+"\n";
        for (ServerWorker worker : workerList){
            if (worker.getLogin()!=null) {
                worker.send(onlineMsg);
            }
        }
        clientSocket.close();
    }

    public String getLogin(){
        return login;
    }

    private void handleLogin(OutputStream outputStream, String[] tokens) throws IOException {
        if (tokens.length == 3){
            String login = tokens[1];
            String password = tokens[2];

            if ((login.equals("guest") && password.equals("guest")) || (login.equals("a") && password.equals("a"))){
                String msg = "ok login\n";
                outputStream.write(msg.getBytes());
                this.login = login;
                System.out.println("User "+login+ " logged in succesfully!\n");


                List<ServerWorker> workerList = server.getWorkerList();
                //Send current user all other online logins
                for (ServerWorker worker : workerList){
                    if (worker.getLogin()!=null) {
                        if (!login.equals(worker.getLogin())) {
                            String onlineMsg2 = "online " + worker.getLogin();
                            send(onlineMsg2);
                        }
                    }

                }
                // Send other online  users current status
                String onlineMsg = "online "+login+"\n";
                for (ServerWorker worker : workerList){
                    if (worker.getLogin()!=null) {
                        worker.send(onlineMsg);
                    }
                }
            } else {
                String msg = "error login\n";
                outputStream.write(msg.getBytes());
                System.out.println("Login Failed for " + login + " on Port: " + clientSocket.getLocalPort() + "\n");
            }
        }
    }

    private void send(String onlinemsg) throws IOException{
        if (login!=null){
            outputStream.write(onlinemsg.getBytes());
        }

    }
}
