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

    public double easeOutQuart(double x) {
        return 1 - Math.pow(1 - x, 4);
    }

    public double easeInQuart(double x) {
        return x * x * x * x;
    }

    public double easeOutCirc(double x) {
        return Math.sqrt(1 - Math.pow(x - 1, 2));
    }

    public double easeInOutCirc(double x) {
        return x < 0.5
                ? (1 - Math.sqrt(1 - Math.pow(2 * x, 2))) / 2
                : (Math.sqrt(1 - Math.pow(-2 * x + 2, 2)) + 1) / 2;
    }

    public double easeInOutExpo(double x) {
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
