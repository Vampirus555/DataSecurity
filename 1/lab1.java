import java.io.*;
import java.util.Random;


public class lab1{
    public static void main(String[] args) throws IOException {
        String inputFilePath = new java.io.File("leasing.txt").getPath();
        String outputFilePath = new java.io.File("leasing_new.txt").getPath();
        
        while(true){
            // Читаем байты файла
        FileInputStream fis = new FileInputStream(inputFilePath);
        byte[] bytes = fis.readAllBytes();
        fis.close();

        // Заменяем пробелы на послед-ть s-s-b
        for (int i = 0; i < bytes.length; i++) {
            if (bytes[i] == ' ') {
                byte[] sequence = new byte[] { '\b', ' ' };
                // Вставляем последовательность b-s после пробела
                bytes = insertBytes(bytes, sequence, i + 1);
                i += 2;
            }
        }

        // Заменяем рандомное кол-во послед-тей s-s-b на послед-ти s-b-s в случайных местах
        int count = 0;
        Random random = new Random();
        while (count < (int) (Math.random() * 100)) {
            int index = random.nextInt(bytes.length - 2);
            if (bytes[index] == ' ' && bytes[index + 1] == ' ' && bytes[index + 2] == '\b') {
                bytes[index + 1] = '\b';
                bytes[index + 2] = ' ';
                count++;
            }
        }

        // Записываем байты в выходной файл
        FileOutputStream outputStream = new FileOutputStream(outputFilePath);
        outputStream.write(bytes);
        outputStream.close();
        
        
        
        if (encrypt(inputFilePath).equals(encrypt(outputFilePath))) {
            System.out.println("Коллизия обнаружена");
            break;
        } 
        }
        
    }

    public static byte[] insertBytes(byte[] original, byte[] insert, int index) {
        byte[] result = new byte[original.length + insert.length];
        // Копия исходного в выходной от начала до пробела (места вставки)
        System.arraycopy(original, 0, result, 0, index);
        // Копия послед-ти в выходной от начала до конца
        System.arraycopy(insert, 0, result, index, insert.length);
        // Копия исходного в выходной от пробела (места вставки) до конца
        System.arraycopy(original, index, result, index + insert.length, original.length - index);

        return result;
    }

    private static String encrypt(String filePath) {
        try {
            ProcessBuilder pb = new ProcessBuilder("cmd.exe", "/c", "openssl dgst -md5 " + filePath);
            Process process = pb.start();
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line, result = "";
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
                result = line.split("= ")[1];
            }

            int exitCode = process.waitFor();
            if (exitCode != 0) {
                System.err.println("Error: " + exitCode);
            }

            return result;
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            return e.toString();
        }
    }
}