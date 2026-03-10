import java.sql.*;
import java.util.*;

public class ProductDAO {
    public void displayTotalProducts() {
        String sql = "SELECT COUNT(*) FROM products";
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) System.out.println("Total products: " + rs.getInt(1));
        } catch (SQLException e) { System.err.println("Error counting products: " + e.getMessage()); }
    }
    public void displayTotalCategories() {
        String sql = "SELECT COUNT(*) FROM categories";
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) System.out.println("Total categories: " + rs.getInt(1));
        } catch (SQLException e) { System.err.println("Error counting categories: " + e.getMessage()); }
    }

    /*public void addCategoryPrompt(Scanner scanner) {
        System.out.print("Enter category name: ");
        String name = scanner.nextLine();
        System.out.print("Enter category description: ");
        String desc = scanner.nextLine();
        String sql = "INSERT INTO categories (name, description) VALUES (?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, name);
            stmt.setString(2, desc);
            stmt.executeUpdate();
            System.out.println("Category added.");
        } catch (SQLException e) { System.err.println("Error adding category: " + e.getMessage()); }
    }*/
    
public void addCategoryPrompt(Scanner scanner) {
    System.out.print("Enter category name: ");
    String name = scanner.nextLine();

    System.out.print("Enter category description: ");
    String description = scanner.nextLine();

    String sql = "INSERT INTO categories (name, description) VALUES (?, ?)";

    try (Connection con = DBConnection.getConnection();
         PreparedStatement stmt = con.prepareStatement(sql)) {

        stmt.setString(1, name);
        stmt.setString(2, description);

        int rows = stmt.executeUpdate();
        if (rows > 0) {
            System.out.println("Category added successfully!");
        } else {
            System.out.println("Failed to add category.");
        }

    } catch (SQLException e) {
        System.out.println("Error adding category.");
        e.printStackTrace();
    }
}




    /*public void addProductPrompt(Scanner scanner) {
        System.out.print("Enter product title: ");
        String title = scanner.nextLine();
        System.out.print("Enter description: ");
        String desc = scanner.nextLine();
        System.out.print("Enter price: ");
        double price = Double.parseDouble(scanner.nextLine());
        System.out.print("Enter stock: ");
        int stock = Integer.parseInt(scanner.nextLine());
        System.out.print("Enter images path: ");
        String images = scanner.nextLine();
        System.out.print("Enter category ID: ");
        int catId = Integer.parseInt(scanner.nextLine());
        String sql = "INSERT INTO products (title, description, price, stock, images, category_id) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, title); stmt.setString(2, desc);
            stmt.setDouble(3, price); stmt.setInt(4, stock);
            stmt.setString(5, images); stmt.setInt(6, catId);
            stmt.executeUpdate();
            System.out.println("Product added.");
        } catch (SQLException e) { System.err.println("Error adding product: " + e.getMessage()); }
    }*/
    public void addProductPrompt(Scanner scanner) {
    try (Connection con = DBConnection.getConnection();
         PreparedStatement stmt = con.prepareStatement("SELECT * FROM categories")) {
        
        ResultSet rs = stmt.executeQuery();
        System.out.println("Available categories:");
        while (rs.next()) {
    int id = rs.getInt("id");

    // Use correct column
    String name;
    try {
        name = rs.getString("name");   // RECOMMENDED for your DB
    } catch (SQLException ex) {
        name = rs.getString(2);        // fallback: 2nd column
    }

    System.out.println(id + ". " + name);
}

    } catch (Exception e) {
        e.printStackTrace();
    }

    System.out.print("Enter product title: ");
    String title = scanner.nextLine();
    System.out.print("Enter description: ");
    String description = scanner.nextLine();
    System.out.print("Enter price: ");
    double price = Double.parseDouble(scanner.nextLine());
    System.out.print("Enter stock quantity: ");
    int stock = Integer.parseInt(scanner.nextLine());
    System.out.print("Enter images path: ");
    String images = scanner.nextLine();
    System.out.print("Enter category ID from above list: ");
    int categoryId = Integer.parseInt(scanner.nextLine());

    String sql = "INSERT INTO products (title, description, price, stock, images, category_id) VALUES (?, ?, ?, ?, ?, ?)";

    try (Connection con = DBConnection.getConnection();
         PreparedStatement pstmt = con.prepareStatement(sql)) {
        pstmt.setString(1, title);
        pstmt.setString(2, description);
        pstmt.setDouble(3, price);
        pstmt.setInt(4, stock);
        pstmt.setString(5, images);
        pstmt.setInt(6, categoryId);

        int rows = pstmt.executeUpdate();
        System.out.println(rows > 0 ? "Product added successfully." : "Failed to add product.");
    } catch (Exception e) {
        e.printStackTrace();
        System.out.println("Error adding product.");
    }
}


    public void displayAllProducts() {
        String sql = "SELECT id, title, price, stock FROM products";
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            System.out.println("Products:");
            while (rs.next()) {
                System.out.printf("[%d] %s | ₹%.2f | stock: %d\n", rs.getInt(1), rs.getString(2), rs.getDouble(3), rs.getInt(4));
            }
        } catch (SQLException e) { System.err.println("Error listing products: " + e.getMessage()); }
    }

    public Product getProductById(int id) {
        String sql = "SELECT * FROM products WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                Product p = new Product();
                p.setId(rs.getInt("id")); p.setTitle(rs.getString("title"));
                p.setPrice(rs.getDouble("price")); p.setStock(rs.getInt("stock"));
                p.setDescription(rs.getString("description"));
                p.setImages(rs.getString("images"));
                p.setCategoryId(rs.getInt("category_id"));
                return p;
            }
        } catch (SQLException e) { System.err.println("Get product error: " + e.getMessage()); }
        return null;
    }
    // Display all categories (admin and user menu)
/*public void displayAllCategories() {
    String sql = "SELECT id, name, description FROM categories";
    try (Connection conn = DBConnection.getConnection();
         Statement stmt = conn.createStatement();
         ResultSet rs = stmt.executeQuery(sql)) {
        System.out.println("Categories:");
        while (rs.next()) {
            System.out.printf("[%d] %s - %s\n", rs.getInt("id"), rs.getString("name"), rs.getString("description"));
        }
    } catch (Exception e) {
        System.err.println("Error getting categories: " + e.getMessage());
    }
}*/
/*public void displayAllCategories() {
    try (Connection con = DBConnection.getConnection();
         PreparedStatement stmt = con.prepareStatement("SELECT * FROM categories")) {
        ResultSet rs = stmt.executeQuery();
        System.out.println("Categories:");
        while (rs.next()) {
            System.out.println(rs.getInt("id") + ". " + rs.getString("title") + " - " + rs.getString("description"));
        }
    } catch (Exception e) {
        e.printStackTrace();
    }
}
public boolean displayProductsByCategory(int categoryId) {
    String sql = "SELECT * FROM products WHERE category_id = ?";
    boolean found = false;

    try (Connection con = DBConnection.getConnection();
         PreparedStatement stmt = con.prepareStatement(sql)) {

        stmt.setInt(1, categoryId);
        ResultSet rs = stmt.executeQuery();

        System.out.println("Products in this category:");
        while (rs.next()) {
            found = true;
            System.out.println(
                rs.getInt("id") + ". " + rs.getString("title") +
                " | Price: ₹" + rs.getDouble("price") +
                " | Stock: " + rs.getInt("stock"));
        }
    } catch (Exception e) {
        e.printStackTrace();
    }

    return found;
}*/
// Robust displayAllCategories: uses ResultSetMetaData to find sensible column names
public void displayAllCategories() {
    String sql = "SELECT * FROM categories";
    try (Connection con = DBConnection.getConnection();
         PreparedStatement stmt = con.prepareStatement(sql);
         ResultSet rs = stmt.executeQuery()) {

        ResultSetMetaData md = rs.getMetaData();
        int colCount = md.getColumnCount();

        // Detect likely column names
        String idCol = null, titleCol = null, descCol = null;
        for (int i = 1; i <= colCount; i++) {
            String col = md.getColumnLabel(i).toLowerCase();
            if (idCol == null && (col.equals("id") || col.endsWith("_id") || col.equals("category_id")))
                idCol = md.getColumnLabel(i);
            if (titleCol == null && (col.equals("title") || col.equals("name") || col.equals("category") || col.equals("category_name") || col.equals("cat_name")))
                titleCol = md.getColumnLabel(i);
            if (descCol == null && (col.equals("description") || col.equals("desc") || col.equals("details")))
                descCol = md.getColumnLabel(i);
        }

        // Fallbacks if not found
        if (idCol == null) idCol = md.getColumnLabel(1);
        if (titleCol == null) {
            // try second column, or same as idCol
            titleCol = (colCount >= 2) ? md.getColumnLabel(2) : idCol;
        }
        if (descCol == null && colCount >= 3) descCol = md.getColumnLabel(Math.min(3, colCount));

        System.out.println("Categories:");
        boolean found = false;
        while (rs.next()) {
            found = true;
            int id = rs.getInt(idCol);
            String title = rs.getString(titleCol);
            String desc = (descCol != null) ? rs.getString(descCol) : "";
            System.out.println(id + ". " + title + (desc != null && !desc.isEmpty() ? " - " + desc : ""));
        }
        if (!found) {
            System.out.println("No categories found.");
        }
    } catch (SQLException e) {
        System.err.println("Error fetching categories: " + e.getMessage());
        e.printStackTrace();
    }
}

/**
 * Robust displayProductsByCategory. Returns true if at least one product displayed.
 */
public boolean displayProductsByCategory(int categoryId) {
    String sql = "SELECT * FROM products WHERE category_id = ?";
    boolean found = false;

    try (Connection con = DBConnection.getConnection();
         PreparedStatement stmt = con.prepareStatement(sql)) {

        stmt.setInt(1, categoryId);
        try (ResultSet rs = stmt.executeQuery()) {

            ResultSetMetaData md = rs.getMetaData();
            int colCount = md.getColumnCount();

            String idCol = null, titleCol = null, priceCol = null, stockCol = null;
            for (int i = 1; i <= colCount; i++) {
                String col = md.getColumnLabel(i).toLowerCase();
                if (idCol == null && (col.equals("id") || col.endsWith("_id") || col.equals("product_id")))
                    idCol = md.getColumnLabel(i);
                if (titleCol == null && (col.equals("title") || col.equals("name") || col.equals("product") || col.equals("product_name")))
                    titleCol = md.getColumnLabel(i);
                if (priceCol == null && (col.equals("price") || col.equals("cost") || col.equals("mrp")))
                    priceCol = md.getColumnLabel(i);
                if (stockCol == null && (col.equals("stock") || col.equals("quantity") || col.equals("qty")))
                    stockCol = md.getColumnLabel(i);
            }

            if (idCol == null) idCol = md.getColumnLabel(1);
            if (titleCol == null) titleCol = (colCount >= 2) ? md.getColumnLabel(2) : idCol;

            System.out.println("Products in this category:");
            while (rs.next()) {
                found = true;
                int id = rs.getInt(idCol);
                String title = rs.getString(titleCol);
                String priceStr = (priceCol != null) ? String.valueOf(rs.getDouble(priceCol)) : "N/A";
                String stockStr = (stockCol != null) ? String.valueOf(rs.getInt(stockCol)) : "N/A";

                System.out.println(id + ". " + title + " | Price: " + priceStr + " | Stock: " + stockStr);
            }

            if (!found) {
                System.out.println("No products found for this category.");
            }
        }
    } catch (SQLException e) {
        System.err.println("Error fetching products by category: " + e.getMessage());
        e.printStackTrace();
    }

    return found;
}


    public void deleteProductPrompt(Scanner scanner) {
    System.out.print("Enter Product ID to delete: ");
    int productId = Integer.parseInt(scanner.nextLine());
    try (Connection con = DBConnection.getConnection();
         PreparedStatement stmt = con.prepareStatement("DELETE FROM products WHERE id = ?")) {
        stmt.setInt(1, productId);
        int rows = stmt.executeUpdate();
        System.out.println(rows > 0 ? "Product deleted." : "Product ID not found or already deleted.");
    } catch (Exception e) {
        e.printStackTrace();
        System.out.println("Error deleting product.");
    }
}

    public void deleteCategoryPrompt(Scanner scanner) {
    System.out.print("Enter Category ID to delete: ");
    int categoryId = Integer.parseInt(scanner.nextLine());
    try (Connection con = DBConnection.getConnection();
         PreparedStatement stmt = con.prepareStatement("DELETE FROM categories WHERE id = ?")) {
        stmt.setInt(1, categoryId);
        int rows = stmt.executeUpdate();
        System.out.println(rows > 0 ? "Category deleted." : "Category ID not found or already deleted.");
    } catch (Exception e) {
        e.printStackTrace();
        System.out.println("Error deleting category.");
    }
}


public void checkout(User user, Scanner scanner) {
    Connection con = null;
    PreparedStatement stmt = null;
    ResultSet rs = null;

    try {
        con = DBConnection.getConnection();

        // 1. Read cart items for this user
        String sql = "SELECT c.id, c.quantity, p.title, p.price " +
                     "FROM cart c JOIN products p ON c.id = p.id " +
                     "WHERE c.user_id = ?";
        stmt = con.prepareStatement(sql);
        stmt.setInt(1, user.getId());
        rs = stmt.executeQuery();

        // 2. Build receipt lines and total
        List<String> receiptLines = new ArrayList<>();
        double total = 0.0;

        System.out.println("\nYour Cart Summary:");
        while (rs.next()) {
            int pid = rs.getInt("id");
            int qty = rs.getInt("quantity");
            String title = rs.getString("title");
            double price = rs.getDouble("price");
            double lineTotal = qty * price;

            String line = "Product ID: " + pid +
                          " | " + title +
                          " | Qty: " + qty +
                          " | Price: " + price +
                          " | Subtotal: " + lineTotal;

            System.out.println(line);          // show now
            receiptLines.add(line);            // save for receipt

            total += lineTotal;
        }

        if (receiptLines.isEmpty()) {
            System.out.println("Your cart is empty. Cannot checkout.");
            return;
        }

        System.out.println("Total amount: " + total);

        // 3. Address and payment method
        System.out.print("Enter shipping address: ");
        String address = scanner.nextLine();

        System.out.print("Choose payment method [1: Cash on delivery, 2: Card]: ");
        String paymentOption = scanner.nextLine();
        String paymentMethod = paymentOption.equals("2") ? "Card" : "Cash on delivery";

        System.out.print("Confirm checkout? [y/n]: ");
        String confirm = scanner.nextLine();

        if (!confirm.equalsIgnoreCase("y")) {
            System.out.println("Checkout cancelled.");
            return;
        }

        // 4. Clear cart
        PreparedStatement clearCart = con.prepareStatement("DELETE FROM cart WHERE user_id = ?");
        clearCart.setInt(1, user.getId());
        clearCart.executeUpdate();

        // 5. Print receipt automatically
        System.out.println("\n===== ORDER RECEIPT =====");
        System.out.println("Customer: " + user.getName() + " (ID: " + user.getId() + ")");
        System.out.println("Shipping address: " + address);
        System.out.println("Payment method: " + paymentMethod);
        System.out.println("\nItems:");
        for (String line : receiptLines) {
            System.out.println(line);
        }
        System.out.println("\nTotal paid: " + total);
        System.out.println("Date/Time: " + java.time.LocalDateTime.now());
        System.out.println("Thank you for shopping!");
        System.out.println("=========================\n");

    } catch (Exception e) {
        e.printStackTrace();
        System.out.println("Error during checkout.");
    } finally {
        // close rs, stmt, con if you have helper methods
    }
}

        
}
