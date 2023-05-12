import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerC {

    public static VerifVoicesCounter counter = new VerifVoicesCounter();

    public static void main(String args[]) throws Exception {
        String publicKeyPath = new File("publickey.pem").getAbsolutePath();
        String messageFilePath = new File("message.txt").getAbsolutePath();
        String signatureFilePath = new File("signature.bin").getAbsolutePath();

        String verifySignature = "openssl dgst -sha256 -verify " + publicKeyPath + " -signature " + signatureFilePath + " " + messageFilePath;

        try (ServerSocket server = new ServerSocket(5005)) { // Запускаем сервер на порту 5005
            Socket client = server.accept();
            BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream())); // Канал чтения из сокета
            BufferedWriter out = new BufferedWriter(new OutputStreamWriter(client.getOutputStream())); // Канал записи в сокет

            while (!client.isClosed()) {
                String entry;
                while ((entry = in.readLine()) != null) {
                    System.out.println("Проверяю на остановку");
                    if (entry.equalsIgnoreCase("stop"))
                        break;
                    System.out.println("Проверяю команду на получение результата");
                    if (entry.equalsIgnoreCase("result"))
                        out.write(counter.getCount() + "\n");
                        

                    char lastChar = entry.substring(entry.length() - 1).charAt(0);
                    String message = entry.substring(0,entry.length() - 1);

                    if(lastChar =='1'){
                        FileOutputStream fos = new FileOutputStream(messageFilePath);
                        fos.write(hexStringToByteArray(message));
                        fos.close();
                    }
                    else if(lastChar =='2'){
                        FileOutputStream fos = new FileOutputStream(signatureFilePath);
                        fos.write(hexStringToByteArray(message));
                        fos.close();

                        System.out.println("Проверяю подпись");
                        String result = executeCommand(verifySignature);
                        if(result.equals("Verifide OK"))
                            counter.setCount();
                    }

                    // String[] parts = entry.split(" ");
                    // String message = parts[0]; 
                    // String type = parts[1]; 
                    // if(type.equals("1")){
                    //     FileOutputStream fos = new FileOutputStream(messageFilePath);
                    //     fos.write(hexStringToByteArray(message));
                    //     fos.close();
                    // }
                    // else if(type.equals("2")){
                    //     FileOutputStream fos = new FileOutputStream(signatureFilePath);
                    //     fos.write(hexStringToByteArray(message));
                    //     fos.close();
                    // }
                    
                    

                    
                     
                    
                }
                
            }
            System.out.println("Client disconnected");
            System.out.println("Closing connections & channels...");
            in.close();
            client.close();
            server.close();
            System.out.println("Closing server connections & channels - DONE.");
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
