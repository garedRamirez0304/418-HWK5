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

public class Pack implements Query<Integer, Integer> {

    Integer[] window = new Integer[10];
    int size;


    // TODO

    public Pack() {
        // TODO
    }


    @Override
    public void start(Sink<Integer> sink) {
        size = 0;
    }

    @Override
    public void next(Integer item, Sink<Integer> sink) {
        System.out.println("In pack");
        if (size < 10) {
            System.out.println("Item gotten " + item);
            // fill up window for 10 items
            window[size] = item;
            size ++;
        }
        if (size == 10){
            int max_val = 0;
            for (int i = 0; i < 10; i ++) {
                max_val = (int) Math.max(max_val, window[i]);
            }
            int bits_needed = 1;
            int power = 1;
            while(max_val / (Math.pow(2, power)) > 1) {
                bits_needed ++;
                power ++;
            }

            System.out.println("Max value " + max_val);

            sink.next(bits_needed);
            System.out.println("Bits needed " + bits_needed);
            String toSend = "";
            for (int i = 0; i < 10; i++) {
                String curr_bits = Integer.toBinaryString(window[i]);
                System.out.println(curr_bits + " Bitsss");
                while(curr_bits.length() < bits_needed) {
                    curr_bits = "0" + curr_bits;
                }
                System.out.println("New value " + curr_bits);
                toSend += curr_bits;
                while(toSend.length() >= 8) {
                    System.out.println("To send " + toSend);
                    sink.next(Integer.parseInt(toSend.substring(0, 8),2));
                    toSend = toSend.substring(8);
                }
            }
            if (toSend.length() > 0) {
                while(toSend.length() < 8) {
                    toSend = toSend + "0";
                }
                System.out.println("Sending the rest " + toSend);
                sink.next(Integer.parseInt(toSend, 2));
            }
            size = 0;
        }
    }

    @Override
    public void end(Sink<Integer> sink) {
        sink.end();
    }
}

