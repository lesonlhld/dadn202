package letrungson.com.smartcontroller.tools;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.TimeZone;

public class Transform {
    private static final char[] HEX_ARRAY = "0123456789ABCDEF".toCharArray();

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

    public static String bytesToHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        for (int j = 0; j < bytes.length; j++) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = HEX_ARRAY[v >>> 4];
            hexChars[j * 2 + 1] = HEX_ARRAY[v & 0x0F];
        }
        return new String(hexChars);
    }

    public static String convertToCurrentTimeZone(String Date) {
        String converted_date = "";
        try {

            DateFormat utcFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            utcFormat.setTimeZone(TimeZone.getTimeZone("UTC"));

            java.util.Date date = utcFormat.parse(Date);

            DateFormat currentTFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            currentTFormat.setTimeZone(TimeZone.getTimeZone(getCurrentTimeZone()));

            converted_date = currentTFormat.format(date);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return converted_date;
    }


    //get the current time zone

    public static String getCurrentTimeZone() {
        TimeZone tz = Calendar.getInstance().getTimeZone();
        return tz.getID();
    }
}
