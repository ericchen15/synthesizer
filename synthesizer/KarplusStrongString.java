package synthesizer;

/**
 * Created by ericlgame on 23-Feb-16.
 */
public abstract class KarplusStrongString {

    private static final int SR = 44100; // Sampling Rate
    private double deltaVolume; // energy change factor
    private double maxVolume;
    private double frequency;
    private BoundedQueue<Double> buffer;
    private int status; // 1 = need to pluck, -1 = locked
    private double volumeControl = .05;
    private double C;

    public KarplusStrongString(double frequency) {
        buffer = new ArrayRingBuffer<Double>((int) Math.round(SR / frequency));
        System.out.println(SR / frequency);
        for (int i = 0; i < buffer.capacity(); i++) {
            buffer.enqueue(0.0);
        }
        status = 0;
    }

    /* Create a guitar string of the given frequency.  */
    public KarplusStrongString(double frequency, double deltaVolume) {
        this(frequency);
        this.deltaVolume = deltaVolume;
    }

    // tuned = 0 indicates no lowpass filter
    public KarplusStrongString(double frequency, int tuned) {
        double ideal_buffer = SR / frequency;
        int actual_buffer;
        double delay;
        if (tuned == 1) {
            actual_buffer = (int) Math.floor((SR / frequency) + .3);
            delay = ideal_buffer - actual_buffer + .5;
        } else {
            actual_buffer = (int) Math.floor((SR / frequency) - .2);
            delay = ideal_buffer - actual_buffer;
        }
        buffer = new ArrayRingBuffer<Double>(actual_buffer);
        for (int i = 0; i < buffer.capacity(); i++) {
            buffer.enqueue(0.0);
        }
        status = 0;
        C = (1 - delay) / (1 + delay);
    }

    public KarplusStrongString(double frequency, int tuned, double deltaVolume) {
        this(frequency, tuned);
        this.deltaVolume = deltaVolume;
    }

    public abstract void pluck();
    public abstract void tic();
    public abstract void release();

    /* Return the double at the front of the buffer. */
    public double sample() {
        return buffer.peek();
    }

    public double deltaVolume() {
        return deltaVolume;
    }

    public BoundedQueue<Double> buffer() {
        return buffer;
    }

    public void clear() {
        while (!buffer.isEmpty()) {
            buffer.dequeue();
        }
        /*
        for (int i = 0; i < buffer.capacity(); i++) {
            buffer.dequeue();
        } */
    }

    public void setDeltaVolume(double newDV) {
        deltaVolume = newDV;
    }

    public void setFrequency(double frequency) {
        buffer = new ArrayRingBuffer<Double>((int) Math.round(SR / frequency));
        for (int i = 0; i < buffer.capacity(); i++) {
            buffer.enqueue(0.0);
        }
    }

    public double getMaxVolume() {
        return maxVolume;
    }

    public void setMaxVolume(double maxVolume) {
        this.maxVolume = maxVolume;
    }

    public double checkMax(double x) {
        double maxV = maxVolume;
        if (x > maxV) {
            return maxV;
        } else if (x * -1 > maxV) {
            return maxV * -1;
        } else {
            return x;
        }
    }

    public int status() {
        return status;
    }

    public void setStatus(int newStatus) {
        status = newStatus;
    }

    public void increaseVolume() {
        maxVolume += volumeControl;
    }

    public void decreaseVolume() {
        maxVolume -= volumeControl;
    }

    public double C() {
        return C;
    }
}
