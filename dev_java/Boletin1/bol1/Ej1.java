package bol1;

public class Ej1 {
    public static boolean sonMultiplos(int a, int b) {
        boolean resultado = false;

        if (a % b == 0 || b % a == 0) {
            resultado = true;
        }
        return resultado;
    }

    //test
    public static void main(String[] args) {
        System.out.println(sonMultiplos(10, 5));
    }}
