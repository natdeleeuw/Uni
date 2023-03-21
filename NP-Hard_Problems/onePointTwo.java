import java.util.*;

class onePointTwo {
    /**
     * Returns a list of all combinations of k elements from the set {1,...,n} without repetitions
     */
    public static List<int[]> getCombinationsWithoutRepetition(int n, int k) {
        //Call your solver here

        HashSet<Solver.Constraint> constraints = new HashSet<>();


        Set<Solver.Tuple> tuplesA  = new HashSet<>();
        Set<Solver.Tuple> tuplesB = new HashSet<>();

        for(int i = 0; i < n; i++) {
            tuplesA.add(new Solver.Tuple(i, 1));
            tuplesB.add(new Solver.Tuple(i, -1));
        }
        constraints.add(new Solver.Constraint(tuplesA, k));
        constraints.add(new Solver.Constraint(tuplesB, -k));


        ArrayList<Boolean[]> solutions = Solver.solve(constraints, n);

        ArrayList<int[]> results = new ArrayList<>();

        for(Boolean[] solution: solutions) {
            int index = 0;
            int[] result = new int[k];
            for(int i = 0; i < solution.length; i++) {
                if(solution[i]) {
                    result[index] = i + 1;
                    index++;
                }
            }
            results.add(result);
        }

        return results;


    }
}

class Solver {


    public static ArrayList<Boolean[]> solve(HashSet<Constraint> constraints, int n) {




        Map<Integer, int[]> dlMap2 = new HashMap<>();
        Map<Integer, HashSet<Constraint>> constraintMap = new HashMap<>();

        ArrayList<Boolean[]> solutions = new ArrayList<>();

        Map<Integer, Integer> pastDecisions = new HashMap<>();
        int dl = 0;

        int[] decisionLevel0 = new int[n];
        for(int i = 0; i < n; i++) {
            decisionLevel0[i] = -1;
        }

        dlMap2.put(0, decisionLevel0);
        constraintMap.put(0, (HashSet<Constraint>) constraints.clone());

        while(true) {

            Queue<Integer> changedDecisions = new LinkedList<>();
            if(dl ==-1) {
                break;
            }


            int[] currentDL = dlMap2.get(dl).clone();
            Set<Constraint> oldConstraints = (Set<Constraint>) constraintMap.get(dl).clone();
            int decisionsLeft = 0;
            for(int i = 0; i < n; i++) {
                if(currentDL == null) {
                    int a = 2;
                }
                if(currentDL[i] == -1) {
                    decisionsLeft++;
                }
            }
            if(decisionsLeft == 0) {
                if(test(constraints, currentDL)) {

                    Boolean[] solution = new Boolean[n];

                    for (int i = 0; i < n; i++) {
                        if (currentDL[i] == 1) {
                            solution[i] = true;
                        } else {
                            solution[i] = false;
                        }
                    }

                    solutions.add(solution);
                }
                constraintMap.remove(dl);
                dlMap2.remove(dl);
                if(!pastDecisions.containsKey(dl)) {
                    return solutions;
                }
                int pastDecision = pastDecisions.remove(dl);

                int[] pastMap = dlMap2.remove(dl -1);
                pastMap[pastDecision] = 0;
                dlMap2.put(dl-1, pastMap);
                dl--;
                continue;

            }

            dl++;

            int decisionVariable = -1;
            int size = 0;
            for(int i = 0; i < n; i++) {
                if(currentDL[i] == -1) {
                    decisionVariable = i;
                }
            }

            pastDecisions.put(dl, decisionVariable);

            currentDL[decisionVariable] = 1;
            changedDecisions.add(decisionVariable);

            Set<Constraint> currentConstraints = getConstraints(currentDL, oldConstraints, n);


//            propogate info
            boolean ranIntoProblem = false;

            while(!changedDecisions.isEmpty()) {

                int changedDecision = changedDecisions.poll();
                changedDecisions = new LinkedList<>();
                currentConstraints = getConstraints(currentDL, currentConstraints, n);

                for(Constraint constraint : currentConstraints) {

                    int count_negatives = 0;
                    for (Tuple currentTupple : constraint.tuples) {
                        if (currentTupple.weight == -1) {
                            count_negatives++;
                        }
                    }

//                not enough negatives
                    if (count_negatives < -1 * constraint.b) {
                        ranIntoProblem = true;
                        break;
                    }

                    if (count_negatives == -1 * constraint.b) {

                        for(Tuple currentTupple : constraint.tuples) {
                            if(currentTupple.weight == 1 && currentDL[currentTupple.index] != -1) {
                                changedDecisions.add(currentTupple.index);
                                currentDL[currentTupple.index] = 0;
                            } else if(currentTupple.weight == -1 && currentDL[currentTupple.index] != -1) {
                                changedDecisions.add(currentTupple.index);
                                currentDL[currentTupple.index] = 1;
                            }
                        }

                    }


                }
            }


            if(ranIntoProblem) {
                int pastDecision  = pastDecisions.remove(dl);
                int[] pastDL = dlMap2.remove(dl - 1);
                pastDL[pastDecision] = 0;
                dlMap2.put(dl - 1, pastDL);
                dl--;
                continue;

            } else {
                constraintMap.put(dl, (HashSet<Constraint>) currentConstraints);
                dlMap2.put(dl, currentDL);
            }




        }


        return solutions;





    }


    private static boolean test(Set<Constraint> constraints, int[] currentDL) {
        for(Constraint constraint: constraints) {
            int sum = 0;
            for(Tuple currentTuple: constraint.tuples) {
                sum += currentTuple.weight * currentDL[currentTuple.index];
            }

            if( sum > constraint.b) {
                return false;
            }
        }
        return true;

    }

    private static Set<Constraint> getConstraints(int currentDecisions[], Set<Constraint> constraints, int n) {

        Set<Constraint> currentConstraints = new HashSet<>();

        for(Constraint constraint : constraints) {

            int changeK = 0;
            Set<Tuple> newTuples = new HashSet<>();
            for(Tuple tuple : constraint.tuples) {
                if(currentDecisions[tuple.index] != -1) {
                    changeK -= tuple.weight * currentDecisions[tuple.index];
                } else {
                    newTuples.add(new Tuple(tuple.index, tuple.weight));
                }
            }
            if(!newTuples.isEmpty()) {
                Constraint newConstraint = new Constraint(newTuples, constraint.b + changeK);
                currentConstraints.add(newConstraint);
            }


        }
        return currentConstraints;
    }

    public static class Tuple {
        int index;
        int weight;

        public Tuple(int index, int weight) {
            this.index = index;
            this.weight = weight;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Tuple tupple = (Tuple) o;
            return index == tupple.index;
        }

        @Override
        public int hashCode() {
            return Objects.hash(index);
        }
    }


    public static class Constraint {
        Set<Tuple> tuples;
        int b;

        public Constraint(Set<Tuple> tupples, int b) {
            this.tuples = tupples;
            this.b = b;
        }


    }
}

