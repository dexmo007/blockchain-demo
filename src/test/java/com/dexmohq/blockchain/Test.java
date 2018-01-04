package com.dexmohq.blockchain;

import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class Test {

    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        System.out.println(encoder.matches("supersecret",
                "$2a$10$agMVzi.uSHsiDsMhavAZeOzpQui/XJeSqk7ztnTLw1PuAIs2ujcTG"));

    }
}
