package com.conti.elf_reader.gui.tables.call_tree_tables;

import com.conti.elf_reader.data_analyzers.call_tree_analyzer.data.DataElementCallTree;
import com.conti.elf_reader.data_parsers.indirect_calls.IndirectCalls;
import com.conti.elf_reader.data_parsers.indirect_calls.data.IndirectCallReplacement;
import com.conti.elf_reader.gui.utils.GuiUtils;
import com.utils.log.Logger;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TreeItem;
import javafx.scene.control.cell.TextFieldTreeCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.util.StringConverter;

import java.nio.file.Path;
import java.util.Collection;
import java.util.Map;

public class CustomTreeViewCell extends TextFieldTreeCell<DataElementCallTree> {

    private final PaneTabCallTree paneTabCallTree;

    public CustomTreeViewCell(PaneTabCallTree paneTabCallTree) {

        super(new StringConverter<DataElementCallTree>() {

            @Override
            public String toString(DataElementCallTree object) {
                return object != null ? object.toString() : "";
            }

            @Override
            public DataElementCallTree fromString(String string) {
                return paneTabCallTree.functionNameToDataElementMap.getOrDefault(string, null);
            }
        });
        this.paneTabCallTree = paneTabCallTree;
    }

    @Override
    public void commitEdit(DataElementCallTree newValue) {

        if (newValue == null) {
            Logger.printWarning("invalid function name!" +
                    System.lineSeparator() + "Please choose a function name that exists in the call tree.");
            return;
        }

        super.commitEdit(newValue);

        final String newFunctionName = newValue.getFunctionName();
        if(!DataElementCallTree.indirectCallFunctionName.equals(newFunctionName)) {
            final String pathInTree = computePathInTree();
            final int indexInCalls = computeIndexInCalls();

            Map<IndirectCallReplacement, IndirectCallReplacement> indirectCallReplacementMap =
                    paneTabCallTree.getIndirectCallReplacementMap();
            final IndirectCallReplacement indirectCallReplacement = new IndirectCallReplacement(
                    pathInTree, indexInCalls, newFunctionName);
            indirectCallReplacementMap.putIfAbsent(indirectCallReplacement, indirectCallReplacement);

            final Path indirectCallReplacementsFilePath = paneTabCallTree.getIndirectCallReplacementsFilePath();
            final Collection<IndirectCallReplacement> indirectCallReplacements = indirectCallReplacementMap.values();
            IndirectCalls.save(indirectCallReplacementsFilePath, indirectCallReplacements);
        }

        getTreeItem().getChildren().clear();
        expandToFirstLevel(newValue);
    }

    @Override
    public void cancelEdit() {

        super.cancelEdit();
        updateItem(getItem(), isEmpty());
    }

    @Override
    public void updateItem(DataElementCallTree dataElementCallTree, boolean empty) {

        super.updateItem(dataElementCallTree, empty);

        if (empty || dataElementCallTree == null) {
            setText("");
            setGraphic(null);
            setContextMenu(null);
            return;
        }

        final String pathInTree = computePathInTree();
        final int indexInCalls = computeIndexInCalls();
        IndirectCallReplacement indirectCallReplacement = paneTabCallTree.getIndirectCallReplacementMap()
                .getOrDefault(new IndirectCallReplacement(pathInTree, indexInCalls, "")
                        , null);
        final boolean isIndirectCallReplacement = indirectCallReplacement != null;

        final boolean isIndirectCallOriginal = DataElementCallTree.indirectCallFunctionName
                .equals(dataElementCallTree.getFunctionName());

        final boolean isIndirectCall = isIndirectCallReplacement || isIndirectCallOriginal;

        if (isIndirectCallReplacement && isIndirectCallOriginal) {
            String replacementValue = indirectCallReplacement.getReplacementValue();
            DataElementCallTree replacementDataElementCallTree = paneTabCallTree.getFunctionNameToDataElementMap()
                    .getOrDefault(replacementValue, null);
            if (replacementDataElementCallTree != null) {
                dataElementCallTree = replacementDataElementCallTree;
                getTreeItem().setValue(dataElementCallTree);
                expandToFirstLevel(dataElementCallTree);
            }
        }

        setText(dataElementCallTree.toString());
        setEditable(isIndirectCall);

        Image icon = getTreeItemIcon(isIndirectCall);
        setGraphic(icon != null ? new ImageView(icon) : null);

        final int depthInTreeView = getTreeView().getTreeItemLevel(getTreeItem());
        ContextMenu contextMenu = createContextMenu(depthInTreeView);
        setContextMenu(contextMenu);
    }

    private String computePathInTree() {

        StringBuilder pathInTreeStringBuilder = new StringBuilder();
        TreeItem<DataElementCallTree> parentTreeItem = getTreeItem();
        while ((parentTreeItem = parentTreeItem.getParent()) != null) {
            pathInTreeStringBuilder.insert(0, ":" + parentTreeItem.getValue().getFunctionName() + ":");
        }
        return pathInTreeStringBuilder.toString();
    }

    private int computeIndexInCalls() {

        final TreeItem<DataElementCallTree> treeItem = getTreeItem();
        TreeItem<DataElementCallTree> parent = treeItem.getParent();
        if (parent == null)
            return -1;

        return parent.getChildren().indexOf(treeItem);
    }

    protected Image getTreeItemIcon(boolean isIndirectCall) {

        DataElementCallTree dataElementCallTree = getItem();
        if (dataElementCallTree == null)
            return null;

        if (isIndirectCall)
            return GuiUtils.imageIndirectCall;

        return null;
    }

    private void expandToFirstLevel(DataElementCallTree dataElementCallTree) {

        if (dataElementCallTree.getCalls() != null) {
            paneTabCallTree.addCellsToTreeView(getTreeItem(), null, -1);
        }
    }

    protected ContextMenu createContextMenu(final int depthInTreeView) {

        ContextMenu contextMenu = new ContextMenu();

        MenuItem menuItemNavigateTo = new MenuItem("navigate to");
        menuItemNavigateTo.setOnAction(event ->
                paneTabCallTree.navigateTo(itemProperty().getValue().getFunctionName()));
        contextMenu.getItems().add(menuItemNavigateTo);

        if (depthInTreeView > 0) {
            MenuItem menuItemCollapseAllToThisLevel = new MenuItem("collapse all to this level");
            menuItemCollapseAllToThisLevel.setOnAction(event ->
                    paneTabCallTree.collapseTreeViewToLevel(depthInTreeView));
            contextMenu.getItems().add(menuItemCollapseAllToThisLevel);
        }

        if (getItem().getCalls() != null && getItem().getCalls().size() > getTreeItem().getChildren().size()) {
            MenuItem menuItemExpandOneMoreLevel = new MenuItem("expand one more level");
            menuItemExpandOneMoreLevel.setOnAction(event ->
                    paneTabCallTree.addCellsToTreeView(getTreeItem(), null, -1));
            contextMenu.getItems().add(menuItemExpandOneMoreLevel);
        }

        return contextMenu;
    }
}
