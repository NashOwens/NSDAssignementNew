import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Post extends JPanel {
    private Client client;
    private JTextArea Post = new JTextArea(20, 20);
    private JTextField Title = new JTextField(20);
    private JButton submit = new JButton("Submit");

    public Post(Client client) {
        this.client = client;

        submit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

            }
        });
        add(Title, BorderLayout.NORTH);
        add(Post, BorderLayout.CENTER);
        add(submit, BorderLayout.SOUTH);
        setVisible(true);

    }
}
