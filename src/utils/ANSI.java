package utils;

/**
 * Created by magnus on 2014-03-06.
 */
public final class ANSI {
    private ANSI() {}
    private static boolean hasAnsi = !System.getProperty("os.name").startsWith("Windows");
    public static final String reset             = hasAnsi ? "\u001b[0m"  : "";
    public static final String defaultForeground = hasAnsi ? "\u001b[39m" : "";
    public static final String green             = hasAnsi ? "\u001b[32m" : "";
    public static final String red               = hasAnsi ? "\u001b[31m" : "";
    public static final String bold              = hasAnsi ? "\u001b[1m"  : "";
    public static final String underline         = hasAnsi ? "\u001b[4m"  : "";
    public static final String noUnderline       = hasAnsi ? "\u001b[24m" : "";

    public static String highlightSubstitutionSymbol(String symbol) {
        if (hasAnsi) return underline + symbol + noUnderline;
        else return "<" + symbol + ">";
    }
}
