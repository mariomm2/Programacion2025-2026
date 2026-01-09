package bol1;

import java.util.Scanner;

public class Ej9 {
	    public static void main(String[] args) {
	        Scanner scan = new Scanner(System.in);
	        int num;
	        int max;
	        int min;

	        System.out.print("Introduce el priemr numero: ");
	        num = scan.nextInt();
	        max = num;
	        min = num;

	        for (int i = 2; i <= 10; i++) {
	            System.out.print("Introduce el siguiente numer" + i + ": ");
	            num = scan.nextInt();
	            if (num > max) {
	                max = num;
	            }
	            if (num < min) {
	                min = num;
	            }}
	        System.out.println("Max: " + max);
	        System.out.println("Min: " + min);
	    }}

