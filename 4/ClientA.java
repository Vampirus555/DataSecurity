import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class ClientA {
    public static void main(String args[]) throws Exception {
        String privateKeyPath = new File("privatekey.pem").getPath();
        String publicKeyPath = new File("publickey.pem").getPath();
        String messageFilePath = new File("message.txt").getPath();
        String encryptedMessageFilePath = new File("message.enc").getPath();
        
        String generatePrivateKey = "openssl genpkey -algorithm RSA -out " + privateKeyPath + " -pkeyopt rsa_keygen_bits:1024";
        String generatePublicKey = "openssl rsa -pubout -in " + privateKeyPath + " -out " + publicKeyPath;
        String encryptFile = "openssl rsautl -encrypt -inkey " + publicKeyPath + " -pubin -in " + messageFilePath + " -out " + encryptedMessageFilePath;


        // Генерируем пару ключей
        executeCommand(generatePrivateKey);
        executeCommand(generatePublicKey);

        Scanner in = new Scanner(System.in);
        String session = "1";

        try {
            // try {
            //     Process process = Runtime.getRuntime().exec("java ServerB");
            // } catch (IOException e) {
            //     e.printStackTrace();
            // }
            // Подключаемся к серверу B
            Socket socketB = new Socket("localhost", 5000);
            BufferedReader inFromB = new BufferedReader(new InputStreamReader(socketB.getInputStream()));
            BufferedWriter outToB = new BufferedWriter(new OutputStreamWriter(socketB.getOutputStream()));


            // try {
            //     Process process = Runtime.getRuntime().exec("java ServerC");
            // } catch (IOException e) {
            //     e.printStackTrace();
            // }
            // Подключаемся к серверу С
            Socket socketC = new Socket("localhost", 5005);
            BufferedReader inFromC = new BufferedReader(new InputStreamReader(socketC.getInputStream()));
            BufferedWriter outToC = new BufferedWriter(new OutputStreamWriter(socketC.getOutputStream()));    
            
            
            


            while (!socketB.isOutputShutdown()) {
                while(session.equals("1")){
                    FileOutputStream fos = new FileOutputStream(messageFilePath);
                    System.out.println("Ваш голос:");
                    String voice = in.nextLine();
                    fos.write(voice.getBytes()); // Голос
                    fos.close();

                    System.out.println("Шифрую файл");
                    executeCommand(encryptFile); // Шифруем файл
                    System.out.println("Файл зашифрован");

                    System.out.println("Читаю файл");
                    FileInputStream fis = new FileInputStream(encryptedMessageFilePath);
                    byte[] encMessageBytes = new byte[fis.available()];
                    fis.read(encMessageBytes); // Читаем зашифрованное сообщение
                    fis.close();
                    System.out.println("Прочитал файл");

                    System.out.println("Передаю файл");
                    outToB.write(byteArrayToHexString(encMessageBytes) + "\n"); // Передаем файл серверу B
                    outToB.flush();
                    Thread.sleep(3000);

                    
                    
                    System.out.println("Получаю файл");
                    String signedMessage = inFromB.readLine(); // Получаем подписанное сообщение
                    // byte[] signedMessageBytes = hexStringToByteArray(signedMessage.replace("\n", ""));

                
                    System.out.println("Передаю файл серверу С");
                    // Передаем что-то серверу C
                    outToC.write(voice + "1"  + "\n");
                    outToC.flush();
                    Thread.sleep(1000);
                    outToC.write(signedMessage  + "2" + "\n");
                    outToC.flush();
                    Thread.sleep(1000);

                    outToC.write("result" + "\n");
                    outToC.flush();
                    Thread.sleep(1000);

                    String result = inFromC.readLine();
                    System.out.println("Кол-во верифицированных голосов: " + result);


                    System.out.println("1 - продолжить, 0 - завершить. Ваш выбор:");
                    session = in.nextLine();

                }
                outToB.write("stop" + "\n");
                outToB.flush();
                outToC.write("stop" + "\n");
                outToC.flush();
                
            }

            

            inFromB.close();
            outToB.close();
            inFromC.close();
            outToC.close();
            socketB.close();
            socketC.close();
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
