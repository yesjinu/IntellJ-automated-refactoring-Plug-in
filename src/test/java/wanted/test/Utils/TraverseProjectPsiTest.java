package wanted.test.Utils;

import com.intellij.ide.DataManager;
import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiFile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.concurrency.Promise;
import wanted.test.base.AbstractLightCodeInsightTestCase;
import wanted.utils.TraverseProjectPsi;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
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

    /* test TraversePsi::findFile - Contains only 1 file */
    public void testFindFile() throws TimeoutException, ExecutionException {
        AnActionEvent e = createAnActionEvent("file2.java");
        Project project = e.getProject();
        String expected = "[PsiJavaFile:file2.java]";
        assertEquals(TraverseProjectPsi.findFile(project).size(), 1);
        assertEquals(TraverseProjectPsi.findFile(project).toString(), expected);
    }

    /* test TraversePsi::findFile - Contains several files */
    public void testFindFile2() throws TimeoutException, ExecutionException {
        String file1 = "TravProjPsiFile1.java";
        String file2 = "TravProjPsiFile2.java";
        String file3 = "TravProjPsiFile3.java";
        String file4 = "TravProjPsiDir/TravProjPsiFile4.java";
        String file5 = "TravProjPsiDir/TravProjPsiFile5.java";

        String[] fileNames = {file1, file2, file3, file4, file5};
        AnActionEvent e = createAnActionEventWithSeveralFiles(fileNames);
        Project project = e.getProject();

        List<PsiFile> actual = TraverseProjectPsi.findFile(project);
        List<String> expected = Arrays.asList("TravProjPsiFile4.java", "TravProjPsiFile5.java", "TravProjPsiFile1.java", "TravProjPsiFile2.java", "TravProjPsiFile3.java");
        assertEquals(actual.size(), 5);
        for (PsiFile f : actual) {
            assertTrue(expected.contains(f.getName()));
        }
    }

    /* test TraversePsi::getRootClasses - root classes and non-root classes mixed case */
    public void testGetRootClasses() throws TimeoutException, ExecutionException {
        String file1 = "TravProjPsiFile1.java";
        String file2 = "TravProjPsiFile2.java";
        String file3 = "TravProjPsiFile3.java";
        String file4 = "TravProjPsiDir/TravProjPsiFile4.java";
        String file5 = "TravProjPsiDir/TravProjPsiFile5.java";

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

    /**
     * Method to create dummy AnActionEvent with given file
     *
     * @param fileNames files to test
     * @return AnActionEvent with given file context
     */
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