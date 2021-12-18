import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

public class UserListPane extends JPanel implements UserStatusListener {

    private final Client client;
    private JList<String> userListUI;
    private DefaultListModel<String> userListModel;

    public UserListPane(Client client) {
        this.client = client;
        this.userListModel = client.getUserList();

        userListUI = new JList<>(userListModel);
        client.addUserStatusListener(this);
        setLayout(new BorderLayout());
        add(new JScrollPane(userListUI), BorderLayout.CENTER);

        userListUI.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() > 1){
                    String login = userListUI.getSelectedValue();
                    if(!(login==null)){
                        MsgPane msgpane = new MsgPane(client, login, new ArrayList<>());

                        JFrame j = new JFrame("msg " + login);
                        j.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                        j.setSize(500, 500);
                        j.getContentPane().add(msgpane, BorderLayout.CENTER);
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
    public void online(String login) {
        if (!userListModel.contains(login)) {
            userListModel.addElement(login);
        }
    }

    @Override
    public void offline(String login) {
        userListModel.removeElement(login);
    }
}
