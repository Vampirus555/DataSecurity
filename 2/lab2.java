import java.io.*;
import java.util.*;


public class lab2{

    public static void main(String[] args) {
        String originalImagePath = new File("28.bmp").getPath();
        String modifiedImagePath = new File("28embed.bmp").getPath();
        String filePath = new File("leasing.txt").getPath();

        String hex = encrypt(filePath);
        String secretMessage = "";
        
        //Преобразование строки в массив байтов
        
        byte[] stringBytes = HexStringToByteArray(hex);
        
        

        // Преобразование масcива байтов в строку битов
        for (int i = 0; i < stringBytes.length; i++) {
            secretMessage += String.format("%8s", Integer.toBinaryString(stringBytes[i] & 0xFF)).replace(' ', '0');
        }
        System.out.println("Хэш: " + hex);
        System.out.println("Биты>: " + secretMessage);
        
        int messageIndex = 0;
        
        try {
            // Чтение исходного файла изображения
            FileInputStream inputStream = new FileInputStream(originalImagePath);
            byte[] header = new byte[122];
            inputStream.read(header);
            byte[] imageBytes = new byte[inputStream.available()];
            inputStream.read(imageBytes);
            inputStream.close();


            // Генерирация ключа
            int[] key = generateKey(secretMessage.length(), 0, imageBytes.length);
            
            // Вставим секретное сообщение в байты изображения, используя метод замены LSB
            for (int i = 0; i < key.length; i++) {
                if (messageIndex < secretMessage.length()) {
                    // Получаем двоичное представление текущего байта
                    String binaryByte = String.format("%8s", Integer.toBinaryString(imageBytes[key[i]] & 0xFF)).replace(' ', '0');
                    
                    // Заменим младший значащий бит текущего байта следующим битом секретного сообщения
                    char[] binaryCharArray = binaryByte.toCharArray();
                    binaryCharArray[7] = secretMessage.charAt(messageIndex % secretMessage.length());
                    binaryByte = new String(binaryCharArray);
                    
                    
                    // Установим измененный байт обратно в байты изображения
                    imageBytes[key[i]] = (byte) Integer.parseInt(binaryByte, 2);
                    
                    // Увеличим индекс сообщения
                    messageIndex++;
                }
            }
            
            // Запишем измененные байты изображения в новый файл
            FileOutputStream outputStream = new FileOutputStream(modifiedImagePath);
            outputStream.write(header);
            outputStream.write(imageBytes);
            outputStream.close();

            
            
            // Чтение измененного файл изображения
            inputStream = new FileInputStream(modifiedImagePath);
            header = new byte[122];
            inputStream.read(header);
            byte[] modifiedImageBytes = new byte[inputStream.available()];
            inputStream.read(modifiedImageBytes);
            inputStream.close();
            
            // Извлечение секретного сообщения из байтов измененного изображения, используя метод замены LSB
            StringBuilder messageBuilder = new StringBuilder();
            messageIndex = 0;
            for (int i = 0; i < key.length; i++) {
                if (messageIndex < secretMessage.length()) {
                    // Получаем двоичное представление текущего байта
                    String binaryByte = String.format("%8s", Integer.toBinaryString(modifiedImageBytes[key[i]] & 0xFF)).replace(' ', '0');
                    
                    // Извлекаем наименее значимый бит текущего байта и добавьте его в секретное сообщение
                    messageBuilder.append(binaryByte.charAt(7));
                    
                    // Увеличиваем индекс сообщения
                    messageIndex++;
                }
            }
            String retrievedMessage = messageBuilder.toString();
            
            byte[] newStringBytes = getBytes(retrievedMessage);

            String hashRetrived = byteArrayToHexString(newStringBytes);
            
            // Output the original and retrieved message to the console
            System.out.println("Оригинальное сообщение: " + hex);
            System.out.println("Извлеченное сообщение: " + hashRetrived);
            
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static String encrypt(String filePath) {
        try {
            ProcessBuilder pb = new ProcessBuilder("cmd.exe", "/c", "openssl dgst -sha1 " + filePath);
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

    public static byte[] getBytes(String bitString) {
        
        byte[] result = new byte[bitString.length()/8];
        
        // Выполняем итерацию по битовой строке, по 8 бит за раз
        for (int pos = 0; pos < bitString.length()/8; pos++) {
            String eightBits = bitString.substring(pos*8, (pos+1)*8);
            result[pos] = (byte) Integer.parseInt(eightBits, 2);
        }
       
        return result;
    }

    public static byte[] HexStringToByteArray(String hex) {
        int len = hex.length();
        byte[] stringBytes = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            stringBytes[i / 2] = (byte) ((Character.digit(hex.charAt(i), 16) << 4)
                    + Character.digit(hex.charAt(i + 1), 16));
        }
        return stringBytes;
    }

    public static String byteArrayToHexString(byte[] b) {
        String result = "";
        for (int i = 0; i < b.length; i++) {
            result += Integer.toString((b[i] & 0xff) + 0x100, 16).substring(1);
        }
        return result;
    }

    public static int[] generateKey(int count, int min, int max) {

        int[] result = new int[count];
        Set<Integer> keySet = new HashSet<>();

        Random random = new Random();
        int i = 0;
        while (i < count) {
            int pos = random.nextInt(max - min + 1) + min;
            if (!keySet.contains(pos)) {
                keySet.add(pos);
                result[i] = pos;
                i++;
            }
        }

        return result;
    }
}