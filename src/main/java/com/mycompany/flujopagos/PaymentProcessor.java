package com.mycompany.flujopagos;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

public class PaymentProcessor {
    public static void processPayments(List<List<String>> developmentData, List<List<String>> trmData) throws ParseException {
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
        switch (paymentCondition) {
            case "001":
                return 60;
            case "002":
                return 30;
            case "003":
                return 10;
            default:
                return 0;
        }
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
}
