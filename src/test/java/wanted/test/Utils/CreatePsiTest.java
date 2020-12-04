package wanted.test.Utils;

import com.intellij.openapi.command.impl.DummyProject;
import com.intellij.openapi.fileTypes.StdFileTypes;
import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import com.intellij.psi.impl.PsiFileFactoryImpl;
import com.intellij.psi.impl.source.tree.JavaElementType;
import org.junit.Test;
import wanted.utils.CreatePsi;

public class CreatePsiTest {

    Project project = DummyProject.getInstance();
    PsiFileFactoryImpl factory = (PsiFileFactoryImpl)PsiFileFactory.getInstance(project);
    PsiElement element = factory.createElementFromText("string", StdFileTypes.JAVA.getLanguage(), JavaElementType.CLASS, null);


    @Test
    void testCreateSetMethod1(){

    }

}
