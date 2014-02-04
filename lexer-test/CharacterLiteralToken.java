/**
 * Created by Doris on 14-2-3.
 */
public class CharacterLiteralToken extends Token {
    public final int value;
    @Override public Object getValue() {
        return value;
    }

    public CharacterLiteralToken(String image) {
        super(LexerTestConstants.CHARACTER_LITERAL, image);
        if(image.charAt(1) == '\\'){
            switch (image.charAt(2)){
                case 'n':
                    value = 10;
                    break;
                default:
                    throw new RuntimeException("Bad escape code in character literal");
            }
        }else{
            value = (int)(image.charAt(1));
            // value =  image;
        }

    }
}
