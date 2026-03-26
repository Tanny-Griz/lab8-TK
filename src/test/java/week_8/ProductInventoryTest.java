package week_8;

import input.InputUtils;
import org.junit.Before;
import org.junit.Test;
import org.mockito.MockedStatic;
import test_utils.PrintUtils;

import java.sql.*;
import java.util.ArrayList;

import static junit.framework.TestCase.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mockStatic;

public class ProductInventoryTest {
    
    private static final int TIMEOUT = 10000;
    
    private ProductManager productInventoryProgram;
    
    private final String testDatabaseURL = "jdbc:sqlite:products_test.db";
    private final String developmentDatabaseURL = "jdbc:sqlite:products.db";
    
    private final String[][] exampleDataSet = {
            {"Leopard", "100"},
            {"Aardvark", "200"},
            {"Badger", "300"},
    };
    
    
    private String[][] exampleDataSetSorted = {
            {"Aardvark", "200"},
            {"Badger", "300"},
            {"Leopard", "100"}
    };
    
    private String exampleDataSetSortedString =
            "            { \"Aardvark\", \"200\" },\n" +
            "            { \"Badger\", \"300\" },\n" +
            "            { \"Leopard\", \"100\" }";
    
    
    
    @Before
    public void setUp() {
        
        // Replace database with test DB
        DBConfig.db_url = testDatabaseURL;
        
        deleteTestData();   // remove all data from test DB
        addTestData();
        
        productInventoryProgram = new ProductManager();
        productInventoryProgram.database = new ProductDB();
        
    }
    
    
    
    private void deleteTestData() {
        try (Connection con = DriverManager.getConnection(testDatabaseURL);
             Statement statement = con.createStatement() ) {

            String sql = "DELETE FROM inventory";
            statement.execute(sql);

        } catch (SQLException e) {
            fail("SQLException deleting data from test database." + e.getMessage());
        }
    }
    

    private void addTestData() {
        String sql = "INSERT INTO inventory (name, quantity) values (?, ?)";

        try (Connection con = DriverManager.getConnection(testDatabaseURL);
             PreparedStatement statement = con.prepareStatement(sql)) {
            
            for (String[] data : exampleDataSet) {
                statement.setString(1, data[0]);
                statement.setInt(2, Integer.parseInt(data[1]));
                statement.execute();
            }

        } catch (SQLException e) {
            fail("SQLException adding data to test database." + e.getMessage());
        }
    }
   

    @Test(timeout = TIMEOUT)
    public void testTestDatabaseAndTableExists() {
        testTableExists(testDatabaseURL);
    }

    
    @Test(timeout = TIMEOUT)
    public void testDevelopmentDatabaseAndTableExists()  {
        testTableExists(developmentDatabaseURL);
    }

    
    public void testTableExists(String dbURL)  {
        
        try (Connection conn = DriverManager.getConnection(dbURL);
            Statement statement = conn.createStatement() ) {
    
            String tableInfo = "PRAGMA table_info(inventory)";
            ResultSet rs = statement.executeQuery(tableInfo);
            
            rs.next();
            String productNameCol = rs.getString(2);
            String productNameType = rs.getString(3);
            
            rs.next();
            String quantityCol = rs.getString(2);
            String quantityType = rs.getString(3);
            
            assertFalse("The database should only contain two columns, name and quantity.", rs.next());   // No more columns.
            
            assertEquals("The first column's name should be 'name'", "name", productNameCol.toLowerCase());
            assertEquals("The first column's type should be 'TEXT'", "text", productNameType.toLowerCase());
    
            assertEquals("The second column's name should be 'quantity'", "quantity", quantityCol.toLowerCase());
            assertEquals("The second column's type should be 'INTEGER'", "integer", quantityType.toLowerCase());
    
    
        } catch (SQLException e) {
            fail("Databases are not configured correctly, " + e.getMessage());
        }
    }
    
    
    
    @Test(timeout = TIMEOUT)
    public void testShowAllProducts() {
        
        productInventoryProgram = new ProductManager();
        productInventoryProgram.database = new ProductDB();
        
        PrintUtils.catchStandardOut();
        productInventoryProgram.showAll();
        String out = PrintUtils.resetStandardOut();
        
        // Should contain example data sorted, in the given order
        
        int startSearch = 0;
        
        for (String[] row : exampleDataSetSorted) {
            for (String data : row) {
                
                startSearch = out.indexOf(data, startSearch);
                assertTrue("Displaying all data; with example data: \n" + exampleDataSetSortedString +
                                "\nthe program printed \n" + out +
                                "\nMake sure you print all the data, name followed by quantity, \n" +
                                "sorted by product name, in alphabetical order\n" +
                                "Remember your database can sort data.",
                        startSearch != -1);
            }
        }
    }
    
    
    
    @Test(timeout = TIMEOUT)
    public void testAddNewDuplicateProductShowErrorMessage() {
    
        PrintUtils.catchStandardOut();

        try (MockedStatic<InputUtils> mockInputUtil = mockStatic(InputUtils.class)) {

            mockInputUtil.when(() -> InputUtils.intInput(anyString()))
                    .thenReturn(1400);

            mockInputUtil.when(() -> InputUtils.stringInput(anyString()))
                    .thenReturn("Aardvark");


            // TODO


            productInventoryProgram.addProduct();

            String out = PrintUtils.resetStandardOut();

            String expectedErrorMessage = "Product already in database";
            assertTrue("If user tries to add a duplicate product, print this exact message: " + expectedErrorMessage, out.contains(expectedErrorMessage));

        }

        catch(Exception e) {
            fail("Only ask for the input requested.\n" + e);
        }
    }
    
    
    
    @Test(timeout = TIMEOUT)
    public void testAddNewDuplicateProduct() {

        try (MockedStatic<InputUtils> mockInputUtil = mockStatic(InputUtils.class)) {

            mockInputUtil.when(() -> InputUtils.intInput(anyString()))
                    .thenReturn(400)
                    .thenReturn(100000);

            mockInputUtil.when(() -> InputUtils.stringInput(anyString()))
                    .thenReturn("Hedgehog")
                    .thenReturn("Hedgehog")
                    .thenThrow(new RuntimeException());


            productInventoryProgram.addProduct();

            // Check the database

            String[][] examplePlusNew = {
                    {"Aardvark", "200"},
                    {"Badger", "300"},
                    {"Hedgehog", "400"},
                    {"Leopard", "100"}
            };

            checkDatabase(examplePlusNew);   // Contains assert statements

            productInventoryProgram.addProduct();

            checkDatabase(examplePlusNew);   // Database should not change, Hedgehog quantity should still be 400
        }

        catch(Exception e) {
            fail("Only ask for the input requested.\n" + e);
        }
    }
    
    
    
   @Test(timeout = TIMEOUT)
    public void testAddNewProduct() {

       try (MockedStatic<InputUtils> mockInputUtil = mockStatic(InputUtils.class)) {

           mockInputUtil.when(() -> InputUtils.stringInput(anyString()))
                   .thenReturn("Hedgehog")       // new product 1
                   .thenReturn("Velociraptor");    // new product 2

           mockInputUtil.when(() -> InputUtils.intInput(anyString()))
                   .thenReturn(400)   // new quantity 1
                   .thenReturn(100);    // new quantity 2


           PrintUtils.catchStandardOut();

           productInventoryProgram.addProduct();

           String out = PrintUtils.resetStandardOut();

           String unexpectedErrorMessage = "Product already in database";
           assertFalse("If user adds a new product, don't print an error message: ", out.contains(unexpectedErrorMessage));

           // Check the database

           String[][] examplePlusNew = {
                   {"Aardvark", "200"},
                   {"Badger", "300"},
                   {"Hedgehog", "400"},
                   {"Leopard", "100"}
           };

           checkDatabase(examplePlusNew);   // Contains assert statements

           // Expect to add 100 Velociraptors

           productInventoryProgram.addProduct();

           String[][] examplePlusAnotherNew = {
                   {"Aardvark", "200"},
                   {"Badger", "300"},
                   {"Hedgehog", "400"},
                   {"Leopard", "100"},
                   {"Velociraptor", "100"},
           };

           checkDatabase(examplePlusAnotherNew);
       }

       catch(Exception e) {
           fail("Only ask for the input requested.\n" + e);
       }
    }
  
    
    
    @Test(timeout = TIMEOUT)
    public void testEditProduct() {

        try (MockedStatic<InputUtils> mockInputUtil = mockStatic(InputUtils.class)) {

            mockInputUtil.when(() -> InputUtils.intInput(anyString()))
                    .thenReturn(400)
                    .thenReturn(32);

            mockInputUtil.when(() -> InputUtils.stringInput(anyString()))
                    .thenReturn("Aardvark")
                    .thenReturn("Aardvark");

            // in case student adds an "are you sure" check
            mockInputUtil.when( () -> InputUtils.yesNoInput(anyString()))
                    .thenReturn(true);


            productInventoryProgram.editProductQuantity();

            // Check the database

            String[][] exampleWithEdit = {
                    {"Aardvark", "400"},
                    {"Badger", "300"},
                    {"Leopard", "100"}
            };

            checkDatabase(exampleWithEdit);

            productInventoryProgram.editProductQuantity();

            // Check the database

            String[][] exampleWithEdit2 = {
                    {"Aardvark", "32"},
                    {"Badger", "300"},
                    {"Leopard", "100"}
            };

            checkDatabase(exampleWithEdit2);
        }

        catch(Exception e) {
            fail("Only ask for the input requested.\n" + e);
        }
    }
    

    @Test(timeout = TIMEOUT)
    public void testEditNonExistentProduct() {

        try (MockedStatic<InputUtils> mockInputUtil = mockStatic(InputUtils.class)) {

            mockInputUtil.when(() -> InputUtils.intInput(anyString()))
                    .thenReturn(64);

            mockInputUtil.when(() -> InputUtils.stringInput(anyString()))
                    .thenReturn("Velociraptor");

            // in case student adds an "are you sure" check
            mockInputUtil.when( () -> InputUtils.yesNoInput(anyString()))
                    .thenReturn(true);

            productInventoryProgram.editProductQuantity();
            checkDatabase(exampleDataSetSorted);  // no edits made
        }

        catch(Exception e) {
            fail("Only ask for the input requested.\n" + e);
        }

    }
    
    
    @Test(timeout = TIMEOUT)
    public void testEditNonExistentProductShowsErrorMessage() {

        try (MockedStatic<InputUtils> mockInputUtil = mockStatic(InputUtils.class)) {

            mockInputUtil.when(() -> InputUtils.intInput(anyString()))
                    .thenReturn(64);

            mockInputUtil.when(() -> InputUtils.stringInput(anyString()))
                    .thenReturn("Velociraptor");

            // in case student adds an "are you sure" check
            mockInputUtil.when( () -> InputUtils.yesNoInput(anyString()))
                    .thenReturn(true);

            PrintUtils.catchStandardOut();

            productInventoryProgram.editProductQuantity();

            String out = PrintUtils.resetStandardOut();

            String expectedErrorMessage = "Product to edit not found";
            assertTrue("If user tries to edit a product that is not in the database, print this message: " + expectedErrorMessage, out.contains(expectedErrorMessage));
        }

        catch(Exception e) {
            fail("Only ask for the input requested.\n" + e);
        }
    }

    
    @Test(timeout = TIMEOUT)
    public void testDeleteProduct() {

        try (MockedStatic<InputUtils> mockInputUtil = mockStatic(InputUtils.class)) {

            mockInputUtil.when(() -> InputUtils.stringInput(anyString()))
                    .thenReturn("Badger")
                    .thenReturn("Aardvark")
                    .thenReturn("Leopard")
                    .thenReturn("Squirrel");  // delete from empty database

            // in case student adds an "are you sure" check
            mockInputUtil.when( () -> InputUtils.yesNoInput(anyString()))
                    .thenReturn(true);

            productInventoryProgram.deleteProduct();

            // Check the database

            String[][] exampleDeleted = {
                    {"Aardvark", "200"},
                    {"Leopard", "100"}
            };

            checkDatabase(exampleDeleted);

            productInventoryProgram.deleteProduct();

            String[][] exampleDeleteAnother = {
                    {"Leopard", "100"},
            };

            checkDatabase(exampleDeleteAnother);

            // delete final item

            productInventoryProgram.deleteProduct();

            String[][] exampleDeleteLast = {

            };

            checkDatabase(exampleDeleteLast);

            // Delete from empty database, should not error

            productInventoryProgram.deleteProduct();

            checkDatabase(exampleDeleteLast);

        }

        catch(Exception e) {
            fail("Only ask for the input requested.\n" + e);
        }
    }
    
    
    
    @Test(timeout = TIMEOUT)
    public void testDeleteNonExistentProduct() {

        try (MockedStatic<InputUtils> mockInputUtil = mockStatic(InputUtils.class)) {

            mockInputUtil.when(() -> InputUtils.stringInput(anyString()))
                    .thenReturn("Owl");

            // TODO Validate program does ask for a name

            // in case student adds an "are you sure" check
            mockInputUtil.when( () -> InputUtils.yesNoInput(anyString()))
                    .thenReturn(true);

            productInventoryProgram.deleteProduct();

            // Check the database. Data should be the same.
            checkDatabase(exampleDataSetSorted);
        }

        catch(Exception e) {
            fail("Only ask for the input requested.\n" + e);
        }

    }
    
    
    @Test(timeout = TIMEOUT)
    public void testDeleteNonExistentProductShowsErrorMessage() {

        try (MockedStatic<InputUtils> mockInputUtil = mockStatic(InputUtils.class)) {

            mockInputUtil.when(() -> InputUtils.stringInput(anyString()))
                    .thenReturn("Owl");

            // in case student adds an "are you sure" check
            mockInputUtil.when( () -> InputUtils.yesNoInput(anyString()))
                    .thenReturn(true);

            PrintUtils.catchStandardOut();

            productInventoryProgram.deleteProduct();

            String out = PrintUtils.resetStandardOut();

            String expectedErrorMessage = "Product to delete not found";
            assertTrue("If user tries to delete a product that is not in the database, print this message: " + expectedErrorMessage, out.contains(expectedErrorMessage));

        }

        catch(Exception e) {
            fail("Only ask for the input requested.\n" + e);
        }

    }


    private void checkDatabase(String[][] expected) {

        // Verify database contains same data as expected. Data provided as a 2d array
        DBContents dbContents = null;
        
        try (Connection con = DriverManager.getConnection(testDatabaseURL);
             Statement statement = con.createStatement()) {
            
            String sql = "SELECT * FROM inventory ORDER BY name";
            
            ResultSet rs = statement.executeQuery(sql);
            
            dbContents = rsToObject(rs);
            
        } catch (SQLException e) {
            fail("SQLException checking data from test database." + e.getMessage());
        } catch (Exception e) {
            fail("Exception checking data from test database." + e.getMessage());
        }
        
        assertNotNull(dbContents);
        
        assertEquals("Database contains a different number of rows of data than expected", expected.length, dbContents.rows());
        
        for (int rowCounter = 0; rowCounter < dbContents.rows(); rowCounter++) {
            
            String name = dbContents.getName(rowCounter);
            int quantity = dbContents.getQuantity(rowCounter);
            
            assertEquals("Name column data does not match actual data", expected[rowCounter][0], name);
            assertEquals("Quantity column data does not match actual data", Integer.parseInt(expected[rowCounter][1]), quantity);

        }
    }
    
    
    
    private DBContents rsToObject(ResultSet rs) throws Exception {
        
        ArrayList<String> names = new ArrayList<>();
        ArrayList<Integer> quantities = new ArrayList<>();
        
        while (rs.next()) {
            names.add(rs.getString(1));
            quantities.add(rs.getInt(2));
        }
        
        rs.close();
        
        return new DBContents(names, quantities);
    }
    
    
    static class DBContents {
        
        ArrayList<String> names;
        ArrayList<Integer> quantities;
        
        
        public DBContents(ArrayList<String> names, ArrayList<Integer> quantities) throws Exception {
            if (names.size() != quantities.size()) {
                throw new Exception("Should be same number of names and quantities in the actual database to the expected data.");
            }
            this.names = names;
            this.quantities = quantities;
        }
        
        int rows() {
            return names.size();
        }
        
        String getName(int row) {
            return names.get(row);
        }
        
        int getQuantity(int row){
            return quantities.get(row);
        }
    }
    
    
}