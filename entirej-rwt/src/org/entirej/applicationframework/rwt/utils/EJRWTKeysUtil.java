/*******************************************************************************
 * Copyright 2013 CRESOFT AG
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 * Contributors:
 *     CRESOFT AG - initial API and implementation
 ******************************************************************************/
package org.entirej.applicationframework.rwt.utils;

import java.util.HashMap;
import java.util.Map;

public class EJRWTKeysUtil
{

    private static final Map<String, Integer> KEY_MAP = new HashMap<String, Integer>();
    static
    {
        KEY_MAP.put("BACKSPACE", Integer.valueOf(8));
        KEY_MAP.put("BS", Integer.valueOf(8));
        KEY_MAP.put("TAB", Integer.valueOf(9));
        KEY_MAP.put("RETURN", Integer.valueOf(13));
        KEY_MAP.put("ENTER", Integer.valueOf(13));
        KEY_MAP.put("CR", Integer.valueOf(13));
        KEY_MAP.put("PAUSE", Integer.valueOf(19));
        KEY_MAP.put("BREAK", Integer.valueOf(19));
        KEY_MAP.put("CAPS_LOCK", Integer.valueOf(20));
        KEY_MAP.put("ESCAPE", Integer.valueOf(27));
        KEY_MAP.put("ESC", Integer.valueOf(27));
        KEY_MAP.put("SPACE", Integer.valueOf(32));
        KEY_MAP.put("PAGE_UP", Integer.valueOf(33));
        KEY_MAP.put("PAGE_DOWN", Integer.valueOf(34));
        KEY_MAP.put("END", Integer.valueOf(35));
        KEY_MAP.put("HOME", Integer.valueOf(36));
        KEY_MAP.put("ARROW_LEFT", Integer.valueOf(37));
        KEY_MAP.put("ARROW_UP", Integer.valueOf(38));
        KEY_MAP.put("ARROW_RIGHT", Integer.valueOf(39));
        KEY_MAP.put("ARROW_DOWN", Integer.valueOf(40));
        KEY_MAP.put("PRINT_SCREEN", Integer.valueOf(44));
        KEY_MAP.put("INSERT", Integer.valueOf(45));
        KEY_MAP.put("DEL", Integer.valueOf(46));
        KEY_MAP.put("DELETE", Integer.valueOf(46));
        KEY_MAP.put("F1", Integer.valueOf(112));
        KEY_MAP.put("F2", Integer.valueOf(113));
        KEY_MAP.put("F3", Integer.valueOf(114));
        KEY_MAP.put("F4", Integer.valueOf(115));
        KEY_MAP.put("F5", Integer.valueOf(116));
        KEY_MAP.put("F6", Integer.valueOf(117));
        KEY_MAP.put("F7", Integer.valueOf(118));
        KEY_MAP.put("F8", Integer.valueOf(119));
        KEY_MAP.put("F9", Integer.valueOf(120));
        KEY_MAP.put("F10", Integer.valueOf(121));
        KEY_MAP.put("F11", Integer.valueOf(122));
        KEY_MAP.put("F12", Integer.valueOf(123));
        KEY_MAP.put("NUMPAD_0", Integer.valueOf(96));
        KEY_MAP.put("NUMPAD_1", Integer.valueOf(97));
        KEY_MAP.put("NUMPAD_2", Integer.valueOf(98));
        KEY_MAP.put("NUMPAD_3", Integer.valueOf(99));
        KEY_MAP.put("NUMPAD_4", Integer.valueOf(100));
        KEY_MAP.put("NUMPAD_5", Integer.valueOf(101));
        KEY_MAP.put("NUMPAD_6", Integer.valueOf(102));
        KEY_MAP.put("NUMPAD_7", Integer.valueOf(103));
        KEY_MAP.put("NUMPAD_8", Integer.valueOf(104));
        KEY_MAP.put("NUMPAD_9", Integer.valueOf(105));
        KEY_MAP.put("NUMPAD_MULTIPLY", Integer.valueOf(106));
        KEY_MAP.put("NUMPAD_ADD", Integer.valueOf(107));
        KEY_MAP.put("NUMPAD_SUBTRACT", Integer.valueOf(109));
        KEY_MAP.put("NUMPAD_DECIMAL", Integer.valueOf(110));
        KEY_MAP.put("NUMPAD_DIVIDE", Integer.valueOf(111));
        KEY_MAP.put("NUM_LOCK", Integer.valueOf(144));
        KEY_MAP.put("SCROLL_LOCK", Integer.valueOf(145));
        KEY_MAP.put(",", Integer.valueOf(188));
        KEY_MAP.put(".", Integer.valueOf(190));
        KEY_MAP.put("/", Integer.valueOf(191));
        KEY_MAP.put("`", Integer.valueOf(192));
        KEY_MAP.put("[", Integer.valueOf(219));
        KEY_MAP.put("\\", Integer.valueOf(220));
        KEY_MAP.put("]", Integer.valueOf(221));
        KEY_MAP.put("'", Integer.valueOf(222));
    }
    private final static String               ALT     = "ALT+";
    private final static String               CTRL    = "CTRL+";
    private final static String               SHIFT   = "SHIFT+";

    private EJRWTKeysUtil()
    {
    }

    public static KeyInfo toKeyInfo(int key, boolean shift, boolean control, boolean alt)
    {
        return new EJRWTKeysUtil.KeyInfo(key, shift, control, alt);
    }

    public static KeyInfo toKeyInfo(String keySequence)
    {
        return new EJRWTKeysUtil.KeyInfo(keySequence);
    }

    public static int toKeyCode(String keySequence)
    {
        int lastPlusIndex = keySequence.lastIndexOf("+");

        String keyPart = "";
        if (lastPlusIndex != -1)
        {
            keyPart = keySequence.substring(lastPlusIndex + 1);
        }
        else
        {
            keyPart = keySequence;
        }
        return getKeyCode(keyPart);
    }

    public static boolean hasShiftMask(String key)
    {
        return key.indexOf(SHIFT) != -1;
    }

    public static boolean hasAltMask(String key)
    {
        return key.indexOf(ALT) != -1;
    }

    public static boolean hasControlMask(String key)
    {
        return key.indexOf(CTRL) != -1;
    }

    private static int getKeyCode(String key)
    {
        int result = -1;
        Object value = KEY_MAP.get(key);
        if (value instanceof Integer)
        {
            result = ((Integer) value).intValue();
        }
        else if (key.length() == 1)
        {
            result = Character.toLowerCase(key.charAt(0));
        }
        else
        {
            throw new IllegalArgumentException("Unrecognized key: " + key);
        }
        return result;
    }

    public static class KeyInfo
    {
        public final int     key;
        public final boolean shift;
        public final boolean control;
        public final boolean alt;

        public KeyInfo(int key, boolean shift, boolean control, boolean alt)
        {
            this.key = key;
            this.shift = shift;
            this.control = control;
            this.alt = alt;
        }

        public KeyInfo(String keySequence)
        {
            key = toKeyCode(keySequence);
            shift = hasShiftMask(keySequence);
            control = hasControlMask(keySequence);
            alt = hasAltMask(keySequence);
        }

        @Override
        public int hashCode()
        {
            final int prime = 31;
            int result = 1;
            result = prime * result + (alt ? 1231 : 1237);
            result = prime * result + (control ? 1231 : 1237);
            result = prime * result + key;
            result = prime * result + (shift ? 1231 : 1237);
            return result;
        }

        @Override
        public boolean equals(Object obj)
        {
            if (this == obj)
            {
                return true;
            }
            if (obj == null)
            {
                return false;
            }
            if (getClass() != obj.getClass())
            {
                return false;
            }
            KeyInfo other = (KeyInfo) obj;
            if (alt != other.alt)
            {
                return false;
            }
            if (control != other.control)
            {
                return false;
            }
            if (key != other.key)
            {
                return false;
            }
            if (shift != other.shift)
            {
                return false;
            }
            return true;
        }
    }
}
