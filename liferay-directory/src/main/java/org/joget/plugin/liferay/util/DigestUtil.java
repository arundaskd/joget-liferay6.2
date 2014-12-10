package org.joget.plugin.liferay.util;

import com.liferay.portal.kernel.util.Base64;
//import com.liferay.portal.kernel.util.StringBundler;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.apache.commons.codec.binary.Hex;

public class DigestUtil
{
    public static String digest(String algorithm, boolean _BASE_64, String[] text)
    {
        if (_BASE_64) {
            return digestBase64(algorithm, text);
        }

        return digestHex(algorithm, text);
    }

    public String digestBase64(ByteBuffer byteBuffer)
    {
        return digestBase64("SHA", byteBuffer);
    }

    public String digestBase64(String text) {
        return digestBase64("SHA", new String[] { text });
    }

    public String digestBase64(String algorithm, ByteBuffer byteBuffer) {
        byte[] bytes = digestRaw(algorithm, byteBuffer);

        return Base64.encode(bytes);
    }

    public static String digestBase64(String algorithm, String[] text) {
        byte[] bytes = digestRaw(algorithm, text);

        return Base64.encode(bytes);
    }

    public String digestHex(ByteBuffer byteBuffer) {
        return digestHex("SHA", byteBuffer);
    }

    public String digestHex(String text) {
        return digestHex("SHA", new String[] { text });
    }

    public String digestHex(String algorithm, ByteBuffer byteBuffer) {
        byte[] bytes = digestRaw(algorithm, byteBuffer);

        return Hex.encodeHexString(bytes);
    }

    public static String digestHex(String algorithm, String[] text) {
        byte[] bytes = digestRaw(algorithm, text);

        return Hex.encodeHexString(bytes);
    }

    public byte[] digestRaw(ByteBuffer byteBuffer) {
        return digestRaw("SHA", byteBuffer);
    }

    public byte[] digestRaw(String text) {
        return digestRaw("SHA", new String[] { text });
    }

    public byte[] digestRaw(String algorithm, ByteBuffer byteBuffer) {
        MessageDigest messageDigest = null;
        try
        {
            messageDigest = MessageDigest.getInstance(algorithm);

            messageDigest.update(byteBuffer);
        }
        catch (NoSuchAlgorithmException nsae) {
        }
        return messageDigest.digest();
    }

    public static byte[] digestRaw(String algorithm, String[] text) {
        MessageDigest messageDigest = null;
        try
        {
            messageDigest = MessageDigest.getInstance(algorithm);

            StringBuilder sb = new StringBuilder(text.length * 2 - 1);

            for (String t : text) {
                if (sb.length() > 0) {
                    sb.append(":");
                }

                sb.append(t);
            }

            String s = sb.toString();

            messageDigest.update(s.getBytes("UTF-8"));
        } catch (NoSuchAlgorithmException nsae) {
        }
        catch (UnsupportedEncodingException uee) {
        }
        return messageDigest.digest();
    }
}