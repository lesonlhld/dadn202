package letrungson.com.smartcontroller.tools;

public class Tranform {
    static public String BinaryToDaily(String input) {
        char[] A = input.toCharArray();
        boolean daily = true;
        for (int i = 0; i < 7; i++) {
            if (A[i] == '0') {
                daily = false;
                break;
            }
        }
        if (daily) {
            return "Daily";
        }

        StringBuilder result = new StringBuilder("");
        if (A[0] == '1') {
            result.append("Mon, ");
        }
        if (A[1] == '1') {
            result.append("Tue, ");
        }
        if (A[2] == '1') {
            result.append("Wed, ");
        }
        if (A[3] == '1') {
            result.append("Thu, ");
        }
        if (A[4] == '1') {
            result.append("Fri, ");
        }
        if (A[5] == '1') {
            result.append("Sat, ");
        }
        if (A[6] == '1') {
            result.append("Sun, ");
        }

        if (result.length() == 0) {
            return "No day";
        } else {
            result.deleteCharAt(result.length() - 2);
            return result.toString();
        }
    }
}
