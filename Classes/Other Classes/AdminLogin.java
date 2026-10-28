package project;

import java.util.Scanner;
import DAO.AdminDAO;

public class AdminLogin {

    public boolean login(Scanner sc) {

        System.out.println("\n================ ADMIN LOGIN ================");

        System.out.print("Username: ");
        String user = sc.nextLine().trim();

        System.out.print("Password: ");
        String pass = sc.nextLine();

        AdminDAO dao = new AdminDAO();

        if (dao.login(user, pass)) {
            System.out.println("Login successful. Welcome, " + user + ".");
            return true;
        } else {
            System.out.println("Invalid username or password.");
            return false;
        }
    }
}
