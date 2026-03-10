import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.*;
import java.text.SimpleDateFormat;
import java.io.File;
import java.nio.file.Files;
import java.sql.Connection;
import java.sql.PreparedStatement;

public class CartFrame extends JFrame {

    private static java.util.List<CartItem> cart = new ArrayList<>();

    private JTable table;
    private DefaultTableModel model;
    private JLabel totalLabel;

    public CartFrame() {
        setTitle("Shopping Cart");
        setSize(800, 480);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(8, 8));

        model = new DefaultTableModel(
                new String[]{"Product ID", "Title", "Price", "Qty", "Subtotal"}, 0) {
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };

        table = new JTable(model);
        add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel bottom = new JPanel(new BorderLayout());
        totalLabel = new JLabel("Total: ₹0.00");
        bottom.add(totalLabel, BorderLayout.WEST);

        JPanel ops = new JPanel();
        JButton removeBtn = new JButton("Remove Selected");
        JButton updateQty = new JButton("Update Qty");
        JButton checkout = new JButton("Checkout");
        ops.add(removeBtn);
        ops.add(updateQty);
        ops.add(checkout);
        bottom.add(ops, BorderLayout.EAST);

        add(bottom, BorderLayout.SOUTH);

        removeBtn.addActionListener(e -> removeSelected());
        updateQty.addActionListener(e -> updateSelectedQty());
        checkout.addActionListener(e -> checkout());

        refreshTable();
    }

    // ===========================
    // STOCK HELPERS
    // ===========================
    private void increaseStockInDB(int productId, int qty) {
        String sql = "UPDATE products SET stock = stock + ? WHERE id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement stmt = con.prepareStatement(sql)) {

            stmt.setInt(1, qty);
            stmt.setInt(2, productId);
            stmt.executeUpdate();

        } catch (Exception ex) {
            ex.printStackTrace();
        }
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
        }
    }

    // ===========================
    // ADD TO CART (called from ProductListPanel)
    // ===========================
    public static void addToCart(CartItem item) {
        for (CartItem ci : cart) {
            if (ci.getProductId() == item.getProductId()) {
                ci.setQuantity(ci.getQuantity() + item.getQuantity());
                return;
            }
        }
        cart.add(item);
    }

    // ===========================
    // REFRESH TABLE
    // ===========================
    private void refreshTable() {
        model.setRowCount(0);
        double total = 0;

        for (CartItem ci : cart) {
            double sub = ci.getPrice() * ci.getQuantity();
            model.addRow(new Object[]{
                    ci.getProductId(),
                    ci.getProductName(),
                    ci.getPrice(),
                    ci.getQuantity(),
                    sub
            });
            total += sub;
        }

        totalLabel.setText(String.format("Total: ₹%.2f", total));
    }

    // ===========================
    // REMOVE ITEM (FIXED)
    // ===========================
    private void removeSelected() {
        int r = table.getSelectedRow();
        if (r == -1) {
            JOptionPane.showMessageDialog(this, "Select an item.");
            return;
        }

        int productId = Integer.parseInt(model.getValueAt(r, 0).toString());

        CartItem toRemove = null;

        for (CartItem ci : cart) {
            if (ci.getProductId() == productId) {
                toRemove = ci;
                break;
            }
        }

        if (toRemove != null) {
            // INCREASE STOCK BACK IN DB
            increaseStockInDB(productId, toRemove.getQuantity());
            cart.remove(toRemove);
        }

        refreshTable();
    }

    // ===========================
    // UPDATE QUANTITY (FULLY FIXED)
    // ===========================
    private void updateSelectedQty() {

        int r = table.getSelectedRow();
        if (r == -1) {
            JOptionPane.showMessageDialog(this, "Select an item.");
            return;
        }

        int productId = Integer.parseInt(model.getValueAt(r, 0).toString());

        String qStr = JOptionPane.showInputDialog(this, "New quantity:");
        if (qStr == null) return;

        try {
            int newQty = Integer.parseInt(qStr);

            if (newQty <= 0) {
                JOptionPane.showMessageDialog(this, "Quantity must be positive.");
                return;
            }

            for (CartItem ci : cart) {

                if (ci.getProductId() == productId) {

                    int oldQty = ci.getQuantity();

                    if (newQty > oldQty) {
                        // BUYING MORE → decrease stock
                        int diff = newQty - oldQty;
                        decreaseStockInDB(productId, diff);
                    } else if (newQty < oldQty) {
                        // RETURNING ITEMS → increase stock
                        int diff = oldQty - newQty;
                        increaseStockInDB(productId, diff);
                    }

                    ci.setQuantity(newQty);
                    break;
                }
            }

            refreshTable();

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Invalid number.");
        }
    }
    

    // ===========================
    // CHECKOUT
    // ===========================
    private void checkout() {
        if (cart.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Cart is empty.");
            return;
        }

        StringBuilder sb = new StringBuilder();
        sb.append("E-COMMERCE RECEIPT\n");
        sb.append("Date: ").append(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date())).append("\n\n");

        double total = 0;

        for (CartItem ci : cart) {
            double sub = ci.getPrice() * ci.getQuantity();
            sb.append(String.format("%s x%d  @ %.2f = %.2f\n",
                    ci.getProductName(),
                    ci.getQuantity(),
                    ci.getPrice(),
                    sub));
            total += sub;
        }

        sb.append("\nTotal: ₹").append(String.format("%.2f", total)).append("\n");

        ReceiptFrame rf = new ReceiptFrame(sb.toString());
        rf.setVisible(true);

        cart.clear();
        refreshTable();
    }
}
