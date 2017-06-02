package org.checkerframework.checker.initialization;

import jdk.nashorn.internal.ir.annotations.Immutable;
import org.checkerframework.checker.initialization.qual.Initialized;
import org.jmlspecs.annotation.Readonly;

/**
 * Created by mier on 28/05/17.
 * New Type Hierarchy
 *                    Readonly
 *               /       |         \
 *           Mutable PolyImmutable Immutable
 */
public class Example {
    @Immutable Node f;
    @PolyImmutable Object o;
    @PolyImmutable Number n;

    @Mutable Example () {
        this.f = new @Immutable Node(this);
    }

    @Immutable Example () {
        this.f = new @Immutable Node(this);// "this" is immutable here <= q(this) = q(ret)
        // The instance method invocation in constructor still needs to satisfy the standard method invocation rule
        // i.e. typecheck because for receiver, @Immutable <: @Readonly |> @PolyImmutable
        this.postInit();// If no left hand side exist, then its lhs type is equivalant to @Readonly
    }

    // @Initialize allows "this" to be able to be immutable, mutable and polyimmutable when assigning fields,
    @Initialize         // Just like extending the scope of the constructor field assignment
    void postInit(@Poly Example this) {
        // Typecheck because of the existence of @Initialize
        this.o = new @Poly Object();
        // Just like in constructor, instance method invocation in constructor still needs to satisfy T-INVKs
        // the standard method inoacation typing rule.
        this.anotherPostInit();
    }

    // Method with annotation @Initialize can only be called within the constructor or another instance method with @Initialize annotation
    @Initialize
    void anotherPostInit(@PolyImmutable Example this) {
        this.n = new @PolyImmutable Integer(3);
    }

}

class Node {
    // Think of changing PolyImmutable to Immutable and check the below makes sense or not.
    @PolyImmutable Example e;

    @Immutable Node (@Immutable Example e) {
        this.e = e;
    }

    @Mutable Node (@Mutable Example e) {
        this.e = e;
    }

    // We don't allow Readonly constructor return type, which aims to be consistent with the constraint that
    // new object is either mutable or immutable.(Readonly will import third type - Readonly to new objects)
    @Readonly Node (@Mutable Example e) {
        this.e = e;
    }

    // It makes sense to type check this constuctor because assigning mutable object to mutable field or
    // immutable object to immutable field makes sense.
    @PolyImmutable Node (@PolyImmutable Example e) {
        this.e = e;
    }

    // Meaningful T-NEW. Invalid to assign immutable instance to a poly field as the instance could be mutable.
    @PolyImmutable Node (@Immutable Example e) {
        this.e = e;
    }
}
