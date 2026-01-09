package ej9Enero;

import java.util.Scanner;

public class EjMultiplos {
	public static void main(String[] args) {
        Scanner scan = new Scanner(System.in);
        int numero;

        System.out.print("Introduce el numero: ");
        numero = scan.nextInt();

        if (numero%2==0 && numero%3==0) {
            System.out.println("Es multiplo de ambos");
        } else if (numero%2==0) {
            System.out.println("Es multiplo de 2");
        } else if (numero%3==0) {
            System.out.println("Es multiplo de 3");
        } else {
            System.out.println("No es multiplo de ningunno");
        }
}}
