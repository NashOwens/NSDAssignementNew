import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.ArrayList;


public class UserMenu extends JFrame {

    private final Client client;
    private JButton EXIT = new JButton("Exit");
    private JButton AddTopic = new JButton("add topic");
    private JButton NewPost = new JButton("New Post");

    public UserMenu(Client client) throws IOException {
        super ("Client " +client.getUsername());
        this.client = client;

        client.getTopics();
        setSize(800,600);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        add(client, BorderLayout.CENTER);
        add(new UserListPane(client), BorderLayout.EAST);
        add(new TopicListPane(client), BorderLayout.WEST);
        add(AddTopic, BorderLayout.BEFORE_FIRST_LINE);
        //add(NewPost);
        add(EXIT, BorderLayout.SOUTH);

        NewPost.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFrame f = new JFrame();
                f.setSize(400,300);
                f.add(new Post(client), BorderLayout.CENTER);
                f.setVisible(true);
            }
        });

        AddTopic.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new AddTopicPane(client);
            }
        });
        EXIT.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    client.handleLogOff();
                    revalidate();
                    repaint();
                    System.exit(0);
                } catch (IOException exception) {
                    exception.printStackTrace();
                }

            }
        });
        setVisible(true);
    }

}
