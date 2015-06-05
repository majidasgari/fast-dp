package ir.ac.iust.oie.fastdp;

import edu.stanford.nlp.ling.TaggedWord;
import ir.ac.iust.oie.fastdp.converter.PersianDadeganConverter;
import ir.ac.iust.oie.fastdp.flexcrf.FlexCrfFeatureGenerator;
import ir.ac.iust.text.utils.*;
import iust.ac.ir.nlp.jhazm.Normalizer;
import iust.ac.ir.nlp.jhazm.POSTagger;
import iust.ac.ir.nlp.jhazm.SentenceTokenizer;
import iust.ac.ir.nlp.jhazm.WordTokenizer;
import org.apache.commons.cli.*;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by majid on 12/20/14.
 */
public class Runner {

    public static boolean prepared = false;
    private static Logger logger = LoggerUtils.getLogger(Runner.class, "fast-dp.log");

    private static void prepareFiles() throws IOException {
        if (prepared) return;
        FileHandler.setCopyRoot("resources");
        FileHandler.prepareFile("linux", "crf");
        String[] files = new String[]{"crf.exe", "crf.pdb", "cygwin1.dll"};
        FileHandler.prepareFile("windows", files);
        files = new String[]{"model.zip", "option.txt"};
        FileHandler.prepareFile(".", files);
        Path unzipDestination = FileHandler.getPath(".").toAbsolutePath();
        if (!Files.exists(unzipDestination.resolve("model.txt")))
            UnZip.unZipIt(FileHandler.getPath("model.zip").toAbsolutePath().toString(),
                    unzipDestination.toString());
        if (File.separator.equals("/")) {
            for (File file : new File("resources/linux").listFiles()) {
                file.setExecutable(true);
            }
        }
        prepared = true;
    }

    public static void main(String[] args) {
        // create the Options
        Options options = new Options();
        options.addOption("a", "action", true, "action to do. convert is the only option.");
        options.addOption("l", "locale", true, "locale of action. fa is the only option.");
        options.addOption("i", "input", true, "input, standard CONLL dependency parsing corpus file.");
        options.addOption("o", "output", true, "output, standard CONLL NER/POS corpus file.");
        options.addOption("m", "model", true, "fast-dp FlexCRF model file, for prediction.");

        CommandLineParser parser = new BasicParser();
        Action action = null;
        String locale = null;
        Path inputPath = null, outputPath = null, modelPath = null;
        try {
            CommandLine line = parser.parse(options, args);
            if (!line.hasOption("a") || !line.hasOption("i"))
                showHelp(options);
            action = Action.valueOf(line.getOptionValue("a"));
            if (action == null)
                showHelp(options);
            if (action == Action.convert && (!line.hasOption("l") || !line.hasOption("o")))
                showHelp(options);
            else if (action == Action.prediction && (!line.hasOption("l") || !line.hasOption("o")))
                showHelp(options);

            locale = line.getOptionValue("l");
            if (!locale.equals("fa")) showHelp(options);
            inputPath = Paths.get(line.getOptionValue("i"));
            if (!Files.exists(inputPath)) {
                logger.info("file does not exists: " + inputPath.toFile().getAbsolutePath());
                System.exit(1);
            } else logger.info("input file: " + inputPath.toFile().getAbsolutePath());
            if (line.hasOption("m")) {
                modelPath = Paths.get(line.getOptionValue("m"));
                if (!Files.exists(inputPath)) {
                    logger.info("model file does not exists: " + modelPath.toFile().getAbsolutePath());
                    System.exit(1);
                }
            }
            if (line.hasOption("o")) {
                outputPath = Paths.get(line.getOptionValue("o"));
                logger.info("output path is: " + outputPath.toFile().getAbsolutePath());
            }
        } catch (ParseException exp) {
            logger.trace(exp);
            showHelp(options);
        }

        assert action != null;
        try {
            switch (action) {
                default:
                case convert:
                    logger.trace("convert, locale = " + locale);
                    if (locale.equals("fa")) {
                        convert(inputPath, outputPath);
                    }
                    break;
                //HELP:
                //http://nlp.stanford.edu/nlp/javadoc/javanlp/edu/stanford/nlp/ie/crf/CRFClassifier.html
//                case train:
//                    CRFClassifier.main(new String[]{"-trainFile", inputPath.toFile().getAbsolutePath(),
//                            "-testFile", testPath.toFile().getAbsolutePath(),
//                            "-macro", ">", modelPath.toFile().getAbsolutePath()});
                case prediction:
                    prediction("سلام خوبی؟", outputPath);
                    break;
            }
        } catch (Exception e) {
            logger.error(e);
        }
    }

    public static void prediction(String text, Path outputPath) throws IOException, InterruptedException {
        prepareFiles();
        Path posFile = makePosFile(text, outputPath);
        Path transliteratedFile = outputPath.toAbsolutePath().resolve(outputPath.getFileName() + ".tran");
        Transliterator.transliterate(posFile, transliteratedFile);
        System.out.println("transliterate to: " + transliteratedFile.toAbsolutePath());
        Path crfUntaggedPath = outputPath.toAbsolutePath().resolve("data.untagged");
        Files.deleteIfExists(crfUntaggedPath);
        Files.createFile(crfUntaggedPath);
        FlexCrfFeatureGenerator.main(array("-ulb", transliteratedFile.toAbsolutePath().toString(),
                crfUntaggedPath.toString(), "no"));
//        Files.deleteIfExists(transliteratedFile);
        Path MODEL_FOLDER = Paths.get(".").toAbsolutePath().getParent();
        Files.copy(crfUntaggedPath, MODEL_FOLDER.resolve("data.untagged"), StandardCopyOption.REPLACE_EXISTING);
        NativeCommandRunner.runCommand("crf", "-prd", "-d", MODEL_FOLDER.toFile().getAbsolutePath(), "-o",
                "option.txt");
        Files.move(MODEL_FOLDER.resolve("data.untagged.model"), outputPath.resolve("data.untagged.model"),
                StandardCopyOption.REPLACE_EXISTING);
    }

    public static Path makePosFile(String text, Path outputPath) throws IOException {
        prepareFiles();
        text = Normalizer.i().Run(text);
        List<String> sentences = SentenceTokenizer.i().Tokenize(text);
        List<ColumnedLine> columnedLines = new ArrayList<>();
        for (String sentence : sentences) {
            List<TaggedWord> taggedWords = POSTagger.i().batchTag(WordTokenizer.i().Tokenize(sentence));
            for (TaggedWord taggedWord : taggedWords)
                columnedLines.add(new ColumnedLine(taggedWord.word() + "\t" + taggedWord.tag()));
            columnedLines.add(new ColumnedLine(""));
        }
        Path posFile = outputPath.toAbsolutePath().resolve(outputPath.getFileName() + ".pos");
        List<String> lines = new ArrayList<>();
        for (ColumnedLine columnedLine : columnedLines) lines.add(columnedLine.toString());
        lines.add("");
        Files.deleteIfExists(posFile);
        Files.write(posFile, lines, Charset.forName("UTF-8"));
        return posFile;
    }

    public static void convert(Path inputPath, Path outputPath) throws IOException {
        new PersianDadeganConverter(inputPath, outputPath).run();
        logger.trace("converting pos tags ...");
        Path posFile = outputPath.toAbsolutePath().getParent().resolve(outputPath.getFileName() + ".pos");
        POSTagChanger.changePOSTags(outputPath, posFile, null);
        Path translatedFile = outputPath.toAbsolutePath().getParent().resolve(outputPath.getFileName() + ".trans");
        Transliterator.transliterate(outputPath, translatedFile);
        Path crfUntaggedPath = outputPath.toAbsolutePath().getParent().resolve(outputPath.getFileName() + ".tagged");
        FlexCrfFeatureGenerator.main(array("-lbl", translatedFile.toAbsolutePath().toString(),
                crfUntaggedPath.toAbsolutePath().toString(), "no"));
        Files.deleteIfExists(translatedFile);
    }

    private static String[] array(String... input) {
        return input;
    }

    private static void showHelp(Options options) {
        HelpFormatter formatter = new HelpFormatter();
        final StringBuilder helpBuilder = new StringBuilder().append('\n');
        helpBuilder.append("Welcome to Fast Dependency Parser.").append('\n');
        helpBuilder.append("Required options for convert: a,l,i,o").append('\n');
        helpBuilder.append("Required options for prepareForFlex: a,l,i,o").append('\n');
        helpBuilder.append("Required options for prediction: a,l,i,o,m").append('\n');
        formatter.printHelp(new StringBuilderWriter(helpBuilder), 80, "java -jar fast-dp.jar", null,
                options, 0, 0, "Thank you", false);
        logger.info(helpBuilder);
        System.exit(0);
    }
}
