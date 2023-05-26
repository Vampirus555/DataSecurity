import java.util.Arrays;

public class LFSR {
    private int[] tapPositions; // Позиции отводов (задаются полиномом)
    private int[] register; // Значения регистра

    public LFSR(int[] polynomial, int[] initialRegister) {
        this.tapPositions = Arrays.copyOf(polynomial, polynomial.length);
        this.register = Arrays.copyOf(initialRegister, initialRegister.length);
    }

    public int getNextBit() {
        // Сохраняем последний бит регистра (выходной)
        int outputBit = register[register.length - 1];
        
        // Производим сдвиг регистра путем использования arraycopy
        System.arraycopy(register, 0, register, 1, register.length - 1);
        
        // В саммое начало вставляем сохраненный последний бит регистра
        register[0] = outputBit;
        
        // Если выходной бит == 1, то биты отвода меняют своё значение на противоположное, и все биты сдвигаются вправо
        if (outputBit == 1) {
            for (int i = 1; i < register.length; i++) {
                if (tapPositions[i] == 1) {
                    register[i] = register[i] ^ 1;
                }
            }
        }

        
        return outputBit;
    }
}



// public class LFSR {
    
//     private int tapPositions; // Позиции отводов (задаются полиномом)

//     private int register; // Значения регистра
    

//     public LFSR(int tapPositions, int register) {
//         this.tapPositions = tapPositions;
//         this.register = register;
        
//     }

//     public int getNextBit() {
//         int digits = (int) Math.log10(register) + 1; //определяем количество цифр в регистре

//         int output = register & (1 << (digits - 1)); //получаем число, у которогопоследний бит будет равен последнему нашего регистра
//         output = output >> (digits - 1); //сдвигаем полученный бит в начало, чтобы получить фактический последний бит регистра

//         // Если последний бит равен 1, то сдвигаем биты регистра вправо, вставляя в старший бит 1 и выполняем XOR
//         if (output == 1){
//             register >>>= 1 | 0x1;
//             register = register ^ tapPositions;
//         }
//         // если 0, то просто сдвигаем биты, а новый старший бит заполнится нулем
//         else if(output == 0) register >>>= 1;
        
//         // возвращаем последний бит
//         return output;
//     }

    
// }