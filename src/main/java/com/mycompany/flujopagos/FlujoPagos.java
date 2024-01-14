package com.mycompany.flujopagos;

import java.io.IOException;
import java.text.ParseException;
import java.util.List;


public class FlujoPagos {

    public static void main(String[] args) {
         try {
            // Leer datos de prueba de desarrollo
            List<List<String>> developmentData = Reader.readCSV("Prueba_Tecnica_Desarrollo.txt");

            // Leer datos de TRM
            List<List<String>> trmData = Reader.readCSV("TRM2023.txt");

        
            // Procesar pagos
            //PaymentProcessor.processPayments(developmentData, trmData);

            
            PaymentProcessorGUI gui = new PaymentProcessorGUI(developmentData, trmData);
            gui.setVisible(true);
            
        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }
    }
}
