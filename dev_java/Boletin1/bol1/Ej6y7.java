package bol1;

public class Ej6y7 {

	    public static String costeEnvio(double peso, String destino) {
	        String resultado = "";
	        double coste = 0;

	        if (peso <= 0 || peso > 20) {
	            resultado = "Error peso";
	        } else {
	            if (destino.equals("Local")) {
	                coste = 5;
	            } else if (destino.equals("Nacional")) {
	                coste = 10;
	            } else if (destino.equals("Internacional")) {
	                coste = 25;
	            } else {
	                resultado = "Error destino";
	            }

	            if (resultado.equals("")) {
	                if (peso > 5 && peso <= 10) {
	                    coste = coste + 2.5;
	                } else if (peso > 10) {
	                    coste = coste + 5;
	                }
	                resultado = "Total: " + coste + " €";
	            }}
	        return resultado;
	    }

	    //test
	    public static void main(String[] args) {
	        System.out.println(costeEnvio(8, "Nacional"));
	    }}

