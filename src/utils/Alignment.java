package utils;

/**
 * Created by Doris on 14-3-5.
 */
public class Alignment {
    /**
     * Aligns the address @address to @alignmentSize byte-boundaries by
     * rounding it upwards to the smallest multiple of
     * @alignmentSize larger than or equal to @address.
     */
    public static int align(int address, int alignmentSize){
        return ((address + alignmentSize - 1) / alignmentSize) * alignmentSize;
    }
}

