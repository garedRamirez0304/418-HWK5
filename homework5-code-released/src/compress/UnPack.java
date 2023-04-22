package compress;

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

public class UnPack implements Query<Integer, Integer> {

    Integer[] window = new Integer[10];
    int size;
    int header;
    String total_bits;
    int count;


    // TODO

    public UnPack() {
        // TODO
    }


    @Override
    public void start(Sink<Integer> sink) {
        size = 0;
        header = -1;
        total_bits = "";
        count = 0;
    }

    @Override
    public void next(Integer item, Sink<Integer> sink) {
        // First int sent is the header for the size of the packed values
        if (header == -1) {
            System.out.println("header size " + item);
            header = item;
            total_bits = "";
            return;
        }
        System.out.println("Unpack next" + item);

        String curr_item = Integer.toBinaryString(item);
        System.out.println("Unpack item " + curr_item);
        while(curr_item.length() < 8) {
            curr_item = "0" + curr_item;
        }
        total_bits += curr_item;
        while (total_bits.length() >= header) {
            System.out.println("Total bits before " + total_bits);
            System.out.println("Sending out " + Integer.parseInt(total_bits.substring(0, header), 2));
            sink.next(Integer.parseInt(total_bits.substring(0, header), 2));
            total_bits = total_bits.substring(header);
            System.out.println("Total bits after " + total_bits);
            count++;
        }

        System.out.println("count " + count);
        if (count == 10) {
            count = 0;
            header = -1;
        }


    }

    @Override
    public void end(Sink<Integer> sink) {
        sink.end();
    }
}
