import javax.swing.*;
import java.awt.*;

public class DashboardFrame extends JFrame {
    private User currentUser;

    public DashboardFrame(User user) {
        this.currentUser = user;
        setTitle("Dashboard - " + (user == null ? "Guest" : user.getName()));
        setSize(1000, 650);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout(10,10));

        JPanel top = new JPanel(new BorderLayout());
        JLabel title = new JLabel("E-Commerce Store", SwingConstants.LEFT);
        title.setFont(new Font("SansSerif", Font.BOLD, 22));
        top.add(title, BorderLayout.WEST);

        JPanel userPanel = new JPanel();
        String label = (user == null) ? "Guest" : (user.getName() + " <" + user.getEmail() + ">");
        userPanel.add(new JLabel("Signed in as: " + label));
        JButton cartBtn = new JButton("Cart");
        JButton logout = new JButton("Logout");
        userPanel.add(cartBtn);
        userPanel.add(logout);
        top.add(userPanel, BorderLayout.EAST);
        add(top, BorderLayout.NORTH);

        JSplitPane split = new JSplitPane();
        split.setResizeWeight(0.75);
        ProductListPanel productList = new ProductListPanel();
        split.setLeftComponent(productList);

        JPanel right = new JPanel();
        right.setLayout(new BoxLayout(right, BoxLayout.Y_AXIS));
        right.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
        JPanel summary = new JPanel(new GridLayout(5,1,6,6));
        summary.setBorder(BorderFactory.createTitledBorder("Quick Actions"));
        JButton refreshBtn = new JButton("Refresh Products");
        JButton searchBtn = new JButton("Search Products");
        JButton adminBtn = new JButton("Admin Panel");
        JButton categoriesBtn = new JButton("Filter by Category");
        summary.add(refreshBtn); summary.add(searchBtn); summary.add(categoriesBtn); summary.add(adminBtn);
        right.add(summary);
        right.add(Box.createVerticalGlue());
        split.setRightComponent(right);
        add(split, BorderLayout.CENTER);

        adminBtn.addActionListener(e -> {
    if (currentUser != null && currentUser.isAdmin()) {
        new AdminPanelFrame().setVisible(true);
    } else {
        JOptionPane.showMessageDialog(this, "Admin access required.");
    }
});
logout.addActionListener(e -> {
            if (currentUser != null) {
                try { new UserDAO().setOnlineStatus(currentUser.getId(), false); } catch (Exception ex) { /* ignore */ }
            }
            this.dispose();
            new LoginFrame().setVisible(true);
        });



        cartBtn.addActionListener(e -> new CartFrame().setVisible(true));
        refreshBtn.addActionListener(e -> productList.refreshProducts());
        searchBtn.addActionListener(e -> productList.showSearchDialog());
        categoriesBtn.addActionListener(e -> productList.showCategoryFilterDialog());
        adminBtn.addActionListener(e -> {
            if (currentUser != null && currentUser.isAdmin()) new AdminPanelFrame().setVisible(true);
            else JOptionPane.showMessageDialog(this, "Admin access required.");
        });
    }
}
