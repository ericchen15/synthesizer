package synthesizer;

public class GuitarString extends KarplusStrongString {

    private double pluckDelta;
    private double releaseDelta;
    private double filterIn;
    private double filterOut;

    public GuitarString(double frequency) {
        super(frequency, 1);
        setMaxVolume(0.4);
        pluckDelta = .996;
        releaseDelta = .9;
        filterIn = 0;
        filterOut = 0;
    }

    /* Pluck the guitar string by replacing the buffer with white noise. */
    public void pluck() {
        setDeltaVolume(pluckDelta);
        clear();
        for (int i = 0; i < buffer().capacity(); i++) {
            buffer().enqueue((Math.random() - 0.5) * 2 * getMaxVolume());
        }
    }

    /* Advance the simulation one time step by performing one iteration of
     * the Karplus-Strong algorithm. 
     */
    public void tic() {
        double first = buffer().dequeue();
        double second = buffer().peek();
        double x = (first + second) / 2; // lowpass filter
        filterOut = C() * x + filterIn - C() * filterOut; // allpass tuning filter
        filterIn = x;
        buffer().enqueue(filterOut * deltaVolume());
    }

    public void release() {
        setDeltaVolume(releaseDelta);
    }
}
