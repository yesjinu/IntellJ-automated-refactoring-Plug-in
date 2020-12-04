package wanted.test.Utils;

import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import org.junit.jupiter.api.Assertions;
import wanted.test.base.AbstractLightCodeInsightTestCase;
import wanted.utils.CreatePsi;

/**
 * @author seha Park
 */
public class CreatePsiTest extends AbstractLightCodeInsightTestCase {

    public void testCreateSetMethod() {
        Project project = getProject();
        PsiElementFactory factory = PsiElementFactory.getInstance(project);

        PsiField member = factory.createField("a", PsiType.INT);
        String accessModifier = "public";
        PsiMethod createElement = CreatePsi.createSetMethod(project, member, accessModifier);

        String expected = "public void setA(int newValue){" +
                " a = newValue; ";
        System.out.println(createElement.toString());
        System.out.println(createElement.getText());
        Assertions.assertEquals(createElement.toString(), expected);
        Assertions.assertTrue(true);
    }

}
