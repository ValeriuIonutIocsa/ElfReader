package com.utils.io;

import org.junit.Test;

import java.nio.file.Path;
import java.nio.file.Paths;

public class IoUtilsTest {

    @Test
    public void testClearReadOnlyFlags() {

        final Path path = Paths.get("C:\\Users\\uid39522\\Desktop\\LetFrameOrganizer");
        IoUtils.clearReadOnlyFlags(path);
    }

    @Test
    public void testCleanDirectory_NotDirectory() {

        final Path path = Paths.get("C:\\Users\\Public\\Desktop\\SEEP RCP 6.3.1.lnk");
        IoUtils.cleanDirectory(path);
    }

    @Test
    public void testDeleteDirectory_NotDirectory() {

        final Path path = Paths.get("C:\\Users\\Public\\Desktop\\SEEP RCP 6.3.1.lnk");
        IoUtils.deleteDirectory(path);
    }
}