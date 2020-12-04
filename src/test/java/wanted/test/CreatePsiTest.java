package wanted.test;

import com.intellij.mock.Mock;
import com.intellij.mock.MockProject;
import com.intellij.mock.MockProjectEx;
import com.intellij.openapi.Disposable;
import com.intellij.openapi.command.impl.DummyProject;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.rd.DisposableEx;
import com.intellij.psi.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;
import wanted.utils.CreatePsi;

import static com.intellij.ide.lightEdit.LightEditUtil.getProject;

public class CreatePsiTest {


    @Test
    public void testCreateSetMethod() {
        Project project = getProject();
        PsiElementFactory factory = PsiElementFactory.getInstance(project);
/*
        PsiField member = factory.createField("a", PsiType.INT);
        String accessModifier = "public";
        PsiMethod createElement = CreatePsi.createSetMethod(project, member, accessModifier);

        String expected = "public void setA(int newValue){" +
                " a = newValue; ";
        System.out.println(createElement.toString());
        System.out.println(createElement.getText());
        Assertions.assertEquals(createElement.toString(), expected);
        Assertions.assertTrue(true); */
    }

}
