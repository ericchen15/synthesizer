package synthesizer;

/**
 * Created by ericlgame on 11-Jul-17.
 */

public class TunedSquareWave extends SquareWave{
    public TunedSquareWave(double frequency) {
        super(frequency / 2);
    }

    public void tic() {
        super.tic();
        super.tic();
    }
}
