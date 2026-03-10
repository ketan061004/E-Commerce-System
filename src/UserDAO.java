import java.sql.*;

public class UserDAO {
    public boolean registerUser(User user) {
        String sql = "INSERT INTO users (name, email, password, is_admin, is_online) VALUES (?, ?, ?, ?, 0)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, user.getName());
            stmt.setString(2, user.getEmail());
            stmt.setString(3, HashUtil.sha256(user.getPassword()));
            stmt.setBoolean(4, user.isAdmin());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) { System.err.println("Registration error: " + e.getMessage()); return false; }
    }

    public User loginUser(String email, String password) {
        String sql = "SELECT * FROM users WHERE email=? AND password=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, email);
            stmt.setString(2, HashUtil.sha256(password));
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                User user = new User(rs.getInt("id"), rs.getString("name"), rs.getString("email"),
                    rs.getBoolean("is_admin"), rs.getBoolean("is_online"));
                setOnlineStatus(user.getId(), true);
                return user;
            }
        } catch (SQLException e) { System.err.println("Login error: " + e.getMessage()); }
        return null;
    }

    public void setOnlineStatus(int userId, boolean online) {
        String sql = "UPDATE users SET is_online=? WHERE id=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setBoolean(1, online);
            stmt.setInt(2, userId);
            stmt.executeUpdate();
        } catch (SQLException e) { System.err.println("Online status error: " + e.getMessage()); }
    }
    // Delete a user by email (admin menu)
public boolean deleteUserByEmail(String email) {
    String sql = "DELETE FROM users WHERE email = ?";
    try (Connection conn = DBConnection.getConnection();
         PreparedStatement stmt = conn.prepareStatement(sql)) {
        stmt.setString(1, email);
        int affected = stmt.executeUpdate();
        return affected > 0;
    } catch (Exception e) {
        System.err.println("Error deleting user: " + e.getMessage());
        return false;
    }
}
    // Display offline users (is_online = 0)
public void displayOfflineUsers() {
    String sql = "SELECT id, name, email FROM users WHERE is_online=0";
    try (Connection conn = DBConnection.getConnection();
         Statement stmt = conn.createStatement();
         ResultSet rs = stmt.executeQuery(sql)) {
        System.out.println("Currently offline users:");
        while (rs.next()) {
            System.out.println("[" + rs.getInt("id") + "] " + rs.getString("name") + " <" + rs.getString("email") + ">");
        }
    } catch (SQLException e) {
        System.err.println("Error listing offline users: " + e.getMessage());
    }
}

    /*  public void displayOnlineUsers() {
        String sql = "SELECT id, name, email FROM users WHERE is_online=1";
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            System.out.println("Currently online users:");
            while (rs.next()) {
                System.out.println("[" + rs.getInt("id") + "] " + rs.getString("name") + " <" + rs.getString("email") + ">");
            }
        } catch (SQLException e) { System.err.println("Error listing online users: " + e.getMessage()); }
    }*/

}
