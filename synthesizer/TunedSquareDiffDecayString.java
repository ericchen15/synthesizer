package synthesizer;

/**
 * Created by ericlgame on 11-Jul-17.
 */

public class TunedSquareDiffDecayString extends SquareDiffDecayString{

    public TunedSquareDiffDecayString(double frequency) {
        super(frequency / 4);
    }

    public void tic() {
        super.tic();
        super.tic();
        super.tic();
        super.tic();
    }
}
