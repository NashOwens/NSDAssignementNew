import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

public class TopicMsgPane extends JPanel {
    private final Client client;
    private final String login;

    private DefaultListModel<String> History = new DefaultListModel<>();
    private JTextField input = new JTextField();

    public TopicMsgPane(Client client, String Topic) throws IOException {
        this.client = client;
        this.login = Topic;

        History = client.getHistoryList();

        setLayout(new BorderLayout());
        JList<String> msgList = new JList<>(History);
        add(new JScrollPane(msgList), BorderLayout.CENTER);
        add(input, BorderLayout.SOUTH);


        input.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    String text = input.getText();
                    client.msg(Topic, text);
                    History.addElement("You: "+text);
                    input.setText("");
                    revalidate();
                    repaint();
                } catch (IOException exception) {
                    exception.printStackTrace();
                }
            }
        });

    }

}
