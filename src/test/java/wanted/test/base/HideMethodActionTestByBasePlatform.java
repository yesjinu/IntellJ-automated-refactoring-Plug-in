package wanted.test.base;

import com.intellij.openapi.fileTypes.StdFileTypes;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiFileFactory;
import com.intellij.testFramework.fixtures.BasePlatformTestCase;
import wanted.refactoring.HideMethodAction;


public class HideMethodActionTestByBasePlatform extends BasePlatformTestCase {

    public void testHideMethodAction() {
        String codeBlock = "class a {\n" +
                "    public void aF() { }\n" +
                "}\n" +
                "\n" +
                "class b {\n" +
                "    void bF() { }\n" +
                "}";

        String ansBlock = "class a {\n" +
                "    private void aF() { }\n" +
                "}\n" +
                "\n" +
                "class b {\n" +
                "    private void bF() { }\n" +
                "}";

        PsiFileFactory factory = PsiFileFactory.getInstance(getProject());

        PsiFile file = factory.createFileFromText(StdFileTypes.JAVA.getLanguage(), codeBlock);

        HideMethodAction action = new HideMethodAction();

//         assertTrue(action.refactorValid(file));
//         action.runRefactoring(file);
//         assertTrue(TextUtils.codeEqual(file.getText(), ansBlock));
    }

}
