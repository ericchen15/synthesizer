package synthesizer;

/**
 * Created by ericlgame on 11-July-16.
 */

public class TunedSquareString extends SquareString {

    public TunedSquareString(double frequency) {
        super(frequency / 2);
    }

    public void tic() {
        super.tic();
        super.tic();
    }
}