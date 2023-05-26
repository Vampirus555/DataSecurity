import java.io.*;

public class Lab5 {
    public static void main(String[] args) {
        String originalImagePath = new File("tux.bmp").getPath();
        String modifiedImagePath = new File("tuxEmbed.bmp").getPath();

       
    
//------------------------------
//  Итог: теперь полученный результат стал больше похож на правду (можно увидеть в текущей версии коммита в файле "tuxEmbed"),
//  но возможно при работе с интовыми значениями что-то пошло не так
//  Вывод: попробовать использовать значения полинома и регистра, чтобы было проще выделять последний бит регистра и проводить XOR
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

    

    
}