public class User {
    private int id;
    private String name;
    private String email;
    private boolean isAdmin;
    private boolean isOnline;
    private String password; // plain password for registration/login only

    public User() {}

    public User(int id, String name, String email, boolean isAdmin, boolean isOnline) {
        this.id = id; 
        this.name = name; 
        this.email = email;
        this.isAdmin = isAdmin; 
        this.isOnline = isOnline;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public boolean isAdmin() { return isAdmin; }
    public void setAdmin(boolean isAdmin) { this.isAdmin = isAdmin; }

    public boolean isOnline() { return isOnline; }
    public void setOnline(boolean isOnline) { this.isOnline = isOnline; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    @Override
    public String toString() {
        return "[" + id + "] " + name + " <" + email + ">" + (isAdmin ? " (admin)" : "");
    }
}
