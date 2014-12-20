package ir.ac.iust.oie.fastdp.conll;

import org.apache.log4j.Logger;

/**
 * Created by majid on 12/20/14.
 */
public class Word {
    public static Logger logger = Logger.getLogger(Word.class);
    private int id;
    private String wordForm;
    private String lemma;
    private String cPOSTag;
    private String POSTag;
    private String features;
    private int head;
    private String dependencyRelation;
    private String pHead;
    private String pHeadDependencyRelation;

    public Word() {
    }

    public Word(String line) {
        fillLineByLine(line);
    }

    public boolean fillLineByLine(String line) {
        logger.trace(line);
        String[] splits = line.split("\\t");
        if (splits.length != 10) return false;
        id = Integer.parseInt(splits[0]);
        wordForm = splits[1];
        lemma = splits[2];
        cPOSTag = splits[3];
        POSTag = splits[4];
        features = splits[5];
        head = Integer.parseInt(splits[6]);
        dependencyRelation = splits[7];
        pHead = splits[8];
        pHeadDependencyRelation = splits[9];
        return true;
    }

    public int getId() {
        return id;
    }

    public String getWordForm() {
        return wordForm;
    }

    public String getLemma() {
        return lemma;
    }

    public String getcPOSTag() {
        return cPOSTag;
    }

    public String getPOSTag() {
        return POSTag;
    }

    public String getFeatures() {
        return features;
    }

    public int getHead() {
        return head;
    }

    public String getDependencyRelation() {
        return dependencyRelation;
    }

    public String getpHead() {
        return pHead;
    }

    public String getpHeadDependencyRelation() {
        return pHeadDependencyRelation;
    }
}
