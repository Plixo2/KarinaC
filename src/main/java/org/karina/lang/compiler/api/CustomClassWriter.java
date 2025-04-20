package org.karina.lang.compiler.api;


import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

import org.karina.lang.compiler.logging.Log;
import org.karina.lang.compiler.model_api.ClassModel;
import org.karina.lang.compiler.model_api.Model;
import org.karina.lang.compiler.model_api.pointer.ClassPointer;
import org.karina.lang.compiler.objects.KType;
import org.karina.lang.compiler.utils.ObjectPath;
import org.karina.lang.compiler.utils.Region;
import org.objectweb.asm.ClassWriter;


public class CustomClassWriter extends ClassWriter {

    Region region;
    Model model;
    public CustomClassWriter(final int flags, Model model, Region region) {
        super(null, flags);
        this.model = model;
        this.region = region;
    }

    @Override
    protected String getCommonSuperClass(final String type1, final String type2) {
        var pathA = ObjectPath.fromJavaPath(type1);
        var pathB = ObjectPath.fromJavaPath(type2);

        var pointerA = this.model.getClassPointer(this.region, pathA);
        var pointerB = this.model.getClassPointer(this.region, pathB);
        if (pointerA == null) {
            //Log.warn(this.region, "Cannot find class " + type1);
            return "java/lang/Object";
        }
        if (pointerB == null) {
           // Log.warn(this.region, "Cannot find class " + type2);
            return "java/lang/Object";
        }
        return getCommonSuperClass(pointerA, pointerB).path().mkString("/");
    }

    private ClassPointer getCommonSuperClass(ClassPointer pointerA, ClassPointer pointerB) {

        var classModelA = this.model.getClass(pointerA);
        var classModelB = this.model.getClass(pointerB);

        if (Modifier.isInterface(classModelA.modifiers())) {
            if (implementsType(classModelB, classModelA)) {
                return pointerA;
            } else {
                return KType.ROOT.pointer();
            }
        }

        if (Modifier.isInterface(classModelB.modifiers())) {
            if (implementsType(classModelA, classModelB)) {
                return pointerB;
            } else {
                return KType.ROOT.pointer();
            }
        }

        var ancestorsA = getAncestorsOf(classModelA);
        var ancestorsB = getAncestorsOf(classModelB);
        int i = ancestorsA.size() - 1;
        int j = ancestorsB.size() - 1;

        var lastCommon = KType.ROOT.pointer();

        while (i >= 0 && j >= 0) {
            var pa = ancestorsA.get(i);
            var pb = ancestorsB.get(j);

            if (pa.equals(pb)) {
                lastCommon = pa;
            } else {
                break;
            }

            i--;
            j--;
        }

        return lastCommon;
    }

    private boolean implementsType(ClassModel classModel, ClassModel interfaceModel) {

        while (classModel != null) {

            var interfaces = classModel.interfaces();
            for (var itf : interfaces) {
                if (itf.pointer().equals(interfaceModel.pointer())) {
                    return true;
                }
            }
            for (var itf : interfaces) {
                var interfaceClassModel = this.model.getClass(itf.pointer());
                if (implementsType(interfaceClassModel, interfaceModel)) {
                    return true;
                }
            }

            var superClass = classModel.superClass();
            if (superClass == null) {
                break;
            }
            classModel = this.model.getClass(superClass.pointer());
        }
        return false;

    }

    private List<ClassPointer> getAncestorsOf(ClassModel classModel) {
        var list = new ArrayList<ClassPointer>();

        while (classModel != null) {

            var superClass = classModel.superClass();
            if (superClass == null) {
                break;
            }
            list.add(superClass.pointer());
            classModel = this.model.getClass(superClass.pointer());
        }

        return list;
    }

}
