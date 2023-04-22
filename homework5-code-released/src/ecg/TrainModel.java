package ecg;

import dsl.S;
import dsl.Q;
import dsl.Query;

public class TrainModel {

	// The average value of the signal l[n] over the entire input.
	public static Query<Integer,Double> qLengthAvg() {
		// TODO
//		 Hint: Use PeakDetection.qLength()
		Query<Integer, Double> q = Q.pipeline(PeakDetection.qLength(), Q.foldAvg());
//		return PeakDetection.qLength();
		return q;
	}

	public static void main(String[] args) {
		System.out.println("***********************************************");
		System.out.println("***** Algorithm for finding the threshold *****");
		System.out.println("***********************************************");
		System.out.println();

//		Q.execute(Data.ecgStream("100-all.csv"), qLengthAvg(), S.printer());
		Q.execute(Data.ecgStream("100-all.csv"), qLengthAvg(), S.printer());
	}

}
