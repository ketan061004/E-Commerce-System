public class Product {
    private int id;
    private String title;
    private String description;
    private double price;
    private int stock;
    private String images; // comma-separated file paths
    private Integer categoryId;
    private String categoryName;

    public Product() {}

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }

    public int getStock() { return stock; }
    public void setStock(int stock) { this.stock = stock; }

    public String getImages() { return images; }
    public void setImages(String images) { this.images = images; }

    public Integer getCategoryId() { return categoryId; }
    public void setCategoryId(Integer categoryId) { this.categoryId = categoryId; }

    public String getCategoryName() { return categoryName; }
    public void setCategoryName(String categoryName) { this.categoryName = categoryName; }

    @Override
    public String toString() {
        return "["+id+"] " + title + " | ₹" + price + " | stock: " + stock + (categoryName!=null ? " | " + categoryName : "");
    }
}
