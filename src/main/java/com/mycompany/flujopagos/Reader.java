package com.mycompany.flujopagos;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;



public class Reader {
    public static List<List<String>> readCSV(String fileName) throws IOException {
        List<List<String>> records = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] values = line.split(";");
                records.add(List.of(values));
            }
        }
        return records;
    }
}
