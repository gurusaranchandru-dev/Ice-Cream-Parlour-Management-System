package DAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import project.DBConnecton;

public class AdminDAO {

    public boolean login(String username, String password) {

        String sql = "SELECT admin_id FROM admin "
                   + "WHERE username = ? AND password = ? "
                   + "AND status = 'ACTIVE'";

        try (Connection con = DBConnecton.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, username);
            ps.setString(2, password);

            try (ResultSet rs = ps.executeQuery()) {

                if (rs.next()) {
                    System.out.println("Login successful!");
                    return true;
                } else {
                    System.out.println("Invalid username or password!");
                    return false;
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
