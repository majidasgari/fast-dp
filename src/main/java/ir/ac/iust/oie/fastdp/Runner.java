package ir.ac.iust.oie.fastdp;

import ir.ac.iust.oie.fastdp.converter.PersianDadeganConverter;
import ir.ac.iust.oie.fastdp.flexcrf.FlexCrfFeatureGenerator;
import ir.ac.iust.text.utils.LoggerUtils;
import ir.ac.iust.text.utils.StringBuilderWriter;
import org.apache.commons.cli.*;
import org.apache.log4j.Logger;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Created by majid on 12/20/14.
 */
public class Runner {

    private static Logger logger = LoggerUtils.getLogger(Runner.class, "fast-dp.log");

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
                        new PersianDadeganConverter(inputPath, outputPath).run();
                        logger.trace("converting pos tags ...");
                        Path posFile = outputPath.toAbsolutePath().getParent().resolve(outputPath.getFileName() + ".pos");
                        POSTagChanger.changePOSTags(outputPath, posFile, 40000);
                        Path translatedFile = outputPath.toAbsolutePath().getParent().resolve(outputPath.getFileName() + ".trans");
                        Transliterator.transliterate(posFile, translatedFile);
                        Path crfUntaggedPath = outputPath.toAbsolutePath().getParent().resolve(outputPath.getFileName() + ".tagged");
                        FlexCrfFeatureGenerator.main(array("-lbl", translatedFile.toAbsolutePath().toString(),
                                crfUntaggedPath.toAbsolutePath().toString(), "no"));
                        Files.deleteIfExists(translatedFile);
                    }
                    break;
                //HELP:
                //http://nlp.stanford.edu/nlp/javadoc/javanlp/edu/stanford/nlp/ie/crf/CRFClassifier.html
//                case train:
//                    CRFClassifier.main(new String[]{"-trainFile", inputPath.toFile().getAbsolutePath(),
//                            "-testFile", testPath.toFile().getAbsolutePath(),
//                            "-macro", ">", modelPath.toFile().getAbsolutePath()});
                case prediction:
                    break;
            }
        } catch (Exception e) {
            logger.error(e);
        }
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
