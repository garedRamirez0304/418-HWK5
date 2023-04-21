package ra;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiPredicate;

import dsl.Query;
import dsl.Sink;
import utils.Or;
import utils.Pair;

// A streaming implementation of the theta join operator.
//
// We view the input as consisting of two channels:
// one with items of type A and one with items of type B.
// The output should contain all pairs (a, b) of input items,
// where a \in A is from the left channel, b \in B is from the
// right channel, and the pair (a, b) satisfies a predicate theta.

public class ThetaJoin<A,B> implements Query<Or<A,B>,Pair<A,B>> {

	// TODO
	BiPredicate<A, B> theta;

	List<A> leftVals;

	List<B> rightVals;

	private ThetaJoin(BiPredicate<A,B> theta) {
		// TODO
		this.theta = theta;
	}

	public static <A,B> ThetaJoin<A,B> from(BiPredicate<A,B> theta) {
		return new ThetaJoin<>(theta);
	}

	@Override
	public void start(Sink<Pair<A,B>> sink) {
		// TODO
		leftVals = new ArrayList<>();
		rightVals = new ArrayList<>();
	}

	@Override
	public void next(Or<A,B> item, Sink<Pair<A,B>> sink) {
		// TODO
		if (item.isLeft()) {
			for (B b: rightVals) {
				if(theta.test(item.getLeft(), b)) {
					sink.next(Pair.from(item.getLeft(), b));
				}
			}
			leftVals.add(item.getLeft());
		} else if (item.isRight()){
			for (A a: leftVals) {
				if(theta.test(a, item.getRight())) {
					sink.next(Pair.from(a, item.getRight()));
				}
			}
			rightVals.add(item.getRight());
		}

	}

	@Override
	public void end(Sink<Pair<A,B>> sink) {
		// TODO
		sink.end();
	}
	
}
