import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

public class TopicMsgPane extends JPanel implements TopicMessageListener {
    private final Client client;
    private final String login;

    private DefaultListModel<String> History;
    private JTextField input = new JTextField();
    private JButton EXIT = new JButton("EXIT");

    public TopicMsgPane(Client client, String Topic) throws IOException {
        this.client = client;
        this.login = Topic;

        History = client.getHistoryList();
        Clock clock = new Clock();

        setLayout(new BorderLayout());
        JList<String> msgList = new JList<>(History);
        add(new JScrollPane(msgList), BorderLayout.CENTER);
        add(input, BorderLayout.SOUTH);
        add(EXIT, BorderLayout.NORTH);

        EXIT.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                client.clearHistoryList();
                setVisible(false);
                revalidate();

            }
        });

        client.addTopicMessageListener(this);

        input.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    String text = input.getText();
                    String[] temp = new String[3];
                    temp[0] = Topic;
                    temp[1] = client.getUsername();
                    temp[2] = text;
                    if (!(text.equals(""))){
                        client.SendTopicMsg(temp);
                        History.addElement("You("+clock.tick()+"): "+text);
                    }
                    input.setText("");
                } catch (IOException exception) {
                    exception.printStackTrace();
                }
            }
        });

    }

    @Override
    public void OnMessage(String fromLogin, String time, String msgBody) {
            String line = fromLogin +"("+time+"): " + msgBody;
            History.addElement(line);
    }
}
