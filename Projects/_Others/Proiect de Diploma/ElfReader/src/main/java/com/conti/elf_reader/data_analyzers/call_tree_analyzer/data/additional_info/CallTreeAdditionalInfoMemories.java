package com.conti.elf_reader.data_analyzers.call_tree_analyzer.data.additional_info;

import com.conti.elf_reader.data_analyzers.call_tree_analyzer.data.memories.ErrorDetails;
import com.conti.elf_reader.data_analyzers.call_tree_analyzer.data.memories.ErrorType;
import com.conti.elf_reader.data_info.tables.call_tree_tables.DataInfoCallTreeMemories;
import com.conti.elf_reader.gui.utils.GuiUtils;
import com.conti.elf_reader.utils.data_types.HexString;
import com.utils.xml.stax.XmlReader;
import com.utils.xml.stax.XmlWriter;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import javax.xml.stream.events.StartElement;

public class CallTreeAdditionalInfoMemories extends CallTreeAdditionalInfo {

    private final String sourceFile;
    private final HexString address;
    private final String memoryName;
    private final String protection;

    private ErrorType errorType = null;
    private ErrorDetails errorDetails = new ErrorDetails();

    public CallTreeAdditionalInfoMemories(String sourceFile, HexString address, String memoryName, String protection) {

        super(DataInfoCallTreeMemories.getInstance());

        this.sourceFile = sourceFile;
        this.address = address;
        this.memoryName = memoryName;
        this.protection = protection;
    }

    public CallTreeAdditionalInfoMemories(XmlReader xmlReader, StartElement startElement) {

        super(DataInfoCallTreeMemories.getInstance());

        sourceFile = xmlReader.getAttribute(startElement,
                columnInfoArray[0].getColumnTitleName());
        address = new HexString(xmlReader.getAttribute(startElement,
                columnInfoArray[1].getColumnTitleName()));
        memoryName = xmlReader.getAttribute(startElement,
                columnInfoArray[2].getColumnTitleName());
        protection = xmlReader.getAttribute(startElement,
                columnInfoArray[3].getColumnTitleName());
    }

    @Override
    public void writeDataElement(XmlWriter xmlWriter) {

        xmlWriter.writeAttribute(columnInfoArray[0].getColumnTitleName(),
                sourceFile);
        xmlWriter.writeAttribute(columnInfoArray[1].getColumnTitleName(),
                address != null ? address.toString() : "");
        xmlWriter.writeAttribute(columnInfoArray[2].getColumnTitleName(),
                memoryName);
        xmlWriter.writeAttribute(columnInfoArray[3].getColumnTitleName(),
                protection);
    }

    @Override
    public Object[] getAdditionalTableRowData() {

        Image errorImage = getErrorImage();
        return new Object[]{
                sourceFile,
                address,
                memoryName,
                protection,
                errorImage != null ? new ImageView(errorImage) : null,
                errorDetails
        };
    }

    public Image getErrorImage() {

        if (errorType == null)
            return null;

        switch (errorType) {

            case Error:
                return GuiUtils.imageError;

            case Warning:
                return GuiUtils.imageWarning;

            case Info:
                return GuiUtils.imageInfo;

            default:
                return null;
        }
    }

    public String getMemoryName() {
        return memoryName;
    }

    public String getProtection() {
        return protection;
    }

    public void setErrorType(ErrorType errorType) {

        if (this.errorType == null || this.errorType.getLevel() < errorType.getLevel()) {
            this.errorType = errorType;
        }
    }

    public ErrorDetails getErrorDetails() {
        return errorDetails;
    }
}
