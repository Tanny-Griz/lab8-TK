package week_8;

import static input.InputUtils.*;

class ProductManager {
    
    ProductDB database;    // TODO use this object to access instance methods in the ProductDB class
    
    private final String menu = "" +
            "1. Show all products\n" +
            "2. Add a product\n" +
            "3. Edit a product's quantity\n" +
            "4. Delete a product\n" +
            "9. Quit";


    public static void main(String[] args) {
        // You do not need to modify this method.
        ProductManager productManager = new ProductManager();
        productManager.start();
    }
    
    
    public void start() {
        // You do not need to modify this method.
        database = new ProductDB();
        showMenuDoAction();
    }
    
    
    private void showMenuDoAction() {
        // You do not need to modify this method.
        
        int choice;
        
        while (true) {

            System.out.println(menu);
            choice = intInput("Enter choice");

            if (choice == 1) {
                showAll();
            } else if (choice == 2) {
                addProduct();
            } else if (choice == 3) {
                editProductQuantity();
            } else if (choice == 4) {
                deleteProduct();
            } else if (choice == 9) {
                break;   // end the loop
            } else {
                System.out.println("Invalid choice, please try again");
            }
        }
    }
    
    
    protected void showAll() {
        // TODO write and call method in ProductDB to fetch all products, sorted by product name.
        // TODO display all product names and quantities neatly,
        //  For each product, display the name first, then the quantity.
        //  For example, display the name, then quantity, of one product per line.

    }
    
    protected void addProduct() {
        // TODO ask user for product name and quantity. Use intInput, not positiveIntInput.
        // TODO write and call method in ProductDB to add this product
        // TODO Deal with product already existing in DB. Do not add another product with the same name.
        //  If a product with this name is already in the database,
        //  display this exact message: "Product already in database"

    }
    
    protected void editProductQuantity() {
        // TODO ask user for the name of the product to edit, and the new quantity.
        //  Use intInput, not positiveIntInput.
        // TODO write and call method in ProductDB to edit this product's quantity.
        //  Display confirmation message if product found and quantity edited.

        // TODO handle product not existing in DB. If a product with the name given does not exist,
        //  do not make any changes to the database, and display
        //  this exact message: "Product to edit not found"

    }
    
    protected void deleteProduct() {
        // TODO ask user for the name of a product to delete.
        // TODO write and call method in ProductDB to delete this product's record from the database.
        //  Display a confirmation message.

        // TODO handle product not existing in DB. If a product with the name given does not exist,
        //  do not make any changes to the database, and display
        //  this exact message: "Product to delete not found"

    }
    
}