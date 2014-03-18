package pt.rocket.framework.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.KeySpec;
import java.util.Calendar;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

import android.util.Base64;


import de.akquinet.android.androlog.Log;

/**
 * Utils class that contains global methods to be used through out development
 * 
 * @author josedourado
 * 
 */
public class Utils {

    private static String TAG=Utils.class.getSimpleName();
    static String password = Constants.PIN;
    static int iterationCount = 1000;
    static int keyLength = 128;
    static byte[] iv;
    static KeySpec keySpec;
    static SecretKeyFactory keyFactory;
    static byte[] keyBytes;
    static SecretKey key;

    /**
     * This method generates a unique and always different MD5 hash based on a
     * given key
     * 
     * @param key
     * @return the unique MD5
     */
    public static String uniqueMD5(String key) {
        String md5String = "";
        try {
            Calendar calendar = Calendar.getInstance();
            
           key = key + System.nanoTime();

            // Create MD5 Hash
            MessageDigest digest = java.security.MessageDigest.getInstance("MD5");
            digest.update(key.getBytes());
            byte messageDigest[] = digest.digest();

            // Create Hex String
            StringBuffer hexString = new StringBuffer();
            for (int i = 0; i < messageDigest.length; i++) {
                hexString.append(Integer.toHexString(0xFF & messageDigest[i]));
            }
            md5String = hexString.toString();

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        return md5String;

    }

    /**
     * This method generates the MD5 hash based on a
     * given key
     * 
     * @param key
     * @return the unique MD5
     */
    public static String cleanMD5(String key) {
    	if(key == null || key.length() == 0)
    		return "";
        String md5String = "";
        try {
            Calendar calendar = Calendar.getInstance();

            // Create MD5 Hash
            MessageDigest digest = java.security.MessageDigest.getInstance("MD5");
            digest.update(key.getBytes());
            byte messageDigest[] = digest.digest();

            // Create Hex String
            StringBuffer hexString = new StringBuffer();
            for (int i = 0; i < messageDigest.length; i++) {
                hexString.append(Integer.toHexString(0xFF & messageDigest[i]));
            }
            md5String = hexString.toString();

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        return md5String;

    }
    
    /**
     * Method to encrypt a given data
     * @param obj
     * @return
     */
    public static String encrypt(Object obj) {

        int saltLength = keyLength / 8; // same size as key output

        SecureRandom randomb = new SecureRandom();
        byte[] salt = new byte[saltLength];
        randomb.nextBytes(salt);
        try {
            

            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            
            iv = new byte[cipher.getBlockSize()];
            Log.i(TAG,"IV size => "+iv.length);
            randomb.nextBytes(iv);
            IvParameterSpec ivParams = new IvParameterSpec(iv);
            
            keySpec = new PBEKeySpec(password.toCharArray(), salt, iterationCount, keyLength);
            keyFactory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
            keyBytes = keyFactory.generateSecret(keySpec).getEncoded();
            key = new SecretKeySpec(keyBytes, "AES");
            
            cipher.init(Cipher.ENCRYPT_MODE, key, ivParams);
            
            byte[] ciphertext = cipher.doFinal(((String) obj).getBytes("UTF-8"));
            
            byte[] base64encodedSecretData = Base64.encode(ciphertext,Base64.DEFAULT);
            String secretString = new String(base64encodedSecretData);

            return new String(secretString);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }

    /**
     * Method to decrypt a given string
     * @param ciphertext
     * @return
     */
    public static String decrypt(String ciphertext) {
        try {
            if (ciphertext == null || ciphertext.length() == 0)
                throw new Exception("Empty string");

            byte[] fields = Base64.decode(ciphertext, Base64.DEFAULT);
//            byte[] cipherBytes = Base64.encode(fields[0].getBytes(), Base64.DEFAULT);
//            byte[] salt = Base64.encode(fields[1].getBytes(), Base64.DEFAULT);
//            Log.i(TAG," salt decrypt IV size => "+salt.length);
//            byte[] iv = fields[2].getBytes();
            
            Log.i(TAG,"decrypt IV size => "+iv.length);

//            KeySpec keySpec = new PBEKeySpec(password.toCharArray(), salt, iterationCount, keyLength);
//            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
//            byte[] keyBytes = keyFactory.generateSecret(keySpec).getEncoded();
//            SecretKey key = new SecretKeySpec(keyBytes, "AES");

            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            IvParameterSpec ivParams = new IvParameterSpec(iv);

            byte[] decrypted = null;
            cipher.init(Cipher.DECRYPT_MODE, key, ivParams);
            decrypted = cipher.doFinal(fields);

            return new String(decrypted);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

}
