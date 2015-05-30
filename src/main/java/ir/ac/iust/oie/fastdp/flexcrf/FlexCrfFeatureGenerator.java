package ir.ac.iust.oie.fastdp.flexcrf;

/**
 * Created by Majid on 31/05/2015.
 */

import java.io.*;
import java.util.ArrayList;
import java.util.StringTokenizer;

public class FlexCrfFeatureGenerator {
    private static boolean SINGLE_WORD = true;
    private static boolean TWO_CONSECUTIVE_WORDS = true;
    private static boolean SINGLE_POS_TAG = true;
    private static boolean TWO_CONSECUTIVE_POS_TAGS = true;
    private static boolean THREE_CONSECUTIVE_POS_TAGS = false;
    private static boolean SINGLE_POS_TAG_AND_SINGLE_WORD = true;
    private static boolean TWO_CONSECUTIVE_POS_TAGS_AND_SINGLE_WORD = false;
    private static boolean TWO_CONSECUTIVE_WORDS_AND_SINGLE_POS_TAG = false;
    private static String[] PARAMETER_NAMES = new String[]{"SINGLE_WORD", "TWO_CONSECUTIVE_WORDS", "SINGLE_POS_TAG", "TWO_CONSECUTIVE_POS_TAGS", "THREE_CONSECUTIVE_POS_TAGS", "SINGLE_POS_TAG_AND_SINGLE_WORD", "TWO_CONSECUTIVE_POS_TAGS_AND_SINGLE_WORD", "TWO_CONSECUTIVE_WORDS_AND_SINGLE_POS_TAG"};

    public FlexCrfFeatureGenerator() {
    }

    public static void main(String[] args) {
        if (args.length == 0) {
            showHelp();
        } else if (args.length < 4) {
            showHelp();
        } else {
            File file = new File("features");
            ArrayList<String> inputLines = new ArrayList<>();
            String outputFile;
            String line;
            if (file.exists()) {
                inputLines = getFileContentsAsLines("features");
                outputFile = null;
                boolean result = false;
                String[] toLower = null;

                label115:
                for (int hasLabel = 0; hasLabel < inputLines.size(); ++hasLabel) {
                    String cps = (String) inputLines.get(hasLabel);
                    toLower = cps.split(" ");
                    outputFile = toLower[0];
                    result = Boolean.parseBoolean(toLower[1]);

                    for (int seq = 0; seq < PARAMETER_NAMES.length; ++seq) {
                        line = PARAMETER_NAMES[seq];
                        if (outputFile.equalsIgnoreCase(line)) {
                            switch (seq) {
                                case 0:
                                    SINGLE_WORD = result;
                                    continue label115;
                                case 1:
                                    TWO_CONSECUTIVE_WORDS = result;
                                    continue label115;
                                case 2:
                                    SINGLE_POS_TAG = result;
                                    continue label115;
                                case 3:
                                    TWO_CONSECUTIVE_POS_TAGS = result;
                                    continue label115;
                                case 4:
                                    THREE_CONSECUTIVE_POS_TAGS = result;
                                    continue label115;
                                case 5:
                                    SINGLE_POS_TAG_AND_SINGLE_WORD = result;
                                    continue label115;
                                case 6:
                                    TWO_CONSECUTIVE_POS_TAGS_AND_SINGLE_WORD = result;
                                    continue label115;
                                case 7:
                                    TWO_CONSECUTIVE_WORDS_AND_SINGLE_POS_TAG = result;
                                default:
                                    continue label115;
                            }
                        }
                    }
                }
            }

            inputLines = getFileContentsAsLines(args[1]);
            outputFile = args[2];
            StringBuilder var16 = new StringBuilder();
            boolean var17 = false;
            if (args.length > 3 && args[3].equalsIgnoreCase("tolower")) {
                var17 = true;
            }

            boolean var18 = false;
            if (args[0].equalsIgnoreCase("-lbl")) {
                var18 = true;
            }

            new Observation();
            Sequence var20 = new Sequence();
            line = null;

            for (int lineNumber = 0; lineNumber < inputLines.size(); ++lineNumber) {
                line = (String) inputLines.get(lineNumber);
                if (var17) {
                    line = line.toLowerCase();
                }

                StringTokenizer tok = new StringTokenizer(line, " \t\r\n");
                int len = tok.countTokens();
                if (len <= 0) {
                    if (var20.size() > 0) {
                        for (int var21 = 0; var21 < var20.size(); ++var21) {
                            try {
                                Observation var19 = getFeatures(var20, var21);

                                for (int e = 0; e < var19.size(); ++e) {
                                    var16.append((String) var19.get(e)).append(' ');
                                }

                                if (var18 && ((Observation) var20.get(var21)).size() > 1) {
                                    var16.append((String) ((Observation) var20.get(var21)).get(((Observation) var20.get(var21)).size() - 1));
                                }

                                var16.append('\n');
                            } catch (Exception var15) {
                                var15.printStackTrace();
                                System.out.println("Error in line number = " + lineNumber);
                                return;
                            }
                        }

                        var16.append('\n');
                    }

                    var20.clear();
                } else {
                    Observation obsr = new Observation();

                    while (tok.hasMoreTokens()) {
                        obsr.add(tok.nextToken());
                    }

                    var20.add(obsr);
                }
            }

            writeFileContentsFromString(outputFile, var16.toString());
        }
    }

    static Observation getFeatures(Sequence seq, int i) {
        Observation cps = new Observation();
        cps.clear();
        int len = seq.size();
        StringBuilder buff = new StringBuilder();
        if (i >= 0 && i <= len - 1) {
            int j;
            String temp;
            if (SINGLE_WORD) {
                for (j = -2; j <= 2; ++j) {
                    if (i + j >= 0 && i + j < len) {
                        temp = (String) ((Observation) seq.get(i + j)).get(0);
                        if (j == -1 || j == 0) {
                            buff.append("#");
                        }

                        buff.append("w:").append(j).append(":").append(temp);
                        cps.add(buff.toString());
                        buff.setLength(0);
                    }
                }
            }

            String temp1;
            if (TWO_CONSECUTIVE_WORDS) {
                for (j = -1; j <= 0; ++j) {
                    if (i + j >= 0 && i + j + 1 < len) {
                        temp = (String) ((Observation) seq.get(i + j)).get(0);
                        temp1 = (String) ((Observation) seq.get(i + j + 1)).get(0);
                        if (j == -1) {
                            buff.append("#");
                        }

                        buff.append("ww:").append(j).append(":").append(j + 1).append(":").append(temp).append(":").append(temp1);
                        cps.add(buff.toString());
                        buff.setLength(0);
                    }
                }
            }

            if (SINGLE_POS_TAG) {
                for (j = -2; j <= 2; ++j) {
                    if (i + j >= 0 && i + j < len) {
                        temp = (String) ((Observation) seq.get(i + j)).get(1);
                        if (j == -1 || j == 0) {
                            buff.append("#");
                        }

                        buff.append("p:").append(j).append(":").append(temp);
                        cps.add(buff.toString());
                        buff.setLength(0);
                    }
                }
            }

            if (TWO_CONSECUTIVE_POS_TAGS) {
                for (j = -2; j <= 1; ++j) {
                    if (i + j >= 0 && i + j + 1 < len) {
                        temp = (String) ((Observation) seq.get(i + j)).get(1);
                        temp1 = (String) ((Observation) seq.get(i + j + 1)).get(1);
                        if (j == -1) {
                            buff.append("#");
                        }

                        buff.append("pp:").append(j).append(":").append(j + 1).append(":").append(temp).append(":").append(temp1);
                        cps.add(buff.toString());
                        buff.setLength(0);
                    }
                }
            }

            String temp2;
            if (THREE_CONSECUTIVE_POS_TAGS) {
                for (j = -2; j <= 0; ++j) {
                    if (i + j >= 0 && i + j + 2 < len) {
                        temp = (String) ((Observation) seq.get(i + j)).get(1);
                        temp1 = (String) ((Observation) seq.get(i + j + 1)).get(1);
                        temp2 = (String) ((Observation) seq.get(i + j + 2)).get(1);
                        buff.append("ppp:").append(j).append(":").append(j + 1).append(":").append(j + 2).append(":").append(temp).append(":").append(temp1).append(":").append(temp2);
                        cps.add(buff.toString());
                        buff.setLength(0);
                        if (j == -1) {
                            String temp3 = (String) ((Observation) seq.get(i + j + 1)).get(0);
                            buff.append("pppw:").append(j).append(":").append(j + 1).append(":").append(j + 2).append(":").append(j + 1).append(":").append(temp).append(":").append(temp1).append(":").append(temp2).append(":").append(temp3);
                            cps.add(buff.toString());
                            buff.setLength(0);
                        }
                    }
                }
            }

            if (SINGLE_POS_TAG_AND_SINGLE_WORD) {
                for (j = -1; j <= 0; ++j) {
                    if (i + j >= 0 && i + j < len) {
                        temp = (String) ((Observation) seq.get(i + j)).get(1);
                        temp1 = (String) ((Observation) seq.get(i + j)).get(0);
                        if (j == -1 || j == 0) {
                            buff.append("#");
                        }

                        buff.append("pw:").append(j).append(":").append(j).append(":").append(temp).append(":").append(temp1);
                        cps.add(buff.toString());
                        buff.setLength(0);
                    }
                }
            }

            if (TWO_CONSECUTIVE_POS_TAGS_AND_SINGLE_WORD) {
                for (j = -1; j < 0; ++j) {
                    if (i + j >= 0 && i + j + 1 < len) {
                        temp = (String) ((Observation) seq.get(i + j)).get(1);
                        temp1 = (String) ((Observation) seq.get(i + j + 1)).get(1);
                        temp2 = (String) ((Observation) seq.get(i + j)).get(0);
                        if (j == -1) {
                            buff.append("#");
                        }

                        buff.append("ppw:").append(j).append(":").append(j + 1).append(":").append(j).append(":").append(temp).append(":").append(temp1).append(":").append(temp2);
                        cps.add(buff.toString());
                        buff.setLength(0);
                        temp2 = (String) ((Observation) seq.get(i + j + 1)).get(0);
                        if (j == -1) {
                            buff.append("#");
                        }

                        buff.append("ppw:").append(j).append(":").append(j + 1).append(":").append(j + 1).append(":").append(temp).append(":").append(temp1).append(":").append(temp2);
                        cps.add(buff.toString());
                        buff.setLength(0);
                    }
                }
            }

            if (TWO_CONSECUTIVE_WORDS_AND_SINGLE_POS_TAG) {
                for (j = -1; j < 0; ++j) {
                    if (i + j >= 0 && i + j + 1 < len) {
                        temp = (String) ((Observation) seq.get(i + j)).get(0);
                        temp1 = (String) ((Observation) seq.get(i + j + 1)).get(0);
                        temp2 = (String) ((Observation) seq.get(i + j)).get(1);
                        if (j == -1) {
                            buff.append("#");
                        }

                        buff.append("pww:").append(j).append(":").append(j + 1).append(":").append(j).append(":").append(temp).append(":").append(temp1).append(":").append(temp2);
                        cps.add(buff.toString());
                        buff.setLength(0);
                        temp2 = (String) ((Observation) seq.get(i + j + 1)).get(1);
                        if (j == -1) {
                            buff.append("#");
                        }

                        buff.append("pww:").append(j).append(":").append(j + 1).append(":").append(j + 1).append(":").append(temp).append(":").append(temp1).append(":").append(temp2);
                        cps.add(buff.toString());
                        buff.setLength(0);
                    }
                }
            }

            return cps;
        } else {
            return cps;
        }
    }

    private static void showHelp() {
        System.out.println("usage: ./chunkingfeasel -lbl/-ulb (labeled or unlabeled data) <input file> <output file> [tolower]");
    }


    public static ArrayList<String> getFileContentsAsLines(String filename) {
        ArrayList<String> lines = new ArrayList<>();
        File file = new File(filename);
        FileInputStream fis = null;
        BufferedInputStream bis = null;
        BufferedReader dis = null;

        try {
            fis = new FileInputStream(file);
            bis = new BufferedInputStream(fis);
            dis = new BufferedReader(new InputStreamReader(bis));
            String e = null;

            while ((e = dis.readLine()) != null) {
                lines.add(e);
            }

            fis.close();
            bis.close();
            dis.close();
            return lines;
        } catch (FileNotFoundException var7) {
            return null;
        } catch (IOException var8) {
            return null;
        }
    }

    public static boolean writeFileContentsFromString(String filename, String content) {
        FileOutputStream outputStream = null;

        boolean bytes;
        try {
            File ex = new File(filename);
            outputStream = new FileOutputStream(ex);
            byte[] bytes1 = content.getBytes();
            outputStream.write(bytes1);
            boolean ex1 = true;
            return ex1;
        } catch (FileNotFoundException var17) {
            bytes = false;
        } catch (IOException var18) {
            bytes = false;
            return bytes;
        } finally {
            try {
                outputStream.close();
            } catch (IOException var16) {
                return false;
            }
        }

        return bytes;
    }
}
