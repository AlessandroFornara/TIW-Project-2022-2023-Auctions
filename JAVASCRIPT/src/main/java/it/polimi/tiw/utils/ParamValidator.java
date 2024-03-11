package it.polimi.tiw.utils;

public class ParamValidator {

    private ParamValidator(){}
    public static boolean isPositiveInteger(String str) {
        try {
            int x = Integer.parseInt(str);
            return x > 0;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}
