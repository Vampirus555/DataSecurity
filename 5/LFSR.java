public class LFSR {
    private int register;
    private int feedback;

    public LFSR(int initial, int polynomial) {
        this.register = initial;
        this.feedback = polynomial;
    }

    public int getNextBit() {
        int output = register & 1;
        int feedbackBit = ((register >> feedback) ^ (register >> (feedback - 1))) & 1;

        register = ((register << 1) | feedbackBit) & ((1 << feedback) - 1);

        return output;
    }
}