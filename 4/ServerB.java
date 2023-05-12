import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerB {
    public static void main(String[] args) throws Exception {
        String messageFilePath = new File("message.txt").getPath();
        String encryptedMessageFilePath = new File("message.enc").getPath();
        // String decryptedMessageFilePath = new File("message.dec").getPath();
        String signatureFilePath = new File("signature.bin").getPath();
        String privateKeyPath = new File("privatekey.pem").getPath();
        String publicKeyPath = new File("publickey.pem").getPath();

        
        // String printKeyStructure = "openssl rsa -text -noout -in " + privateKeyPath;
        // String decryptFile = "openssl rsautl -decrypt -inkey " + privateKeyPath + " -in " + encryptedMessageFilePath + " -out " + decryptedMessageFilePath;
        String calculateSignature = "openssl dgst -sha256 -sign " + privateKeyPath + " -out " + signatureFilePath + " " + messageFilePath;

        

        try (ServerSocket server = new ServerSocket(5000)) { // Запускаем сервер на порту 5000
            Socket client = server.accept();
            BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream())); // Канал чтения из сокета
            BufferedWriter out = new BufferedWriter(new OutputStreamWriter(client.getOutputStream())); // Канал записи в сокет

            while (!client.isClosed()) {
                String entry;
                while ((entry = in.readLine()) != null) {
                    if (entry.equalsIgnoreCase("stop"))
                        break;

                    System.out.println("Получаю файл");
                    // Считываем данные после их получения
                    String encMessageHexString = entry;
                    byte[] encMessageBytes = hexStringToByteArray(encMessageHexString.replace("\n", ""));
                    FileOutputStream fos = new FileOutputStream(encryptedMessageFilePath);
                    fos.write(encMessageBytes);
                    fos.close();

                    
                    System.out.println("Подписываю файл");
                    // Подписываем сообщение
                    executeCommand(calculateSignature); 


                    FileInputStream fis = new FileInputStream(encryptedMessageFilePath);
                    byte[] signMessageBytes = new byte[fis.available()];
                    fis.read(signMessageBytes); // Читаем зашифрованное сообщение
                    fis.close();

                    System.out.println("Отправляю подписанный файл");
                    out.write(byteArrayToHexString(signMessageBytes) + "\n");
                    out.flush();
                }
                
            }
            in.close();
            out.close();
            client.close();
            server.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    // Перевод строки, записанной в 16сс, в массив байтов
    public static byte[] hexStringToByteArray(String hexString) {
        if (hexString.length() % 2 != 0) {
            hexString = "0" + hexString;
        }
        int len = hexString.length();
        byte[] byteArray = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            byteArray[i / 2] = (byte) ((Character.digit(hexString.charAt(i), 16) << 4)
                    + Character.digit(hexString.charAt(i + 1), 16));
        }
        return byteArray;
    }

    // Перевод массива байтов в строку в шестнадцатеричном представлении
    public static String byteArrayToHexString(byte[] b) {
        String result = "";
        for (int i = 0; i < b.length; i++) {
            result += Integer.toString((b[i] & 0xff) + 0x100, 16).substring(1);
        }
        return result;
    }

    // Выполнение команды в командной строке Windows
    public static String executeCommand(String command) {
        try {
            Process process = new ProcessBuilder("cmd.exe", "/c", command).start();
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line, result = "";
            while ((line = reader.readLine()) != null) {
                result += line;
            }

            int exitCode = process.waitFor();
            if (exitCode != 0) {
                System.err.println("Error: " + exitCode);
            }
            return result;
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }
}
