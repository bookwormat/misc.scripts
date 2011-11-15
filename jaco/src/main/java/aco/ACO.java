/*
   This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package aco;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 *  {@author Michael Groh http://www.mgroh.net}
 *  {@author Benjamin Ferrari http://bookworm.at}
 *
*/
public class ACO {

    private static final int ITERATIONS = 1000;
    private static final int AVG_INTERVAL = 20;
    private static int LOCAL_ITERATIONS = 10000;

    private double[][] graph;
    private double[][] pheromones;
    private double[][] eta;
    private double[][] tau;

    private static final double EVAPORATE_FACTOR = 0.07;
    private static final double BETA = 3.0;
    private static final double ALPHA = 0.6;

    public ACO(List<Point> points) throws IOException {

        //init graph
        graph = new double[points.size()][points.size()];
        for (int i = 0; i < points.size(); i++) {
            Point a = points.get(i);
            for (int j = i + 1; j < points.size(); j++) {
                Point b = points.get(j);
                double distance = Math.sqrt(Math.pow(a.x - b.x, 2) + Math.pow(a.y - b.y, 2));
                graph[i][j] = distance;
                graph[j][i] = distance;
            }
        }

        //init pheromones
        pheromones = new double[points.size()][points.size()];
        for (int i = 0; i < pheromones.length; i++) {
            for (int j = 0; j < pheromones.length; j++) {
                pheromones[i][j] = 1.0;
            }
        }

        //init eta
        eta = new double[points.size()][points.size()];
        for (int i = 0; i < eta.length; i++) {
            for (int j = 0; j < eta[i].length; j++) {
                eta[i][j] = Math.pow((1.0 / graph[i][j]), BETA);
            }
        }

        //init tau
        tau = new double[points.size()][points.size()];
        for (int i = 0; i < tau.length; i++) {
            for (int j = 0; j < tau[i].length; j++) {
                tau[i][j] = Math.pow(pheromones[i][j], ALPHA);
            }
        }
    }


    private void pheromoneUpdate(Solution solution, double score) {
        for (int i = 0; i < pheromones.length; i++) {
            for (int j = 0; j < pheromones[i].length; j++) {
                if (solution.getNodesAt(i).contains(j)) {
                    pheromones[i][j] = pheromones[i][j] + 1 / score;
                }
                pheromones[i][j] *= (1.0 - EVAPORATE_FACTOR);
                pheromones[j][i] = pheromones[i][j];
                tau[i][j] = Math.pow(pheromones[i][j], ALPHA);
            }
        }
    }


    public Solution solve() {
        double bestScore = Double.MAX_VALUE;
        Solution bestSolution = null;
        List<Double> scoreHistory = new ArrayList<Double>(AVG_INTERVAL);

        for (int i = 0; i < ITERATIONS; i++) {
            Ant ant = new Ant(graph, eta, tau);
            Solution solution = ant.solve();
            double score = solution.score();
            pheromoneUpdate(solution, score);
            scoreHistory.add(score);
            if (score < bestScore) {
                bestScore = score;
                bestSolution = solution;
                System.out.println("New best solution in iteration " + i + ": " + bestScore);
            }

            if (i % AVG_INTERVAL == 0 && i > 0) {
                double total = 0;
                for (Double aScore : scoreHistory) {
                    total += aScore;
                }
                System.out.println("AVG (iterations " + (i - AVG_INTERVAL) + " to " + i + "): " + (total / scoreHistory.size()));
                scoreHistory.clear();
            }
        }



        return bestSolution;
    }


    private static List<Point> parseInput(File file) throws IOException {
        List<Point> points = new ArrayList<Point>();
        List lines = FileUtils.readLines(file, "UTF-8");
        for (Object l : lines) {
            String line = (String) l;
            String[] coords = line.split("  ");
            Point p = new Point();
            p.x = Double.parseDouble(coords[0]);
            p.y = Double.parseDouble(coords[1]);
            points.add(p);
        }
        return points;
    }

    private void printGraph(double[][] g) {
        for (double[] row : g) {
            for (double v : row) {
                System.out.printf("%.4f ", v);
            }
            System.out.println();
        }
    }


    public static void main(String[] args) throws Exception {
        if (args.length < 1) {
            System.out.println("You must specify the filename as first parameter!");
            System.exit(-1);
        }
        String filename = args[0];
        File file = new File(filename);
        if (!file.exists()) {
            System.out.println("File '" + filename + "' does not exist!");
            System.exit(-1);
        }
        ACO aco = new ACO(parseInput(file));
        long start = System.currentTimeMillis();

        Solution solution = aco.solve();
        System.out.println("Solution from ACO:\n" + solution);
        System.out.println("Solution energy from ACO: " + solution.score());
        System.out.println("trying to improve with local search...");
        solution = solution.localOptimize(LOCAL_ITERATIONS);
        System.out.println("final Solution:\n" + solution);
        System.out.println("final Solution energy: " + solution.score());
        System.out.println("Calculated in " + ((System.currentTimeMillis() - start)) + "ms");
        System.out.println("Ants=" + ITERATIONS + ", alpha=" + ALPHA + ", beta=" + BETA + ", evaporate factor=" + EVAPORATE_FACTOR);
        System.out.println("(for " + filename + ")");
    }
}
