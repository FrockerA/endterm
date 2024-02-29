import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

interface PaymentStrategy {
    void pay(double amount);
}


class Product {
    protected int id;
    protected String name;
    protected double price;

    public Product(int id, String name, double price) {
        this.id = id;
        this.name = name;
        this.price = price;
    }

    public void displayInfo() {
        System.out.println("Product ID: " + id);
        System.out.println("Product Name: " + name);
        System.out.println("Price: " + price);
    }
}



class ShoppingCart {
    private List<Product> items;
    private PaymentStrategy paymentStrategy;

    public ShoppingCart(PaymentStrategy paymentStrategy) {
        this.items = new ArrayList<>();
        this.paymentStrategy = paymentStrategy;
    }

    public void addItem(Product item) {
        items.add(item);
        System.out.println(item.name + " added to the cart.");
    }

    public void displayCart() {
        System.out.println("\nShopping Cart Contents:");
        for (Product item : items) {
            item.displayInfo();
        }
    }

    public void processPayment() {
        double totalAmount = calculateTotalAmount();
        paymentStrategy.pay(totalAmount);
        System.out.println("Thank you for your purchase!");
    }

    private double calculateTotalAmount() {
        return items.stream().mapToDouble(item -> item.price).sum();
    }

    public List<Product> getItems() {
        return items;
    }
}

public class EnhancedLaunch {
    private static Connection connection;
    private static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        initializeDatabase();

        while (true) {
            printMenu();

            System.out.print("Enter your choice (1-6): ");
            int choice = scanner.nextInt();
            scanner.nextLine();

            if (choice == 1) {
                createDonut();
            } else if (choice == 2) {
                readDonuts();
            } else if (choice == 3) {
                updateDonut();
            } else if (choice == 4) {
                deleteDonut();
            } else if (choice == 5) {
                buyDonuts();
            } else if (choice == 6) {
                closeDatabaseConnection();
                closeScanner();
                System.out.println("Exiting Donut Shop App. Goodbye!");
                System.exit(0);
            } else {
                System.out.println("Invalid choice. Please enter a number between 1 and 6.");
            }
        }
    }

    private static void printMenu() {
        System.out.println("\nDonut Shop Operations:");
        System.out.println("1. Create Donut");
        System.out.println("2. Read Donuts");
        System.out.println("3. Update Donut");
        System.out.println("4. Delete Donut");
        System.out.println("5. Buy Donuts");
        System.out.println("6. Exit");
    }

    private static void buyDonuts() {
        printPaymentOptions();

        System.out.print("Enter your choice (1-3): ");
        int paymentChoice = scanner.nextInt();
        scanner.nextLine();

        PaymentStrategy paymentStrategy = null;

        if (paymentChoice == 1) {
            System.out.print("Enter your PayPal email: ");
            String email = scanner.nextLine();
            paymentStrategy = new PayPalPaymentStrategy(email);
        } else if (paymentChoice == 2) {
            System.out.print("Enter your card number: ");
            String cardNumber = scanner.nextLine();
            System.out.print("Enter expiry date (MM/YYYY): ");
            String expiryDate = scanner.nextLine();
            paymentStrategy = new CardPaymentStrategy(cardNumber, expiryDate);
        } else if (paymentChoice == 3) {
            System.out.print("Enter your Google Pay phone number: ");
            String phoneNumber = scanner.nextLine();
            paymentStrategy = new GooglePayPaymentStrategy(phoneNumber);
        } else {
            System.out.println("Invalid payment choice. Please enter 1, 2, or 3.");
            return;
        }

        ShoppingCart shoppingCart = new ShoppingCart(paymentStrategy);

        while (true) {
            printBuyDonutsMenu();

            System.out.print("Enter your choice (1-3): ");
            int cartChoice = scanner.nextInt();
            scanner.nextLine();

            if (cartChoice == 1) {
                addDonutToCart(shoppingCart);
            } else if (cartChoice == 2) {
                shoppingCart.displayCart();
            } else if (cartChoice == 3) {
                shoppingCart.processPayment();
                break;
            } else {
                System.out.println("Invalid choice. Please enter a number between 1 and 3.");
            }
        }
    }

    private static void printBuyDonutsMenu() {
        System.out.println("\nAdd items to your shopping cart:");
        System.out.println("1. Add Donut");
        System.out.println("2. View Shopping Cart");
        System.out.println("3. Proceed to Payment");
    }

    private static void addDonutToCart(ShoppingCart shoppingCart) {
        System.out.print("Enter Donut ID to add to cart: ");
        int donutId = scanner.nextInt();
        scanner.nextLine();

        Donut selectedDonut = findDonutById(donutId);

        if (selectedDonut != null) {
            shoppingCart.addItem(selectedDonut);
        } else {
            System.out.println("Invalid Donut ID.");
        }
    }

    private static void initializeDatabase() {
        try {
            String url = "jdbc:postgresql://localhost:5432/Person";
            String username = "postgres";
            String password = "fasada2015";

            connection = DriverManager.getConnection(url, username, password);

            createDonutsTable();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void createDonutsTable() {
        String createTableSQL = "CREATE TABLE IF NOT EXISTS donuts (" +
                "id SERIAL PRIMARY KEY," +
                "donut_name VARCHAR(255) NOT NULL," +
                "donut_flavor VARCHAR(255)," +
                "donut_price DOUBLE PRECISION," +
                "donut_size VARCHAR(255)" +
                ")";
        try (Statement statement = connection.createStatement()) {
            statement.executeUpdate(createTableSQL);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void createDonut() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("\nEnter donut details:");

        System.out.print("Donut Name: ");
        String donutName = scanner.nextLine();

        System.out.print("Donut Flavor: ");
        String donutFlavor = scanner.nextLine();

        System.out.print("Donut Price: ");
        double donutPrice = scanner.nextDouble();
        scanner.nextLine();

        System.out.print("Donut Size: ");
        String donutSize = scanner.nextLine();

        insertDonut(donutName, donutFlavor, donutPrice, donutSize);

        System.out.println("Donut created successfully!");
    }

    private static void readDonuts() {
        System.out.println("\nList of Donuts:");

        String selectDonutsSQL = "SELECT * FROM donuts";

        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(selectDonutsSQL)) {

            while (resultSet.next()) {
                int donutId = resultSet.getInt("id");
                String donutName = resultSet.getString("donut_name");
                String donutFlavor = resultSet.getString("donut_flavor");
                double donutPrice = resultSet.getDouble("donut_price");
                String donutSize = resultSet.getString("donut_size");

                Donut donut = new Donut(donutId, donutName, donutPrice, donutFlavor);
                donut.displayInfo();
                System.out.println();
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void insertDonut(String donutName, String donutFlavor, double donutPrice, String donutSize) {
        String insertDonutSQL = "INSERT INTO donuts (donut_name, donut_flavor, donut_price, donut_size) " +
                "VALUES (?, ?, ?, ?)";

        try (PreparedStatement preparedStatement = connection.prepareStatement(insertDonutSQL, Statement.RETURN_GENERATED_KEYS)) {
            preparedStatement.setString(1, donutName);
            preparedStatement.setString(2, donutFlavor);
            preparedStatement.setDouble(3, donutPrice);
            preparedStatement.setString(4, donutSize);

            preparedStatement.executeUpdate();

            try (ResultSet generatedKeys = preparedStatement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    int donutId = generatedKeys.getInt(1);
                    System.out.println("Donut created successfully! Donut ID: " + donutId);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void updateDonut() {
        Scanner scanner = new Scanner(System.in);
        System.out.print("\nEnter the Donut ID to update: ");
        int donutId = scanner.nextInt();
        scanner.nextLine();

        System.out.println("Enter new details for the donut:");

        System.out.print("Donut Name: ");
        String newDonutName = scanner.nextLine();

        System.out.print("Donut Flavor: ");
        String newDonutFlavor = scanner.nextLine();

        System.out.print("Donut Price: ");
        double newDonutPrice = scanner.nextDouble();
        scanner.nextLine();

        System.out.print("Donut Size: ");
        String newDonutSize = scanner.nextLine();

        String updateDonutSQL = "UPDATE donuts SET donut_name = ?, donut_flavor = ?, donut_price = ?, donut_size = ? WHERE id = ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(updateDonutSQL)) {
            preparedStatement.setString(1, newDonutName);
            preparedStatement.setString(2, newDonutFlavor);
            preparedStatement.setDouble(3, newDonutPrice);
            preparedStatement.setString(4, newDonutSize);
            preparedStatement.setInt(5, donutId);

            preparedStatement.executeUpdate();

            System.out.println("Donut updated successfully!");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void deleteDonut() {
        Scanner scanner = new Scanner(System.in);
        System.out.print("\nEnter the Donut ID to delete: ");
        int donutId = scanner.nextInt();

        String deleteDonutSQL = "DELETE FROM donuts WHERE id = ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(deleteDonutSQL)) {
            preparedStatement.setInt(1, donutId);

            preparedStatement.executeUpdate();

            System.out.println("Donut deleted successfully!");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static Donut findDonutById(int donutId) {
        String selectDonutSQL = "SELECT * FROM donuts WHERE id = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(selectDonutSQL)) {
            preparedStatement.setInt(1, donutId);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    int id = resultSet.getInt("id");
                    String donutName = resultSet.getString("donut_name");
                    String donutFlavor = resultSet.getString("donut_flavor");
                    double donutPrice = resultSet.getDouble("donut_price");

                    return new Donut(id, donutName, donutPrice, donutFlavor);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    private static void printPaymentOptions() {
        System.out.println("\nChoose a payment method:");
        System.out.println("1. PayPal");
        System.out.println("2. Card");
        System.out.println("3. Google Pay");
    }

    private static void closeDatabaseConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void closeScanner() {
        scanner.close();
    }
}
