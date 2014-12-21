package ir.ac.iust.oie.fastdp;

import edu.stanford.nlp.ie.crf.CRFClassifier;
import ir.ac.iust.oie.fastdp.converter.PersianDadeganConverter;
import ir.ac.iust.oie.fastdp.utils.LoggerUtil;
import ir.ac.iust.oie.fastdp.utils.StringBuilderWriter;
import org.apache.commons.cli.*;
import org.apache.log4j.Logger;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Created by majid on 12/20/14.
 */
public class Runner {

    private static Logger logger = LoggerUtil.getLogger(Runner.class);

    public static void main(String[] args) {
        // create the Options
        Options options = new Options();
        options.addOption("a", "action", true, "action to do. convert is the only option.");
        options.addOption("l", "locale", true, "locale of action. fa is the only option.");
        options.addOption("i", "input", true, "input, standard CONLL dependency parsing corpus file.");
        options.addOption("o", "output", true, "output, standard CONLL NER/POS corpus file.");
        options.addOption("m", "model", true, "model file, for train, test and prediction of CRF");

        CommandLineParser parser = new BasicParser();
        Action action = null;
        String locale = null;
        Path inputPath = null, outputPath = null, modelPath = null;
        try {
            CommandLine line = parser.parse(options, args);
            if (!line.hasOption("a") || !line.hasOption("i"))
                showHelp(options);
            action = Action.valueOf(line.getOptionValue("a"));
            if (action == null) showHelp(options);
            if (action == Action.convert && (!line.hasOption("l") || !line.hasOption("o"))) showHelp(options);
            else if (action != Action.convert && !line.hasOption("m")) showHelp(options);
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
                case convert:
                    logger.trace("convert, locale = " + locale);
                    if (locale.equals("fa"))
                        new PersianDadeganConverter(inputPath, outputPath).run();
                    break;
                case train:
                    CRFClassifier classifier = CRFClassifier.getDefaultClassifier();
                    classifier.train();
                    break;
                case test:
                    break;
                case predict:
                    break;
            }
        } catch (Exception e) {
            logger.error(e);
        }
    }

    private static void showHelp(Options options) {
        HelpFormatter formatter = new HelpFormatter();
        final StringBuilder helpBuilder = new StringBuilder().append('\n');
        helpBuilder.append("Welcome to Fast Dependency Parser.").append('\n');
        helpBuilder.append("Required options for convert: a,l,i,o").append('\n');
        helpBuilder.append("Required options for train: a,i,m").append('\n');
        helpBuilder.append("Required options for test: a,i,m").append('\n');
        helpBuilder.append("Required options for predict: a,i,m").append('\n');
        formatter.printHelp(new StringBuilderWriter(helpBuilder), 80, "java -jar fast-dp.jar", null,
                options, 0, 0, "Thank you", false);
        logger.info(helpBuilder);
        System.exit(0);
    }
}
