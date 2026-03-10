import javax.swing.*;
import java.awt.*;

public class LoginFrame extends JFrame {
    private JTextField emailField;
    private JPasswordField passwordField;
    private UserDAO userDAO = new UserDAO();

    public LoginFrame() {
        setTitle("E-Commerce - Login");
        setSize(420, 260);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        JLabel title = new JLabel("Welcome to E-Commerce", SwingConstants.CENTER);
        title.setFont(new Font("SansSerif", Font.BOLD, 18));
        add(title, BorderLayout.NORTH);

        JPanel form = new JPanel(new GridLayout(2,2,8,8));
        form.setBorder(BorderFactory.createEmptyBorder(20,40,20,40));
        form.add(new JLabel("Email:"));
        emailField = new JTextField();
        form.add(emailField);
        form.add(new JLabel("Password:"));
        passwordField = new JPasswordField();
        form.add(passwordField);
        add(form, BorderLayout.CENTER);

        JPanel buttons = new JPanel();
        JButton loginBtn = new JButton("Login");
        JButton registerBtn = new JButton("Register");
        JButton guestBtn = new JButton("Continue as Guest");
        buttons.add(loginBtn);
        buttons.add(registerBtn);
        buttons.add(guestBtn);
        add(buttons, BorderLayout.SOUTH);

        loginBtn.addActionListener(e -> attemptLogin());
        registerBtn.addActionListener(e -> new RegisterFrame().setVisible(true));
        guestBtn.addActionListener(e -> {
            new DashboardFrame(null).setVisible(true);
            this.dispose();
        });

        passwordField.addActionListener(e -> attemptLogin());
    }

    private void attemptLogin() {
    String email = emailField.getText().trim();
    String pass = new String(passwordField.getPassword());

    if (email.isEmpty() || pass.isEmpty()) {
        JOptionPane.showMessageDialog(this, "Enter email and password.");
        return;
    }

    
    if (email.equals("admin@system") && pass.equals("admin123")) {
        JOptionPane.showMessageDialog(this, "Admin Login Successful!");
        User admin = new User();
        admin.setId(0);
        admin.setName("System Admin");
        admin.setEmail("admin@system");
        admin.setAdmin(true);   // IMPORTANT
        new DashboardFrame(admin).setVisible(true);
        this.dispose();
        return;
    }

   
    try {
        User user = userDAO.loginUser(email, pass);
        if (user != null) {
            JOptionPane.showMessageDialog(this, "Login Successful! Welcome " + user.getName());
            new DashboardFrame(user).setVisible(true);
            this.dispose();
        } else {
            JOptionPane.showMessageDialog(this, "Invalid email or password.");
        }
    } catch (Exception ex) {
        ex.printStackTrace();
        JOptionPane.showMessageDialog(this, "Login error: " + ex.getMessage());
    }
}

}
