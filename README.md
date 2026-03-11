 🛒 Java E-Commerce Management System

A Java-based E-Commerce Management System** that simulates an online shopping platform where users can browse products, add items to a cart, and generate purchase receipts.

The project includes a **Java Swing graphical user interface (GUI)** and database connectivity to manage users, products, and orders efficiently.

---

 📌 Features

👤 User Registration and Login
🛍 Browse products and categories
🛒 Add products to shopping cart
📦 Manage cart items
🧾 Generate purchase receipts
🛠 Admin panel for managing products
💾 Database connectivity for storing user and product data
🖥 Simple and interactive Java Swing interface

---

🧠 How It Works

The system follows a **database-driven architecture** where user data and product details are stored in a database.

Main workflow:

1. User registers or logs into the system
2. The system loads available products from the database
3. Users can add products to their cart
4. The cart calculates the total price
5. After checkout, a **receipt is generated**

The application uses **JDBC (Java Database Connectivity)** to interact with the database.

---

🖥️ Application Modules

1️⃣ User Authentication Module

Handles user registration and login.

Features:

* New user registration
* Secure login validation
* User information storage in database

Classes used:

* `User.java`
* `UserDAO.java`
* `LoginFrame.java`
* `RegisterFrame.java`

---

2️⃣ Product Management Module

Displays and manages products in the system.

Features:

* View available products
* Product categories
* Admin product management

Classes used:

* `Product.java`
* `ProductDAO.java`
* `Category.java`
* `ProductListPanel.java`

---

3️⃣ Shopping Cart Module

Allows users to add products and manage purchases.

Features:

* Add items to cart
* View cart items
* Calculate total price

Classes used:

* `CartFrame.java`
* `CartDAO.java`
* `CartItem.java`

---

4️⃣ Admin Panel Module

Allows administrators to control system data.

Features:

* Manage products
* Manage categories
* View system dashboard

Classes used:

* `AdminPanelFrame.java`
* `DashboardFrame.java`

---

📂 Project Structure

```
ECommerce-System
│
├── src
│   ├── AdminPanelFrame.java
│   ├── CartDAO.java
│   ├── CartFrame.java
│   ├── CartItem.java
│   ├── Category.java
│   ├── ConsoleUI.java
│   ├── DashboardFrame.java
│   ├── DBConnection.java
│   ├── ECommerceApp.java
│   ├── LoginFrame.java
│   ├── Main.java
│   ├── Product.java
│   ├── ProductDAO.java
│   ├── ProductListPanel.java
│   ├── ReceiptFrame.java
│   ├── RegisterFrame.java
│   ├── User.java
│   └── UserDAO.java
│
├── screenshots
│   └── output.png
│
├── presentation
│   └── ecommerce_presentation.pptx
│
└── README.md
```

---

⚙️ Installation & Setup

1️⃣ Clone the Repository

```
git clone https://github.com/yourusername/ecommerce-system.git
```

2️⃣ Open the Project

Open the project in any Java IDE:

* NetBeans
* Eclipse
* IntelliJ IDEA

---
3️⃣ Configure Database

Update database credentials in:

```
DBConnection.java
```

Example:

```
jdbc:mysql://localhost:3306/ecommerce
```

---
4️⃣ Compile the Program

```
javac *.java
```

---

5️⃣ Run the Application

```
java Main
```

The E-Commerce GUI application will start.

---

🧪 Example Usage

User Login

```
Username : user123
Password : password
```

---

Shopping Process

1. Login to the system
2. Browse available products
3. Add items to the cart
4. Checkout and generate receipt

---

🛠 Technologies Used

Java
Java Swing
JDBC
MySQL
Object-Oriented Programming (OOP)

---

🎓 Educational Purpose

This project demonstrates concepts of:

* E-Commerce system design
* Database connectivity with Java
* Java Swing GUI development
* Object-Oriented Programming
* Basic software architecture

---

👨‍💻 Author

Ketan Kumar Sahu
Aman Kumar Sahu
Ayush Kumar 
B.Tech – Computer Science / Information Technology

