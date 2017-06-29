import qual.Mutable;
import qual.PolyImmutable;
import qual.Readonly;

import java.util.Date;

public class DateCell {

    @PolyImmutable Date date;

    @PolyImmutable Date getDate(@PolyImmutable DateCell this) {
        return this.date;
    }

    @PolyImmutable Date getWhatever(@Readonly DateCell this) {
        return new @PolyImmutable Date();// In the T-NEW of updated version, instantiation to @PolyImmutable object is allowed
    }

    void cellSetHours(@Mutable DateCell this) {
        // ReIm argues that viewpoint adapting to lhs(@Mutable here) trasmits the context to current "this" via below type rules:
        // q(this-cellSetHours) <: q(md) |> q(this-getDate) Which is q(this-cellSetHours) <: @Mutable |> @PolyImmutable = @Mutable
        // And it gives an counterexample that if we adapt to the receiver of the method invocation, we get a not-useful constraint:
        // q(this-cellSetHours) <: q(this-cellSetHours) |> q(this-getDate) Which is q(this-cellSetHours) <: q(this-cellSetHours)

        // But in fact, we can still transmit that mutability context into current "this" even without adapting to lhs.
        // q(this-cellSetHours) |> q(ret-getDate) <: q(md) which becomes q(this-cellSetHours) <: @Mutable. It still can make current "this"
        // to be mutable.
        // Truly, viewpoint adaptation to receiver doesn't impose additional constraint on receiver. But this makes sense. Because poly
        // means that it can be substited by any qualifiers including poly itself. That's exactly the purpose of method with poly "this" -
        // invocable on all possible types. ReIm also suffers this "not-useful" contraint problem on return type adaptation:
        // q(md) |> q(ret-getDate) <: q(md) which becomes q(md) <: q(md). So there is no reason for ReIm to argue against this "seems-like"
        // trivial constraint
        @Mutable Date md = this.getDate();
        md.setHours(1);
    }

    void cellGetHours(@Readonly DateCell this) {
        // In receiver viewpoint adaptation:
        // q(this-cellGetHours) |> @PolyImmutable <: @Readonly => q(this-cellGetHours) <: @Readonly So cellGetHours is invocable on
        // any types of receiver. In inference, if we prefer top(@Readonly), it still infers current "this" to @Readonly.
        @Readonly Date rd = this.getDate();
        int hour = rd.getHours();
    }

    void typecheckInReImButNotTypeCheckIfAdaptedToReceiver(@Readonly DateCell this) {
        // Doesn't typecheck if adapted to receiver because @Readonly |> @PolyImmutable <: @Mutable is false.
        // But it typechecks in ReIm because @Mutable |> @PolyImmutable <: @Mutable holds.
        // In fall term 2016, I said, we want to get @Mutable object from a @Readonly object(also @Immutable object). That's true.
        // For example, it's universal that a method can create a local object and return it to the caller, so it can be mutated.
        // But here, we are instantiating poly return type to mutable type from a readonly receiver. Doing so doesn't harm anything of course,
        // but it makes the type system not intuitive anymore. Intuitively, a poly something becomes anything that is used to access it, that
        // should be the receiver. If a programmer really wants to mutable the returned object, he/she should make the return type of getWhatever mutable.
        @Mutable Date whatever = this.getWhatever();
    }

    // For a method declaration, if type of this, formal parameters and return type are annotated with @Readonly, @Immutable, @Mutable,
    // adaptating to whatever doesn't make a difference. Only when they are @PolyImmutable, there is real difference between the two.
    // The most tricky case - receiver, return type are @PolyImmutable is proven to be valid in receiver adaptation methodology.
    // ReIm doesn't support instatiating poly and readonly objects, so if return type is poly, method receiver is also poly. ReIm's
    // reasoning using the cellSetHourse, cellGetHours, getDate methods failed to show the necessity of viewpoint adapting to lhs. The
    // only benefit is that a poly return type method can be instantiated to any lhs types regardless of any receiver the method is invoked
    // on. But is that a good matter? It only confuses the user. So I think viewpoint adaping to receiver makes more sense than lhs.
}