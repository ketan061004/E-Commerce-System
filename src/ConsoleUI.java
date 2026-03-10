import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.*;

public class ConsoleUI {
    private UserDAO userDAO = new UserDAO();
    private ProductDAO productDAO = new ProductDAO();
    private Scanner scanner = new Scanner(System.in);
    private List<CartItem> cart = new ArrayList<>();

    public void start() {
        boolean exit = false;
        while (!exit) {
            System.out.println("\n=== Welcome to E-Commerce System ===");
            System.out.println("1. Register (user)");
            System.out.println("2. Login");
            System.out.println("3. Exit");
            System.out.print("Choose option: ");
            String choice = scanner.nextLine();

            switch (choice) {
                case "1":
                    registerUser();
                    break;
                /*
                 * case "2": {
                 * User user = loginUser();
                 * if (user != null) {
                 * System.out.println("\nLogin successful. Welcome " + user.getName() + "!");
                 * if (user.isAdmin()) adminMenu(user);
                 * else userMenu(user);
                 * }
                 * break;
                 * }
                 */
                case "2": {
                    System.out.println("Login as: 1. Admin  2. User");
                    String type = scanner.nextLine().trim();

                    if (type.equals("1")) { // Admin login
                        System.out.print("Enter admin id: ");
                        String adminId = scanner.nextLine();
                        System.out.print("Enter admin password: ");
                        String adminPassword = scanner.nextLine();

                        // Fixed admin credentials
                        if (adminId.equals("admin") && adminPassword.equals("admin123")) {
                            System.out.println("\nAdmin login successful!");
                            adminMenu(null); // or pass a dummy User if needed
                        } else {
                            System.out.println("Invalid admin credentials.");
                        }

                    } else if (type.equals("2")) { // Normal user login
                        User user = loginUser();
                        if (user != null) {
                            System.out.println("\nLogin successful. Welcome " + user.getName() + "!");
                            userMenu(user);
                        }
                    } else {
                        System.out.println("Invalid choice.");
                    }
                    break;
                }
                case "3":
                    System.out.println("Goodbye!");
                    exit = true;
                    break;
                default:
                    System.out.println("Invalid input. Try again.");
            }
        }
    }

    // Registration with admin/user role selection
    private void registerUser() {
        System.out.print("Enter name: ");
        String name = scanner.nextLine();
        System.out.print("Enter email: ");
        String email = scanner.nextLine();
        System.out.print("Enter password: ");
        String password = scanner.nextLine();
        /*
         * System.out.print("Register as admin? (y/n): ");
         * String adminInput = scanner.nextLine().trim().toLowerCase();
         * boolean isAdmin = adminInput.equals("y") || adminInput.equals("yes");
         */
        User user = new User();
        user.setName(name);
        user.setEmail(email);
        user.setPassword(password);
        user.setAdmin(false);
        boolean success = userDAO.registerUser(user);
        if (success) {
            // String role = isAdmin ? "Admin" : "User";
            System.out.println("Registration successful!. You can now login.");
        } else {
            System.out.println("Registration failed. Email might already exist.");
        }
    }

    private User loginUser() {
        System.out.print("Enter email: ");
        String email = scanner.nextLine();
        System.out.print("Enter password: ");
        String password = scanner.nextLine();
        User user = userDAO.loginUser(email, password);
        if (user == null)
            System.out.println("Login failed. Invalid credentials.");
        return user;
    }

    private void adminMenu(User admin) {
        while (true) {
            System.out.println("\n[Admin Menu]");
            System.out.println("1. View offline registered users");
            System.out.println("2. View total products");
            System.out.println("3. View total categories");
            System.out.println("4. Add category");
            System.out.println("5. Add product");
            System.out.println("6. View products");
            System.out.println("7. View categories");
            System.out.println("8. Delete registered user (online/offline)");
            System.out.println("9. Logout");
            System.out.println("10. Delete product");
            System.out.println("11. Delete category");

            System.out.print("Choose option: ");
            String choice = scanner.nextLine();
            switch (choice) {
                // case "1": userDAO.displayOnlineUsers(); break;
                case "1":
                    userDAO.displayOfflineUsers();
                    break;
                case "2":
                    productDAO.displayTotalProducts();
                    break;
                 case "3": productDAO.displayTotalCategories(); break;
                 case "4": productDAO.addCategoryPrompt(scanner); break;
                case "5":
                    productDAO.addProductPrompt(scanner);
                    break;
                case "6":
                    productDAO.displayAllProducts();
                    break;
                case "7": productDAO.displayAllCategories(); break;
                case "8":
                    deleteRegisteredUserPrompt();
                    break;
                // case "6": userDAO.setOnlineStatus(admin.getId(), false); return;

                case "9":
                    if (admin != null) {
                        userDAO.setOnlineStatus(admin.getId(), false);
                    }
                    return;
                case "10":
                    productDAO.deleteProductPrompt(scanner);
                    break;
                case "11": productDAO.deleteCategoryPrompt(scanner); break;

                default:
                    System.out.println("Invalid input. Try again.");
            }
        }
    }

    private void userMenu(User user) {
        cart.clear();
        while (true) {
            System.out.println("\n[User Menu]");
            System.out.println("1. Browse products");
            System.out.println("2. Add product to cart");
            System.out.println("3. View cart");
            System.out.println("4. Remove from cart");
            System.out.println("5. Checkout");
            System.out.println("6. Logout");
            System.out.print("Choose option: ");
            String choice = scanner.nextLine();
            switch (choice) {
                // case "1": productDAO.displayAllProducts(); break;
                /*case "1": // Browse products by category
                    productDAO.displayAllCategories();
                    System.out.print("Enter Category ID to view products: ");
                    int catId = Integer.parseInt(scanner.nextLine());
                    productDAO.displayProductsByCategory(catId);
                    break;*/
                case "1":  // Browse products by category
    System.out.println("\n=== Browse Products ===");

    // Step 1 → Show all categories
    productDAO.displayAllCategories();
    
    System.out.print("Choose a category ID to view products: ");
    int categoryId = Integer.parseInt(scanner.nextLine());

    System.out.println("\n--- Products under selected category ---");

    if (!productDAO.displayProductsByCategory(categoryId)) {
        System.out.println("Invalid category or no products found.");
    }
    break;


                case "2":
                    System.out.print("Enter Product ID to add to cart: ");
                    int productId = scanner.nextInt();
                    System.out.print("Enter quantity: ");
                    int quantity = scanner.nextInt();
                    scanner.nextLine(); // clear buffer
                    addProductToCart(user.getId(), productId, quantity);
                    break;

                case "3":
                    displayCartFromDB(user.getId());
                    break;

                case "4":
                    removeFromCartPrompt(user);
                    break;
                case "5":
                    productDAO.checkout(user, scanner);
                    break;
                case "6":
                    userDAO.setOnlineStatus(user.getId(), false);
                    return;
                default:
                    System.out.println("Invalid input. Try again.");
            }
        }
    }

    private void displayCartFromDB(int userId) {
        Connection con = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            con = DBConnection.getConnection();
            String sql = "SELECT c.id, c.quantity, p.title, p.price FROM cart c JOIN products p ON c.id = p.id WHERE c.user_id = ?";
            stmt = con.prepareStatement(sql);
            stmt.setInt(1, userId);
            rs = stmt.executeQuery();

            boolean found = false;
            System.out.println("Items in your cart:");
            while (rs.next()) {
                found = true;
                int pid = rs.getInt("id");
                int qty = rs.getInt("quantity");
                String title = rs.getString("title");
                double price = rs.getDouble("price");
                System.out.println(
                        "Product ID: " + pid + ", Title: " + title + ", Quantity: " + qty + ", Price: " + price);
            }
            if (!found) {
                System.out.println("Your cart is empty.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // close resources
        }
    }

    public boolean addProductToCart(int userId, int productId, int quantity) {
        Connection con = null;
        PreparedStatement addCartStmt = null;
        PreparedStatement updateStockStmt = null;
        ResultSet rs = null;
        try {
            con = DBConnection.getConnection();

            // Check current stock
            String checkStockQuery = "SELECT stock FROM products WHERE id = ?";
            PreparedStatement checkStockStmt = con.prepareStatement(checkStockQuery);
            checkStockStmt.setInt(1, productId);
            rs = checkStockStmt.executeQuery();
            if (!rs.next() || rs.getInt("stock") < quantity) {
                System.out.println("Not enough stock available.");
                return false;
            }

            // Add to cart (insert or update quantity)
            String addCartQuery = "INSERT INTO cart (user_id, id , quantity) VALUES (?, ?, ?) "
                    + "ON DUPLICATE KEY UPDATE quantity = quantity + ?";
            addCartStmt = con.prepareStatement(addCartQuery);
            addCartStmt.setInt(1, userId);
            addCartStmt.setInt(2, productId);
            addCartStmt.setInt(3, quantity);
            addCartStmt.setInt(4, quantity);
            addCartStmt.executeUpdate();

            // Update product stock
            String updateStockQuery = "UPDATE products SET stock = stock - ? WHERE id = ? AND stock >= ?";
            updateStockStmt = con.prepareStatement(updateStockQuery);
            updateStockStmt.setInt(1, quantity);
            updateStockStmt.setInt(2, productId);
            updateStockStmt.setInt(3, quantity); // Check so stock does not go negative
            int rows = updateStockStmt.executeUpdate();

            if (rows > 0) {
                System.out.println("Product added to cart and stock updated.");
                return true;
            } else {
                System.out.println("Stock update failed, possible concurrency issue.");
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            // close statements and connection if needed
        }
    }

    private void displayCart() {
        if (cart.isEmpty()) {
            System.out.println("Your cart is empty.");
            return;
        }
        System.out.println("Items in cart:");
        for (CartItem item : cart)
            System.out.println(item);
    }

    private void removeFromCartPrompt(User user) {
        // Display cart from DB first
        displayCartFromDB(user.getId());
        System.out.print("Enter Product ID to remove: ");
        try {
            int pid = Integer.parseInt(scanner.nextLine());
            try (Connection con = DBConnection.getConnection();
                    PreparedStatement stmt = con.prepareStatement("DELETE FROM cart WHERE user_id = ? AND id = ?")) {
                stmt.setInt(1, user.getId());
                stmt.setInt(2, pid);
                int rows = stmt.executeUpdate();
                if (rows > 0) {
                    System.out.println("Product removed from cart.");
                } else {
                    System.out.println("Product not found in cart.");
                }
            }
        } catch (Exception e) {
            System.out.println("Invalid input.");
        }
    }

    // Admin utility: delete registered user by email
    private void deleteRegisteredUserPrompt() {
        System.out.print("Enter email of user to delete: ");
        String email = scanner.nextLine();
        boolean success = userDAO.deleteUserByEmail(email);
        System.out.println(success ? "User deleted." : "Delete failed. Email not found or error occurred.");
    }
}
