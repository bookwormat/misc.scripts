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

import java.util.*;

/**
 *  {@author Michael Groh http://www.mgroh.net}
 *  {@author Benjamin Ferrari http://bookworm.at}
 *
*/
public class Ant {

    private double[][] eta;
    private double[][] tau;

    private Solution solution;
    private List<Integer> visited = new LinkedList<Integer>();
    private List<Integer> unvisited = new LinkedList<Integer>();
    private static final double ALPHA = 0.6;


    public Ant(double[][] graph, double[][] eta, double[][] tau) {
        this.eta = eta;
        this.tau = tau;
        solution = new Solution(graph);

        for (int i = 0; i < graph.length; i++) {
            unvisited.add(new Integer(i));
        }

        //random startnode
//        Collections.shuffle(unvisited);
        visited.add(unvisited.remove(0));

    }

    public Solution solve() {

        while (!unvisited.isEmpty()) {
            // calc quotient for probabilties
            double quotient = 0;
            for (Integer start : visited) {
                for (Integer target : unvisited) {
                    quotient += eta[start][target] * tau[start][target];
                }
            }
            chooseNodes(quotient);
        }
        return solution;
    }


    private void chooseNodes(double quotient) {
        double sum = 0;
        double rand = new Random().nextDouble();

        for (Integer start : visited) {
            for (Integer target : unvisited) {
                double p = eta[start][target] * tau[start][target] / quotient;
                sum += p;
                if (sum > rand) {
                    visited.add(target);
                    unvisited.remove(new Integer(target));
                    solution.addNodeTo(start, target);
                    return;
                }
            }
        }
    }
}
