package ir.ac.iust.oie.fastdp.converter;

import ir.ac.iust.oie.fastdp.conll.Sentence;
import ir.ac.iust.oie.fastdp.conll.Word;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by majid on 12/20/14.
 */
public class PersianDadeganConverter {
    public static Logger logger = Logger.getLogger(PersianDadeganConverter.class);

    private Path inputPath;
    private Path outputPath;

    public PersianDadeganConverter(Path inputPath, Path outputPath) {
        this.inputPath = inputPath;
        this.outputPath = outputPath;
    }

    public void run() throws IOException {
        List<String> lines = Files.readAllLines(inputPath, Charset.forName("UTF-8"));
        ArrayList<String> outputLines = new ArrayList<>();
        Sentence currentSentence = new Sentence();
        int lineNumber = 1;
        for (String wordData : lines) {
            logger.trace(String.valueOf(lineNumber++));
            boolean added = currentSentence.addWord(wordData);
            if (!added) {
                processSentence(currentSentence, outputLines);
                currentSentence = new Sentence();
            }
        }
        Files.write(outputPath, outputLines, Charset.forName("UTF-8"), StandardOpenOption.CREATE);
        logger.info("bye bye.");
    }

    private void processSentence(Sentence sentence, ArrayList<String> outputLines) {
        for (int i = 1; i <= sentence.getLength(); i++) {
            Word word = sentence.getWord(i);
            outputLines.add(word.getWordForm() + '\t' + word.getcPOSTag() + '\t'
                    + (word.getHead() == 0 ? "ROOT" : sentence.getWord(word.getHead()).getcPOSTag()));
        }
        outputLines.add("");
        outputLines.add("");
    }
}
