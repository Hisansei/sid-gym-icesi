package co.edu.icesi.sidgymicesi.util;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class HashGen {
    public static void main(String[] args) {
        var encoder = new BCryptPasswordEncoder();

        System.out.println("laura.h  →  " + encoder.encode("hash_lh123"));
        System.out.println("pedro.m  →  " + encoder.encode("hash_pm123"));
        System.out.println("ana.s    →  " + encoder.encode("hash_as123"));
        System.out.println("luis.r   →  " + encoder.encode("hash_lr123"));
        System.out.println("sofia.g  →  " + encoder.encode("hash_sg123"));

        System.out.println("juan.p   →  " + encoder.encode("hash_jp123"));
        System.out.println("maria.g  →  " + encoder.encode("hash_mg123"));
        System.out.println("carlos.l →  " + encoder.encode("hash_cl123"));
        System.out.println("carlos.m →  " + encoder.encode("hash_cm123"));
        System.out.println("sandra.o →  " + encoder.encode("hash_so123"));
        System.out.println("paula.r  →  " + encoder.encode("hash_pr123"));
        System.out.println("andres.c →  " + encoder.encode("hash_ac123"));
    }
}
