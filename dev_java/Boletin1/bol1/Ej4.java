package bol1;

public class Ej4 {
	 public static String calcularSalario(double salarioBase, int antiguedad, int horasExtra) {
	        String resultado = "";

	        if (salarioBase < 900 || salarioBase > 5000) {
	            resultado = "Salario base no valido";
	        } else {
	            double bonificacion = 0;

	            if (antiguedad >= 5) {
	                bonificacion = salarioBase*0.02*antiguedad;

	                if (bonificacion > salarioBase*0.20) {
	                    bonificacion = salarioBase*0.20;
	                }}

	            double pagoExtra = horasExtra * 15;
	            double salarioFinal = salarioBase + bonificacion + pagoExtra;
	            resultado = "Salario final: " + salarioFinal + " €";
	        }
	        return resultado;
	 }
	 
	 //test
	 public static void main(String[] args) {
	        System.out.println(calcularSalario(1200, 6, 10));
	    }}
