package ecg;

import dsl.S;
import dsl.Q;
import dsl.Query;

// This file is devoted to the analysis of the heart rate of the patient.
// It is assumed that PeakDetection.qPeaks() has already been implemented.

public class HeartRate {

	// RR interval length (in milliseconds)
	public static Query<Integer,Double> qIntervals() {
		// TODO
		Query<Integer, Long> peak = PeakDetection.qPeaks();
		Query<Integer, Double> q = Q.pipeline(
				peak,
				Q.sWindow2((x, y) -> (double)(y - x)),
				Q.map(x -> x * 2.77777778)
		);
		return q;
	}

	// Average heart rate (over entire signal) in bpm.
	public static Query<Integer,Double> qHeartRateAvg() {
		// TODO
		return Q.pipeline(
						qIntervals(),
						Q.map(x -> x / 1000),
						Q.foldAvg(),
						Q.map(x -> 60/x)
				);

	}

	// Standard deviation of NN interval length (over the entire signal)
	// in milliseconds.
	public static Query<Integer,Double> qSDNN() {
		// TODO
		return Q.pipeline(
				qIntervals(),
				Q.foldStdev()
		);
	}

	// RMSSD measure (over the entire signal) in milliseconds.
	public static Query<Integer,Double> qRMSSD() {
		Query<Double, SumDifCount> q1 = Q.map(x -> new SumDifCount(x, 1, 0));
		Query<SumDifCount, SumDifCount> q2 = Q.fold(SumDifCount.zero, SumDifCount::add);
		Query<SumDifCount,Double> q3 = Q.map(p -> Math.sqrt((p.tot/p.c)));
		return Q.pipeline(qIntervals(), q1, q2, q3);
	}
	private static class SumDifCount {
		public static final SumDifCount zero = new SumDifCount(0.0, 0, 0);
		public final long c;
		public final double s;
		public final double tot;
		public SumDifCount(double s, long c, double tot) {
			this.tot = tot;
			this.s = s;
			this.c = c;
		}
		public SumDifCount add(SumDifCount p) {
			if (s == 0) {
				return new SumDifCount(p.s, p.c, p.tot);
			}
			return new SumDifCount(p.s, c + p.c, Math.pow(p.s - s, 2) + tot);
		}
	}

	private static class DifCount {
		public static final DifCount zero = new DifCount(0.0, 0, 0);
		public final long c;
		public final double s;
		public final double tot;
		public DifCount(double s, long c, double tot) {
			this.tot = tot;
			this.s = s;
			this.c = c;
		}
		public DifCount add(DifCount p) {
			if (s == 0) {
				return new DifCount(p.s, p.c, p.tot);
			}
			double calc = Math.abs(p.s - s);
			int greater = 0;
			if (calc > 50) {
				greater = 1;
			}
			return new DifCount(p.s, c + p.c, tot + greater);
		}
	}

	// Proportion (in %) derived by dividing NN50 by the total number
	// of NN intervals (calculated over the entire signal).
	public static Query<Integer,Double> qPNN50() {
		Query<Double, DifCount> q1 = Q.map(x -> new DifCount(x, 1, 0));
		Query<DifCount, DifCount> q2 = Q.fold(DifCount.zero, DifCount::add);
		Query<DifCount,Double> q3 = Q.map(p -> Math.sqrt((p.tot/p.c * 100)));
		return Q.pipeline(qIntervals(), q1, q2, q3);
	}

	public static void main(String[] args) {
		System.out.println("****************************************");
		System.out.println("***** Algorithm for the Heart Rate *****");
		System.out.println("****************************************");
		System.out.println();

		System.out.println("***** Intervals *****");
		Q.execute(Data.ecgStream("100.csv"), qIntervals(), S.printer());
		System.out.println();

		System.out.println("***** Average heart rate *****");
		Q.execute(Data.ecgStream("100-all.csv"), qHeartRateAvg(), S.printer());
		System.out.println();

		System.out.println("***** HRV Measure: SDNN *****");
		Q.execute(Data.ecgStream("100-all.csv"), qSDNN(), S.printer());
		System.out.println();

		System.out.println("***** HRV Measure: RMSSD *****");
		Q.execute(Data.ecgStream("100-all.csv"), qRMSSD(), S.printer());
		System.out.println();

		System.out.println("***** HRV Measure: pNN50 *****");
		Q.execute(Data.ecgStream("100-all.csv"), qPNN50(), S.printer());
		System.out.println();
	}

}
