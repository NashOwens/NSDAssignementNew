import javax.swing.*;
import java.io.*;
import java.net.Socket;
import java.util.ArrayList;

public class Client extends JPanel {
    private final String hostName;
    private final int Port;
    private Socket socket;
    private OutputStream serverOut;
    private InputStream serverIn;
    private BufferedReader BufferedIn;
    private String Username;

    public JTextArea userLog = new JTextArea(20,25);

    private ArrayList<UserStatusListener> userStatusListeners = new ArrayList<>();
    private ArrayList<MessageListener> messageListeners = new ArrayList<>();
    private ArrayList<TopicMessageListener> topicMsgListener = new ArrayList<>();

    private DefaultListModel<String> userList = new DefaultListModel<>();
    private DefaultListModel<String> TopicList = new DefaultListModel<>();
    private DefaultListModel<String> TopicHistory = new DefaultListModel<>();

    private static Clock clock = new Clock();

    public Client(String hostName, int Port) {
        this.hostName = hostName;
        this.Port = Port;

        setSize(800,600);
        userLog.setSize(200,300);
        add(userLog);
        setVisible(true);

        this.addUserStatusListener(new UserStatusListener() {
            @Override
            public void online(String login) {
                userLog.append("\nOnline "+ login);
                if (!userList.contains(login)){
                    userList.addElement(login);
                }

            }
            @Override
            public void offline(String login) {
                userLog.append("\nOffline "+login);
                userList.removeElement(login);
                }
        });

        this.addMessageListener((fromLogin, Time, msgBody) ->
                userLog.append("\nyou have a message from "+fromLogin+" at "+java.time.LocalDate.now()+
                        java.time.LocalTime.now()));

    }
    public DefaultListModel<String> getUserList(){
        return userList;
    }

    public void msg(String sendTo, String msgBody) throws IOException {
        String cmd = "msg " + sendTo + " " + msgBody + "\n";
        serverOut.write(cmd.getBytes());
    }

    public boolean login(String user, String pass) throws IOException {
        String cmd = "login " + user + " " + pass + "\n";
        serverOut.write(cmd.getBytes());

        String response = BufferedIn.readLine();
        userLog.append("\nResponse Msg from server: " + response);

        if ("Ok login".equalsIgnoreCase(response)){
            Username = user;
            startMessageReader();
            return true;
        } else {
            return false;
        }
    }

    public String getUsername(){
        return Username;
    }

    public void handleLogOff() throws IOException {
        String cmd= "logoff\n";
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
                    if("Online".equalsIgnoreCase(cmd)){
                        handleOnline(tokens);
                    } else if("Offline".equalsIgnoreCase(cmd)){
                        handleOffline(tokens);
                    } else if("msg".equalsIgnoreCase(cmd)){
                        String[] tokensMsg = line.split(" ",4);
                        handleMsg(tokensMsg);
                    } else if("logoff".equalsIgnoreCase(cmd)){
                        handleLogOff();
                    } else if("getTopic".equalsIgnoreCase(cmd)){
                        String[] tokensMsg = line.split(" ",2);
                        setTopics(tokensMsg);
                    } else if("getTopicHistory".equalsIgnoreCase(cmd)){
                        String[] tokensMsg = line.split(" ", 4);
                        setTopicHistory(tokensMsg);
                    } else if("msgTopic".equalsIgnoreCase(cmd)){
                        String[] tokensMsg = line.split(" ",4);
                        handleTopicMsg(tokensMsg);
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

    private void handleTopicMsg(String[] tokensMsg) {
        String login = tokensMsg[1];
        String time = tokensMsg[2];
        String msgBody = tokensMsg[3];

        for (TopicMessageListener listener : topicMsgListener){
            if(!(Username.equals(login))) {
                listener.OnMessage(login, time, msgBody);
            }
        }
    }

    public void SendTopicMsg(String[] tokensMsg) throws IOException {
        String outMsg = "msgTopic "+tokensMsg[0] +" "+tokensMsg[1]+" "+tokensMsg[2]+"\n";
        serverOut.write(outMsg.getBytes());
    }

    private void setTopicHistory(String[] tokensMsg) {
        String post = tokensMsg[1] +"(" +tokensMsg[2]+ "): "+tokensMsg[3];
        TopicHistory.addElement(post);
    }

    public void GetTopicHistory(String topic) throws IOException {
        String outMsg = "getTopicHistory "+topic+"\n";
        serverOut.write(outMsg.getBytes());
    }


    private void setTopics(String[] tokens) {
        String Topic = tokens[1];
        if (!TopicList.contains(Topic)) {
            TopicList.addElement(Topic);
        }
    }

    private void handleMsg(String[] tokensMsg) {
        String login = tokensMsg[1];
        String time = tokensMsg[2];
        String msgBody = tokensMsg[3];

        for (MessageListener listener : messageListeners){
            listener.onMessage(login, time, msgBody);
        }
    }

    private void handleOffline(String[] tokens) {
        String logout = tokens[1];
        for (UserStatusListener listener : userStatusListeners){
            listener.offline(logout);
            this.removeUserStatusListener(listener);
        }
    }

    private void handleOnline(String[] tokens) {
        String login = tokens[1];
        for (UserStatusListener listener : userStatusListeners){
            listener.online(login);
        }
    }

    public void getTopics() throws IOException {
        String cmd ="getTopic\n";
        serverOut.write(cmd.getBytes());
    }

    public void addToTopics(String topic) {
        TopicList.addElement(topic);
    }

    public DefaultListModel<String> getTopicList() {
        return TopicList;
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

    public void addUserStatusListener(UserStatusListener listener){ userStatusListeners.add(listener); }
    public void removeUserStatusListener(UserStatusListener listener){
        userStatusListeners.remove(listener);
    }
    public void addMessageListener(MessageListener listener){
        messageListeners.add(listener);
    }
    public void removeMessageStatusListener(MessageListener listener){
        messageListeners.remove(listener);
    }
    public void addTopicMessageListener(TopicMessageListener listener) { topicMsgListener.add(listener); }
    public void removeTopicMessageListener(TopicMessageListener listener) { topicMsgListener.remove(listener); }

    public DefaultListModel<String> getHistoryList() {
        return TopicHistory;
    }
    public void clearHistoryList() {
        TopicHistory.clear();
    }
}
