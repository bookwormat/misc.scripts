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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

/**
 *  {@author Michael Groh http://www.mgroh.net}
 *  {@author Benjamin Ferrari http://bookworm.at}
 *
*/
public class Solution {

    private static Random randgen = new Random(System.currentTimeMillis());

    private List<Integer>[] copyValues(List<Integer>[] values) {
        List<Integer>[] newvalues = new List[values.length];
        int i = 0;
        for(List<Integer> list : values){
            newvalues[i] = new ArrayList<Integer>(list);
            i+=1;
        }
        return newvalues;
    }

    private static class KExchange implements Algorithm {
    
        private int k;

        private KExchange(int k){
            this.k = k;
        }

        @Override
        public Solution execute(Solution solution) {
            OneExchange oneExchange = new OneExchange();
            for(int i=0;i<k;i++){
                solution = oneExchange.execute(solution);
            }
            return solution;
        }
        

    }

    private static class OneExchange implements Algorithm {

        @Override
        public Solution execute(Solution input) {
            Solution solution = new Solution(input);
            Integer s1 = solution.chooseRandomNode();
            Integer s2 = solution.chooseRandomNode();
            List<Integer> s1nodes = solution.getNodesAt(s1);
            List<Integer> s2nodes = solution.getNodesAt(s2);

            Integer a = chooseRandom(s1nodes);
            Integer b = chooseRandom(s2nodes);

            if(a.equals(s2) || b.equals(s1)){ return execute(input); }

            s1nodes.remove(a);
            s1nodes.add(b);

            s2nodes.remove(b);
            s2nodes.add(a);

            return solution;
        }

    }
    List<Integer>[] values;
    double[][] graph;

    public Solution(double[][] graph) {
        this.graph = graph;
        values = new List[graph.length];
        for (int i = 0; i < graph.length; i++) {
            values[i] = new LinkedList<Integer>();
        }
    }

    private Integer chooseRandomNode(){
        List<Integer> candidates = allNotEmptyNodes();
        return chooseRandom(candidates);
    }

    private static Integer chooseRandom(List<Integer> candidates){
        return candidates.get(randgen.nextInt(candidates.size()));
    }

    private static Integer chooseRandom(List<Integer> candidates, Collection<Integer> excludes){
        candidates.removeAll(excludes);
        return candidates.get(randgen.nextInt(candidates.size()));
    }

    public Solution(Solution other) {
        this.graph = other.graph;
        this.values = copyValues(other.values);
    }

    List<Integer> getNodesAt(int i) {
        return values[i];
    }

    private List<Integer> allNotEmptyNodes() {
        List<Integer> results = new ArrayList<Integer>();
        int i = 0;
        for (List<Integer> neighbours : values) {
            if (!neighbours.isEmpty()) {
                results.add(i);
            }
            i += 1;
        }
        return results;
    }

    void addNodeTo(int i, Integer node) {
        values[i].add(node);
    }

    public double score() {
        double total = 0;
        for (int i = 0; i < values.length; i++) {
            List<Integer> nodes = values[i];
            double max = 0;
            for (int j : nodes) {
                double distance = graph[i][j];
                if (distance > max) {
                    max = distance;
                }
            }
            total += Math.pow(max, 3);
        }
        return total;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < values.length; i++) {
            sb.append(i);
            if (!values[i].isEmpty()) {
                sb.append(" (");
                sb.append(String.format("%4.4f",calcEnergy(i, values[i])));
                sb.append(")");
            }            
            sb.append(" --> ");
            sb.append(values[i]);

            sb.append("\n");
        }
        return sb.toString();
    }

    private double calcEnergy(int from, List<Integer> toList) {
        double max = 0;
        for (Integer to : toList) {
            double energy = graph[from][to];
            if (energy > max) {
                max = energy;
            }
        }
        return max;
    }

    public Solution localOptimize(int runs){
        Solution improved = localOptimize(new OneExchange(), runs);
        improved.localOptimize(new KExchange(2), runs);
        improved.localOptimize(new KExchange(3), runs);
        return improved;
    }

    private Solution localOptimize(Algorithm algorithm, int runs) {
        Solution best = this;

        for (int i = 0; i < runs; i++) {
            Solution other = algorithm.execute(best);
            if (other.score() < best.score()) {
                best = other;
            }
        }

        return best;
    }
}
