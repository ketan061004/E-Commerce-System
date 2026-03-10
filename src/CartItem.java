public class CartItem {
    private int productId;
    private String productName;
    private double price;
    private int quantity;

    public CartItem() {}

    public CartItem(int productId, String productName, double price, int quantity) {
        this.productId = productId;
        this.productName = productName;
        this.price = price;
        this.quantity = quantity;
    }

    public int getProductId() { return productId; }
    public void setProductId(int productId) { this.productId = productId; }

    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }

    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    public void increaseQuantity(int amount) { this.quantity += amount; }
    public void decreaseQuantity(int amount) { this.quantity = Math.max(1, this.quantity - amount); }

    public double getTotalPrice() { return price * quantity; }

    @Override
    public String toString() {
        return String.format("%-5d %-20s Qty: %-3d Price: ₹%.2f  Total: ₹%.2f",
                productId, productName, quantity, price, getTotalPrice());
    }
}
