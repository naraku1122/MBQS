package com.project.mbqs.utils;

import android.app.Activity;
import android.util.Base64;

import androidx.appcompat.app.AlertDialog;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

public class MyUtilsOperation {
    private static SecretKeySpec secretKey;
    private static byte[] key;

    public static String encodeEmail(String decodedEmail){
        return decodedEmail.replace(".",",");
    }

    public static String decodeEmail(String encodedEmail){
        return encodedEmail.replace(",",".");
    }

    /// Encrypt the given string using the specified key.

    public static void setKey(String myKey){
        MessageDigest sha = null;
        try {
            key = myKey.getBytes("UTF-8");
            sha = MessageDigest.getInstance("SHA-1");
            key = sha.digest(key);
            key = Arrays.copyOf(key, 16);
            secretKey = new SecretKeySpec(key, "AES");
        }
        catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }
    public static String encrypt(String strToEncrypt, String key)
    {
        setKey(key);
        try
        {
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            return Base64.encodeToString(cipher.doFinal(strToEncrypt.getBytes("UTF-8")),Base64.DEFAULT);
        }
        catch (Exception e)
        {
            System.out.println("Error while encrypting: " + e.toString());
        }
        return null;
    }

    public static String decrypt(String strToDecrypt, String secret)
    {
        try
        {
            setKey(secret);
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5PADDING");
            cipher.init(Cipher.DECRYPT_MODE, secretKey);
            return new String(cipher.doFinal(Base64.decode(strToDecrypt,Base64.DEFAULT)));
        }
        catch (Exception e)
        {
            System.out.println("Error while decrypting: " + e.toString());
        }
        return null;
    }


    //About Us Dialog
    public static void showAboutUsDialog(Activity activity){

        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
// Add the buttons
        builder.setPositiveButton("OK", (dialog, id) -> {
            // User clicked OK button
            dialog.dismiss();
        });
        builder.setTitle("About Us");
        builder.setMessage("Developer Name : Joseph Ornopia");

// Create the AlertDialog
        AlertDialog dialog = builder.create();
        dialog.show();
    }
}
