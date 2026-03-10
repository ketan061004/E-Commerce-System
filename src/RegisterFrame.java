import javax.swing.*;
import java.awt.*;

public class RegisterFrame extends JFrame {
    private JTextField nameField, emailField;
    private JPasswordField passField, confirmField;
    private UserDAO userDAO = new UserDAO();

    public RegisterFrame() {
        setTitle("Register - E-Commerce");
        setSize(480, 340);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        JLabel title = new JLabel("Create an Account", SwingConstants.CENTER);
        title.setFont(new Font("SansSerif", Font.BOLD, 16));
        add(title, BorderLayout.NORTH);

        JPanel form = new JPanel(new GridLayout(4,2,8,8));
        form.setBorder(BorderFactory.createEmptyBorder(15,30,15,30));
        form.add(new JLabel("Full Name:")); nameField = new JTextField(); form.add(nameField);
        form.add(new JLabel("Email:")); emailField = new JTextField(); form.add(emailField);
        form.add(new JLabel("Password:")); passField = new JPasswordField(); form.add(passField);
        form.add(new JLabel("Confirm Password:")); confirmField = new JPasswordField(); form.add(confirmField);
        add(form, BorderLayout.CENTER);

        JPanel buttons = new JPanel();
        JButton registerBtn = new JButton("Register");
        JButton cancelBtn = new JButton("Cancel");
        buttons.add(registerBtn); buttons.add(cancelBtn);
        add(buttons, BorderLayout.SOUTH);

        registerBtn.addActionListener(e -> registerUser());
        cancelBtn.addActionListener(e -> this.dispose());
    }

    private void registerUser() {
        String name = nameField.getText().trim();
        String email = emailField.getText().trim();
        String pass = new String(passField.getPassword());
        String confirm = new String(confirmField.getPassword());

        if (name.isEmpty() || email.isEmpty() || pass.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill all fields.");
            return;
        }
        if (!pass.equals(confirm)) {
            JOptionPane.showMessageDialog(this, "Passwords do not match.");
            return;
        }

        try {
            User user = new User();
            user.setName(name);
            user.setEmail(email);
            user.setPassword(pass); // dao will hash
            user.setAdmin(false);

            boolean ok = userDAO.registerUser(user);
            if (ok) {
                JOptionPane.showMessageDialog(this, "Registration successful. You can now login.");
                this.dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Registration failed. Email may already be used.");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Registration error: " + ex.getMessage());
        }
    }
}
