package com.conti.elf_reader.gui.tables;

import com.conti.elf_reader.data_analyzers.DataElementTableViewRow;
import com.conti.elf_reader.data_analyzers.size_analyzer.data.DataElementSize;
import com.conti.elf_reader.data_info.tables.DataInfoSize;
import com.conti.elf_reader.gui.PaneTabTable;
import com.conti.elf_reader.settings.SettingNames;
import com.conti.elf_reader.settings.Settings;
import com.utils.xml.stax.XmlReader;
import org.tbee.javafx.scene.layout.MigPane;

import javax.xml.stream.events.StartElement;
import java.nio.file.Path;
import java.nio.file.Paths;

public class PaneTabTableSize extends PaneTabTable {

    private PaneCoreArchitectureFile migPaneCoreArchitectureFile;

    public PaneTabTableSize(Settings settings) {
        super(settings, DataInfoSize.getInstance());
    }

    @Override
    protected void elfPathChanged() {

        super.elfPathChanged();

        migPaneCoreArchitectureFile.elfPathChanged();
    }

    @Override
    protected MigPane createMigPaneSettings() {

        MigPane settingsPanel = super.createMigPaneSettings();

        migPaneCoreArchitectureFile = new PaneCoreArchitectureFile(this, "n");
        settingsPanel.add(migPaneCoreArchitectureFile, "grow, width max(100%, 100%),, wrap");

        return settingsPanel;
    }

    @Override
    protected void saveSettings() {

        String coreArchitectureFilePathString = getCoreArchitectureFilePath().toAbsolutePath().toString();
        if (!coreArchitectureFilePathString.isEmpty()) {
            settings.set(SettingNames.core_architecture_file_path, coreArchitectureFilePathString);
        }

        super.saveSettings();
    }

    @Override
    protected DataElementTableViewRow createTableViewItem(XmlReader xmlReader, StartElement startElement, int rowIndex) {
        return new DataElementSize(xmlReader, startElement, rowIndex);
    }

    public Path getCoreArchitectureFilePath() {
        return Paths.get(migPaneCoreArchitectureFile.getCoreArchitectureFilePath());
    }
}