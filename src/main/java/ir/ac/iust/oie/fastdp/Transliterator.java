package ir.ac.iust.oie.fastdp;

import ir.ac.iust.text.utils.ColumnedLine;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Majid on 30/05/2015.
 */
public class Transliterator {
    private static final char[] table = new char[]{'ء', '\'', 'آ', '|', 'أ', '>', 'ؤ', '&', 'إ', '<', 'ئ', '}', 'ا',
            'A', 'ب', 'b', 'ة', 'p', 'ت', 't', 'ث', 'v', 'ج', 'j', 'ح', 'H', 'خ', 'x', 'د', 'd', 'ذ', '*', 'ر', 'r',
            'ز', 'z', 'س', 's', 'ش', '$', 'ص', 'S', 'ض', 'D', 'ط', 'T', 'ظ', 'Z', 'ع', 'E', 'غ', 'g', 'ـ', '_', 'ف',
            'f', 'ق', 'q', 'ك', 'k', 'ل', 'l', 'م', 'm', 'ن', 'n', 'ه', 'h', 'و', 'w', 'ی', 'Y', 'ي', 'y', 'ى', 'y',
            'ٰ', '`', 'ٱ', '{', 'پ', 'P', 'چ', 'J', 'ژ', 'V', 'گ', 'G', 'ک', 'k', '=', '=', '.', '.', ',', ',', '،',
            ',', '؟', '?', '!', '!', ' ', '_', '\u200c', '_', '\n', '\n', '\r', '\r', '۱', '1', '۲', '2', '۳', '3',
            '۴', '4', '۵', '5', '۶', '6', '۷', '7', '۸', '8', '۹', '9', '۰', '0', '«', '\"', '»', '\"', ':', ':',
            '%', '%', '/', '/', '\\', '\\', '-', '-', '_', '_', 'a', 'a', 'b', 'b', 'c', 'c', 'd', 'd', 'e', 'e',
            'f', 'f', 'g', 'g', 'h', 'h', 'i', 'i', 'j', 'j', 'k', 'k', 'l', 'l', 'm', 'm', 'n', 'n', 'o', 'o', 'p',
            'p', 'q', 'q', 'r', 'r', 's', 's', 't', 't', 'u', 'u', 'v', 'v', 'w', 'w', 'x', 'x', 'y', 'y', 'z', 'z',
            'A', 'A', 'B', 'G', 'C', 'C', 'D', 'D', 'E', 'E', 'F', 'F', 'G', 'G', 'H', 'H', 'I', 'I', 'J', 'J', 'K',
            'K', 'L', 'L', 'M', 'M', 'N', 'N', 'O', 'O', 'P', 'P', 'Q', 'Q', 'R', 'R', 'S', 'S', 'T', 'T', 'U', 'U',
            'V', 'V', 'W', 'W', 'X', 'X', 'Y', 'Y', 'Z', 'Z', 'ً', 'F', 'ٌ', 'N', 'ٍ', 'K', 'َ', 'a', 'ُ', 'u', 'ِ',
            'i', 'ّ', '~', 'ْ', 'o'};
    static HashMap<Character, Character> map = new HashMap<>();

    static {
        for (int i = 0, tableLength = table.length; i < tableLength; i += 2)
            map.put(table[i], table[i + 1]);
    }

    public static void transliterate(Path input, Path output) throws IOException {
        List<ColumnedLine> lines = ColumnedLine.getLines(input);
        StringBuilder builder = new StringBuilder();
        for (ColumnedLine line : lines) {
            if (line.isEmpty()) builder.append('\n');
            else builder.append(line.toString(0, transliterate(line.column(0)).toString())).append('\n');
        }
        System.out.println("number of lines is " + lines.size());
        List<String> outputLines = new ArrayList<>();
        outputLines.add(builder.toString());
        Files.deleteIfExists(output);
        Files.write(output, outputLines, Charset.forName("UTF-8"));
    }

    public static CharSequence transliterate(CharSequence input) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < input.length(); i++) {
            Character character = map.get(input.charAt(i));
            builder.append(character == null ? "*" : character);
        }
        return builder;
    }
}
