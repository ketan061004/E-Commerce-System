import javax.swing.*;
import java.awt.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AdminPanelFrame extends JFrame {
    public AdminPanelFrame() {
        setTitle("Admin Panel - Manage Products & Categories");
        setSize(800, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        JPanel top = new JPanel();
        JButton refresh = new JButton("Refresh");
        JButton add = new JButton("Add Product");
        JButton delete = new JButton("Delete Product");
        JButton addCat = new JButton("Add Category");
        JButton delCat = new JButton("Delete Category");
        top.add(refresh); top.add(add); top.add(delete); top.add(addCat); top.add(delCat);
        add(top, BorderLayout.NORTH);

        DefaultListModel<String> listModel = new DefaultListModel<>();
        JList<String> list = new JList<>(listModel);
        add(new JScrollPane(list), BorderLayout.CENTER);

        refresh.addActionListener(e -> {
            listModel.clear();
            List<Product> prods = fetchAllProducts();
            for (Product p : prods) listModel.addElement(p.toString());
        });

        add.addActionListener(e -> {
            JTextField title = new JTextField();
            JTextField desc = new JTextField();
            JTextField price = new JTextField();
            JTextField stock = new JTextField();
            List<Category> cats = fetchAllCategories();
            Category[] arr = cats.toArray(new Category[0]);
            JComboBox<Category> catBox = new JComboBox<>(arr);
            Object[] fields = {
                "Title:", title,
                "Description:", desc,
                "Price:", price,
                "Stock:", stock,
                "Category:", catBox
            };
            int res = JOptionPane.showConfirmDialog(this, fields, "New Product", JOptionPane.OK_CANCEL_OPTION);
            if (res == JOptionPane.OK_OPTION) {
                try {
                    String sql = "INSERT INTO products (title, description, price, stock, images, category_id) VALUES (?, ?, ?, ?, ?, ?)";
                    try (Connection con = DBConnection.getConnection();
                         PreparedStatement stmt = con.prepareStatement(sql)) {
                        stmt.setString(1, title.getText());
                        stmt.setString(2, desc.getText());
                        stmt.setDouble(3, Double.parseDouble(price.getText()));
                        stmt.setInt(4, Integer.parseInt(stock.getText()));
                        stmt.setString(5, ""); // images blank
                        Category c = (Category) catBox.getSelectedItem();
                        stmt.setInt(6, c != null ? c.getId() : 0);
                        int rows = stmt.executeUpdate();
                        JOptionPane.showMessageDialog(this, rows > 0 ? "Product added." : "Add failed.");
                        refresh.doClick();
                    }
                } catch (Exception ex) { ex.printStackTrace(); JOptionPane.showMessageDialog(this, "Add error: " + ex.getMessage()); }
            }
        });

        delete.addActionListener(e -> {
            String sel = list.getSelectedValue();
            if (sel == null) { JOptionPane.showMessageDialog(this, "Select a product from list."); return; }
            // parse id from "[id]" style
            int start = sel.indexOf('[');
            int end = sel.indexOf(']');
            if (start != -1 && end > start) {
                int id = Integer.parseInt(sel.substring(start+1, end));
                try (Connection con = DBConnection.getConnection();
                     PreparedStatement stmt = con.prepareStatement("DELETE FROM products WHERE id = ?")) {
                    stmt.setInt(1, id);
                    int rows = stmt.executeUpdate();
                    JOptionPane.showMessageDialog(this, rows > 0 ? "Deleted." : "Delete failed.");
                    refresh.doClick();
                } catch (Exception ex) { ex.printStackTrace(); JOptionPane.showMessageDialog(this, "Delete error: " + ex.getMessage()); }
            } else {
                JOptionPane.showMessageDialog(this, "Could not determine product id.");
            }
        });

        addCat.addActionListener(e -> {
            JTextField name = new JTextField();
            JTextField desc = new JTextField();
            Object[] fields = {"Name:", name, "Description:", desc};
            int res = JOptionPane.showConfirmDialog(this, fields, "New Category", JOptionPane.OK_CANCEL_OPTION);
            if (res == JOptionPane.OK_OPTION) {
                try (Connection con = DBConnection.getConnection();
                     PreparedStatement stmt = con.prepareStatement("INSERT INTO categories (name, description) VALUES (?, ?)")) {
                    stmt.setString(1, name.getText());
                    stmt.setString(2, desc.getText());
                    int rows = stmt.executeUpdate();
                    JOptionPane.showMessageDialog(this, rows > 0 ? "Category added." : "Add failed.");
                } catch (Exception ex) { ex.printStackTrace(); JOptionPane.showMessageDialog(this, "Add category error: " + ex.getMessage()); }
            }
        });

        delCat.addActionListener(e -> {
            List<Category> cats = fetchAllCategories();
            Category[] arr = cats.toArray(new Category[0]);
            Category sel = (Category) JOptionPane.showInputDialog(this, "Select category to delete:", "Delete Category", JOptionPane.PLAIN_MESSAGE, null, arr, arr.length>0?arr[0]:null);
            if (sel == null) return;
            try (Connection con = DBConnection.getConnection();
                 PreparedStatement stmt = con.prepareStatement("DELETE FROM categories WHERE id = ?")) {
                stmt.setInt(1, sel.getId());
                int rows = stmt.executeUpdate();
                JOptionPane.showMessageDialog(this, rows > 0 ? "Deleted." : "Delete failed.");
                refresh.doClick();
            } catch (Exception ex) { ex.printStackTrace(); JOptionPane.showMessageDialog(this, "Delete category error: " + ex.getMessage()); }
        });

        // load initially
        refresh.doClick();
    }

    private List<Product> fetchAllProducts() {
        List<Product> list = new ArrayList<>();
        String sql = "SELECT p.*, c.name as category_name FROM products p LEFT JOIN categories c ON p.category_id = c.id";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement stmt = con.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                Product p = new Product();
                p.setId(rs.getInt("id"));
                p.setTitle(rs.getString("title"));
                p.setPrice(rs.getDouble("price"));
                p.setStock(rs.getInt("stock"));
                p.setCategoryName(rs.getString("category_name"));
                list.add(p);
            }
        } catch (Exception ex) { ex.printStackTrace(); }
        return list;
    }

    private List<Category> fetchAllCategories() {
        List<Category> list = new ArrayList<>();
        String sql = "SELECT id, name, description FROM categories";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement stmt = con.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                Category c = new Category();
                c.setId(rs.getInt("id"));
                c.setName(rs.getString("name"));
                c.setDescription(rs.getString("description"));
                list.add(c);
            }
        } catch (Exception ex) { ex.printStackTrace(); }
        return list;
    }
}
