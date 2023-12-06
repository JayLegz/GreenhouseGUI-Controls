import java.io.Serializable;

public class TwoTuple<A, B> implements Serializable {
    public final A first;
    public final B second;

    public TwoTuple(A a, B b) {
        first = a;
        second = b;
    }
}
