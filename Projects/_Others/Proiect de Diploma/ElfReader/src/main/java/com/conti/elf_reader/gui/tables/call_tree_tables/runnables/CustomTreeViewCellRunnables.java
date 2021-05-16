package com.conti.elf_reader.gui.tables.call_tree_tables.runnables;

import com.conti.elf_reader.data_analyzers.call_tree_analyzer.data.DataElementCallTree;
import com.conti.elf_reader.data_analyzers.call_tree_analyzer.data.DataElementCallTreeRunnables;
import com.conti.elf_reader.gui.tables.call_tree_tables.CustomTreeViewCell;
import com.conti.elf_reader.gui.tables.call_tree_tables.PaneTabCallTree;
import com.conti.elf_reader.gui.utils.GuiUtils;
import com.utils.log.Logger;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TreeItem;
import javafx.scene.image.Image;

import java.util.List;

class CustomTreeViewCellRunnables extends CustomTreeViewCell {

    private final List<TreeItem<DataElementCallTree>> treeViewItemList;

    CustomTreeViewCellRunnables(
            PaneTabCallTree paneTabCallTree, List<TreeItem<DataElementCallTree>> treeViewItemList) {

        super(paneTabCallTree);

        this.treeViewItemList = treeViewItemList;
    }

    @Override
    protected ContextMenu createContextMenu(int depthInTreeView) {

        ContextMenu contextMenu = super.createContextMenu(depthInTreeView);

        DataElementCallTreeRunnables treeViewRootDataElement = (DataElementCallTreeRunnables)
                getTreeView().getRoot().getValue();
        if (!treeViewRootDataElement.getAdditionalInfo().isTask())
            return contextMenu;

        MenuItem menuItemSelectNodes = new MenuItem("select function(s) for measurement");
        menuItemSelectNodes.setOnAction(e -> selectNodes());
        contextMenu.getItems().add(menuItemSelectNodes);

        MenuItem menuItemDeselectNodes = new MenuItem("deselect function(s) for measurement");
        menuItemDeselectNodes.setOnAction(e -> deselectNodes());
        contextMenu.getItems().add(menuItemDeselectNodes);

        MenuItem menuItemSelectAllNodesOnLevel = new MenuItem("select all nodes on this level");
        menuItemSelectAllNodesOnLevel.setOnAction(e -> selectAllNodesOnLevel());
        contextMenu.getItems().add(menuItemSelectAllNodesOnLevel);

        return contextMenu;
    }

    private void selectNodes() {

        List<TreeItem<DataElementCallTree>> selectedItems = getTreeView().getSelectionModel().getSelectedItems();
        TreeItem<DataElementCallTree> firstSelectedItem = selectedItems.get(0);
        int selectedDepth = getTreeView().getTreeItemLevel(firstSelectedItem);
        int selectedItemsSize = selectedItems.size();
        for (int i = 1; i < selectedItemsSize; i++) {

            if (getTreeView().getTreeItemLevel(selectedItems.get(i)) != selectedDepth) {
                Logger.printWarning("To be selected for measurement, all the selected functions that belong to a task"
                        + System.lineSeparator() + "must have the same depth in tree");
                return;
            }
        }

        for (TreeItem<DataElementCallTree> treeItem : treeViewItemList) {
            if (getTreeView().getTreeItemLevel(treeItem) != selectedDepth) {
                setItemSelected(treeItem, false);
            }
        }

        for (TreeItem<DataElementCallTree> selectedItem : selectedItems) {
            setItemSelected(selectedItem, true);
        }

        getTreeView().refresh();
    }

    private void deselectNodes() {

        List<TreeItem<DataElementCallTree>> selectedItems = getTreeView().getSelectionModel().getSelectedItems();
        for (TreeItem<DataElementCallTree> selectedItem : selectedItems) {
            setItemSelected(selectedItem, false);
        }

        getTreeView().refresh();
    }

    private void selectAllNodesOnLevel() {

        int selectedDepth = getTreeView().getTreeItemLevel(getTreeItem());
        for (TreeItem<DataElementCallTree> treeItem : treeViewItemList) {
            setItemSelected(treeItem, getTreeView().getTreeItemLevel(treeItem) == selectedDepth);
        }

        getTreeView().refresh();
    }

    private void setItemSelected(TreeItem<DataElementCallTree> treeItem, boolean b) {
        ((CustomTreeItemRunnables) treeItem).setSelected(b);
    }

    @Override
    protected Image getTreeItemIcon(boolean isIndirectCall) {

        DataElementCallTree item = getItem();
        if (item == null)
            return null;

        DataElementCallTreeRunnables dataElementCallTreeRunnables = (DataElementCallTreeRunnables) item;

        if (dataElementCallTreeRunnables.getAdditionalInfo().isTask()) {
            return GuiUtils.imageTask;
        }

        if (((CustomTreeItemRunnables) getTreeItem()).isSelected()) {
            return GuiUtils.imageSelected;
        }

        if (dataElementCallTreeRunnables.getAdditionalInfo().isRecursive()) {
            return GuiUtils.imageRecursiveCall;
        }

        return super.getTreeItemIcon(isIndirectCall);
    }
}
