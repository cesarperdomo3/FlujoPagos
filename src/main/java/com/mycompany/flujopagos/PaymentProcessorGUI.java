
package com.mycompany.flujopagos;

import javax.swing.*;
import java.awt.*;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class PaymentProcessorGUI extends javax.swing.JFrame {

    /**
     * Creates new form PaymentProcessorGUI
     */
    public PaymentProcessorGUI() {
        initComponents();
    }
    private JTextArea resultTextArea;
    public PaymentProcessorGUI(List<List<String>> developmentData, List<List<String>> trmData) throws ParseException {
        setTitle("Resultados de Procesamiento de Pagos");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
            resultTextArea = new JTextArea();
    resultTextArea.setEditable(false);

    JScrollPane scrollPane = new JScrollPane(resultTextArea);
    add(scrollPane);
    
     processPaymentsAndDisplayResults(developmentData, trmData);
    
    }

   private void processPaymentsAndDisplayResults(List<List<String>> developmentData, List<List<String>> trmData) throws ParseException {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

        // Iterar sobre registros de prueba de desarrollo
        for (List<String> record : developmentData) {
            // Saltar la primera línea (encabezados)
            if (record.get(0).equals("Numero documento")) {
                continue;
            }

            // Obtener campos relevantes
            String docNumber = record.get(0);
            String currencyCode = record.get(11);
            String paymentCondition = record.get(12);
            String paymentDateStr = record.get(5);
            String pendingAmountStr = record.get(10);

            // Verificar si el importe pendiente está vacío
            if (!pendingAmountStr.isEmpty()) {
                // Obtener fecha de vencimiento (asumiendo que está en la columna "Fecha vto")
                Date dueDate = dateFormat.parse(record.get(5));

                // Calcular fecha de pago (viernes de la semana correspondiente)
                Date paymentDate = calculatePaymentDate(dueDate);

                // Verificar condición de pago y ajustar la fecha de vencimiento si es necesario
                if (!paymentCondition.isEmpty()) {
                    int daysToAdd = getDaysToAdd(paymentCondition);
                    dueDate = addDays(dueDate, daysToAdd);
                }

                // Obtener TRM correspondiente
                double trm = getTRM(trmData, currencyCode);
                  if (trm != 0.0) {
                    // Realizar cálculos y generar resultados
                    double amountInCOP = Double.parseDouble(pendingAmountStr);
                    double amountInForeignCurrency = TrmCalculator.calculateAmountInForeignCurrency(amountInCOP, trm);
                    
                    String formattedAmount = formatCurrency(amountInForeignCurrency);
                    
                    resultTextArea.append("Documento: " + docNumber + ", Monto en moneda extranjera: " + formattedAmount +
                        ", Fecha de vencimiento: " + dateFormat.format(dueDate) +
                        ", Fecha de pago: " + dateFormat.format(paymentDate) + "\n");
                    // Puedes mostrar o almacenar los resultados según sea necesario
                    System.out.println("Documento: " + docNumber + ", Monto en moneda extranjera: " + formattedAmount +
                            " , Fecha de vencimiento: " + dateFormat.format(dueDate) +
                            ", Fecha de pago: " + dateFormat.format(paymentDate));
                        } else {
                            System.out.println("Documento: " + docNumber + ", TRM es cero, no se puede realizar la conversión.");
                        }
            }
        }
    }

    private static Date calculatePaymentDate(Date dueDate) {
        // Calcular fecha de pago (viernes de la semana correspondiente)
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(dueDate);

        // Ir al próximo viernes
        while (calendar.get(Calendar.DAY_OF_WEEK) != Calendar.FRIDAY) {
            calendar.add(Calendar.DAY_OF_WEEK, 1);
        }

        return calendar.getTime();
    }

    private static int getDaysToAdd(String paymentCondition) {
        // Obtener la cantidad de días según la condición de pago
        return switch (paymentCondition) {
            case "001" -> 60;
            case "002" -> 30;
            case "003" -> 10;
            default -> 0;
        };
    }

    private static Date addDays(Date date, int days) {
        // Agregar la cantidad de días a la fecha
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.DAY_OF_YEAR, days);
        return calendar.getTime();
    }

    private static double getTRM(List<List<String>> trmData, String currencyCode) {
        // Buscar la TRM correspondiente en los datos de TRM
        for (List<String> trmRecord : trmData) {
            String trmCurrencyCode = trmRecord.get(1);
            if (trmCurrencyCode.equals(currencyCode)) {
                return Double.parseDouble(trmRecord.get(3).replace(",", ".")); // Convertir a double, reemplazar coma por punto
            }
        }
        // Devuelve un valor predeterminado si no se encuentra la TRM
        return 0.0;
    }
    
private static String formatCurrency(double amount) {
    // Formatear el número a dos decimales utilizando punto como separador decimal
    DecimalFormat decimalFormat = new DecimalFormat("#0.00", DecimalFormatSymbols.getInstance(Locale.US));
    return decimalFormat.format(amount);
}
    
    
    
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 400, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 300, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new PaymentProcessorGUI().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
}
