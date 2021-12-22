import javax.swing.*;
import java.awt.*;
import java.io.IOException;

public class LoginGUI extends JFrame {
    private final Client client;

    JTextField loginField = new JTextField();
    JPasswordField passField = new JPasswordField();
    JButton loginButton = new JButton("Login");
    JButton EXIT = new JButton("Exit");

    public LoginGUI() {
        super ("Login");

        this.client = new Client("localhost", 12345);
        client.connect();

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel pane = new JPanel();
        pane.setLayout(new BoxLayout(pane, BoxLayout.Y_AXIS));
        pane.add(loginField);
        pane.add(passField);
        pane.add(loginButton);
        pane.add(EXIT);

        getContentPane().add(pane, BorderLayout.CENTER);

        pack();
        setVisible(true);
        loginButton.addActionListener(e -> doLogin());
        EXIT.addActionListener(e -> Exit());

    }

    private void Exit() {
        try {
            client.handleLogOff();
            System.exit(0);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void doLogin() {
        String login = loginField.getText();
        String pass = passField.getText();
        try {
            if(client.login(login, pass)) {
                //bring up user list
                setVisible(false);
                UserMenu menu = new UserMenu(client);
                menu.setVisible(true);
            } else {
                JOptionPane.showMessageDialog(this, "Error logging in");
            }
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }

    public static void main(String[] args) throws IOException {
        LoginGUI login = new LoginGUI();
        login.setVisible(true);
    }
}
