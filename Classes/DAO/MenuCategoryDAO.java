package DAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import project.DBConnecton;

public class MenuCategoryDAO {

    // Add category
    public boolean addCategory(String name, String description) {

        String sql = "INSERT INTO menu_categories "
                   + "(category_name, description) VALUES (?, ?)";

        try (Connection con = DBConnecton.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, name);
            ps.setString(2, description);

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // View all categories
    public List<String> getAllCategories() {

        List<String> categories = new ArrayList<>();
        String sql = "SELECT category_name FROM menu_categories "
                   + "ORDER BY category_id";

        try (Connection con = DBConnecton.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                categories.add(rs.getString("category_name"));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return categories;
    }

    // Update category
    public boolean updateCategory(
            int categoryId, String name, String description) {

        String sql = "UPDATE menu_categories SET "
                   + "category_name = ?, description = ? "
                   + "WHERE category_id = ?";

        try (Connection con = DBConnecton.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, name);
            ps.setString(2, description);
            ps.setInt(3, categoryId);

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Delete category
    public boolean deleteCategory(int categoryId) {

        String sql = "DELETE FROM menu_categories "
                   + "WHERE category_id = ?";

        try (Connection con = DBConnecton.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, categoryId);

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.out.println(
                "Category cannot be deleted if menu items use it."
            );
            e.printStackTrace();
            return false;
        }
    }
}
