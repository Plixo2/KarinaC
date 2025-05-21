package org.karina.lang.compiler.utils;

import lombok.RequiredArgsConstructor;
import org.karina.lang.compiler.model_api.ClassModel;
import org.karina.lang.compiler.model_api.Model;
import org.karina.lang.compiler.model_api.pointer.ClassPointer;

import java.lang.reflect.Modifier;

/**
 * Checks for accessibility of a reference.
 * Checks for all given modifiers, like protected, private, etc.
 * Also checks for so called "nest members".
 */
@RequiredArgsConstructor
public class ProtectionChecking {
    private final Model model;

    public boolean canReference(ClassPointer referenceSite, ClassPointer definitionSite, int modifier) {
        if (Modifier.isPublic(modifier)) {
            return true;
        }

        var referenceClass = this.model.getClass(referenceSite);
        var definitionClass = this.model.getClass(definitionSite);
        if (this.isInSameClass(referenceClass, definitionClass) || this.isANestMember(referenceClass, definitionClass)) {
            //can access everything
            return true;
        }
        if (isInSamePackage(referenceClass, definitionClass)) {
            //in same package for protected and default
            return !Modifier.isPrivate(modifier);
        }
        if (isSubclass(referenceClass, definitionClass)) {
            return Modifier.isProtected(modifier);
        }
        return false;
    }

    private boolean isInSameClass(ClassModel referenceSite, ClassModel definitionSite) {
        return referenceSite.pointer().equals(definitionSite.pointer());
    }

    private boolean isANestMember(ClassModel referenceSite, ClassModel definitionSite) {
        return definitionSite.nestMembers().contains(referenceSite.pointer());
    }

    private boolean isSubclass(ClassModel referenceSite, ClassModel definitionSite) {

        while (referenceSite != null) {
            if (referenceSite.pointer().equals(definitionSite.pointer())) {
                return true;
            }
            var superClass = referenceSite.superClass();
            if (superClass == null) {
                return false;
            }
            referenceSite = this.model.getClass(superClass.pointer());
        }

        return false;
    }

    //TODO can be made safer and better
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
