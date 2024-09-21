import graphs.Graph;

import java.util.*;

public class DynamicProgrammingSeamFinder implements SeamFinder {
    private Picture picture;
    private EnergyFunction f;
    private double[][] minEnergyPath;

    public List<Integer> findSeam(Picture picture, EnergyFunction f) {
        // TODO: Your code here!
        this.picture = picture;
        this.f = f;

        //QUESTION: HOW DOES BATINA'S SUGGESTED SOLUTION KEEP TRACK OF NEIGHBORS?

        //1 initialize a 2d array to represent cost of minimum energy path
        minEnergyPath = new double[picture.width()][picture.height()];

        //2 iteratively compute the minimum energy path to each pixel starting from the left
        //& working to the right considering each pixel's preceding path costs, the (x - 1, y - 1) pixel;
        //(x - 1) pixel; and (x + 1, y + 1) pixel
        //eg figure out the min cost to each pixel
        //this is the first chunk in the python example

        //make case for first column -> calculate energy for every pixel in the first col.
        for(int i = 0; i < picture.height(); i++){
            minEnergyPath[0][i] = f.apply(picture, 0, i);
        }
        //the order of the for loops might be backwards
        //column by column, left to right
        for(int i = 1; i < picture.width(); i++){ //i is where I am in the column
            for(int j = 0; j < picture.height(); j++){ //j is where I am in the row
                                     //computes energy of neighbors             //computes energy of the pixel itself
                minEnergyPath[i][j] = min(minEnergyPath[i - 1], j).getValue() + f.apply(picture, i, j);
                /*if(j == 0){
                    minEnergyPath[i][j] = f.apply(picture, i, j) + Math.min(minEnergyPath[i - 1][j], minEnergyPath[i - 1][j + 1]);
                }else if(j == picture.height() - 1){
                    minEnergyPath[i][j] = f.apply(picture, i, j) + Math.min(minEnergyPath[i - 1][j], minEnergyPath[i - 1][j - 1]);
                }else{
                    minEnergyPath[i][j] =f.apply(picture, i, j) + min3(minEnergyPath[i - 1][j], minEnergyPath[i - 1][j + 1], minEnergyPath[i - 1][j - 1]);
                }*/
            }
        }

        //3 compute the shortest path by starting from the right edge and working back towards
        //left edge, adding each min cost y-coord to a List
        //eg figure out the shortest path accross the picture *based* on min costs found in //2
        //this is the second chunk in the python example

        List<Integer> shortestPath = new ArrayList<>();
        //finds min in the last column
        int j = min(minEnergyPath[picture.width() - 1]).getKey();
        shortestPath.add(j);

        //get y coordinate of the smallest value in each column, backtracking
        for(int i = picture.width() - 2; i >= 0; i--){
            j = min(minEnergyPath[i], j).getKey();
            shortestPath.add(j);
        }

        Collections.reverse(shortestPath);
        return shortestPath;
/*

        double minEPixel = Double.MAX_VALUE;
        int minEPixelI = -1;
        for(int i = 0; i < picture.height() - 1; i++){
            if(minEnergyPath[picture.width() - 1][i] < minEPixel){
                minEPixel = minEnergyPath[picture.width() - 1][i];
                minEPixelI = i;
            } 
        }
        for(int i = picture.width() - 1; i >= 0; i--){
            //go to each column
            //find the neighbors from the last pixel in the column (3)
            //pick the smallest one
            //store the y coordinate of that pixel
            shortestPath.add(minEPixelI);
            if(i == 0 && minEPixelI != 0 && minEPixelI != picture.height() - 1){
                minEPixelI = Math.min(minEnergyPath[i][minEPixelI], minEnergyPath[i + 1][minEPixel + 1]);
                minEPixelI = indexOfMin(i - 1, minEPixel); 
            }else if(minEPixelI == 0){
                minEPixel = Math.min(minEnergyPath[i - 1][minEPixelI], minEnergyPath[i - 1][minEPixelI + 1]);
                minEPixelI = indexOfMin(i - 1, minEPixel); 
            }else if(minEPixelI == picture.height() - 1){
                minEPixel = Math.min(minEnergyPath[i - 1][minEPixelI], minEnergyPath[i - 1][minEPixelI - 1]);
                minEPixelI = indexOfMin(i - 1, minEPixel);
            }else{
                minEPixel = min3(minEnergyPath[i - 1][minEPixelI], minEnergyPath[i - 1][minEPixelI + 1], minEnergyPath[i - 1][minEPixelI - 1]);
                minEPixelI = indexOfMin(i - 1, minEPixel);  
            }   
        }
        shortestPath.add(minEPixelI);
        System.out.println("shortest path = " + shortestPath.size() + " picture width = " + picture.width());

        //4 collections.reverse the list
        Collections.reverse(shortestPath);
        return shortestPath;
        */
    }

    private static AbstractMap.SimpleEntry<Integer, Double> min(double[] vals){
        return min(vals, 0, vals.length);     
    }

    //takes in 1 section of the 2D array, passes in j - 1 & j + 2 as high & low to avoid null pointers
    private static AbstractMap.SimpleEntry<Integer, Double> min(double[] vals, int j){
        return min(vals, j - 1, j + 2);
    }

    private static AbstractMap.SimpleEntry<Integer, Double> min(double[] vals, int low, int high){
        double value = Double.MAX_VALUE;
        int index = -1;
        if(low < 0){
            low = 0;
        }
        if(high > vals.length){
            high = vals.length;
        }
        //computes the minimum between high & low
        for(int i = low; i < high; i++){
            if(vals[i] < value){
                value = vals[i];
                index = i;
            }
        }
        return new AbstractMap.SimpleEntry<>(index, value);
    }

    //Method to calculate min of 3 doubles
    private double min3(double a, double b, double c){
        return Math.min(a, Math.min(b, c));
    }

    //iterate through the column to find the min match
    private int indexOfMin(int col, double min){
        for(int i = 0; i < picture.height() - 1; i++){
            if(minEnergyPath[col][i] == min){
                return i;
            }
        }
        return -1;
    }
}
