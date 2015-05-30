package ir.ac.iust.oie.fastdp.utils;

import edu.stanford.nlp.util.StringUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Majid on 30/05/2015.
 */
public class Line {
    public String text;
    public int number;
    public String[] splits;
    public boolean isEmpty;

    public static List<Line> getLines(Path path) throws IOException {
        return processLines(Files.readAllLines(path));
    }

    public static List<Line> processLines(List<String> lines) {
        List<Line> list = new ArrayList<>();
        int lineNumber = 0;
        for (String lineString : lines) {
            Line line = new Line();
            line.text = lineString.trim();
            line.number = lineNumber;
            if (line.text.length() == 0) line.isEmpty = true;
            else {
                line.splits = lineString.split("\t");
            }
            list.add(line);
        }
        return list;
    }

    @Override
    public String toString() {
        return StringUtils.join(splits, "\t");
    }

    public String toString(int replaceIndex, String replace) {
        String old = splits[replaceIndex];
        splits[replaceIndex] = replace;
        String toReturn = StringUtils.join(splits, "\t");
        splits[replaceIndex] = old;
        return toReturn;
    }
}