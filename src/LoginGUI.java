import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

public class LoginGUI extends JFrame {
    private final Client client;

    JTextField loginField = new JTextField();
    JPasswordField passField = new JPasswordField();
    JButton loginButton = new JButton("Login");

    public LoginGUI() {
        super ("Login");

        this.client = new Client("localhost", 12345);
        client.connect();

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel pane= new JPanel();
        pane.setLayout(new BoxLayout(pane, BoxLayout.Y_AXIS));
        pane.add(loginField);
        pane.add(passField);
        pane.add(loginButton);
        getContentPane().add(pane, BorderLayout.CENTER);

        pack();
        setVisible(true);
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                doLogin();
            }
        });


    }

    private void doLogin() {
        String login = loginField.getText();
        String pass = passField.getText();
        try {
            if (client.login(login, pass)) {
                //bring up user list
                setVisible(false);
                UserListPane userListPane = new UserListPane(client);

                JFrame frame = new JFrame("User List for "+login);
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.setSize(400,600);

                frame.getContentPane().add(userListPane, BorderLayout.CENTER);
                frame.setVisible(true);
                frame.pack();

            } else {
                JOptionPane.showMessageDialog(this, "Error loggin in");
            }
        } catch (IOException e){
            e.printStackTrace();
        }

    }

    public static void main(String[] args) {
        LoginGUI login = new LoginGUI();
        login.setVisible(true);
    }
}
