package com.wb.nextgen.util.utils;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;


public class CryptHelper {
    private static final String SHARED_KEY = "799741c015681d32";

    public static String encryptEsig(String s) {
        return encryptAES(SHARED_KEY.getBytes(), s);
    }

    public static String encryptAES(byte[] key, String s) {
        try {
            if (key == null) {
                KeyGenerator kgen = KeyGenerator.getInstance("AES");
                kgen.init(128); // 192 and 256 bits may not be available
                SecretKey skey = kgen.generateKey(); // Generate the secret key specs
                key = skey.getEncoded();
            }
            SecretKeySpec skeySpec = new SecretKeySpec(key, "AES");
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.ENCRYPT_MODE, skeySpec);
            byte[] encrypted = cipher.doFinal(s.getBytes());
            String hexEncoded = Hex.encodeHex(encrypted, false);
            // Loggerflx.i(F.TAG, hexEncoded);
            return hexEncoded;
        } catch (NoSuchAlgorithmException nsae) {
            FlixsterLogger.e(F.TAG, "AES algorithm not available", nsae);
        } catch (NoSuchPaddingException nspe) {
            FlixsterLogger.e(F.TAG, "AES padding not available", nspe);
        } catch (InvalidKeyException ike) {
            FlixsterLogger.e(F.TAG, "Invalid key '" + key + "'", ike);
        } catch (IllegalBlockSizeException ibse) {
            FlixsterLogger.e(F.TAG, "Illegal block size", ibse);
        } catch (BadPaddingException bpe) {
            FlixsterLogger.e(F.TAG, "Bad padding", bpe);
        }
        return null;
    }

    public static String decryptAES(byte[] key, String s) {
        try {
            if (key == null) {
                KeyGenerator kgen = KeyGenerator.getInstance("AES");
                kgen.init(128); // 192 and 256 bits may not be available
                SecretKey skey = kgen.generateKey(); // Generate the secret key specs
                key = skey.getEncoded();
            }
            SecretKeySpec skeySpec = new SecretKeySpec(key, "AES");
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.DECRYPT_MODE, skeySpec);
            byte[] unencoded = Hex.decodeHex(new String(s));
            byte[] decrypted = cipher.doFinal(unencoded);
            return new String(decrypted);
        } catch (NoSuchAlgorithmException nsae) {
            FlixsterLogger.e(F.TAG, "AES algorithm not available", nsae);
        } catch (NoSuchPaddingException nspe) {
            FlixsterLogger.e(F.TAG, "AES padding not available", nspe);
        } catch (InvalidKeyException ike) {
            FlixsterLogger.e(F.TAG, "Invalid key '" + key + "'", ike);
        } catch (IllegalBlockSizeException ibse) {
            FlixsterLogger.e(F.TAG, "Illegal block size", ibse);
        } catch (BadPaddingException bpe) {
            FlixsterLogger.e(F.TAG, "Bad padding", bpe);
        }
        return null;
    }

    public static void test() throws Exception {
        String string = "abcdefghijklmnopqrstuvwxyz0123456789!@#$%^&*(),./;'[]";
        String encrypted = encryptAES(SHARED_KEY.getBytes(), string);
        String decrypted = decryptAES(SHARED_KEY.getBytes(), encrypted);
        FlixsterLogger.i(F.TAG, "Clear text = " + string);
        FlixsterLogger.i(F.TAG, "Encrypted text (Hex encoded) = " + encrypted);
        FlixsterLogger.i(F.TAG, "Decrypted text (Hex decoded) = " + decrypted);
        FlixsterLogger.i(F.TAG, string.equals(decrypted) ? "success!" : "failure!");
    }
}
