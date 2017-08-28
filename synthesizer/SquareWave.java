package synthesizer;

/**
 * Created by ericlgame on 14-Mar-16.
 */

public class SquareWave extends KarplusStrongString {

    private double pluckDelta;
    private double releaseDelta;
    private double filterIn;
    private double filterOut;

    public SquareWave(double frequency) {
        super(frequency, 0);
        setMaxVolume(0.2);
        pluckDelta = .9998;
        releaseDelta = .9;
        filterIn = 0;
        filterOut = 0;
    }

    public void pluck() {
        setDeltaVolume(pluckDelta);
        clear();
        int capacity = buffer().capacity();
        int half = capacity / 2;
        int otherHalf = capacity - half;
        for (int i = 0; i < half; i++) {
            buffer().enqueue(getMaxVolume() * -1);
        }
        for (int i = 0; i < otherHalf; i++) {
            buffer().enqueue(getMaxVolume());
        }
    }

    public void tic() {
        double first = buffer().dequeue();
        double x = first * deltaVolume();
        filterOut = C() * x + filterIn - C() * filterOut; // allpass tuning filter
        filterIn = x;
        buffer().enqueue(filterOut * deltaVolume());
    }

    public void release() {
        setDeltaVolume(releaseDelta);
    }
}