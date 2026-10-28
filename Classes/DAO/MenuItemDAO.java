package DAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import project.DBConnecton;
import project.MenuItems;

public class MenuItemDAO {

    // Convert a database row into a MenuItems object
    private MenuItems mapItem(ResultSet rs) throws SQLException {

        return new MenuItems(
            rs.getInt("item_id"),
            rs.getInt("category_id"),
            rs.getString("category_name"),
            rs.getString("item_name"),
            rs.getString("flavour"),
            rs.getDouble("price"),
            rs.getInt("quantity"),
            rs.getString("description"),
            rs.getString("origin"),
            rs.getString("raw_materials"),
            rs.getString("status")
        );
    }

    // Add menu item
    public boolean addMenuItem(MenuItems item) {

        String sql = "INSERT INTO menu_items "
            + "(category_id, item_name, flavour, price, quantity, "
            + "description, origin, raw_materials, status) "
            + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection con = DBConnecton.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, item.getCategoryId());
            ps.setString(2, item.getName());
            ps.setString(3, item.getFlavour());
            ps.setDouble(4, item.getPrice());
            ps.setInt(5, item.getQuantity());
            ps.setString(6, item.getDescription());
            ps.setString(7, item.getOrigin());
            ps.setString(8, item.getRawMaterials());
            ps.setString(9, item.getStatus());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // View all menu items
    public List<MenuItems> getAllMenuItems() {

        List<MenuItems> items = new ArrayList<>();

        String sql = "SELECT m.*, c.category_name "
            + "FROM menu_items m "
            + "JOIN menu_categories c "
            + "ON m.category_id = c.category_id "
            + "ORDER BY m.item_id";

        try (Connection con = DBConnecton.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                items.add(mapItem(rs));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return items;
    }

    // Search menu item by ID
    public MenuItems getMenuItemById(int id) {

        String sql = "SELECT m.*, c.category_name "
            + "FROM menu_items m "
            + "JOIN menu_categories c "
            + "ON m.category_id = c.category_id "
            + "WHERE m.item_id = ?";

        try (Connection con = DBConnecton.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapItem(rs);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    // Search menu items by name
    public List<MenuItems> searchMenuItems(String name) {

        List<MenuItems> items = new ArrayList<>();

        String sql = "SELECT m.*, c.category_name "
            + "FROM menu_items m "
            + "JOIN menu_categories c "
            + "ON m.category_id = c.category_id "
            + "WHERE m.item_name LIKE ?";

        try (Connection con = DBConnecton.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, "%" + name + "%");

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    items.add(mapItem(rs));
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return items;
    }

    // Update menu item
    public boolean updateMenuItem(MenuItems item) {

        String sql = "UPDATE menu_items SET category_id=?, "
            + "item_name=?, flavour=?, price=?, quantity=?, "
            + "description=?, origin=?, raw_materials=?, status=? "
            + "WHERE item_id=?";

        try (Connection con = DBConnecton.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, item.getCategoryId());
            ps.setString(2, item.getName());
            ps.setString(3, item.getFlavour());
            ps.setDouble(4, item.getPrice());
            ps.setInt(5, item.getQuantity());
            ps.setString(6, item.getDescription());
            ps.setString(7, item.getOrigin());
            ps.setString(8, item.getRawMaterials());
            ps.setString(9, item.getStatus());
            ps.setInt(10, item.getId());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Delete menu item
    public boolean deleteMenuItem(int id) {

        String sql = "DELETE FROM menu_items WHERE item_id = ?";

        try (Connection con = DBConnecton.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, id);

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.out.println(
                "Cannot delete item if it is referenced by an order."
            );
            e.printStackTrace();
            return false;
        }
    }
}


