package com.jetbrains.isaev.dao;

import java.io.*;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/**
 * Created by Ilya.Isaev on 01.08.2014.
 */
public class ZipUtils {
    public static byte[] compress(String str) {
        try {
            if (str == null) {
                return null;
            }
            ByteArrayOutputStream obj = new ByteArrayOutputStream();
            GZIPOutputStream gzip = new GZIPOutputStream(obj);

            gzip.write(str.getBytes());
            gzip.close();
            return obj.toByteArray();
        } catch (IOException e) {
            return null;
        }
    }

    public static String decompress(byte[] zipped) {
        try {
            if (zipped == null) {
                return null;
            }
            GZIPInputStream gis = null;
            gis = new GZIPInputStream(new ByteArrayInputStream(zipped));

            BufferedReader bf = new BufferedReader(new InputStreamReader(gis));
            StringBuilder outStr = new StringBuilder();
            String line;
            while ((line = bf.readLine()) != null) {
                outStr.append(line);
            }
            return outStr.toString();
        } catch (IOException e) {
            return null;
        }
    }
}
