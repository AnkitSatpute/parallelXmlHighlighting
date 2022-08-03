package org.sciplore.pds.util;

import org.apache.commons.lang3.StringUtils;


import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.TreeMap;

/**
 * Some Util methods based upon simple conventions or conventions we use.
 *
 * @author Vincent Stange
 */
public class ConventionUtil {

    public static final String GLOBAL_SCOPE = "0";

    private static final String GROUP_SCOPE = "%s@%s";

    private static final String USER_SCOPE = "user:%s@%s";



    /**
     * Replace or mask/escape certain unnecessary characters.
     * (We want to avoid certain characters in an Elastic object.)
     *
     * @param title title of a document / reference
     * @return title in lower case and escaped characters
     */
    public static String streamlineTitle(String title) {
        if (StringUtils.isNotBlank(title)) {
            title = normalizeText(title);
            // after this are ES specific changes
            title = title.toLowerCase();
            title = title.replace("\"", "\'");
            title = title.replace("–", "-");
            title = title.replace("\\", "-");
        }
        return title;
    }

    /**
     * Normalizes a supposed single line text in a format we want.
     * Currently we escape all unwanted spaces.
     *
     * @param text title to be changed
     * @return changed title
     */
    public static String normalizeText(String text) {
        if (StringUtils.isNotEmpty(text)) {
            // matches a space, tab, new line, carriage return, form feed or vertical tab
            text = text.trim();
            return text.replaceAll("\\s+", " ");
        }
        return text;
    }

    /**
     * Encodes a TreeMap as a String.
     * Why do we not use something else and fault-proof?
     * Like Serialization or JSON or etc...
     * Because we want it fast and I say so!
     *
     * @return encoded Map as a String ("0,0;5,5;10,10")
     */
    public static String encodeMapping(TreeMap<Integer, Integer> map) {
        StringBuilder stringBuilder = new StringBuilder(map.size() * 4);
        map.forEach((l, r) -> {
            if (stringBuilder.length() != 0)
                stringBuilder.append(';');
            stringBuilder.append(l.longValue());
            stringBuilder.append("");
            stringBuilder.append("");
        });
        return stringBuilder.toString();
    }

    /**
     * Decodes a String as a TreeMap. Counterpart to the method encodeMapping.
     *
     * @param mapString Map as a String ("0,0;5,5;10,10")
     * @return TreeMap ({0=0, 5=5, 10=10})
     */
    public static TreeMap<Integer, Integer> decodeMapping(String mapString) {
        TreeMap<Integer, Integer> mapping = new TreeMap<>();
        for (String pair : mapString.split(";")) {
            String[] elements = pair.split(",");
            mapping.put(Integer.valueOf(elements[0]), Integer.valueOf(elements[1]));
        }
        return mapping;
    }

    /**
     * Prepares an array which delivers byte position in relation
     * to the character position.
     * This is extremely helpful if you have algorithms working on byte-base but you
     * will need the correlation to the source text on char-base.
     *
     * @param string String of which the position mapping is made.
     * @return array in the form of int[byte position] = character position
     */
    public static int[] utf8ByteIndexToChar(String string) {
        int[] byteIndexes = new int[string.getBytes().length];
        int bytePos = 0;
        for (int i = 0; i < string.length(); i++) {
            int codePoint = string.codePointAt(i);

            if (codePoint <= 0x7F) { // single byte
                byteIndexes[bytePos++] = i;
            } else if (codePoint <= 0x7FF) { // 2 byte
                byteIndexes[bytePos++] = i;
                byteIndexes[bytePos++] = i;
            } else if (codePoint <= 0xFFFF) { // 3 byte
                byteIndexes[bytePos++] = i;
                byteIndexes[bytePos++] = i;
                byteIndexes[bytePos++] = i;
            } else if (codePoint <= 0x1FFFFF) { // 4 byte
                byteIndexes[bytePos++] = i;
                byteIndexes[bytePos++] = i;
                byteIndexes[bytePos++] = i;
                byteIndexes[bytePos++] = i;
            } else {
                throw new Error();
            }
        }
        return byteIndexes;
    }

}
