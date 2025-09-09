/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sistema;

/**
 *
 * @author Bonn
 */
public class Easings {
    
    public static final int EASE_OUT_QUART = 1;
    public static final int EASE_IN_QUART = 2;
    public static final int EASE_OUT_CIRC = 3;
    public static final int EASE_IN_OUT_CIRC = 4;
    public static final int EASE_IN_OUT_EXPO = 5;
    
    public static double ease(double x, int easing) {
        switch (easing) {
            case 1:
                return easeOutQuart(x);
            case 2:
                return easeInQuart(x);
            case 3:
                return easeOutCirc(x);
            case 4:
                return easeInOutCirc(x);
            case 5:
                return easeInOutExpo(x);
            default:
                return x;
        }
    }

    public static double easeOutQuart(double x) {
        return 1 - Math.pow(1 - x, 4);
    }

    public static double easeInQuart(double x) {
        return x * x * x * x;
    }

    public static double easeOutCirc(double x) {
        return Math.sqrt(1 - Math.pow(x - 1, 2));
    }

    public static double easeInOutCirc(double x) {
        return x < 0.5
                ? (1 - Math.sqrt(1 - Math.pow(2 * x, 2))) / 2
                : (Math.sqrt(1 - Math.pow(-2 * x + 2, 2)) + 1) / 2;
    }

    public static double easeInOutExpo(double x) {
        if (x == 0) {
            return 0;
        }

        if (x < 0.5) {
            return Math.pow(2, 20 * x - 10) / 2;
        } else {
            return (2 - Math.pow(2, -20 * x + 10)) / 2;
        }
    }
}
