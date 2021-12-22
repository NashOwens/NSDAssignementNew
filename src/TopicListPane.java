import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.util.ArrayList;

public class TopicListPane extends JPanel implements TopicListener {

    private final Client client;
    private JList<String> TopicListUI;
    private DefaultListModel<String> topicListModel;
    private JTextField Title = new JTextField("Topics/Chatroom's:");

    public TopicListPane(Client client) {
        this.client = client;
        topicListModel = client.getTopicList();

        TopicListUI = new JList<>(topicListModel);
        setLayout(new BorderLayout());
        setSize(200,300);
        Title.setEditable(false);
        add(Title, BorderLayout.NORTH);
        add(new JScrollPane(TopicListUI), BorderLayout.CENTER);

        TopicListUI.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() > 1){
                    String topic = TopicListUI.getSelectedValue();
                    if(!(topic==null)){
                        TopicMsgPane topicMsgPane = null;
                        try {
                            client.GetTopicHistory(topic);
                            topicMsgPane = new TopicMsgPane(client, topic);
                        } catch (IOException exception) {
                            exception.printStackTrace();
                        }

                        JFrame j = new JFrame("msg " + topic);
                        j.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                        j.setSize(500, 500);
                        j.getContentPane().add(topicMsgPane, BorderLayout.CENTER);
                        j.setVisible(true);
                        revalidate();
                        repaint();
                    }
                }
            }
        });
        setVisible(true);
    }

    @Override
    public void OnTopic(String topic) {
        topicListModel.addElement(topic);
    }
}
