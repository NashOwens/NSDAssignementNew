import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.ArrayList;

public class MsgPane extends JPanel implements MessageListener {

    private final Client client;
    private final String login;

    private ArrayList<String[]> History;
    private DefaultListModel<String> listModel = new DefaultListModel<>();
    private JList<String> msgList = new JList<>(listModel);
    private JTextField input = new JTextField();

    public MsgPane(Client client, String Topic, ArrayList<String[]> History) {
        this.client = client;
        this.login = Topic;
        this.History = History;

        client.addMessageListener(this);

        setLayout(new BorderLayout());
        add(new JScrollPane(msgList), BorderLayout.CENTER);
        add(input, BorderLayout.SOUTH);

        for (String[] line : History){
            listModel.addElement(line[1]+": "+ line[2]+"\n");
        }
        input.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    String text = input.getText();
                    if (!(text.equals(""))){
                        client.msg(Topic, text);
                        listModel.addElement("You: "+text);
                    }
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
