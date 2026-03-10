import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProductListPanel extends JPanel {
    private JTable table;
    private DefaultTableModel model;

    public ProductListPanel() {
        setLayout(new BorderLayout());
        model = new DefaultTableModel(new String[]{"ID","Title","Category","Price","Stock"}, 0) {
            public boolean isCellEditable(int r, int c){ return false; }
        };
        table = new JTable(model);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton addToCart = new JButton("Add Selected To Cart");
        JButton details = new JButton("View Details");
        top.add(addToCart); top.add(details);
        add(top, BorderLayout.NORTH);
        add(new JScrollPane(table), BorderLayout.CENTER);

        addToCart.addActionListener(e -> addSelectedToCart());
        details.addActionListener(e -> showSelectedDetails());

        refreshProducts();
    }

    private void decreaseStockInDB(int productId, int qty) {
    String sql = "UPDATE products SET stock = stock - ? WHERE id = ? AND stock >= ?";
    try (Connection con = DBConnection.getConnection();
         PreparedStatement stmt = con.prepareStatement(sql)) {
        stmt.setInt(1, qty);
        stmt.setInt(2, productId);
        stmt.setInt(3, qty);
        stmt.executeUpdate();
    } catch (Exception ex) {
        ex.printStackTrace();
        JOptionPane.showMessageDialog(this, "Error decreasing stock!");
    }
}

private void increaseStockInDB(int productId, int qty) {
    String sql = "UPDATE products SET stock = stock + ? WHERE id = ?";
    try (Connection con = DBConnection.getConnection();
         PreparedStatement stmt = con.prepareStatement(sql)) {
        stmt.setInt(1, qty);
        stmt.setInt(2, productId);
        stmt.executeUpdate();
    } catch (Exception ex) {
        ex.printStackTrace();
        JOptionPane.showMessageDialog(this, "Error increasing stock!");
    }
}

    public void refreshProducts() {
        model.setRowCount(0);
        List<Product> products = fetchAllProducts();
        for (Product p : products) {
            model.addRow(new Object[]{p.getId(), p.getTitle(), p.getCategoryName()!=null?p.getCategoryName():"", p.getPrice(), p.getStock()});
        }
    }

    public List<Product> fetchAllProducts() {
        List<Product> list = new ArrayList<>();
        String sql = "SELECT p.*, c.name as category_name FROM products p LEFT JOIN categories c ON p.category_id = c.id";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement stmt = con.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                Product p = new Product();
                p.setId(rs.getInt("id"));
                p.setTitle(rs.getString("title"));
                p.setDescription(rs.getString("description"));
                p.setPrice(rs.getDouble("price"));
                p.setStock(rs.getInt("stock"));
                p.setImages(rs.getString("images"));
                p.setCategoryId(rs.getObject("category_id") == null ? null : rs.getInt("category_id"));
                p.setCategoryName(rs.getString("category_name"));
                list.add(p);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading products: " + ex.getMessage());
        }
        return list;
    }

    public void showSearchDialog() {
        String q = JOptionPane.showInputDialog(this, "Search by title (partial):");
        if (q == null) return;
        model.setRowCount(0);
        String sql = "SELECT p.*, c.name as category_name FROM products p LEFT JOIN categories c ON p.category_id = c.id WHERE p.title LIKE ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setString(1, "%" + q + "%");
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    model.addRow(new Object[]{
                        rs.getInt("id"),
                        rs.getString("title"),
                        rs.getString("category_name"),
                        rs.getDouble("price"),
                        rs.getInt("stock")
                    });
                }
            }
        } catch (Exception ex) { ex.printStackTrace(); JOptionPane.showMessageDialog(this, "Search error: " + ex.getMessage()); }
    }

    public void showCategoryFilterDialog() {
        List<Category> cats = fetchAllCategories();
        if (cats.isEmpty()) { JOptionPane.showMessageDialog(this, "No categories found."); return; }
        Category[] arr = cats.toArray(new Category[0]);
        Category sel = (Category) JOptionPane.showInputDialog(this, "Select category:", "Category", JOptionPane.PLAIN_MESSAGE, null, arr, arr[0]);
        if (sel == null) return;
        model.setRowCount(0);
        String sql = "SELECT p.*, c.name as category_name FROM products p LEFT JOIN categories c ON p.category_id = c.id WHERE p.category_id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setInt(1, sel.getId());
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    model.addRow(new Object[]{
                        rs.getInt("id"),
                        rs.getString("title"),
                        rs.getString("category_name"),
                        rs.getDouble("price"),
                        rs.getInt("stock")
                    });
                }
            }
        } catch (Exception ex) { ex.printStackTrace(); JOptionPane.showMessageDialog(this, "Filter error: " + ex.getMessage()); }
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

    private void addSelectedToCart() {
        int r = table.getSelectedRow();
        if (r == -1) { JOptionPane.showMessageDialog(this, "Select a product first."); return; }
        int id = Integer.parseInt(model.getValueAt(r, 0).toString());
        String title = model.getValueAt(r,1).toString();
        double price = Double.parseDouble(model.getValueAt(r,3).toString());
        String qStr = JOptionPane.showInputDialog(this, "Enter quantity:");
        if (qStr == null) return;
        int qty;
        try { qty = Integer.parseInt(qStr); } catch (Exception ex) { JOptionPane.showMessageDialog(this, "Invalid quantity."); return; }
        if (qty <= 0) { JOptionPane.showMessageDialog(this, "Quantity must be positive."); return; }
        // decrease stock immediately
decreaseStockInDB(id, qty);

// add to cart
CartFrame.addToCart(new CartItem(id, title, price, qty));

JOptionPane.showMessageDialog(this, "Added to cart.");
refreshProducts(); // refresh UI

        JOptionPane.showMessageDialog(this, "Added to cart.");
    }

    private void showSelectedDetails() {
        int r = table.getSelectedRow();
        if (r == -1) { JOptionPane.showMessageDialog(this, "Select a product."); return; }
        int id = Integer.parseInt(model.getValueAt(r, 0).toString());
        try {
            ProductDAO dao = new ProductDAO();
            Product p = dao.getProductById(id);
            if (p != null) {
                String info = "ID: " + p.getId()
                    + "\nTitle: " + p.getTitle()
                    + "\nCategory: " + (p.getCategoryName()!=null ? p.getCategoryName() : "")
                    + "\nPrice: " + p.getPrice()
                    + "\nStock: " + p.getStock()
                    + "\nDescription: " + (p.getDescription()!=null ? p.getDescription() : "");
                JOptionPane.showMessageDialog(this, info, "Product Details", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "Product info not found.");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error fetching details: " + ex.getMessage());
        }
    }
}
