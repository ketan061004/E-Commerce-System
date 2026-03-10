package ecom.dao;

import ecom.model.CartItem;
import ecom.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CartDAO {

    // ADD TO CART with proper stock check
    public boolean addToCart(int userId, int productId, int quantity) {
        if (quantity <= 0) {
            System.out.println("Quantity must be at least 1.");
            return false;
        }

        Connection con = null;
        PreparedStatement checkStockStmt = null;
        PreparedStatement addCartStmt = null;
        PreparedStatement updateStockStmt = null;
        ResultSet rs = null;

        try {
            con = DBConnection.getConnection();
            if (con == null) return false;

            // 1. Check stock
            String checkStockQuery = "SELECT stock FROM products WHERE id = ?";
            checkStockStmt = con.prepareStatement(checkStockQuery);
            checkStockStmt.setInt(1, productId);
            rs = checkStockStmt.executeQuery();

            if (!rs.next()) {
                System.out.println("Product not found.");
                return false;
            }

            int currentStock = rs.getInt("stock");

            if (currentStock <= 0 || currentStock < quantity) {
                System.out.println("Not enough stock available. Current stock: " + currentStock);
                return false;
            }

            // 2. Add to cart (insert or update)
            String addCartQuery =
                    "INSERT INTO cart (user_id, id, quantity) VALUES (?, ?, ?) " +
                    "ON DUPLICATE KEY UPDATE quantity = quantity + VALUES(quantity)";
            addCartStmt = con.prepareStatement(addCartQuery);
            addCartStmt.setInt(1, userId);
            addCartStmt.setInt(2, productId);
            addCartStmt.setInt(3, quantity);
            addCartStmt.executeUpdate();

            // 3. Reduce stock in products table
            String updateStockQuery =
                    "UPDATE products SET stock = stock - ? WHERE id = ?";
            updateStockStmt = con.prepareStatement(updateStockQuery);
            updateStockStmt.setInt(1, quantity);
            updateStockStmt.setInt(2, productId);
            updateStockStmt.executeUpdate();

            System.out.println("Product added to cart and stock updated.");
            return true;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            try { if (rs != null) rs.close(); } catch (Exception ignored) {}
            try { if (checkStockStmt != null) checkStockStmt.close(); } catch (Exception ignored) {}
            try { if (addCartStmt != null) addCartStmt.close(); } catch (Exception ignored) {}
            try { if (updateStockStmt != null) updateStockStmt.close(); } catch (Exception ignored) {}
        }
    }

    // GET CART ITEMS (used by CartDialog)
    public List<CartItem> getCartItems(int userId) {
        List<CartItem> list = new ArrayList<>();
        String sql = "SELECT user_id, id, quantity FROM cart WHERE user_id = ?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                CartItem item = new CartItem(
                        rs.getInt("user_id"),
                        rs.getInt("id"),
                        rs.getInt("quantity")
                );
                list.add(item);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    // REMOVE ITEM FROM CART and restore stock
    public boolean removeFromCart(int userId, int productId) {
        Connection con = null;
        PreparedStatement getQtyStmt = null;
        PreparedStatement deleteStmt = null;
        PreparedStatement restoreStockStmt = null;
        ResultSet rs = null;

        try {
            con = DBConnection.getConnection();
            if (con == null) return false;

            // Get quantity in cart
            String getQtySql = "SELECT quantity FROM cart WHERE user_id = ? AND id = ?";
            getQtyStmt = con.prepareStatement(getQtySql);
            getQtyStmt.setInt(1, userId);
            getQtyStmt.setInt(2, productId);
            rs = getQtyStmt.executeQuery();

            if (!rs.next()) {
                System.out.println("Item not found in cart.");
                return false;
            }

            int qty = rs.getInt("quantity");

            // Delete from cart
            String delSql = "DELETE FROM cart WHERE user_id = ? AND id = ?";
            deleteStmt = con.prepareStatement(delSql);
            deleteStmt.setInt(1, userId);
            deleteStmt.setInt(2, productId);
            deleteStmt.executeUpdate();

            // Restore stock to products
            String restoreSql = "UPDATE products SET stock = stock + ? WHERE id = ?";
            restoreStockStmt = con.prepareStatement(restoreSql);
            restoreStockStmt.setInt(1, qty);
            restoreStockStmt.setInt(2, productId);
            restoreStockStmt.executeUpdate();

            System.out.println("Item removed from cart and stock restored.");
            return true;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            try { if (rs != null) rs.close(); } catch (Exception ignored) {}
            try { if (getQtyStmt != null) getQtyStmt.close(); } catch (Exception ignored) {}
            try { if (deleteStmt != null) deleteStmt.close(); } catch (Exception ignored) {}
            try { if (restoreStockStmt != null) restoreStockStmt.close(); } catch (Exception ignored) {}
        }
    }

    // CLEAR CART on checkout (no stock restore here because stock was already deducted when adding)
    public boolean clearCart(int userId) {
        String sql = "DELETE FROM cart WHERE user_id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, userId);
            ps.executeUpdate();
            return true;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
