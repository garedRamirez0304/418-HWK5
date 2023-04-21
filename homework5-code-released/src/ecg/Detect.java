package ecg;

import dsl.Query;
import dsl.Sink;

// The detection algorithm (decision rule) that we described in class
// (or your own slight variant of it).
//
// (1) Determine the threshold using the class TrainModel.
//
// (2) When l[n] exceeds the threhold, search for peak (max x[n] or raw[n])
//     in the next 40 samples.
//
// (3) No peak should be detected for 72 samples after the last peak.
//
// OUTPUT: The timestamp of each peak.

public class Detect implements Query<VTL,Long> {

	// Choose this to be two times the average length
	// over the entire signal.
	private static final double THRESHOLD = 118.02269657654423 * 2; // TODO

	int window = -1;
	int sleep = -1;
//	Long current_max = 0L;

	int curr_time;

	VTL current_max = new VTL(0, 0, 0);

	// TODO

	public Detect() {
		// TODO
	}

	@Override
	public void start(Sink<Long> sink) {
		// TODO
	}

	@Override
	public void next(VTL item, Sink<Long> sink) {
		System.out.println("Threshold " + THRESHOLD + " vs " + item.l);
		curr_time ++;
		if (sleep > 0) {

			sleep --;
			return;
		}
		// Begin peak computation
		if (item.l > THRESHOLD && window == -1) {
			this.window = 40;
		}
		// Sliding max computation
		if (window > 0) {
			double max = Math.max(item.l, this.current_max.l);
			if ((long) max == item.l) {
				this.current_max = item;
//				this.current_max.ts = curr_time;
			}
			window --;
		}
		// Sleep and wait before next computation
		if (window == 0) {
			System.out.println("Window ");
			sink.next(this.current_max.ts);
			this.current_max = new VTL(0, 0, 0);
			this.sleep = (int)(72 - (this.curr_time - this.current_max.ts));
			window = -1;
			System.out.println("Sleep time " + sleep);
		}

	}

	@Override
	public void end(Sink<Long> sink) {
		// TODO
		sink.end();
	}
	
}
