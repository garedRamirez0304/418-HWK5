package ra;

import java.util.function.BiPredicate;
import java.util.function.Function;

import dsl.Query;
import dsl.Sink;
import utils.Or;
import utils.Pair;

// A streaming implementation of the equi-join operator.
//
// We view the input as consisting of two channels:
// one with items of type A and one with items of type B.
// The output should contain all pairs (a, b) of input items,
// where a \in A is from the left channel, b \in B is from the
// right channel, and the equality predicate f(a) = g(b) holds.

public class EquiJoin<A,B,T> implements Query<Or<A,B>,Pair<A,B>> {

	// TODO
//	Function<A, T> f;
//	Function<B, T> g;
	ThetaJoin<A, B> join;

	private EquiJoin(Function<A,T> f, Function<B,T> g) {
		// TODO

		this.join = ThetaJoin.from((a, b) -> f.apply(a).equals(g.apply(b)));
	}

	public static <A,B,T> EquiJoin<A,B,T> from(Function<A,T> f, Function<B,T> g) {
		return new EquiJoin<>(f, g);
	}

	@Override
	public void start(Sink<Pair<A,B>> sink) {
		// TODO
		this.join.start(sink);
	}

	@Override
	public void next(Or<A,B> item, Sink<Pair<A,B>> sink) {
		// TODO
		this.join.next(item, sink);

	}

	@Override
	public void end(Sink<Pair<A,B>> sink) {
		// TODO
		this.join.end(sink);
	}
	
}
