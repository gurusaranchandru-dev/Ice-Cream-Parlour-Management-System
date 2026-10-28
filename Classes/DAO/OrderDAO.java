package DAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Map;

import project.DBConnecton;

public class OrderDAO {

    public int placeOrder(Map<Integer, Integer> cart) {

        if (cart == null || cart.isEmpty()) {
            System.out.println("Cart is empty.");
            return -1;
        }

        String findItem = "SELECT price, quantity FROM menu_items "
                        + "WHERE item_id = ? FOR UPDATE";

        String insertOrder = "INSERT INTO orders "
                           + "(total_amount, order_status) "
                           + "VALUES (?, 'CONFIRMED')";

        String insertOrderItem = "INSERT INTO order_items "
            + "(order_id, item_id, quantity, unit_price, subtotal) "
            + "VALUES (?, ?, ?, ?, ?)";

        String updateStock = "UPDATE menu_items "
                           + "SET quantity = quantity - ? "
                           + "WHERE item_id = ?";

        try (Connection con = DBConnecton.getConnection()) {

            con.setAutoCommit(false);

            try {
                double total = 0;

                // Lock items and check stock before saving the order.
                try (PreparedStatement ps =
                         con.prepareStatement(findItem)) {

                    for (Map.Entry<Integer, Integer> entry
                            : cart.entrySet()) {

                        int itemId = entry.getKey();
                        int orderedQty = entry.getValue();

                        if (orderedQty <= 0) {
                            throw new SQLException(
                                "Order quantity must be positive."
                            );
                        }

                        ps.setInt(1, itemId);

                        try (ResultSet rs = ps.executeQuery()) {

                            if (!rs.next()) {
                                throw new SQLException(
                                    "Menu item not found: " + itemId
                                );
                            }

                            double price = rs.getDouble("price");
                            int stock = rs.getInt("quantity");

                            if (stock < orderedQty) {
                                throw new SQLException(
                                    "Insufficient stock for item: "
                                    + itemId
                                );
                            }

                            total += price * orderedQty;
                        }
                    }
                }

                int orderId;

                // Save the main order.
                try (PreparedStatement ps = con.prepareStatement(
                        insertOrder, Statement.RETURN_GENERATED_KEYS)) {

                    ps.setDouble(1, total);
                    ps.executeUpdate();

                    try (ResultSet keys = ps.getGeneratedKeys()) {
                        if (!keys.next()) {
                            throw new SQLException(
                                "Could not generate order ID."
                            );
                        }

                        orderId = keys.getInt(1);
                    }
                }

                // Save each ordered item and reduce stock.
                try (PreparedStatement find =
                         con.prepareStatement(findItem);
                     PreparedStatement addItem =
                         con.prepareStatement(insertOrderItem);
                     PreparedStatement stockUpdate =
                         con.prepareStatement(updateStock)) {

                    for (Map.Entry<Integer, Integer> entry
                            : cart.entrySet()) {

                        int itemId = entry.getKey();
                        int qty = entry.getValue();

                        find.setInt(1, itemId);

                        try (ResultSet rs = find.executeQuery()) {
                            if (!rs.next()) {
                                throw new SQLException(
                                    "Menu item not found: " + itemId
                                );
                            }

                            double price = rs.getDouble("price");
                            int stock = rs.getInt("quantity");

                            if (stock < qty) {
                                throw new SQLException(
                                    "Insufficient stock for item: "
                                    + itemId
                                );
                            }

                            double subtotal = price * qty;

                            addItem.setInt(1, orderId);
                            addItem.setInt(2, itemId);
                            addItem.setInt(3, qty);
                            addItem.setDouble(4, price);
                            addItem.setDouble(5, subtotal);
                            addItem.executeUpdate();

                            stockUpdate.setInt(1, qty);
                            stockUpdate.setInt(2, itemId);
                            stockUpdate.executeUpdate();
                        }
                    }
                }

                con.commit();

                System.out.println("Order placed successfully.");
                System.out.println("Order ID: " + orderId);
                System.out.println("Total amount: " + total);

                return orderId;

            } catch (SQLException | RuntimeException e) {
                con.rollback();
                throw e;

            } finally {
                con.setAutoCommit(true);
            }

        } catch (SQLException e) {
            System.out.println("Order failed: " + e.getMessage());
            return -1;
        }
    }

    // View an order and its individual items.
    public void viewOrder(int orderId) {

        String sql = "SELECT o.order_id, o.order_date, "
            + "o.total_amount, o.order_status, "
            + "m.item_name, oi.quantity, oi.unit_price, oi.subtotal "
            + "FROM orders o "
            + "JOIN order_items oi ON o.order_id = oi.order_id "
            + "JOIN menu_items m ON oi.item_id = m.item_id "
            + "WHERE o.order_id = ?";

        try (Connection con = DBConnecton.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, orderId);

            try (ResultSet rs = ps.executeQuery()) {

                boolean found = false;

                while (rs.next()) {
                    found = true;

                    System.out.println(
                        "Order ID: " + rs.getInt("order_id")
                    );
                    System.out.println(
                        "Date: " + rs.getTimestamp("order_date")
                    );
                    System.out.println(
                        "Item: " + rs.getString("item_name")
                    );
                    System.out.println(
                        "Quantity: " + rs.getInt("quantity")
                    );
                    System.out.println(
                        "Unit price: " + rs.getDouble("unit_price")
                    );
                    System.out.println(
                        "Subtotal: " + rs.getDouble("subtotal")
                    );
                    System.out.println(
                        "Order total: " + rs.getDouble("total_amount")
                    );
                    System.out.println(
                        "Status: " + rs.getString("order_status")
                    );
                    System.out.println("--------------------");
                }

                if (!found) {
                    System.out.println("Order not found.");
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
