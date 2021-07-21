package etherlandscore.etherlandscore.persistance;

import org.bukkit.Bukkit;
import org.jetlang.core.Callback;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Persister {
    private static final Logger logger = Bukkit.getLogger();

    public final String filepath;

    public Persister(String filepath) {
        this.filepath = filepath;
    }

    public void overwrite(String toWrite) {
        try {
            BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(filepath, false));
            bufferedWriter.write(toWrite, 0, toWrite.length());
            bufferedWriter.close();
        } catch (Exception e) {
            logger.log(Level.WARNING, String.format("Unable to overwrite file : %s. ToWrite: %s", filepath, toWrite), e);
        }
    }

    public void appendTo(String toWrite) {
        try {
            BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(filepath, true));
            bufferedWriter.write(toWrite, 0, toWrite.length());
            bufferedWriter.write("\n");
            bufferedWriter.close();
        } catch (Exception e) {
            logger.log(Level.WARNING, String.format("Unable to append to file : %s. ToWrite: %s", filepath, toWrite), e);
        }
    }

    public String read() {
        StringBuilder result = new StringBuilder();
        try {
            String line;
            File file = new File(filepath);
            if (file.exists()) {
                FileReader fileReader = new FileReader(file);
                BufferedReader bufferedReader = new BufferedReader(fileReader);
                while ((line = bufferedReader.readLine()) != null) {
                    result.append(line.trim());
                }
                fileReader.close();
            } else {
                logger.log(Level.WARNING, String.format("Unable to read file at : %s. Returning empty", filepath));
            }
        } catch (Exception e) {
            logger.log(Level.WARNING, String.format("Unable to read file at : %s", filepath), e);
        }
        return result.toString();
    }

    // Read no trim
    public static String readRaw(String toReadPath) {
        StringBuilder result = new StringBuilder();
        try {
            String line;
            File file = new File(toReadPath);
            if (file.exists()) {
                FileReader fileReader = new FileReader(file);
                BufferedReader bufferedReader = new BufferedReader(fileReader);
                while ((line = bufferedReader.readLine()) != null) {
                    result.append(line).append("\n");
                }
                fileReader.close();
            } else {
                logger.log(Level.WARNING, String.format("Unable to read file at : %s. Returning empty", toReadPath));
            }
        } catch (Exception e) {
            logger.log(Level.WARNING, String.format("Unable to read file at : %s", toReadPath), e);
        }
        return result.toString();
    }

    // Read no trim
    public String readRaw() {
        return Persister.readRaw(filepath);
    }

    public List<String> readLines() {
        List<String> result = new ArrayList<String>();
        try {
            String line;
            File file = new File(filepath);
            if (file.exists()) {
                FileReader fileReader = new FileReader(file);
                BufferedReader bufferedReader = new BufferedReader(fileReader);
                while ((line = bufferedReader.readLine()) != null) {
                    result.add(line.trim());
                }
                fileReader.close();
            } else {
                logger.log(Level.WARNING, String.format("Unable to read file at : %s. Returning empty", filepath));
            }
        } catch (Exception e) {
            logger.log(Level.WARNING, String.format("Unable to read file at : %s", filepath), e);
        }
        return result;
    }

    public void getAll(Callback<String> stringCallback) {
        for (String s : readLines()) {
            stringCallback.onMessage(s.trim());
        }
    }

    public boolean fileExists() {
        return Files.exists(Paths.get(filepath));
    }
}
