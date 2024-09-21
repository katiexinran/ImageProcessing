import graphs.*;

import java.io.*;
import java.util.*;

public class SeamFinderMultiTest {
    private static final String dirPath = "data/";
    private static final String[] files = new String[] {
            "HJoceanSmall",
            "stripes",
            "diagonals",
            "diag_test",
            "chameleon",
            "12x10",
            "10x12",
            "10x10",
            "8x3",
            "7x10",
            "7x3",
            "6x5",
            "5x6",
            "4x6",
            "3x8",
            "3x7",
            "3x4",
            "3x3"
    };

    public static void main(String[] args) throws IOException {
        System.out.println("Testing Djikstra Solver");
        test(new AdjacencyListSeamFinder(DijkstraSolver::new));

        System.out.println("\n=============================================");

        System.out.println("Testing Generative Seam Finder");
        test(new GenerativeSeamFinder(DijkstraSolver::new));

        System.out.println("\n=============================================");

        System.out.println("Testing Toposort DAG Solver");
        test(new AdjacencyListSeamFinder(ToposortDAGSolver::new));

        System.out.println("\n=============================================");

        System.out.println("Testing Dynamic Programming Seam Finder");
        test(new DynamicProgrammingSeamFinder());
    }

    private static void test(SeamFinder seamFinder) throws IOException {
        EnergyFunction f = new DualGradientEnergyFunction();
        System.out.printf("%-30.30s  %-30.30s  %-30.30s%n", "File", "Vertical Passed", "Horizontal Passed");

        for (String fileName : files) {
            Picture picture = new Picture(new File(dirPath + fileName + ".png"));

            SeamCarver horzSeamCarver = new SeamCarver(new File(dirPath + fileName + ".png"), f, seamFinder);
            List<Integer> horzSeam = horzSeamCarver.removeHorizontal();
            int horzSeamEnergy = getSeamEnergy(picture, horzSeam, false);
            int horzExpectedEnergy = getExpectedSeamEnergy(fileName, false);
            boolean horzPassed = horzSeamEnergy == horzExpectedEnergy;

            SeamCarver vertSeamCarver = new SeamCarver(new File(dirPath + fileName + ".png"), f, seamFinder);
            List<Integer> vertSeam = vertSeamCarver.removeVertical();
            int vertSeamEnergy = getSeamEnergy(picture, vertSeam, true);
            int vertExpectedEnergy = getExpectedSeamEnergy(fileName, true);
            boolean vertPassed = vertSeamEnergy == vertExpectedEnergy;

            printPass(fileName, horzPassed, vertPassed);

            if (!vertPassed) {
                printFail(vertExpectedEnergy, vertSeam.toString(), vertSeamEnergy, true);
            }
            if (!horzPassed) {
                printFail(horzExpectedEnergy, horzSeam.toString(), horzSeamEnergy, false);
            }
        }
    }

    private static int getSeamEnergy(Picture picture, List<Integer> seam, boolean isVertical) {
        EnergyFunction f = new DualGradientEnergyFunction();
        int total = 0;
        if (!isVertical) {
            for (int x = 0; x < picture.width(); x++) {
                total += f.apply(picture, x, seam.get(x));
            }
        } else {
            for (int y = 0; y < picture.height(); y++) {
                total += f.apply(picture, seam.get(y), y);
            }
        }
        return total;
    }

    private static void printPass(String fileName, boolean vertPassed, boolean horzPassed) {
        System.out.printf("%-30.30s  %-30.30s  %-30.30s%n",
                fileName + ".png",
                vertPassed ? "PASS" : "FAIL",
                horzPassed ? "PASS" : "FAIL");
    }

    private static void printFail(int expectedEnergy, String seam, int seamEnergy, boolean isVertical) {
        String prefix = isVertical ? "Vertical" : "Horizontal";
        System.out.println("\t" + prefix + " expected energy:        \t" + expectedEnergy);
        System.out.println("\t" + prefix + " generated seam          \t" + seam);
        System.out.println("\t" + prefix + " generated seam energy:  \t" + seamEnergy);
    }

    private static int getExpectedSeamEnergy(String fileName, boolean isVertical) throws FileNotFoundException {
        String suffix = isVertical ? "vertical" : "horizontal";
        Scanner reader = new Scanner(new File(dirPath + fileName + "." + suffix + ".txt"));
        int expected = reader.nextInt();
        reader.close();
        return expected;
    }
}
