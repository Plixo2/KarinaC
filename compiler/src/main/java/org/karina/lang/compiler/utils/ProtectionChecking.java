package org.karina.lang.compiler.utils;

import com.google.common.collect.ImmutableList;
import lombok.RequiredArgsConstructor;
import org.checkerframework.checker.units.qual.C;
import org.jetbrains.annotations.Nullable;
import org.karina.lang.compiler.model_api.ClassModel;
import org.karina.lang.compiler.model_api.FieldModel;
import org.karina.lang.compiler.model_api.MethodModel;
import org.karina.lang.compiler.model_api.Model;
import org.karina.lang.compiler.model_api.pointer.ClassPointer;
import org.karina.lang.compiler.model_api.pointer.FieldPointer;
import org.karina.lang.compiler.model_api.pointer.MethodPointer;

import java.lang.reflect.Modifier;

/**
 * Checks for accessibility of a reference.
 * Checks for all given modifiers, like protected, private, etc.
 * Also checks for so called "nest members".
 */
@RequiredArgsConstructor
public class ProtectionChecking {
    private final Model model;


    /// 5.4.4
    /// Tests if class referenceSite can access class referee
    /// From JMA
    public boolean isClassAccessible(ClassModel referenceSite, ClassPointer referee) {
        return isClassAccessible(referenceSite, this.model.getClass(referee));
    }
    public boolean isClassAccessible(ClassModel referenceSite, ClassModel referee) {
        var cF = referee.modifiers();

        if (Modifier.isPublic(cF)) {
            return true;
        }

        return this.isInSamePackage(referenceSite, referee);
    }


    public boolean isFieldAccessible(ClassModel D, FieldPointer R) {
        return isFieldAccessible(D, this.model.getField(R));
    }
    public boolean isFieldAccessible(ClassModel D, FieldModel R) {
        var rF = R.modifiers();
        var rClass = this.model.getClass(R.classPointer());
        return isMethodOrFieldAccessible(D, rClass, rF);
    }



    public boolean isMethodAccessible(ClassModel D, MethodPointer R) {
        return isMethodAccessible(D, this.model.getMethod(R));
    }
    public boolean isMethodAccessible(ClassModel D, MethodModel R) {
        var rF = R.modifiers();
        var rClass = this.model.getClass(R.classPointer());
        return isMethodOrFieldAccessible(D, rClass, rF);
    }


    private boolean isMethodOrFieldAccessible(ClassModel D, ClassModel rClass, int rF) {
        if (!isClassAccessible(D, rClass)) {
            return false;
        }

        if (Modifier.isPublic(rF)) {
            return true;
        }

        if (Modifier.isProtected(rF)) {
            if (isSubClass(D, rClass)) {
                return true;
            }
        }

        var defaultAccess = !Modifier.isPublic(rF) && !Modifier.isProtected(rF) && !Modifier.isPrivate(rF);
        if (Modifier.isProtected(rF) || defaultAccess) {
            if (this.isInSamePackage(D, rClass)) {
                return true;
            }
        }

        if (Modifier.isPrivate(rF)) {
            return isNestmate(D, rClass);
        }

        return false;
    }


//    public boolean canReferenceClass(ClassPointer referenceSite, ClassPointer definitionSite) {
//        var referenceClass = this.model.getClass(referenceSite);
//        var definitionClass = this.model.getClass(definitionSite);
//        var definitionModifiers = definitionClass.modifiers();
//
//        return Modifier.isPublic(definitionModifiers) || isInSameClass(referenceClass, definitionClass) || isInSamePackage(referenceClass, definitionClass);
//
//
//    }
//
//    public boolean canReference(ClassPointer referenceSite, ClassPointer definitionSite, int modifier) {
//        if (Modifier.isPublic(modifier)) {
//            return true;
//        }
//
//        var referenceClass = this.model.getClass(referenceSite);
//        var definitionClass = this.model.getClass(definitionSite);
//        if (this.isInSameClass(referenceClass, definitionClass) || this.isANestMember(referenceClass, definitionClass)) {
//            //can access everything
//            return true;
//        }
//        if (isInSamePackage(referenceClass, definitionClass)) {
//            //in same package for protected and default
//            return !Modifier.isPrivate(modifier);
//        }
//        if (isSubclass(referenceClass, definitionClass)) {
//            return Modifier.isProtected(modifier);
//        }
//        return false;
//    }

    private boolean isNestmate(ClassModel D, ClassModel C) {
        if (C.pointer().equals(D.pointer())) {
            return true;
        }

        var H = nestHost(D);
        var HTick = nestHost(C);

        return H.equals(HTick);
    }


    private ClassPointer nestHost(ClassModel M) {
        var nestHost = M.nestHost();
        if (nestHost == null) {
            return M.pointer();
        }

        var H = this.model.getClass(nestHost);
        if (!isInSamePackage(M, H)) {
            return M.pointer();
        }
        if (!H.nestMembers().contains(M.pointer())) {
            return M.pointer();
        }
        return H.pointer();

    }


    /// tests if D is either a subclass of C or C itself
    private boolean isSubClass(ClassModel D, ClassModel C) {
        if (D.pointer().equals(C.pointer())) {
            return true;
        }

        var superClass = classSuperClassPointer(D);
        if (superClass == null) {
            return false;
        }

        var modelSuperClass = this.model.getClass(superClass);
        return isSubClass(modelSuperClass, C);
    }

    public @Nullable ClassPointer classSuperClassPointer(ClassModel classModel) {
        var superClass = classModel.superClass();
        if (superClass == null) {
            return null;
        } else {
            return superClass.pointer();
        }
    }

    private boolean isInSamePackage(ClassModel referenceSite, ClassModel definitionSite) {
        var outerMostClassOfDefinition = definitionSite;

        while (true) {
            var outer = outerMostClassOfDefinition.outerClass();
            if (outer == null) {
                break;
            }
            outerMostClassOfDefinition = outer;
        }

        var outerMostClassOfReference = referenceSite;
        while (true) {
            var outer = outerMostClassOfReference.outerClass();
            if (outer == null) {
                break;
            }
            outerMostClassOfReference = outer;
        }

        var packagePathOfDef = outerMostClassOfDefinition.path().everythingButLast();
        var packagePathOfRef = outerMostClassOfReference.path().everythingButLast();
        return packagePathOfDef.equals(packagePathOfRef);

    }
}
