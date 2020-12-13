package wanted.test.base;

import com.intellij.openapi.util.io.FileUtil;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.testFramework.PlatformTestUtil;
import com.intellij.testFramework.fixtures.LightJavaCodeInsightFixtureTestCase;
import junit.framework.ComparisonFailure;

import java.io.File;
import java.io.IOException;

/**
 * Abstract class to create light plugin test environment.
 *
 * @author Chanyoung Kim
 * @author seha Park
 */
public abstract class AbstractLightCodeInsightTestCase extends LightJavaCodeInsightFixtureTestCase {

    @Override
    protected String getTestDataPath() {
        return "src/test/";
    }

    @Override
    protected String getBasePath() {
        return "testData";
    }


    /**
     * Check the result of action by file
     *
     * @throws IOException
     */
    protected void checkResultByFile(String expectedFile) throws IOException {
        try {
            myFixture.checkResultByFile(expectedFile, true);
        } catch (ComparisonFailure ex) {
            String actualFileText = myFixture.getFile().getText();
            actualFileText = actualFileText.replace("java.lang.", "");

            final String path = getTestDataPath() + "/" + expectedFile;
            String expectedFileText = StringUtil.convertLineSeparators(FileUtil.loadFile(new File(path)));

            if (!expectedFileText.replaceAll("\\s+", "").equals(actualFileText.replaceAll("\\s+", ""))) {
                assertEquals(expectedFileText, actualFileText);
            }
        }
    }

    /**
     * Compare files under afterPath and beforePath
     * @param afterPath Path of expected output files
     * @param beforePath Path of input files
     * @throws IOException
     * caution: this test checks for whitespace
     */
    protected void checkResultByFiles(final String afterPath, final String beforePath) throws IOException {
        VirtualFile expectedDir = LocalFileSystem.getInstance().findFileByPath(getTestDataPath()+afterPath);
        VirtualFile actualDir = myFixture.findFileInTempDir(beforePath);

        try{
            PlatformTestUtil.assertDirectoriesEqual(expectedDir, actualDir);
        } catch(NullPointerException e)
        {
            // nothing
        }

    }
}