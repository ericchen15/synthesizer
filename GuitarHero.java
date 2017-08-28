import synthesizer.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/** A client that uses the synthesizer package to replicate a plucked guitar string sound */
public class GuitarHero extends JPanel implements KeyListener {
    private static final double CONCERT_A = 440.0;
    private static final double base = indexToET(0, 33, 12); // 65.4064 = C2, 87.3071 = F2, 97.9989 = G2, 110 = A2
    private static String keyboard = " `1qaz2wsx3edc4rfv5tgb6yhn7ujm8ik,9ol.0p;/-['=]\\~!QAZ@WSX#EDC$RFV%TGB^YHN&UJM*IK<(OL>)P:?_{\"+}|";
    private static final int keyboardSize = 52;
    private static List<Integer> keycodes = Arrays.asList(192, 9, 20, 16, 49, 81, 65, 90, 50, 87, 83, 88, 51, 69, 68, 67, 52, 82, 70, 86, 53, 84, 71, 66, 54,
            89, 72, 78, 55, 85, 74, 77, 56, 73, 75, 44, 57, 79, 76, 46, 48, 80, 59, 47, 45, 91, 222, 61, 93, 10, 8, 92);
    private static KarplusStrongString[] strings = new KarplusStrongString[2 * keyboardSize];
    private static int alt = 0;

    public GuitarHero() {
        this.setPreferredSize(new Dimension(500, 500));
        addKeyListener(this);
    }

    public void addNotify() {
        super.addNotify();
        requestFocus();
    }

    public void keyPressed(KeyEvent e) {
        int index = keycodes.indexOf(e.getKeyCode());
        if (index != -1 && strings[index + alt].status() == 0) {
            strings[index + alt].setStatus(1);
        } else if (e.getKeyCode() == KeyEvent.VK_UP) {
            for (KarplusStrongString x : strings) {
                x.increaseVolume();
            }
        } else if (e.getKeyCode() == KeyEvent.VK_DOWN) {
            for (KarplusStrongString x : strings) {
                x.decreaseVolume();
            }
        } else if (e.getKeyCode() == KeyEvent.VK_SPACE) {
            alt = 52 - alt;
        }
    }

    public void keyReleased(KeyEvent e) {
        int index = keycodes.indexOf(e.getKeyCode());
        if (index != -1) {
            strings[index].setStatus(0);
            strings[index].release();
            strings[index + keyboardSize].setStatus(0);
            strings[index + keyboardSize].release();
        }
    }

    public void keyTyped(KeyEvent e) {

    }

    // returns frequency in the octave above root
    private static double normalize(double root, double frequency) {
        if (frequency < root) {
            while (frequency < root) {
                frequency *= 2;
            }
        } else {
            while (frequency > root * 2) {
                frequency /= 2;
            }
        }
        return frequency;
    }

    private static double indexToET(int index, int indexOfA, int divisions) {
        return CONCERT_A * Math.pow(2.0, ((index - indexOfA) / (float)divisions));
    }

    // assumes base is in lowest octave
    private static double indexToGenerator(int index, int indexOfBase, double generator) {
        int note = (index + 12 - indexOfBase) % 12;
        int octaves = Math.floorDiv(index - indexOfBase, 12);
        int fifths = 0;
        while (note != 0) {
            note = (note - 7) % 12;
            fifths++;
        }
        if (fifths >= 7) {
            fifths -= 12;
        }
        double frequency = base * Math.pow(generator, fifths);
        frequency = normalize(base, frequency);
        return frequency * Math.pow(2.0, octaves);
    }

    private static double indexToCustom(int index, int indexOfBase, double[] ratios) {
        int octaveSize = ratios.length;
        int note = (index + octaveSize - indexOfBase) % octaveSize;
        int octaves = Math.floorDiv(index - indexOfBase, octaveSize);
        return base * ratios[note] * Math.pow(2.0, octaves);
    }

    public static void main(String[] args) {

        JFrame f = new JFrame();
        f.getContentPane().add(new GuitarHero());
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.pack();
        f.setVisible(true);
        f.setFocusTraversalKeys(KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS, Collections.EMPTY_SET);

        double[] asymmetric_5limit = {1, (float)16/15, (float)9/8, (float)6/5, (float)5/4, (float)4/3, (float)45/32, (float)3/2, (float)8/5, (float)5/3, (float)9/5, (float)15/8};
        double[] symmetric_5limit = {1, (float)16/15, (float)9/8, (float)6/5, (float)5/4, (float)4/3, (float)45/32, (float)3/2, (float)8/5, (float)5/3, (float)16/9, (float)15/8};
        double[] just_2minor = {1, (float)16/15, (float)9/8, (float)6/5, (float)5/4, (float)27/20, (float)45/32, (float)3/2, (float)8/5, (float)27/16, (float)9/5, (float)15/8};
        double[] pythagorean_6 = {1, (float)16/15, (float)9/8, (float)6/5, (float)5/4, (float)4/3, (float)45/32, (float)3/2, (float)8/5, (float)27/16, (float)9/5, (float)15/8};
        double[] harmonic12 = {1, (float)17/16, (float)9/8, (float)19/16, (float)5/4, (float)21/16, (float)11/8, (float)23/16, (float)3/2, (float)13/8, (float)7/4, (float)15/8};
        double[] harmonic16 = {1, (float)17/16, (float)9/8, (float)19/16, (float)5/4, (float)21/16, (float)11/8, (float)23/16, (float)3/2, (float)25/16, (float)13/8, (float)27/16, (float)7/4, (float)29/16, (float)15/8, (float)31/16};
        double[] harmonic8 = {1, (float)9/8, (float)5/4, (float)11/8, (float)3/2, (float)13/8, (float)7/4, (float)15/8};

        for (int i = 0; i < keyboardSize; i++) {
            strings[i] = new SquareWave(indexToCustom(i, 2, symmetric_5limit));
            strings[i + keyboardSize] = new SquareWave(indexToET(i, 30, 12));
        }

        while (true) {

            /* check if the user has typed a key; if so, process it
            if (StdDraw.hasNextKeyTyped()) {
                char key = StdDraw.nextKeyTyped();
                int index = keyboard.indexOf(key);
                if (index != -1) {
                    KarplusStrongString toPluck = strings[index];
                    toPluck.pluck();
                }
            } */

            for (KarplusStrongString x : strings) {
                if (x.status() == 1) {
                    x.pluck();
                    x.setStatus(-1);
                }
            }

        /* compute the superposition of samples */
            double sample = 0.0;
            for (KarplusStrongString x : strings) {
                sample += x.sample();
            }

        /* play the sample on standard audio */
            StdAudio.play(sample);

        /* advance the simulation of each guitar string by one step */
            for (KarplusStrongString x : strings) {
                x.tic();
            }
        }
    }
}
