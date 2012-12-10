/**
 * 
 */
package truthTable;

/**
 * 
 *
 */
public class StringUtils {

    public static String center(boolean s, int size, int level) {
        if (level == 1) 
            return (s?center("T*", size, " "):center("F*", size, " "));
        return (s?center("T", size, " "):center("F", size, " "));
    }

    public static String center(boolean s, int size) {
        return (s?center("T", size, " "):center("F", size, " "));
    }

    public static String center(String s, int size) {
        return center(s, size, " ");
    }

    //http://stackoverflow.com/questions/8154366/how-to-center-a-string-using-string-format
    public static String center(String s, int size, String pad) {
        if (pad == null)
            throw new NullPointerException("pad cannot be null");
        if (pad.length() <= 0)
            throw new IllegalArgumentException("pad cannot be empty");
        if (s == null || size <= s.length())
            return s;

        StringBuilder sb = new StringBuilder();
        int leftPad = (size - s.length()) / 2;
        for (int i = 0; i < leftPad; i++) {
            sb.append(pad);
        }
        sb.append(s);
        while (sb.length() < size) {
            sb.append(pad);
        }
        return sb.toString();
    }

    public static String repeat(char c, int size) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < size; i++) {
            sb.append(c);
        }
        return sb.toString();
    }

}