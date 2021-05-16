package com.utils.ar;

import static org.junit.Assert.assertNotNull;

import java.io.File;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.Test;

import com.utils.io.IoUtils;
import com.utils.log.Logger;

public class FactoryAArchiveTest {

	static {
		Logger.setDebugMode(true);
	}

	@Test
	public void testParse() throws Exception {

		final InputStream inputStreamTestResourceFile = Thread.currentThread()
				.getContextClassLoader().getResourceAsStream("test.a");
		final File temporaryFile = IoUtils.createTemporaryFile(inputStreamTestResourceFile);

		final AArchive aArchive = FactoryAArchive.INSTANCE.parse(temporaryFile);
		assertNotNull(aArchive);

		Logger.printLine(aArchive);
	}

	@Test
	public void testParseLibrary1() {

		testParseLibraryCommon("D:\\p\\da\\me0\\710\\dame0_0u0_710_T1_CamTrace\\tmp\\ShadowBuild\\obj\\rtaos.a");
	}

	@Test
	public void testParseLibrary2() {

		testParseLibraryCommon("D:\\p\\da\\mc3\\404\\damc3_0u0_404_rg\\tmp\\ShadowBuild\\obj\\CPC3_AGK.a");
	}

    @Test
	public void testParseLibrary3() {

	    testParseLibraryCommon("C:\\Users\\uid39522\\Desktop\\New folder\\BMW_SWC_EisyEvHc.a");
    }

	private void testParseLibraryCommon(final String libraryFilePathString) {

		final Path libraryFilePath = Paths.get(libraryFilePathString);

		final AArchive aArchive = FactoryAArchive.INSTANCE.parse(libraryFilePath);
		assertNotNull(aArchive);

		Logger.printLine(aArchive);
		Logger.printLine("entry count: %s", aArchive.getEntries().size());
	}
}
