package com.conti.elf_reader.gui.tables.call_tree_tables.memories;

import com.conti.elf_reader.data_analyzers.call_tree_analyzer.data.DataElementCallTree;
import com.conti.elf_reader.data_analyzers.call_tree_analyzer.data.DataElementCallTreeMemories;
import com.conti.elf_reader.gui.tables.call_tree_tables.CustomTreeViewCell;
import com.conti.elf_reader.gui.tables.call_tree_tables.PaneTabCallTree;
import javafx.scene.image.Image;

public class CustomTreeViewCellMemories extends CustomTreeViewCell {

    CustomTreeViewCellMemories(PaneTabCallTree paneTabCallTree) {
        super(paneTabCallTree);
    }

    @Override
    protected Image getTreeItemIcon(boolean isIndirectCall) {

        DataElementCallTree item = getItem();
        if(item == null)
            return null;

        DataElementCallTreeMemories dataElementCallTreeMemories = (DataElementCallTreeMemories) item;

        Image errorImage = dataElementCallTreeMemories.getAdditionalInfo().getErrorImage();
        if (errorImage != null)
            return errorImage;

        return super.getTreeItemIcon(isIndirectCall);
    }
}
