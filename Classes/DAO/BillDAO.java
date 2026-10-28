package DAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import project.DBConnecton;

public class BillDAO {

    // Generate a bill for an existing order.
    public boolean generateBill(int orderId) {

        String sql = "INSERT INTO bills (order_id, grand_total) "
                   + "SELECT order_id, total_amount FROM orders "
                   + "WHERE order_id = ?";

        try (Connection con = DBConnecton.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, orderId);

            int rows = ps.executeUpdate();

            if (rows > 0) {
                System.out.println("Bill generated successfully.");
                return true;
            }

            System.out.println("Order not found.");
            return false;

        } catch (SQLException e) {
            System.out.println(
                "Could not generate bill. Check whether a bill "
                + "already exists for this order."
            );
            e.printStackTrace();
            return false;
        }
    }

    // View a bill by bill ID.
    public void viewBill(int billId) {

        String sql = "SELECT bill_id, order_id, bill_date, grand_total "
                   + "FROM bills WHERE bill_id = ?";

        try (Connection con = DBConnecton.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, billId);

            try (ResultSet rs = ps.executeQuery()) {

                if (rs.next()) {
                    System.out.println("----- ICE CREAM BILL -----");
                    System.out.println(
                        "Bill ID: " + rs.getInt("bill_id")
                    );
                    System.out.println(
                        "Order ID: " + rs.getInt("order_id")
                    );
                    System.out.println(
                        "Bill Date: " + rs.getTimestamp("bill_date")
                    );
                    System.out.println(
                        "Grand Total: " + rs.getBigDecimal("grand_total")
                    );
                    System.out.println("--------------------------");
                } else {
                    System.out.println("Bill not found.");
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Find a bill using its order ID.
    public void getBillByOrderId(int orderId) {

        String sql = "SELECT bill_id, order_id, bill_date, grand_total "
                   + "FROM bills WHERE order_id = ?";

        try (Connection con = DBConnecton.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, orderId);

            try (ResultSet rs = ps.executeQuery()) {

                if (rs.next()) {
                    System.out.println("Bill ID: " + rs.getInt("bill_id"));
                    System.out.println("Order ID: " + rs.getInt("order_id"));
                    System.out.println(
                        "Bill Date: " + rs.getTimestamp("bill_date")
                    );
                    System.out.println(
                        "Grand Total: " + rs.getBigDecimal("grand_total")
                    );
                } else {
                    System.out.println("No bill found for this order.");
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}

