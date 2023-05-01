import java.io.*;

public class lab3 {
        
        
   
    public static void main(String[] args) {

        //-------------------------------------------------------------------
        String originalImagePath = new File("tux.bmp").getPath();
        String key = getKey();
        //-------------------------------------------------------------------

        //ECB
        String EcbImagePath = new File("tuxECB.bmp").getPath();
        String fileForEcbPath = new File("temp\\ECB\\bytesForECB.txt").getPath();
        String fileEcbPath = new File("temp\\ECB\\bytesECB.txt").getPath();
        String fileForDecryptEcbPath = new File("temp\\ECB\\bytesForDecryptECB.txt").getPath();
        String fileDecryptEcbPath = new File("temp\\ECB\\bytesDecryptECB.txt").getPath();
        String decryptEcbImagePath = new File("tuxDecryptEcb.bmp").getPath();
        
        function_AES_encrypt(originalImagePath, fileForEcbPath, fileEcbPath, "ecb", key, EcbImagePath);
        function_AES_decrypt(EcbImagePath, fileForDecryptEcbPath, fileDecryptEcbPath, "ecb", key, decryptEcbImagePath);
        //-------------------------------------------------------------------

        //CBC
        String CbcImagePath = new File("tuxCBC.bmp").getPath();
        String fileForCbcPath = new File("temp\\CBC\\bytesForCBC.txt").getPath();
        String fileCbcPath = new File("temp\\CBC\\bytesCBC.txt").getPath();
        String fileForDecryptCbcPath = new File("temp\\CBC\\bytesForDecryptCBC.txt").getPath();
        String fileDecryptCbcPath = new File("temp\\CBC\\bytesDecryptCBC.txt").getPath();
        String decryptCbcImagePath = new File("tuxDecryptCbc.bmp").getPath();

        function_AES_encrypt(originalImagePath, fileForCbcPath, fileCbcPath, "cbc", key, CbcImagePath);
        function_AES_decrypt(CbcImagePath, fileForDecryptCbcPath, fileDecryptCbcPath, "cbc", key, decryptCbcImagePath);
        //-------------------------------------------------------------------

        //CFB
        String CfbImagePath = new File("tuxCFB.bmp").getPath();
        String fileForCfbPath = new File("temp\\CFB\\bytesForCFB.txt").getPath();
        String fileCfbPath = new File("temp\\CFB\\bytesCFB.txt").getPath();
        String fileForDecryptCfbPath = new File("temp\\CFB\\bytesForDecryptCFB.txt").getPath();
        String fileDecryptCfbPath = new File("temp\\CFB\\bytesDecryptCFB.txt").getPath();
        String decryptCfbImagePath = new File("tuxDecryptCfb.bmp").getPath();

        function_AES_encrypt(originalImagePath, fileForCfbPath, fileCfbPath, "cfb", key, CfbImagePath);
        function_AES_decrypt(CfbImagePath, fileForDecryptCfbPath, fileDecryptCfbPath, "cfb", key, decryptCfbImagePath);
        //-------------------------------------------------------------------
        
        //OFB
        String OfbImagePath = new File("tuxOFB.bmp").getPath();
        String fileForOfbPath = new File("temp\\OFB\\bytesForOFB.txt").getPath();
        String fileOfbPath = new File("temp\\OFB\\bytesOFB.txt").getPath();
        String fileForDecryptOfbPath = new File("temp\\OFB\\bytesForDecryptOFB.txt").getPath();
        String fileDecryptOfbPath = new File("temp\\OFB\\bytesDecryptOFB.txt").getPath();
        String decryptOfbImagePath = new File("tuxDecryptOfb.bmp").getPath();

        function_AES_encrypt(originalImagePath, fileForOfbPath, fileOfbPath, "ofb", key, OfbImagePath);
        function_AES_decrypt(OfbImagePath, fileForDecryptOfbPath, fileDecryptOfbPath, "ofb", key, decryptOfbImagePath);
        //-------------------------------------------------------------------
    }

    private static String getKey() {
        try {
            ProcessBuilder pb = new ProcessBuilder("cmd.exe", "/c", "openssl rand -hex 16");
            Process process = pb.start();
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line, result = "";
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
                result = line;
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


    private static void encrypt(String inputFilePath, String outputFilePath, String mode, String key) {
        try {
            ProcessBuilder pb = new ProcessBuilder("cmd.exe", "/c", "openssl enc -aes-256-" + mode + " -in " + inputFilePath + " -out " + outputFilePath + " -pass pass:" + key);
            Process process = pb.start();
            
            int exitCode = process.waitFor();
            if (exitCode != 0) {
                System.err.println("Error: " + exitCode);
            }

           
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            
        }
    }

    private static void decrypt(String inputFilePath, String outputFilePath, String mode, String key) {
        try {
            ProcessBuilder pb = new ProcessBuilder("cmd.exe", "/c", "openssl enc -d -aes-256-" + mode + " -in " + inputFilePath + " -out " + outputFilePath + " -pass pass:" + key);
            Process process = pb.start();

            int exitCode = process.waitFor();
            if (exitCode != 0) {
                System.err.println("Error: " + exitCode);
            }

            
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            
        }
    }

    private static void function_AES_encrypt(String originalImagePath, String fileForEncPath, String fileEncPath, String mode, String key, String EncImagePath){
        try {
            FileInputStream inputStream = new FileInputStream(originalImagePath);
            byte[] header = new byte[110];
            inputStream.read(header);
            byte[] imageBytes = new byte[inputStream.available()];
            inputStream.read(imageBytes);
            inputStream.close();

            FileOutputStream outputStream = new FileOutputStream(fileForEncPath);
            outputStream.write(imageBytes);
            outputStream.close();

            encrypt(fileForEncPath, fileEncPath, mode, key);

            FileInputStream inputStream1 = new FileInputStream(fileEncPath);
            byte[] ecbBytes = new byte[inputStream1.available()];
            inputStream1.read(ecbBytes);
            inputStream1.close();

            FileOutputStream outputStream1 = new FileOutputStream(EncImagePath);
            outputStream1.write(header);
            outputStream1.write(ecbBytes);
            outputStream1.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void function_AES_decrypt(String EncImagePath, String fileForDecryptPath, String fileDecryptPath, String mode, String key, String decryptImagePath){
        try {
            FileInputStream inputStream2 = new FileInputStream(EncImagePath);
            byte[] headerECB = new byte[110];
            inputStream2.read(headerECB);
            byte[] imageBytesECB = new byte[inputStream2.available()];
            inputStream2.read(imageBytesECB);
            inputStream2.close();


            FileOutputStream outputStream2 = new FileOutputStream(fileForDecryptPath);
            outputStream2.write(imageBytesECB);
            outputStream2.close();

            decrypt(fileForDecryptPath, fileDecryptPath,  mode, key);

            FileInputStream inputStream3 = new FileInputStream(fileDecryptPath);
            byte[] ecbDecryptBytes = new byte[inputStream3.available()];
            inputStream3.read(ecbDecryptBytes);
            inputStream3.close();

            FileOutputStream outputStream3 = new FileOutputStream(decryptImagePath);
            outputStream3.write(headerECB);
            outputStream3.write(ecbDecryptBytes);
            outputStream3.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}