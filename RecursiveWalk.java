//package ru.ifmo.rain.Savinov.walk;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * ru.ifmo.rain.Savinov.walk
 * Short Description: (눈_눈)
 *
 * @author nikitos
 * @version 1.0.0
 */

public class RecursiveWalk {
    private final static int FNV1_32_INIT = 0x811c9dc5;
    private final static int FNV1_PRIME_32 = 0x01000193;
    private final static int BUFFER_SIZE = 2048;

    private void start(String inputFileName, String outputFileName) {
        try (PrintWriter writer = new PrintWriter(outputFileName)) {
            BufferedReader buf = new BufferedReader(new InputStreamReader(new FileInputStream(inputFileName), StandardCharsets.UTF_8));
            String currentFileName;

            while ((currentFileName = buf.readLine()) != null) {
                Files.walk(Paths.get(currentFileName)).forEach((curFile) -> {
                    try (InputStream is = new FileInputStream(curFile.toFile())) {
                        int hash = FNV1_32_INIT;
                        int bytesRed;

                        byte[] buff = new byte[BUFFER_SIZE];

                        while ((bytesRed = is.read(buff)) != -1) {
                            for (int i = 0; i < bytesRed; i++) {
                                hash = (hash * FNV1_PRIME_32) ^ (buff[i] & 0xff);
                            }
                        }

                        writer.println(converter(hash) + " " + curFile.getFileName());
                    } catch (UnsupportedEncodingException e) {
                        System.err.println("This encoding does not supported: " + e.getMessage());
                    } catch (FileNotFoundException e) {
                        System.err.println("No such file/dir in input file: " + e.getMessage());
                        writer.println(converter(0) + " " + curFile.getFileName());
                    } catch (IOException e) {
                        System.err.println("Incorrect input/output data: " + e.getMessage());
                        writer.println(converter(0) + " " + curFile.getFileName());
                    } catch (NullPointerException npe) {
                        System.err.println("File/Dir name in input incorrect: " + npe.getMessage());
                        writer.println(converter(0) + " " + curFile.getFileName());
                    }
                });
            }
        } catch (FileNotFoundException e) {
            System.err.println("No such file/dir: " + e.getMessage());
        } catch (IOException e) {
            System.err.println("Incorrect input file data: " + e.getMessage());
        } catch (NullPointerException npe) {
            System.err.println("File/Dir name incorrect: " + npe.getMessage());
        }

    }

    private String converter(int hash) {
        StringBuilder hex = new StringBuilder(Integer.toHexString(hash));

        while (hex.length() < 8) {
            hex = new StringBuilder("0").append(hex);
        }

        return hex.toString();
    }

    public static void main(String[] args) {
        if (args == null || args.length != 2) {
            System.err.println("Incorrect number of arguments");
            return;
        }

        new RecursiveWalk().start(args[0], args[1]);
    }

}
