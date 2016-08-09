package uk.ac.ncl.b3026640.authenticateme.misc;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

/**
 * Created by Jonathan on 04-Aug-16.
 */
public class UserIDGenerator {

    public static String generate() {
        StringBuilder builder = new StringBuilder();
        try {
            SecureRandom secureRandom = SecureRandom.getInstance("SHA1PRNG");
            String rand = Integer.valueOf(secureRandom.nextInt()).toString();
            MessageDigest digest = MessageDigest.getInstance("SHA-1");
            byte[] res = digest.digest(rand.getBytes());
            builder.append(hexEncode(res));
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return builder.toString();
    }

    private static String hexEncode(byte[] in) {
        StringBuilder builder = new StringBuilder();
        char[] digits = {'0', '1', '2', '3', '4','5','6','7','8','9','a','b','c','d','e','f'};
        for (int i = 0; i < in.length; i++) {
            byte b = in[i];
            builder.append(digits[(b&0xf0) >> 4]);
            builder.append(digits[b&0x0f]);
        }
        return builder.toString();
    }
}
