public class LFSR {
    
    private int tapPositions; // Позиции отводов (задаются полиномом)

    private int register; // Значения регистра
    

    public LFSR(int tapPositions, int register) {
        this.tapPositions = tapPositions;
        this.register = register;
        
    }

    public int getNextBit() {
        int digits = (int) Math.log10(register) + 1; //определяем количество цифр в регистре

        int output = register & (1 << (digits - 1)); //получаем число, у которогопоследний бит будет равен последнему нашего регистра
        output = output >> (digits - 1); //сдвигаем полученный бит в начало, чтобы получить фактический последний бит регистра

        // Если последний бит равен 1, то сдвигаем биты регистра вправо, вставляя в старший бит 1 и выполняем XOR
        if (output == 1){
            register >>>= 1 | 0x1;
            register = register ^ tapPositions;
        }
        // если 0, то просто сдвигаем биты, а новый старший бит заполнится нулем
        else if(output == 0) register >>>= 1;
        
        // возвращаем последний бит
        return output;
    }

    
}