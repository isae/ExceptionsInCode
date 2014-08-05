package com.jetbrains.isaev.notifications;

import com.intellij.lang.documentation.AbstractDocumentationProvider;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.Nullable;

/**
 * Created by Ilya.Isaev on 05.08.2014.
 */
public class IssueDescriptionDocumentationProvider extends AbstractDocumentationProvider {
    @Override
    public String generateDoc(PsiElement element, @Nullable PsiElement originalElement) {
        return super.generateDoc(element, originalElement);
        // final JavaDocInfoGenerator javaDocInfoGenerator = new JavaDocInfoGenerator(element.getProject(), element);
        // return JavaDocExternalFilter.filterInternalDocInfo(javaDocInfoGenerator.generateFileInfo());
    }
}
