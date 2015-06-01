package ir.ac.iust.oie.fastdp;

import edu.stanford.nlp.ling.TaggedWord;
import ir.ac.iust.text.utils.WordLine;
import iust.ac.ir.nlp.jhazm.POSTagger;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Majid on 30/05/2015.
 */
public class POSTagChanger {

    public static void changePOSTags(Path input, Path output, Integer maximumLines) throws IOException {
        List<WordLine> lines = WordLine.getLines(input);
        POSTagger posTagger = new POSTagger();
        List<String> sentence = new ArrayList<>();
        List<WordLine> actualSentence = new ArrayList<>();
        StringBuilder builder = new StringBuilder();
        for (int i = 0, linesSize = lines.size(); i < linesSize; i++) {
            WordLine line1 = lines.get(i);
            if (line1.isEmpty) {
                if (!sentence.isEmpty()) {
                    List<TaggedWord> tagged = posTagger.batchTag(sentence);
                    alignSentences(actualSentence, tagged, builder);
                    if (maximumLines != null && i > maximumLines)
                        break;
                    actualSentence.clear();
                    sentence.clear();
                }
            } else {
                sentence.add(line1.splits[0]);
                actualSentence.add(line1);
            }
        }
        if (!sentence.isEmpty()) {
            List<TaggedWord> tagged = posTagger.batchTag(sentence);
            alignSentences(actualSentence, tagged, builder);
        }
        List<String> outputLines = new ArrayList<>();
        outputLines.add(builder.toString());
        Files.deleteIfExists(output);
        Files.write(output, outputLines, Charset.forName("UTF-8"));
    }

    private static void alignSentences(List<WordLine> actualSentence, List<TaggedWord> tagged, StringBuilder builder) {
        if (actualSentence.size() != tagged.size())
            System.exit(1);
        for (int i = 0; i < actualSentence.size(); i++) {
            builder.append(actualSentence.get(i).splits[0]).append('\t')
                    .append(tagged.get(i).tag()).append('\t')
                    .append(actualSentence.get(i).splits[2])
                    .append('\n');
        }
        builder.append('\n');
    }
}
