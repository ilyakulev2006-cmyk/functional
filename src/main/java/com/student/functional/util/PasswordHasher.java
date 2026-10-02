package com.student.functional.util;

import org.mindrot.jbcrypt.BCrypt;

public class PasswordHasher {
    public static String hash(String plain) {
        return BCrypt.hashpw(plain, BCrypt.gensalt(12));
    }
}