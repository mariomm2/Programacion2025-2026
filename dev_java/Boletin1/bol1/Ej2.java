package bol1;

public class Ej2 {

	 public static boolean sonCoprimos(int a, int b) {
	        boolean coprimos = true;
	        int i = 2;

	        while (i <= a && i <= b) {
	            if (a % i == 0 && b % i == 0) {
	                coprimos = false;
	            }
	            i++;
	        }
	        return coprimos;
}
	 //test
	 public static void main(String[] args) {
	        System.out.println(sonCoprimos(4, 9));
	        
	 }}