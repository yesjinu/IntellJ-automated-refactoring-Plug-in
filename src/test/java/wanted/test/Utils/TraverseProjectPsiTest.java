package wanted.test.Utils;

import com.intellij.ide.DataManager;
import com.intellij.lang.Language;
import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.fileTypes.PlainTextFileType;
import com.intellij.openapi.fileTypes.PlainTextLanguage;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectUtil;
import com.intellij.psi.impl.file.PsiDirectoryImpl;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.concurrency.Promise;
import wanted.test.base.AbstractLightCodeInsightTestCase;
import com.intellij.openapi.project.ProjectManager;
import com.intellij.psi.*;
import com.intellij.psi.impl.file.PsiDirectoryFactory;
import wanted.utils.TraverseProjectPsi;

import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * Test class for 'FindPsi' util
 *
 * @author Jinu Noh
 */
public class TraverseProjectPsiTest extends AbstractLightCodeInsightTestCase {

    /* test TraversePsi::getRootPackages */
    public void testGetRootPackages() throws TimeoutException, ExecutionException {
        AnActionEvent e = createAnActionEvent("file1.java");
        Project project = e.getProject();
        String expected = "[PsiPackage:testData]";
        assertEquals(TraverseProjectPsi.getRootPackages(project).size(), 1);
        assertEquals(TraverseProjectPsi.getRootPackages(project).toString(), expected);
    }

    /* test TraversePsi::findFile */
    public void testFindFile() throws TimeoutException, ExecutionException {
        AnActionEvent e = createAnActionEvent("file2.java");
        Project project = e.getProject();
        String expected = "[PsiJavaFile:file2.java]";
        assertEquals(TraverseProjectPsi.findFile(project).size(), 1);
        assertEquals(TraverseProjectPsi.findFile(project).toString(), expected);
    }

    public void testGetRootClasses() throws TimeoutException, ExecutionException {
        String file1 = "TravPsiGetRootClsFile1.java";
        String file2 = "TravPsiGetRootClsFile2.java";
        String file3 = "TravPsiGetRootClsFile3.java";
        String file4 = "TravPsiGetRootClsDir/TravPsiGetRootClsFile4.java";
        String file5 = "TravPsiGetRootClsDir/TravPsiGetRootClsFile5.java";

        String[] fileNames = {file1, file2, file3, file4, file5};
        AnActionEvent e = createAnActionEventWithSeveralFiles(fileNames);
        Project project = getProject();

        Set<PsiClass> actual = TraverseProjectPsi.getRootClasses(project);
        List<String> expected = Arrays.asList("ClassInFile1", "ClassInFile2", "Class1InFile3", "Class2InFile3");

        assertEquals(actual.size(), 4);
        for (PsiClass c : actual) {
            assertTrue(expected.contains(c.getName()));
        }
    }


    /**
     * Method to create dummy AnActionEvent with given file
     *
     * @param fileName file to test
     * @return AnActionEvent with given file context
     */
    public AnActionEvent createAnActionEvent(String fileName) throws TimeoutException, ExecutionException {
        if(fileName!=null) {
            myFixture.configureByFile("testData/traversePsi/" + fileName);
        }

        AnAction anAction = new AnAction() {
            @Override
            public void actionPerformed(@NotNull AnActionEvent e) {
                // do nothing
            }
        };

        Promise<DataContext> contextResult = DataManager.getInstance().getDataContextFromFocusAsync();
        AnActionEvent anActionEvent = new AnActionEvent(null, contextResult.blockingGet(10, TimeUnit.SECONDS),
                "", anAction.getTemplatePresentation(), ActionManager.getInstance(), 0);

        return anActionEvent;
    }

    public AnActionEvent createAnActionEventWithSeveralFiles(String[] fileNames) throws TimeoutException, ExecutionException {
        myFixture.configureByFiles(fileNames);

        AnAction anAction = new AnAction() {
            @Override
            public void actionPerformed(@NotNull AnActionEvent e) {
                // do nothing
            }
        };

        Promise<DataContext> contextResult = DataManager.getInstance().getDataContextFromFocusAsync();
        AnActionEvent anActionEvent = new AnActionEvent(null, contextResult.blockingGet(10, TimeUnit.SECONDS),
                "", anAction.getTemplatePresentation(), ActionManager.getInstance(), 0);

        return anActionEvent;
    }
}