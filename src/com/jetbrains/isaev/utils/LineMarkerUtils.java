package com.jetbrains.isaev.utils;

import com.intellij.openapi.editor.Editor;
import com.intellij.psi.*;
import com.intellij.psi.util.MethodSignature;
import com.jetbrains.isaev.state.BTIssue;
import org.jetbrains.annotations.NotNull;

import java.util.*;

/**
 * Created by Ilya.Isaev on 27.08.2014.
 */
public class LineMarkerUtils {
    public static int hash(PsiJavaFile file) {
        return ("file: " + file.getPackageName() + "." + file.getName()).hashCode();
    }

    public static int hash(PsiIdentifier id) {
        return ("id: " + hash((PsiJavaFile) id.getContainingFile()) + id.getTextOffset() + id.getText()).hashCode();
    }

    public static int hash(PsiMethod m) {
        return ("m: " + hash((PsiJavaFile) m.getContainingFile()) + m.getName() + m.getSignature(PsiSubstitutor.EMPTY)).hashCode();
    }

    public static int hash(PsiMethodCallExpression el) {
        return ("m c:" + el.getTextOffset() + el.getMethodExpression().getCanonicalText()).hashCode();
    }


    public static int hash(PsiClass clazz) {
        return ("c " + clazz.getQualifiedName()).hashCode();
    }


    public static String getMethodSignatureString(PsiMethod method) {
        MethodSignature signature = method.getSignature(PsiSubstitutor.EMPTY);
        StringBuilder s = new StringBuilder();
        final PsiTypeParameter[] typeParameters = signature.getTypeParameters();
        if (typeParameters.length != 0) {
            String sep = "<";
            for (PsiTypeParameter typeParameter : typeParameters) {
                s.append(sep).append(typeParameter.getName());
                sep = ", ";
            }
            s.append(">");
        }
        s.append(signature.getName()).append("(").append(Arrays.asList(signature.getParameterTypes())).append(")");
        return s.toString();
    }

    public static <T extends PsiElement> List<T> getAllChildByClass(PsiElement element, Class<T> typeToken) {
        List<T> list = new LinkedList<T>();
        for (PsiElement elem : element.getChildren()) {
            list.addAll(getAllChildByClass(elem, typeToken));
        }
        if (typeToken.isAssignableFrom(element.getClass())) {
            boolean f = true;
            list.add((T) element);
        }
        return list;
    }

    public static <T1, T2> void checkMapToSet(HashMap<T1, HashSet<T2>> target, T1 toCheck) {
        if (!target.containsKey(toCheck)) target.put(toCheck, new HashSet<T2>());
    }

    public static <T1, T2> void checkMapToList(HashMap<T1, ArrayList<T2>> target, T1 toCheck) {
        if (!target.containsKey(toCheck)) target.put(toCheck, new ArrayList<T2>());
    }

    public static <T1, T2, T3> void checkMapToMap(HashMap<T1, HashMap<T2, T3>> target, T1 toCheck) {
        if (!target.containsKey(toCheck)) target.put(toCheck, new HashMap<T2, T3>());
    }

    public static PsiClass getMostOuterClass(@NotNull PsiClass psiClass) {
        while (psiClass.getContainingClass() != null) psiClass = psiClass.getContainingClass();
        return psiClass;
    }

    public static String getDbClassName(@NotNull PsiClass psiClass) {
        PsiClass tmp = getMostOuterClass(psiClass);
        return tmp.getQualifiedName();
    }

    public static int getRowByElement(PsiElement element, Editor editor) {
        int offset = 0;
        if (element instanceof PsiClass) {
            PsiElement lb = ((PsiClass) element).getLBrace();
            if (lb != null) {
                offset = lb.getTextOffset();
            }
        } else {
            offset = element.getTextOffset();
        }
        return editor.offsetToLogicalPosition(offset).line;
    }

}
