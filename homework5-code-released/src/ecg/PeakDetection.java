package ecg;

import dsl.S;
import dsl.Q;
import dsl.Query;
import utils.Pair;

public class PeakDetection {

	// The curve length transformation:
	//
	// adjust: x[n] = raw[n] - 1024
	// smooth: y[n] = (x[n-2] + x[n-1] + x[n] + x[n+1] + x[n+2]) / 5
	// deriv: d[n] = (y[n+1] - y[n-1]) / 2
	// length: l[n] = t(d[n-w]) + ... + t(d[n+w]), where
	//         w = 20 (samples) and t(d) = sqrt(1.0 + d * d)

	public static Query<Integer,Double> qLength() {
		// adjust >> smooth >> deriv >> length
		// Ignore for half the length
		Query<Integer, Double> raw = Q.map(x -> (Double.valueOf(x - 1024)));
		Query<Double, Double> smooth = Q.pipeline(
				Q.emit(4, (double) 0),
				Q.sWindowInv(5, (double) 0, Double::sum, (x, y) -> x - y),
				Q.map(x -> x/5),
				Q.ignore(2)
		);
		Query<Double, Double> deriv = Q.pipeline(
				Q.emit(2, (double)0),
				Q.sWindow3((a, b, c) -> a - c),
				Q.map(x -> x/2),
				Q.ignore(1)
		);
		Query<Double, Double> length = Q.pipeline(
				Q.emit(40, (double)0),
				Q.sWindowInv(41, (double)0, (x, y) ->x + Math.sqrt(1 + y * y), (x, y) -> x - Math.sqrt(1 + y * y)),
				Q.ignore(20)
		);
		return Q.pipeline(raw, smooth, deriv, length);
//		return raw; , deriv, length
	}

	// In order to detect peaks we need both the raw (or adjusted)
	// signal and the signal given by the curve length transformation.
	// Use the datatype VTL and implement the class Detect.

	public static Query<Integer,Long> qPeaks() {
		// TODO
		// Compute the pipeline results using qLength and get the initial datapoint
		Query<Integer, Double> q = qLength();
		Query<Integer, Pair<Integer, Integer>> q2 = Q.parallel(Q.id(), Q.intStream(10), (x, y));

		// Pass both pieces into parallel and build a new VTL object
		Query<Integer, VTL> q3 = Q.parallel(q, q2, (l, v) -> new VTL(v, (long)93, l));

		// Call detect with new VTL object
		Detect bruh = new Detect();


		return Q.pipeline(q3, bruh);
	}

	public static void main(String[] args) {
		System.out.println("****************************************");
		System.out.println("***** Algorithm for Peak Detection *****");
		System.out.println("****************************************");
		System.out.println();

		Q.execute(Data.ecgStream("100.csv"), qPeaks(), S.printer());
	}


	// compress
}
