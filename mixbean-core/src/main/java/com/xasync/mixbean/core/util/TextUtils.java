/*
Copyright 2022~Forever xasync.com under one or more contributor authorized.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/
package com.xasync.mixbean.core.util;

import org.apache.commons.lang3.StringUtils;

/**
 * TextUtils
 *
 * @author xasync.com
 */
public class TextUtils {

    /**
     * The substring of the specified length is taken forward from the specified position of text,
     * and the white space character is not calculated into the length.
     *
     * @param text   string
     * @param offset a start position
     * @param length the length of substring you need
     * @return substring
     */
    public static String subStrForwardSkipWhitespace(String text, int offset, int length) {
        if (StringUtils.isBlank(text)) {
            return null;
        }
        int count = 1;
        StringBuilder sb = new StringBuilder();
        for (int index = offset - 1; index >= 0 && count <= length; index--) {
            char c = text.charAt(index);
            //skip whitespace
            if (Character.isWhitespace(c)) {
                continue;
            }
            sb.append(c);
            count += 1;
        }
        return sb.reverse().toString();
    }

    /**
     * The substring of the specified length is taken backward from the specified position of text,
     * and the white space character is not calculated into the length.
     *
     * @param text   string
     * @param offset a start position
     * @param length the length of substring you need
     * @return substring
     */
    public static String subStrBackwardSkipWhitespace(String text, int offset, int length) {
        if (StringUtils.isBlank(text)) {
            return null;
        }
        int count = 1;
        StringBuilder sb = new StringBuilder();
        for (int index = offset; index < text.length() && count <= length; index++) {
            char c = text.charAt(index);
            //skip whitespace
            if (Character.isWhitespace(c)) {
                continue;
            }
            sb.append(c);
            count += 1;
        }
        return sb.toString();
    }

}
