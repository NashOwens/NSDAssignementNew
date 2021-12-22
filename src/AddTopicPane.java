import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class AddTopicPane extends JPanel {
    private final Client Client;
    private JButton button = new JButton("Submit");
    private JTextField input = new JTextField(20);
    private JTextArea Title = new JTextArea("Add a Topic:");

    public AddTopicPane(Client client) {
        this.Client = client;

        setLayout(new BorderLayout());
        add(Title, BorderLayout.NORTH);
        add(input, BorderLayout.CENTER);
        add(button, BorderLayout.SOUTH);

        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                SQLdatabase db = new SQLdatabase();
                String topic = input.getText();
                db.AddTopic(topic);
                input.setText("");
                client.addToTopics(topic);
                JOptionPane.showMessageDialog(null, "Topic "+topic+" Created!");
                revalidate();
                repaint();
            }
        });

        JFrame j = new JFrame("Add Topic");
        j.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        j.setSize(500, 500);
        j.getContentPane().add(this, BorderLayout.CENTER);
        j.setVisible(true);

    }
}
