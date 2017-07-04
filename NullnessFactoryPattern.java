import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.checkerframework.checker.nullness.qual.PolyNull;

public class NullnessFactoryPattern {

    @PolyNull Object createObject(@NonNull NullnessFactoryPattern this) {
        return new @PolyNull Object();
    }

    static void test() {
        NullnessFactoryPattern factory = new NullnessFactoryPattern();
        // Are these two valid usage of polymorphism? It's quite similar to
        // Immutability type system. In CF manual, there is a sentence: "It
        // is not permitted to write just one polymorphic qualifier, on a
        // return type. You may write a polymorphic qualifier on a return
        // type only if that polymorphic qualifier also appears elsewhere in
        // the signature, on some formal parameter."
        @NonNull nno = factory.createObject();
        @Nullable no = factory.createObject();
    }
}
