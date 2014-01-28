public class IntegerConstantToken extends Token {
    public final int value;
    @Override public Object getValue() {
        return value;
    }

    public IntegerConstantToken(String image) {
        super(LexerTestConstants.INTEGER_CONSTANT, image);
        value = Integer.parseInt(image);
    }
}
