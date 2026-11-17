package week_8;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/** Talk to your database from here.

 Use the db_url variable in DBConfig to create your database connection
 This is necessary so the tests can replace your database with a test database.

 You can access this variable with

 DBConfig.db_url

*/

public class ProductDB {

    // TODO add methods here to work with the database.
    public List<String> fetchAll() {
        // Create an empty list to store products
        List<String> products = new ArrayList<>();

        // SQL query to get all products in alphabetical order
        String sql = "SELECT name, quantity FROM inventory ORDER BY name";
        String url = DBConfig.db_url;
        // Connect to the database and prepare the query
        try (Connection connection = DriverManager.getConnection(url);
             PreparedStatement statement = connection.prepareStatement(sql)) {

            // Execute the query
            ResultSet rs = statement.executeQuery();
            // Read each product from the result
            while (rs.next()) {
                String name = rs.getString("name");
                int quantity = rs.getInt("quantity");

                // Add the product to the list
                products.add(name + ": " + quantity);
            }

        } catch (SQLException e) {
            System.out.println("Database error: " + e.getMessage());
        }

        return products;
    }

    public boolean insertProduct(String name, int quantity) {
        // SQL query to insert a product
        String sql = "INSERT INTO inventory (name, quantity) VALUES (?, ?)";

        // Connect to the database and prepare the query
        try (Connection connection = DriverManager.getConnection(DBConfig.db_url);
             PreparedStatement statement = connection.prepareStatement(sql)) {

            // Set the product name and quantity
            statement.setString(1, name);
            statement.setInt(2, quantity);

            // Execute the query and return true if a product was added
            return statement.executeUpdate() > 0;

        } catch (SQLException e) {
            System.out.println("Database error: " + e.getMessage());
            return false;
        }
    }

    public boolean updateProductQuantity(String name, int quantity) {

        // SQL query to update product quantity
        String sql = "UPDATE inventory SET quantity = ? WHERE name = ?";

        // Connect to the database and prepare the query
        try (Connection connection = DriverManager.getConnection(DBConfig.db_url);
             PreparedStatement statement = connection.prepareStatement(sql)) {

            // Set the new quantity and product name
            statement.setInt(1, quantity);
            statement.setString(2, name);

            // Return true if a product was updated
            return statement.executeUpdate() > 0;

        } catch (SQLException e) {
            // Handle database errors
            System.out.println("Database error: " + e.getMessage());
            return false;
        }
    }

    public boolean removeProduct(String name) {

        // SQL query to delete a product by name
        String sql = "DELETE FROM inventory WHERE name = ?";

        // Connect to the database and prepare the query
        try (Connection connection = DriverManager.getConnection(DBConfig.db_url);
             PreparedStatement statement = connection.prepareStatement(sql)) {

            // Set the product name
            statement.setString(1, name);

            // Return true if a product was deleted
            return statement.executeUpdate() > 0;

        } catch (SQLException e) {
            // Handle database errors
            System.out.println("Database error: " + e.getMessage());
            return false;
        }
    }
}
