package com.conti.elf_reader.cli;

import com.utils.log.Logger;
import com.utils.io.IoUtils;

import java.io.File;
import java.nio.file.Paths;

public class CommandLineArguments {

    private String settingsFilePathString;
    private boolean commandLineMode;

    public void parse(String[] args) {

        switch (args.length) {

            case 0:
                if (IoUtils.fileExists(Paths.get("ElfReaderCfg.xml"))) {
                    settingsFilePathString = "ElfReaderCfg.xml";
                }
                break;

            case 1:
                if ("-help".equals(args[0])) {
                    Logger.printStatus(printUsageMessage());
                    System.exit(1);
                }
                settingsFilePathString = args[0];
                break;

            case 2:
                if ("-no_gui".equals(args[0])) {
                    commandLineMode = true;
                    settingsFilePathString = args[1];
                    break;
                } else {
                    Logger.printStatus(printUsageMessage());
                    System.exit(1);
                }

            default:
                Logger.printStatus(printUsageMessage());
                System.exit(1);
        }
    }

    private String printUsageMessage() {

        String jarName = "\"" + new File(CommandLineArguments.class.getProtectionDomain().getCodeSource().getLocation().getPath())
                .getName().replace("%20", "") + "\"";
        return System.lineSeparator() + "Usage: java -jar " + jarName + "[-no_gui] -cfg_file_path" + System.lineSeparator();
    }

    public boolean isCommandLineMode() {
        return commandLineMode;
    }

    public String getSettingsFilePathString() {
        return settingsFilePathString;
    }
}
