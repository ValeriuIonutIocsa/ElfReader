package com.conti.elf_reader.data_analyzers.call_tree_analyzer.data.additional_info;

import com.conti.elf_reader.data_info.tables.call_tree_tables.DataInfoCallTreeRunnables;
import com.conti.elf_reader.gui.utils.GuiUtils;
import com.conti.elf_reader.utils.data_types.DataTypes;
import com.utils.xml.stax.XmlReader;
import com.utils.xml.stax.XmlWriter;
import javafx.scene.image.ImageView;

import javax.xml.stream.events.StartElement;
import java.util.Arrays;
import java.util.List;

public class CallTreeAdditionalInfoRunnables extends CallTreeAdditionalInfo {

    private final int selfStack;
    private final int reachable;
    private final List<String> recursivePaths;

    private boolean task = false;

    public CallTreeAdditionalInfoRunnables(int selfStack, int reachable, List<String> recursivePaths) {

        super(DataInfoCallTreeRunnables.getInstance());

        this.selfStack = selfStack;
        this.reachable = reachable;
        this.recursivePaths = recursivePaths;
    }

    public CallTreeAdditionalInfoRunnables(XmlReader xmlReader, StartElement startElement) {

        super(DataInfoCallTreeRunnables.getInstance());

        selfStack = Math.max(0, DataTypes.tryParseInteger(xmlReader.getAttribute(startElement,
                columnInfoArray[0].getColumnTitleName())));
        reachable = Math.max(0, DataTypes.tryParseInteger(xmlReader.getAttribute(startElement,
                columnInfoArray[1].getColumnTitleName())));
        String recursivePathsString = xmlReader.getAttribute(startElement,
                columnInfoArray[2].getColumnTitleName());
        recursivePaths = recursivePathsString != null ?
                Arrays.asList(recursivePathsString.split(",", 0)) : null;
        task = DataTypes.tryParseBoolean(xmlReader.getAttribute(startElement,
                columnInfoArray[3].getColumnTitleName()));
    }

    @Override
    public void writeDataElement(XmlWriter xmlWriter) {

        xmlWriter.writeAttribute(columnInfoArray[0].getColumnTitleName(),
                String.valueOf(selfStack));
        xmlWriter.writeAttribute(columnInfoArray[1].getColumnTitleName(),
                String.valueOf(reachable));
        xmlWriter.writeAttribute(columnInfoArray[2].getColumnTitleName(),
                recursivePaths != null ?
                        String.join(",", recursivePaths) : "");
        xmlWriter.writeAttribute(columnInfoArray[3].getColumnTitleName(),
                String.valueOf(task));
    }

    @Override
    public Object[] getAdditionalTableRowData() {

        return new Object[]{
                selfStack,
                reachable,
                isRecursive() ? new ImageView(GuiUtils.imageRecursiveCall) : null,
                task ? new ImageView(GuiUtils.imageTask) : null
        };
    }

    public int getSelfStack() {
        return selfStack;
    }

    public int getReachable() {
        return reachable;
    }

    public boolean isRecursive() {
        return recursivePaths != null && !recursivePaths.isEmpty();
    }

    public List<String> getRecursivePaths() {
        return recursivePaths;
    }

    public void setTask() {
        this.task = true;
    }

    public boolean isTask() {
        return task;
    }
}
