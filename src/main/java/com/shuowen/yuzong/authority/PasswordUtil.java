package com.shuowen.yuzong.authority;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Scanner;

public class PasswordUtil
{
    private static final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    public static boolean isPasswordEqual(String password, String passwordDB)
    {
        return encoder.matches(password, passwordDB);
    }

    public static String encodePassword(String password)
    {
        return encoder.encode(password);
    }

    public static void main(String[] args)
    {
        Scanner scanner = new Scanner(System.in);
        String password = scanner.nextLine();
        System.out.println(encoder.encode(password));
    }
}
