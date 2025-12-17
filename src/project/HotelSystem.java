package project;

import java.sql.*;
import java.util.Scanner;

public class HotelSystem {

    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in);

        String url = "jdbc:mysql://localhost:3306/hotel_db";
        String dbUser = "root";
        // NOTE: Database credentials are configured locally and not hardcoded for security reasons
        String dbPass = "";

        // LOGIN
        System.out.println("====== HOTEL LOGIN ======");
        System.out.print("Enter Username: ");
        String username = sc.nextLine();

        System.out.print("Enter Password: ");
        String password = sc.nextLine();

        boolean isAdmin = false;
        boolean loginSuccess = false;

        try {
            Connection con = DriverManager.getConnection(url, dbUser, dbPass);
            String sql = "SELECT * FROM users WHERE username=? AND password=?";
            PreparedStatement ps = con.prepareStatement(sql);

            ps.setString(1, username);
            ps.setString(2, password);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                loginSuccess = true;
                if (username.equalsIgnoreCase("admin")) {
                    isAdmin = true;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (!loginSuccess) {
            System.out.println("❌ Invalid Login. Exiting...");
            return;
        }

        System.out.println("✔ Login Successful!\n");

        // ================================
        // ADMIN PANEL
        // ================================
        if (isAdmin) {
            while (true) {
                System.out.println("===== ADMIN PANEL =====");
                System.out.println("1. View Menu");
                System.out.println("2. Add Item");
                System.out.println("3. Update Item");
                System.out.println("4. Delete Item");
                System.out.println("5. View All Bills");
                System.out.println("6. Logout");
                System.out.print("Choice: ");

                int ch = sc.nextInt();
                sc.nextLine();

                try {
                    Connection con = DriverManager.getConnection(url, dbUser, dbPass);

                    switch (ch) {

                        case 1: // VIEW MENU
                            ResultSet rs = con.createStatement().executeQuery("SELECT * FROM menu");
                            System.out.println("---- MENU ----");
                            while (rs.next()) {
                                System.out.println(rs.getInt("itemId") + ". " +
                                        rs.getString("itemName") + " - ₹" + rs.getDouble("price"));
                            }
                            break;

                        case 2: // ADD ITEM
                            System.out.print("Enter New Item Name: ");
                            String name = sc.nextLine();
                            System.out.print("Enter Price: ");
                            double price = sc.nextDouble();
                            sc.nextLine();

                            PreparedStatement add = con.prepareStatement(
                                    "INSERT INTO menu(itemName, price) VALUES (?,?)");
                            add.setString(1, name);
                            add.setDouble(2, price);
                            add.executeUpdate();

                            System.out.println("✔ Item Added!");
                            break;

                        case 3: // UPDATE ITEM
                            System.out.print("Enter Item ID to Update: ");
                            int uid = sc.nextInt();
                            sc.nextLine();

                            System.out.print("Enter New Name: ");
                            String newName = sc.nextLine();
                            System.out.print("Enter New Price: ");
                            double newPrice = sc.nextDouble();

                            PreparedStatement upd = con.prepareStatement(
                                    "UPDATE menu SET itemName=?, price=? WHERE itemId=?");
                            upd.setString(1, newName);
                            upd.setDouble(2, newPrice);
                            upd.setInt(3, uid);
                            upd.executeUpdate();

                            System.out.println("✔ Item Updated!");
                            break;

                        case 4: // DELETE ITEM
                            System.out.print("Enter Item ID to Delete: ");
                            int did = sc.nextInt();

                            PreparedStatement del = con.prepareStatement(
                                    "DELETE FROM menu WHERE itemId=?");
                            del.setInt(1, did);
                            del.executeUpdate();

                            System.out.println("✔ Item Deleted!");
                            break;

                        case 5: // VIEW ALL BILLS
                            ResultSet rBill = con.createStatement().executeQuery("SELECT * FROM orders");
                            System.out.println("\n===== ALL BILLS =====");
                            while (rBill.next()) {
                                System.out.println(
                                    "Order ID: " + rBill.getInt("orderId") +
                                    " | User: " + rBill.getString("username") +
                                    " | Item: " + rBill.getString("itemName") +
                                    " | Qty: " + rBill.getInt("quantity") +
                                    " | Total: ₹" + rBill.getDouble("total") +
                                    " | Time: " + rBill.getTimestamp("orderTime")
                                );
                            }
                            break;

                        case 6:
                            System.out.println("✔ Logged out!");
                            return;

                        default:
                            System.out.println("❌ Invalid Option");
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        // ================================
        // USER PANEL
        // ================================
        while (true) {
            System.out.println("===== USER MENU =====");
            System.out.println("1. View Menu");
            System.out.println("2. Order Food");
            System.out.println("3. Exit");
            System.out.print("Choice: ");

            int ch = sc.nextInt();
            sc.nextLine();

            try {
                Connection con = DriverManager.getConnection(url, dbUser, dbPass);

                switch (ch) {

                    case 1: // VIEW MENU
                        ResultSet rs = con.createStatement().executeQuery("SELECT * FROM menu");
                        System.out.println("---- MENU ----");
                        while (rs.next()) {
                            System.out.println(rs.getInt("itemId") + ". " +
                                    rs.getString("itemName") + " - ₹" + rs.getDouble("price"));
                        }
                        break;

                    case 2: // ORDER FOOD
                        System.out.print("Enter Item ID: ");
                        int oid = sc.nextInt();

                        System.out.print("Enter Quantity: ");
                        int qty = sc.nextInt();

                        PreparedStatement ps = con.prepareStatement("SELECT * FROM menu WHERE itemId=?");
                        ps.setInt(1, oid);
                        ResultSet item = ps.executeQuery();

                        if (item.next()) {
                            String itemName = item.getString("itemName");
                            double price = item.getDouble("price");
                            double total = qty * price;

                            // DISPLAY BILL
                            System.out.println("\n===== BILL =====");
                            System.out.println("Item: " + itemName);
                            System.out.println("Quantity: " + qty);
                            System.out.println("Total Amount: ₹" + total);
                            System.out.println("================\n");

                            // SAVE BILL
                            PreparedStatement billSave = con.prepareStatement(
                                "INSERT INTO orders(username, itemName, quantity, total) VALUES (?, ?, ?, ?)");
                            billSave.setString(1, username);
                            billSave.setString(2, itemName);
                            billSave.setInt(3, qty);
                            billSave.setDouble(4, total);
                            billSave.executeUpdate();

                            System.out.println("✔ Bill saved in database!");
                        } else {
                            System.out.println("❌ Invalid Item ID");
                        }
                        break;

                    case 3:
                        System.out.println("🙏 Thank you! Visit Again");
                        return;
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
