package ir.ac.iust.oie.fastdp.conll;

import java.util.ArrayList;

/**
 * Created by majid on 12/20/14.
 */
public class Sentence {
    private ArrayList<Word> words = new ArrayList<>();

    public boolean addWord(String word) {
        Word wordData = new Word();
        boolean done = wordData.fillLineByLine(word);
        if (done) words.add(wordData);
        return done;
    }

    public void addWord(Word wordData) {
        words.add(wordData);
    }

    public int getLength() {
        return words.size();
    }

    public Word getWord(int position) {
        if (position < 1)
            return null;
        if (position > words.size())
            return null;
        return words.get(position - 1);
    }
}
