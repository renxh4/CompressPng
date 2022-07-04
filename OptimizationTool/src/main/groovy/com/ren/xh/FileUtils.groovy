package com.ren.xh

import org.apache.commons.codec.digest.DigestUtils

import java.security.MessageDigest
import java.security.NoSuchAlgorithmException

class FileUtils {
    static String getFileContent(File file) {
        def reader = new FileReader(file)
        def sb = new StringBuffer()
        def line
        while ((line = reader.readLine()) != null) {
            sb.append(line)
        }
        reader.close()

        return sb.toString()
    }

    static void toFileContent(File file, String json) {
        def fw = new FileWriter(file)
        fw.write(json)
        fw.close()
    }


    public static String getMD5(String path) {
        if (path.endsWith(".xml")) {
            def content = getFileString(path)
            return getMD5Str(content)
        }
        return DigestUtils.md5Hex(new FileInputStream(path));
    }

    static String getFileString(String path) {
        def reader = new FileReader(path)
        def sb = new StringBuffer()
        String line
        while ((line = reader.readLine()) != null) {
            String s = line
            s.replace("\r", "");
            s.replace("\t", "");
            sb.append(s)
        }

        reader.close()

        return sb.toString()

    }

    public static String getMD5Str(String str) {
        byte[] digest = null;
        try {
            MessageDigest md5 = MessageDigest.getInstance("md5");
            digest = md5.digest(str.getBytes("utf-8"));
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        //16是表示转换为16进制数
        String md5Str = new BigInteger(1, digest).toString(16);
        return md5Str;
    }
}