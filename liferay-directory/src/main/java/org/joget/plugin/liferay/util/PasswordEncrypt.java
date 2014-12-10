/**
 * Copyright (c) 2000-2012 Liferay, Inc. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */

package org.joget.plugin.liferay.util;


import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.util.Random;

import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

import jodd.util.BCrypt;

import org.vps.crypt.Crypt;

import com.liferay.portal.PwdEncryptorException;
import com.liferay.portal.kernel.util.Base64;
import com.liferay.portal.kernel.util.CharPool;
import com.liferay.portal.kernel.util.Digester;
import com.liferay.portal.kernel.util.StringPool;
import com.liferay.portal.kernel.util.Validator;

/**
 * @author Brian Wing Shun Chan
 * @author Scott Lee
 * @author Arun Das Karanath
 */
public class PasswordEncrypt {

    public static final char[] SALT_CHARS =
            "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789./"
                    .toCharArray();

    public static final String TYPE_BCRYPT = "BCRYPT";

    /**
     * @deprecated {@link #TYPE_UFC_CRYPT}
     */
    public static final String TYPE_CRYPT = "CRYPT";

    public static final String TYPE_MD2 = "MD2";

    public static final String TYPE_MD5 = "MD5";

    public static final String TYPE_NONE = "NONE";
    
    public static final String TYPE_PBKDF2 = "PBKDF2";

    public static final String TYPE_SHA = "SHA";

    public static final String TYPE_SHA_256 = "SHA-256";

    public static final String TYPE_SHA_384 = "SHA-384";

    public static final String TYPE_SSHA = "SSHA";

    public static final String TYPE_UFC_CRYPT = "UFC-CRYPT";

    public static String encrypt(String algorithm, String clearTextPassword, boolean _base64) {
        return encrypt(algorithm, clearTextPassword, null, _base64);
    }

    public static String encrypt(
            String algorithm, String clearTextPassword,
            String currentEncryptedPassword, boolean _base64) {

        if (algorithm.equals(TYPE_BCRYPT)) {
            byte[] saltBytes = _getSaltFromBCrypt(currentEncryptedPassword);

            return encodePassword(algorithm, clearTextPassword, saltBytes, _base64 );
        } else if (algorithm.equals(TYPE_CRYPT) ||
                algorithm.equals(TYPE_UFC_CRYPT)) {

            byte[] saltBytes = _getSaltFromCrypt(currentEncryptedPassword);

            return encodePassword(algorithm, clearTextPassword, saltBytes, _base64);
        } else if (algorithm.equals(TYPE_NONE)) {
            return clearTextPassword;
        } else if (algorithm.equals(TYPE_PBKDF2)) {
            byte[] saltBytes = null;

            PBKDF2EncryptionConfiguration pbkdf2EncryptionConfiguration = new  PBKDF2EncryptionConfiguration();
            try {
            	pbkdf2EncryptionConfiguration.configure(TYPE_PBKDF2, currentEncryptedPassword);
    			saltBytes = pbkdf2EncryptionConfiguration.getSaltBytes();
    			PBEKeySpec pbeKeySpec = new PBEKeySpec(
    					clearTextPassword.toCharArray(), saltBytes,
    					pbkdf2EncryptionConfiguration.getRounds(),
    					pbkdf2EncryptionConfiguration.getKeySize());
    			
    			String algorithmName = algorithm+"WithHmacSHA1";

    			int index = algorithm.indexOf(CharPool.SLASH);

    			if (index > -1) {
    				algorithmName = algorithm.substring(0, index);
    			}
    			SecretKeyFactory secretKeyFactory = SecretKeyFactory.getInstance(algorithmName);
    			SecretKey secretKey = secretKeyFactory.generateSecret(pbeKeySpec);
    			byte[] secretKeyBytes = secretKey.getEncoded();
    			ByteBuffer byteBuffer = ByteBuffer.allocate(
    					2 * 4 + saltBytes.length + secretKeyBytes.length);

    				byteBuffer.putInt(pbkdf2EncryptionConfiguration.getKeySize());
    				byteBuffer.putInt(pbkdf2EncryptionConfiguration.getRounds());
    				byteBuffer.put(saltBytes);
    				byteBuffer.put(secretKeyBytes);

    				return Base64.encode(byteBuffer.array());
    		} catch (PwdEncryptorException e) {
    			// TODO Auto-generated catch block
    			e.printStackTrace();
    		} catch (NoSuchAlgorithmException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InvalidKeySpecException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}            
            
            return null;
        } else if (algorithm.equals(TYPE_SSHA)) {
            byte[] saltBytes = _getSaltFromSSHA(currentEncryptedPassword);

            return encodePassword(algorithm, clearTextPassword, saltBytes, _base64);
        } else {
            return encodePassword(algorithm, clearTextPassword, null, _base64);
        }
    }

    protected static String encodePassword(
            String algorithm, String clearTextPassword, byte[] saltBytes, boolean _base64) {
        String result = null;

        try {
            if (algorithm.equals(TYPE_BCRYPT)) {
                String salt = new String(saltBytes);

                result = BCrypt.hashpw(clearTextPassword, salt);
            } else if (algorithm.equals(TYPE_CRYPT) ||
                    algorithm.equals(TYPE_UFC_CRYPT)) {

                result = Crypt.crypt(
                        saltBytes, clearTextPassword.getBytes(Digester.ENCODING));
            } else if (algorithm.equals(TYPE_SSHA)) {
                byte[] clearTextPasswordBytes = clearTextPassword.getBytes(
                        Digester.ENCODING);

                // Create a byte array of salt bytes appended to password bytes

                byte[] pwdPlusSalt =
                        new byte[clearTextPasswordBytes.length + saltBytes.length];

                System.arraycopy(
                        clearTextPasswordBytes, 0, pwdPlusSalt, 0,
                        clearTextPasswordBytes.length);

                System.arraycopy(
                        saltBytes, 0, pwdPlusSalt, clearTextPasswordBytes.length,
                        saltBytes.length);

                // Digest byte array

                MessageDigest sha1Digest = MessageDigest.getInstance("SHA-1");

                byte[] pwdPlusSaltHash = sha1Digest.digest(pwdPlusSalt);

                // Appends salt bytes to the SHA-1 digest.

                byte[] digestPlusSalt =
                        new byte[pwdPlusSaltHash.length + saltBytes.length];

                System.arraycopy(
                        pwdPlusSaltHash, 0, digestPlusSalt, 0,
                        pwdPlusSaltHash.length);

                System.arraycopy(
                        saltBytes, 0, digestPlusSalt, pwdPlusSaltHash.length,
                        saltBytes.length);
                // Base64 encode and format string
                result = Base64.encode(digestPlusSalt);
            }  else {
                result = DigestUtil.digest(algorithm, _base64, new String[] { clearTextPassword });
            }
        } catch (NoSuchAlgorithmException nsae) {
        } catch (UnsupportedEncodingException uee) {
        }
        return result;
    }

    private static byte[] _getSaltFromBCrypt(String bcryptString) {

        byte[] saltBytes = null;

        try {
            if (Validator.isNull(bcryptString)) {
                String salt = BCrypt.gensalt();

                saltBytes = salt.getBytes(StringPool.UTF8);
            } else {
                String salt = bcryptString.substring(0, 29);

                saltBytes = salt.getBytes(StringPool.UTF8);
            }
        } catch (UnsupportedEncodingException uee) {

        }

        return saltBytes;
    }

    private static byte[] _getSaltFromCrypt(String cryptString) {

        byte[] saltBytes = null;

        try {
            if (Validator.isNull(cryptString)) {

                // Generate random salt

                Random random = new Random();

                int numSaltChars = SALT_CHARS.length;

                StringBuilder sb = new StringBuilder();

                int x = random.nextInt(Integer.MAX_VALUE) % numSaltChars;
                int y = random.nextInt(Integer.MAX_VALUE) % numSaltChars;

                sb.append(SALT_CHARS[x]);
                sb.append(SALT_CHARS[y]);

                String salt = sb.toString();

                saltBytes = salt.getBytes(Digester.ENCODING);
            } else {

                // Extract salt from encrypted password

                String salt = cryptString.substring(0, 2);

                saltBytes = salt.getBytes(Digester.ENCODING);
            }
        } catch (UnsupportedEncodingException uee) {

        }

        return saltBytes;
    } 

    private static byte[] _getSaltFromSSHA(String sshaString) {

        byte[] saltBytes = new byte[8];

        if (Validator.isNull(sshaString)) {

            // Generate random salt

            Random random = new SecureRandom();

            random.nextBytes(saltBytes);
        } else {

            // Extract salt from encrypted password

            try {
                byte[] digestPlusSalt = Base64.decode(sshaString);
                byte[] digestBytes = new byte[digestPlusSalt.length - 8];

                System.arraycopy(
                        digestPlusSalt, 0, digestBytes, 0, digestBytes.length);

                System.arraycopy(
                        digestPlusSalt, digestBytes.length, saltBytes, 0,
                        saltBytes.length);
            } catch (Exception e) {

            }
        }

        return saltBytes;
    }

    public static byte[] digest(String algorithm, String clearTextPassword) throws NoSuchAlgorithmException {
        MessageDigest mDigest = MessageDigest.getInstance(algorithm);
        byte[] hashText = mDigest.digest(clearTextPassword.getBytes());
        return hashText;
    }

}