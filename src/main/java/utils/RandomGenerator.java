package utils;

import static constants.Roles.ADMIN;
import static constants.Roles.USER;

public class RandomGenerator {
    public static String generateRandomGender() {
        return Math.random() < 0.5 ? "male" : "female";
    }

    public static int generateRandomAge() {
        return (int) (Math.random() * 44) + 17;
    }

    public static String generateRandomRole() {
        return Math.random() < 0.5 ? ADMIN.getRole() : USER.getRole();
    }

    public static String generateRandomLogin(String prefix) {
        return prefix + "login-" + (int) (Math.random() * 10000);
    }

    public static String generateRandomPassword(String prefix) {
        return prefix + "pass-" + (int) (Math.random() * 10000);
    }

    public static String generateRandomScreenName(String prefix) {
        return prefix + "scrName-" + (int) (Math.random() * 10000);
    }

    public static Integer generateRandomIntNumber(int startPoint) {
        return (int) (Math.random() * 10000) + startPoint;
    }
}
