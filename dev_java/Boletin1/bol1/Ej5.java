package bol1;

import java.util.Scanner;

public class Ej5 {
	public static void main(String[] args) {
        Scanner scan = new Scanner(System.in);
        int numero;
        int n;
        int suma=0;

        System.out.print("Introduce un num: ");
        numero = scan.nextInt();
        System.out.print("Introduce suma: ");
        n = scan.nextInt();

        if (numero > 0 && n > 0) {
            for (int i = 1; i <= n; i++) {
                suma+=numero+i;
            }
            System.out.println("Total: " + suma);
        } else {
            System.out.println("Error");
        }}}