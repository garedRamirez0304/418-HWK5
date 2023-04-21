package ecg;

import dsl.S;
import dsl.Q;
import dsl.Query;

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
				Q.map(x -> x/5)
		);
		Query<Double, Double> deriv = Q.pipeline(
				Q.emit(1, (double)0),
				Q.sWindowInv(2, (double)0, Double::sum, (x, y) ->x - y)
		);
		Query<Double, Double> length = Q.pipeline(
				Q.emit(40, (double)0),
				Q.sWindowInv(41, (double)0, (x, y) ->x + Math.sqrt(1 + y * y), (x, y) -> x - y)
		);
		return Q.pipeline(raw, smooth, deriv, length);
	}

	// In order to detect peaks we need both the raw (or adjusted)
	// signal and the signal given by the curve length transformation.
	// Use the datatype VTL and implement the class Detect.

	public static Query<Integer,Long> qPeaks() {
		// TODO
		return null;
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
