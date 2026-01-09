package bol1;

public class Ej3 {

	 public static String tipoCaracter(char c) {
	        String resultado = "Otro caracter";

	        if (c >= 'A' && c <= 'Z') {
	            resultado = "Letra mayus";
	        } else if (c >= 'a' && c <= 'z') {
	            resultado = "Letra minus";
	        } else if (c >= '0' && c <= '9') {
	            resultado = "Entre 0 y 9";
	        } else if (c == ' ') {
	            resultado = "Espacio en blanco";
	        } else if (c == '(' || c == ')' || c == '{' || c == '}') {
	            resultado = "Parentesis o llaves";
	        } else if (c == '.' || c == ',' || c == ';' || c == ':') {
	            resultado = "Signo";
	        }
	        return resultado;
	    }
	 
	 //test
	 public static void main(String[] args) {
	        System.out.println(tipoCaracter('A'));
	        }}
