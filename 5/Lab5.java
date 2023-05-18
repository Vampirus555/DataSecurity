import java.io.*;

public class Lab5 {
    public static void main(String[] args) {
        String originalImagePath = new File("tux.bmp").getPath();
        String modifiedImagePath = new File("tuxEmbed.bmp").getPath();

       
    
//------------------------------
//  Итог: класс LFSR не выдает желаемого результата,
//  при некоторых изменениях случайным образом получилась более контрастное изображение, но это неверный результат
//  Вывод: необходимо поработать над классом LFSR
//------------------------------
        try {
            // Чтение исходного файла изображения
            FileInputStream inputStream = new FileInputStream(originalImagePath);
            byte[] header = new byte[120];
            inputStream.read(header);
            byte[] imageBytes = new byte[inputStream.available()];
            inputStream.read(imageBytes);
            inputStream.close();
            

            int polynomial = 0x11001; //(для x^4 + x + 1)
            int initialValue = 0x1000; 
            LFSR lfsr = new LFSR(initialValue, polynomial);
            

            byte[] modifiedBytes = new byte[imageBytes.length];
            lfsr = new LFSR(polynomial, initialValue);
            for (int i = 0; i < imageBytes.length; i++) {
                byte imageByte = imageBytes[i];
                byte resultByte = 0;
                for (int j = 0; j < 8; j++) {
                    byte originalBit = (byte) ((imageByte >> j) & 0x01);
                    byte xorBit = (byte) lfsr.getNextBit();
                    byte modifiedBit = (byte) (originalBit ^ xorBit);
                    resultByte |= (modifiedBit << j);
                }
                modifiedBytes[i] = resultByte;
            }


            
            
            FileOutputStream outputStream1 = new FileOutputStream(modifiedImagePath);
            outputStream1.write(header);
            outputStream1.write(modifiedBytes);
            outputStream1.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static byte[] encrypt(byte[] image, LFSR lfsr) {
        byte[] encryptedImage = new byte[image.length];
        for (int i = 0; i < image.length; i++) {
            byte encryptedByte = 0;
            for (int j = 0; j < 8; j++) {
                boolean lfsrBit = lfsr.getNextBit() == 1;
                boolean byteBit = ((image[i] >> j) & 1) == 1;
                boolean encryptedBit = byteBit ^ lfsrBit;
                encryptedByte |= (encryptedBit ? 1 : 0) << j;
            }
            encryptedImage[i] = encryptedByte;
        }
        return encryptedImage;
    }

    
}