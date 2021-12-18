import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.util.ArrayList;

public class TopicListPane extends JPanel {

    private final Client client;
    private JList<String> TopicListUI;

    public TopicListPane(Client client) {
        this.client = client;
        DefaultListModel<String> topicListModel = client.getTopicList();

        TopicListUI = new JList<>(topicListModel);
        setLayout(new BorderLayout());
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
                            client.clearHistoryList();
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
}
