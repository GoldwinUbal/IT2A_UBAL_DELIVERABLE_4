package com.arms;

import com.arms.dao.DatabaseInitializer;
import com.arms.dao.UserDao;
import com.arms.ui.Authentication;

import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        // 1. Initialize database
        new DatabaseInitializer().initialize();

        // 2. Instantiate DAOs & utilities (single shared instances)
        UserDao userDao = new UserDao();
        Scanner scanner = new Scanner(System.in);

        // 3. Delegate to Authentication
        new Authentication(userDao, scanner).start();

        scanner.close();
    }
}
