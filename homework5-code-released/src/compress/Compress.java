package compress;

import java.util.Iterator;

import dsl.*;
import ecg.Data;

public class Compress {

	public static final int BLOCK_SIZE = 10;

	public static Query<Integer,Integer> delta() {
		// TODO
		return Q.pipeline(
				Q.emit(1, 0),
				Q.sWindowInv(2, 0, (x, y) -> y - x, (x, y) -> x + y)
				);
	}

	public static Query<Integer,Integer> deltaInv() {
		// TODO
		return Q.pipeline(
				Q.emit(1, 0),
				Q.sWindowInv(2, 0, (x, y) -> x + y, (x, y) -> x)
		);
	}

	public static Query<Integer,Integer> zigzag() {
		// TODO
		Query<Integer, Integer> q = Q.map(x -> {
			System.out.println("Zip zap with " + x);
			if (x >= 0) {
				return 2 * x;
			} else {
				return 2 * Math.abs(x) - 1;
			}
		});
		return q;

	}

	public static Query<Integer,Integer> zigzagInv() {
		// TODO
		return Q.map(x -> {
			System.out.println("In zigzagInv with " + x);
			if (x % 2 == 0) {
				return x / 2;
			} else {
				return -1 * (x + 1) / 2;
			}
		});

	}

	public static Query<Integer,Integer> pack() {
		// TODO
		return new Pack();

	}

	public static Query<Integer,Integer> unpack() {
		// TODO
		return new UnPack();
	}

	public static Query<Integer,Integer> compress() {
		// TODO
		return Q.pipeline(
				delta(),
				zigzag(),
				pack()
		);
	}

	public static Query<Integer,Integer> decompress() {
		// TODO
		return Q.pipeline(
				unpack(),
				zigzagInv(),
				deltaInv()
		);
	}

	public static void main(String[] args) {
		System.out.println("**********************************************");
		System.out.println("***** ToyDSL & Compression/Decompression *****");
		System.out.println("**********************************************");
		System.out.println();

		System.out.println("***** Compress *****");
		{
			// from range [0,2048) to [0,256)
			Query<Integer,Integer> q1 = Q.map(x -> x / 8);
			Query<Integer,Integer> q2 = compress();
			Query<Integer,Integer> q = Q.pipeline(q1, q2);
			Iterator<Integer> it = Data.ecgStream("100-all.csv");
			Q.execute(it, q, S.lastCount());
		}
		System.out.println();

		System.out.println("***** Compress & Decompress *****");
		{
			// from range [0,2048) to [0,256)
			Query<Integer,Integer> q1 = Q.map(x -> x / 8);
			Query<Integer,Integer> q2 = compress();
			Query<Integer,Integer> q3 = decompress();
			Query<Integer,Integer> q = Q.pipeline(q1, q2, q3);
			Iterator<Integer> it = Data.ecgStream("100-all.csv");
			Q.execute(it, q, S.lastCount());
		}
		System.out.println();
	}

}
