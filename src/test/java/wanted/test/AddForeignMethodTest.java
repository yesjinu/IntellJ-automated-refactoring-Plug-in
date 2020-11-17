package wanted.test;

import com.intellij.psi.PsiElement;
import com.intellij.testFramework.fixtures.LightJavaCodeInsightFixtureTestCase;
import org.intellij.sdk.language.psi.SimpleProperty;

public class AddForeignMethodTest extends LightJavaCodeInsightFixtureTestCase {

    /**
     * @return path to wanted.test data file directory relative to working directory in the run configuration for this wanted.test.
     */
    @Override
    protected String getTestDataPath() {
        return "src/test/testData";
    }

    public void testAnnotator() {
        myFixture.configureByFile("AnnotatorTestData.java");
        PsiElement element = myFixture.getElementAtCaret();
        System.out.println(element.toString());
    }

    public void testReference() {
        myFixture.configureByFiles("ReferenceTestData.java", "DefaultTestData.simple");
        PsiElement element = myFixture.getFile().findElementAt(myFixture.getCaretOffset()).getParent();
        assertEquals("https://en.wikipedia.org/", ((SimpleProperty) element.getReferences()[0].resolve()).getValue());
    }
}
