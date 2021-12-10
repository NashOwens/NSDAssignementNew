import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

public class MsgPane extends JPanel implements MessageListener {

    private final Client client;
    private final String login;

    private DefaultListModel<String> listModel = new DefaultListModel<>();
    private JList<String> msgList = new JList<>(listModel);
    private JTextField input = new JTextField();

    public MsgPane(Client client, String login) {
        this.client = client;
        this.login = login;

        client.addMessageListener(this);

        setLayout(new BorderLayout());
        add(new JScrollPane(msgList), BorderLayout.CENTER);
        add(input, BorderLayout.SOUTH);

        input.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    String text = input.getText();
                    client.msg(login, text);
                    listModel.addElement("You: "+text);
                    input.setText("");
                } catch (IOException exception) {
                    exception.printStackTrace();
                }
            }
        });
    }

    @Override
    public void onMessage(String fromLogin, String msgBody) {
        if (login.equalsIgnoreCase(fromLogin)){
            String line = fromLogin +": " + msgBody;
            listModel.addElement(line);
        }
    }
}
