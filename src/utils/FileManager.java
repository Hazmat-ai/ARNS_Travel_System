package utils;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Simple file manager to save/load primitive reservation lines.
 * Format per line: name|email|module|title|price
 */
public class FileManager {

    public static void saveReservations(String filename, List<String> lines) {
        try (PrintWriter pw = new PrintWriter(new FileWriter(filename))) {
            for (String l : lines) pw.println(l);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static List<String> loadReservations(String filename) {
        List<String> out = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = br.readLine()) != null) out.add(line);
        } catch (IOException e) {
            // file missing -> return empty
        }
        return out;
    }
}
