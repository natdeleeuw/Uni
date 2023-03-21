import java.util.*;


class Main {

    public static void main(String[] args) {
    getCombinationsWithoutRepetition(2,3);

    }
    /**
     * Returns a list of all binary strings of length n
     */
    public static List<int[]> getCombinationsWithoutRepetition(int n, int k) {
        Map<Integer, ArrayList> domainMap = new HashMap<>();
        ArrayList<Integer> domain = new ArrayList<Integer>();
        domain.add(0);
        domain.add(1);
        for( int i = 0; i < n; i++){
            domainMap.put(i, (ArrayList) domain.clone());
        }
        ArrayList<Solver.Constraint> constraints = new ArrayList<Constraint>();
        for(int i = 0; i < k; i++){
            for( int j = i + 1; j < k; j++){
                constraints.add(new Constraint(i, j, 2));
                constraints.add(new Constraint(i, j, 3));
            }
        }

        //ArrayList<Boolean[]> solution = Solver.solve( new int[1][n+ 1]);
        List solution = Solver.solve( constraints, domainMap, n);

        //Call your solver here
        ArrayList<String> finalsolution = new ArrayList<>();

        return solution;
    }
}

class Solver {
    public static ArrayList<int[]> solve(ArrayList<Constraint> constraints, HashMap<Integer, ArrayList<Integer>> domain , int n) {




        Map<Integer, HashMap<Integer, ArrayList<Integer> >> dlMap = new HashMap<>();

        ArrayList<int[]> solutions = new ArrayList<>();

        Map<Integer, Tuple> pastDecisions = new HashMap<>();
        int dl = 0;


        HashMap<Integer, ArrayList<Integer>> currentDL0 = new HashMap<>();

        for(Map.Entry<Integer, ArrayList<Integer>> a: domain.entrySet()) {
            currentDL0.put(0 + a.getKey(), (ArrayList<Integer>) a.getValue().clone());

        }

        HashMap<Integer, ArrayList<Constraint>> constraintMap = new HashMap<>();

        for(Constraint constraint: constraints) {
            if(constraintMap.containsKey(constraint.firstIndex)) {
                constraintMap.get(constraint.firstIndex).add(constraint);
            } else {
                ArrayList list = new ArrayList();
                list.add(constraint);
                constraintMap.put(constraint.firstIndex, list);
            }

            if(constraintMap.containsKey(constraint.secondIndex)) {
                constraintMap.get(constraint.secondIndex).add(constraint);
            } else {
                ArrayList list = new ArrayList();
                list.add(constraint);
                constraintMap.put(constraint.secondIndex, list);
            }

        }


        dlMap.put(0, currentDL0);





        while(true) {

            Queue<Integer> changedDecisions = new LinkedList<>();
            if(dl ==-1) {
                break;
            }

            HashMap<Integer, ArrayList<Integer>> currentDL = new HashMap<>();

            for(Map.Entry<Integer, ArrayList<Integer>> a: dlMap.get(dl).entrySet()) {
                currentDL.put(0 + a.getKey(), (ArrayList<Integer>) a.getValue().clone());
            }


            int decisionsLeft = 0;
            for(int i = 0; i < n; i++) {
                if(currentDL.get(i).size() > 1) {
                    decisionsLeft++;
                }
            }

            if(decisionsLeft == 0) {
                if(test(constraints, currentDL)) {

                    int[] solution = new int[n];

                    for (int i = 0; i < n; i++) {

                        solution[i] += currentDL.get(i).get(0);
                    }

                    solutions.add(solution);
                }
                dlMap.remove(dl);
                if(!pastDecisions.containsKey(dl)) {
                    return solutions;
                }
                Tuple pastDecision = pastDecisions.remove(dl);

                ArrayList<Integer> pastMap = dlMap.get(dl -1).get(pastDecision.index);
                pastMap.remove(Integer.valueOf(pastDecision.choice));
                dl--;
                continue;

            }

            dl++;

            int decisionVariable = -1;
            int size = 0;
            for(int i = 0; i < n; i++) {
                if(currentDL.get(i).size() > 1) {
                    if(constraintMap.get(i).size() > size) {
                        decisionVariable = i;
                        size = constraintMap.get(i).size();
                    }

                }
            }

            int choice = currentDL.get(decisionVariable).get(0);

            pastDecisions.put(0 + dl, new Tuple(decisionVariable, choice));

            ArrayList<Integer> remainingOptions = new ArrayList<>();
            remainingOptions.add(choice);


            currentDL.put(decisionVariable, remainingOptions);

            changedDecisions.add(decisionVariable);





//            propogate info
            boolean ranIntoProblem = false;

            while (!changedDecisions.isEmpty()) {
                int changedDecision = changedDecisions.poll();

                ArrayList<Constraint> currentConstraints = constraintMap.get(changedDecision);

                for(Constraint currentConstraint : currentConstraints) {

                    if(currentConstraint.operation == 1) {
                        ArrayList<Integer> newDomain = (ArrayList<Integer>) intersection(currentDL.get(currentConstraint.firstIndex), currentDL.get(currentConstraint.secondIndex));
                        if (currentDL.get(currentConstraint.firstIndex).size()  !=  newDomain.size() ) {
                            changedDecisions.add(currentConstraint.firstIndex);
                        }
                        if (newDomain.size() != currentDL.get(currentConstraint.secondIndex).size()) {
                            changedDecisions.add(currentConstraint.secondIndex);
                        }
                        currentDL.put(currentConstraint.firstIndex, (ArrayList<Integer>) newDomain.clone());
                        currentDL.put(currentConstraint.secondIndex, (ArrayList<Integer>) newDomain.clone());

                    }

                    if(currentConstraint.operation == 2) {
                        if(currentDL.get(currentConstraint.firstIndex).size() == 1){
                            if(currentDL.get(currentConstraint.secondIndex).contains(currentDL.get(currentConstraint.firstIndex).get(0))) {
                                currentDL.get(currentConstraint.secondIndex).remove(Integer.valueOf(currentDL.get(currentConstraint.firstIndex).get(0)));
                                changedDecisions.add(currentConstraint.secondIndex);
                            }
                        }
                        if(currentDL.get(currentConstraint.secondIndex).size() == 1){
                            if(currentDL.get(currentConstraint.firstIndex).contains(currentDL.get(currentConstraint.secondIndex).get(0))) {
                                currentDL.get(currentConstraint.firstIndex).remove(Integer.valueOf(currentDL.get(currentConstraint.secondIndex).get(0)));
                                changedDecisions.add(currentConstraint.firstIndex);

                            }
                        }


                    }
                    if(currentConstraint.operation == 3) {
                        int max = 0;
                        for(int b: currentDL.get(currentConstraint.secondIndex)) {
                            if (b > max) {
                                max = b;
                            }
                        }

                        ArrayList<Integer> newArrayA = new ArrayList<>();

                        for(int a: currentDL.get(currentConstraint.secondIndex)) {
                            if(a > max) {
                                changedDecisions.add(currentConstraint.secondIndex);
                            } else {
                                newArrayA.add(a);
                            }
                        }

                        currentDL.put(currentConstraint.secondIndex, newArrayA);




                        int min = Integer.MAX_VALUE;
                        for(int a : currentDL.get(currentConstraint.firstIndex)) {
                            if (a < min) {
                                min = a;
                            }
                        }

                        ArrayList<Integer> newArrayB = new ArrayList<>();

                        for(int b: currentDL.get(currentConstraint.firstIndex)) {
                            if( b < min) {
                                changedDecisions.add(currentConstraint.firstIndex);
                            } else {
                                newArrayB.add(b);
                            }
                        }
                        currentDL.put(currentConstraint.firstIndex, newArrayB);




                    }
                    if(currentDL.get(currentConstraint.firstIndex).size() == 0 || currentDL.get(currentConstraint.secondIndex).size() == 0) {
                        ranIntoProblem = true;
                        break;
                    }



                }

            }






//            while(!changedDecisions.isEmpty()) {
//                int changedDecision = changedDecisions.poll();
//
//                for(int i : constraintsMap.get(changedDecision)) {
//
//
//
//                    int count_negatives = 0;
//                    for(int j = 0; j < n; j++) {
//                        if(currentConstraints[i][j] == -1) {
//                            count_negatives++;
//                        }
//                    }
//
////                not enough negatives
//                    if(count_negatives < -1 * currentConstraints[i][n]) {
//                        ranIntoProblem = true;
//                        break;
//                    }
//
//                    if(count_negatives == -1 * currentConstraints[i][n]) {
//                        for(int j = 0; j < n; j++) {
//                            if(currentConstraints[i][j] == 1) {
//                                changedDecisions.add(j);
//                                currentDL[j] = 0;
//                            } else if(currentConstraints[i][j] == -1) {
//                                changedDecisions.add(j);
//                                currentDL[j] = 1;
//                            }
//                        }
//
//                        currentConstraints = getConstraints(currentDL, constraints, n);
//                    }
//
//                }
//            }


            if(ranIntoProblem) {
                Tuple pastDecision  = pastDecisions.remove(dl);
                HashMap<Integer, ArrayList<Integer>> pastDL =  dlMap.remove(dl - 1);
                pastDL.get(pastDecision.index).remove(Integer.valueOf(pastDecision.choice));

                dlMap.put(dl - 1, pastDL);
                dl--;
                continue;

            } else {
                dlMap.put(0 + dl, currentDL);
            }




        }


        return solutions;





    }

    private static boolean test(ArrayList<Constraint> constraints, HashMap<Integer, ArrayList<Integer>> currentDL) {

        for(Constraint constraint: constraints) {
            if(constraint.operation == 1) {
                if(currentDL.get(constraint.firstIndex).get(0) != currentDL.get(constraint.secondIndex).get(0)) {
                    return false;
                }
            } else if (constraint.operation == 2) {
                if(currentDL.get(constraint.firstIndex).get(0) == currentDL.get(constraint.secondIndex).get(0)) {
                    return false;
                }
            } else if (constraint.operation == 3) {
                if(currentDL.get(constraint.firstIndex).get(0) > currentDL.get(constraint.secondIndex).get(0)) {
                    return false;
                }

            }
        }
        return true;

    }

    private static List<Integer> intersection(List<Integer> list1, List<Integer> list2) {
        List<Integer> list = new ArrayList<Integer>();

        for (int t : list1) {
            if (list2.contains(t)) {
                list.add(t);
            }
        }
        return list;
    }




    public static class Tuple {
        int index;
        int choice;

        public Tuple(int index, int choice) {
            this.index = index;
            this.choice = choice;
        }
    }


    static class Constraint {
        int firstIndex;
        int secondIndex;

        //        1 -> =, 2-> /=, 3 -> <=
        int operation;

        public Constraint(int firstIndex, int secondIndex, int operation) {
            this.firstIndex = firstIndex;
            this.secondIndex = secondIndex;
            this.operation = operation;
        }
    }


}

//import java.util.*;
//
//class Solution {
//    /**
//     * Returns a list of all permutations in the set {1,...,n}
//     */
//    public static List<int[]> getSetPermutations(int n) {
//        //Call your solver here
//        int[][] constraints = new int[4 * n][n * n + 1];
//
//        for(int i = 0; i < n; i++) {
//            for(int j = 0; j < n; j++) {
//                constraints[i][i * n +j] = 1;
//                constraints[i+n][i * n +j] = -1;
//
//            }
//            constraints[i][n * n] = 1;
//            constraints[i+n][n * n] = -1;
//        }
//
//        for(int i = 0; i < n; i++) {
//            for(int j = 0; j < n; j++) {
//                constraints[i + 2 * n][j * n +i] = 1;
//                constraints[i+3 * n][j * n +i] = -1;
//
//            }
//            constraints[i + 2 * n][n * n] = 1;
//            constraints[i+ 3 * n][n * n] = -1;
//        }
//
//        ArrayList<Boolean[]> solutions = Solver.solve(constraints);
//
//        ArrayList<int[]> results = new ArrayList<>();
//
//        for(Boolean[] solution : solutions){
//            int[] result = new int[n];
//
//            for(int i = 0; i < n; i++) {
//                for(int j = 0; j <n; j++) {
//                    if(solution[i * n + j]) {
//                        result[i] = j +1;
//                    }
//                }
//            }
//            results.add(result);
//
//
//        }
//        return results;
//    }
//}
//
//class Solver {
//    //Copy your solver here
//
//
//
//    public static ArrayList<Boolean[]> solve(int[][] constraints) {
//
//
//
//        int n = constraints[0].length - 1;
//
//        Map<Integer, int[]> dlMap = new HashMap<>();
//
//        ArrayList<Boolean[]> solutions = new ArrayList<>();
//
//        Map<Integer, Integer> pastDecisions = new HashMap<>();
//        int dl = 0;
//
//        int[] decisionLevel0 = new int[n];
//        for(int i = 0; i < n; i++) {
//            decisionLevel0[i] = -1;
//        }
//
//        dlMap.put(0, decisionLevel0);
//
//        Map<Integer, ArrayList<Integer>> constraintsMap = new HashMap<>();
//
//        for(int i = 0; i < constraints.length; i++) {
//            for (int j = 0; j < n; j++) {
//                if(constraintsMap.containsKey(j)) {
//                    constraintsMap.get(j).add(i);
//                } else {
//                    constraintsMap.put(j, new ArrayList<>());
//                    constraintsMap.get(j).add(i);
//                }
//            }
//        }
//
//        while(true) {
//
//            Queue<Integer> changedDecisions = new LinkedList<>();
//            if(dl ==-1) {
//                break;
//            }
//
//            if(!dlMap.containsKey(dl)) {
//                int a = 2;
//            }
//
//            int[] currentDL = dlMap.get(dl).clone();
//            int decisionsLeft = 0;
//            for(int i = 0; i < n; i++) {
//                if(currentDL == null) {
//                    int a = 2;
//                }
//                if(currentDL[i] == -1) {
//                    decisionsLeft++;
//                }
//            }
//            if(decisionsLeft == 0) {
//                if(test(constraints, currentDL)) {
//
//                    Boolean[] solution = new Boolean[n];
//
//                    for (int i = 0; i < n; i++) {
//                        if (currentDL[i] == 1) {
//                            solution[i] = true;
//                        } else {
//                            solution[i] = false;
//                        }
//                    }
//
//                    solutions.add(solution);
//                }
//                dlMap.remove(dl);
//                if(!pastDecisions.containsKey(dl)) {
//                    return solutions;
//                }
//                int pastDecision = pastDecisions.remove(dl);
//
//                int[] pastMap = dlMap.remove(dl -1);
//                pastMap[pastDecision] = 0;
//                dlMap.put(dl-1, pastMap);
//                dl--;
//                continue;
//
//            }
//
//            dl++;
//
//            int decisionVariable = -1;
//            for(int i = 0; i < n; i++) {
//                if(currentDL[i] == -1) {
//                    decisionVariable = i;
//                    break;
//                }
//            }
//
//
//
//            pastDecisions.put(dl, decisionVariable);
//
//            currentDL[decisionVariable] = 1;
//            changedDecisions.add(decisionVariable);
//
//            int[][] currentConstraints = getConstraints(currentDL, constraints, n);
//
//
////            propogate info
//            boolean ranIntoProblem = false;
//
//            while(!changedDecisions.isEmpty()) {
//                int changedDecision = changedDecisions.poll();
//
//                for(int i : constraintsMap.get(changedDecision)) {
//
//                    int count_negatives = 0;
//                    for(int j = 0; j < n; j++) {
//                        if(currentConstraints[i][j] == -1) {
//                            count_negatives++;
//                        }
//                    }
//
////                not enough negatives
//                    if(count_negatives < -1 * currentConstraints[i][n]) {
//                        ranIntoProblem = true;
//                        break;
//                    }
//
//                    if(count_negatives == -1 * currentConstraints[i][n]) {
//                        for(int j = 0; j < n; j++) {
//                            if(currentConstraints[i][j] == 1) {
//                                changedDecisions.add(j);
//                                currentDL[j] = 0;
//                            } else if(currentConstraints[i][j] == -1) {
//                                changedDecisions.add(j);
//                                currentDL[j] = 1;
//                            }
//                        }
//
//                        currentConstraints = getConstraints(currentDL, constraints, n);
//                    }
//
//                }
//            }
//
//
//            if(ranIntoProblem) {
//                int pastDecision  = pastDecisions.remove(dl);
//                int[] pastDL = dlMap.remove(dl - 1);
//                pastDL[pastDecision] = 0;
//
//                int count_NotKnown = 0;
//                for(int i = 0; i < n; i++) {
//                    if(currentDL[i] == -1) {
//                        count_NotKnown++;
//                    }
//                }
//
//                dlMap.put(dl - 1, pastDL);
//                dl--;
//                continue;
//
//            } else {
//                dlMap.put(dl, currentDL);
//            }
//
//
//
//
//        }
//
//
//        return solutions;
//
//
//
//
//
//    }
//
//    private static boolean test(int[][] constraints, int[] currentDL) {
//
//        for(int i = 0; i < constraints.length; i++) {
//            int sum = 0;
//            for(int j = 0; j < constraints[0].length - 1; j++) {
//                sum += constraints[i][j] * currentDL[j];
//            }
//            if(sum > constraints[i][constraints[0].length - 1]) {
//                return false;
//            }
//
//        }
//        return true;
//
//    }
//
//    private static int[][] getConstraints(int currentDecisions[], int[][] constraints, int n) {
//
//        int[][] currentConstraints = new int[constraints.length][constraints[0].length];
//
//
//        //Making the new constraints table taking into account the decisions that were already made
//        for (int i = 0; i < constraints.length; i++) {
//
//            //all decisions subtract from the value of k for the corresponding constraint inequality.
//            int changeK = 0;
//            for (int j = 0; j < constraints[0].length - 1; j++) {
//                //if we have made a decision for that variable, we change the value of K.
//                //Otherwise we simply just make the constraints -1.
//                if (currentDecisions[j] != -1) {
//                    currentConstraints[i][j] = 0;
//                    changeK -= constraints[i][j] * currentDecisions[j];
//                } else {
//                    currentConstraints[i][j] = constraints[i][j];
//                }
//            }
//
//
//            currentConstraints[i][n] = constraints[i][n] + changeK;
//        }
//        return currentConstraints;
//    }
//}
//
//
////import java.util.*;
////
////class Solution {
////    /**
////     * Returns a list of all combinations of k elements from the set {1,...,n} without repetitions
////     */
////    public static List<int[]> getCombinationsWithoutRepetition(int n, int k) {
////        //Call your solver here
////
////
////        int[][] constraints = new int[k * 6][n + 1];
////
////        Map<Integer, ArrayList> domainMap = new HashMap<>();
////        ArrayList<Integer> domain = new ArrayList<Integer>();
////        for( int i = 1; i < n; i++){
////            domain.add(i);
////        }
////        for( int i = 0; i < k; i++){
////            domainMap.put(i, (ArrayList) domain.clone());
////        }
////
////
////
////
////        //ArrayList<Boolean[]> solution = Solver.solve( new int[1][n+ 1]);
////        ArrayList<Integer[]> solution = Solver.solve( new int[1][n+ 1], domainMap);
////
////        //Call your solver here
////        ArrayList<String> finalsolution = new ArrayList<>();
////
////        for(Integer[] singleSolution: solution) {
////            String finalSingleSolution = "";
////            for(Integer number: singleSolution) {
////                if(number == 1) {
////                    finalSingleSolution+= "1";
////                } else {
////                    finalSingleSolution += "0";
////                }
////            }
////            finalsolution.add(finalSingleSolution);
////        }
////        return finalsolution;
////    }
//////
//////        HashSet<Solver.Constraint> constraints = new HashSet<>();
//////
//////
//////        Set<Solver.Tuple> tuplesA  = new HashSet<>();
//////        Set<Solver.Tuple> tuplesB = new HashSet<>();
//////
//////        for(int i = 0; i < n; i++) {
//////            tuplesA.add(new Solver.Tuple(i, 1));
//////            tuplesB.add(new Solver.Tuple(i, -1));
//////        }
//////        constraints.add(new Solver.Constraint(tuplesA, k));
//////        constraints.add(new Solver.Constraint(tuplesB, -k));
//////
//////
//////        ArrayList<Boolean[]> solutions = Solver.solve(constraints, n);
//////
//////        ArrayList<int[]> results = new ArrayList<>();
//////
//////        for(Boolean[] solution: solutions) {
//////            int index = 0;
//////            int[] result = new int[k];
//////            for(int i = 0; i < solution.length; i++) {
//////                if(solution[i]) {
//////                    result[index] = i + 1;
//////                    index++;
//////                }
//////            }
//////            results.add(result);
//////        }
//////
//////        return results;
//////
//////
//////    }
////}
////
////class Solver {
////    //Copy your solver here
////
////
////    public static ArrayList<Boolean[]> solve(int[][] constraints) {
////
////
////
////        int n = constraints[0].length - 1;
////
////        Map<Integer, int[]> dlMap = new HashMap<>();
////
////        ArrayList<Boolean[]> solutions = new ArrayList<>();
////
////        Map<Integer, Integer> pastDecisions = new HashMap<>();
////        int dl = 0;
////
////        int[] decisionLevel0 = new int[n];
////        for(int i = 0; i < n; i++) {
////            decisionLevel0[i] = -1;
////        }
////
////        dlMap.put(0, decisionLevel0);
////
////        Map<Integer, ArrayList<Integer>> constraintsMap = new HashMap<>();
////
////        for(int i = 0; i < constraints.length; i++) {
////            for (int j = 0; j < n; j++) {
////                if(constraintsMap.containsKey(j)) {
////                    constraintsMap.get(j).add(i);
////                } else {
////                    constraintsMap.put(j, new ArrayList<>());
////                    constraintsMap.get(j).add(i);
////                }
////            }
////        }
////
////        while(true) {
////
////            Queue<Integer> changedDecisions = new LinkedList<>();
////            if(dl ==-1) {
////                break;
////            }
////
////            if(!dlMap.containsKey(dl)) {
////                int a = 2;
////            }
////
////            int[] currentDL = dlMap.get(dl).clone();
////            int decisionsLeft = 0;
////            for(int i = 0; i < n; i++) {
////                if(currentDL == null) {
////                    int a = 2;
////                }
////                if(currentDL[i] == -1) {
////                    decisionsLeft++;
////                }
////            }
////            if(decisionsLeft == 0) {
////                if(test(constraints, currentDL)) {
////
////                    Boolean[] solution = new Boolean[n];
////
////                    for (int i = 0; i < n; i++) {
////                        if (currentDL[i] == 1) {
////                            solution[i] = true;
////                        } else {
////                            solution[i] = false;
////                        }
////                    }
////
////                    solutions.add(solution);
////                }
////                dlMap.remove(dl);
////                if(!pastDecisions.containsKey(dl)) {
////                    return solutions;
////                }
////                int pastDecision = pastDecisions.remove(dl);
////
////                int[] pastMap = dlMap.remove(dl -1);
////                pastMap[pastDecision] = 0;
////                dlMap.put(dl-1, pastMap);
////                dl--;
////                continue;
////
////            }
////
////            dl++;
////
////            int decisionVariable = -1;
////            for(int i = 0; i < n; i++) {
////                if(currentDL[i] == -1) {
////                    decisionVariable = i;
////                    break;
////                }
////            }
////
////
////
////            pastDecisions.put(dl, decisionVariable);
////
////            currentDL[decisionVariable] = 1;
////            changedDecisions.add(decisionVariable);
////
////            int[][] currentConstraints = getConstraints(currentDL, constraints, n);
////
////
//////            propogate info
////            boolean ranIntoProblem = false;
////
////            while(!changedDecisions.isEmpty()) {
////                int changedDecision = changedDecisions.poll();
////
////                for(int i : constraintsMap.get(changedDecision)) {
////
////                    int count_negatives = 0;
////                    for(int j = 0; j < n; j++) {
////                        if(currentConstraints[i][j] == -1) {
////                            count_negatives++;
////                        }
////                    }
////
//////                not enough negatives
////                    if(count_negatives < -1 * currentConstraints[i][n]) {
////                        ranIntoProblem = true;
////                        break;
////                    }
////
////                    if(count_negatives == -1 * currentConstraints[i][n]) {
////                        for(int j = 0; j < n; j++) {
////                            if(currentConstraints[i][j] == 1) {
////                                changedDecisions.add(j);
////                                currentDL[j] = 0;
////                            } else if(currentConstraints[i][j] == -1) {
////                                changedDecisions.add(j);
////                                currentDL[j] = 1;
////                            }
////                        }
////
////                        currentConstraints = getConstraints(currentDL, constraints, n);
////                    }
////
////                }
////            }
////
////
////            if(ranIntoProblem) {
////                int pastDecision  = pastDecisions.remove(dl);
////                int[] pastDL = dlMap.remove(dl - 1);
////                pastDL[pastDecision] = 0;
////
////                int count_NotKnown = 0;
////                for(int i = 0; i < n; i++) {
////                    if(currentDL[i] == -1) {
////                        count_NotKnown++;
////                    }
////                }
////
////                dlMap.put(dl - 1, pastDL);
////                dl--;
////                continue;
////
////            } else {
////                dlMap.put(dl, currentDL);
////            }
////
////
////
////
////        }
////
////
////        return solutions;
////
////
////
////
////
////    }
////
////    private static boolean test(int[][] constraints, int[] currentDL) {
////
////        for(int i = 0; i < constraints.length; i++) {
////            int sum = 0;
////            for(int j = 0; j < constraints[0].length - 1; j++) {
////                sum += constraints[i][j] * currentDL[j];
////            }
////            if(sum > constraints[i][constraints[0].length - 1]) {
////                return false;
////            }
////
////        }
////        return true;
////
////    }
////
////    private static int[][] getConstraints(int currentDecisions[], int[][] constraints, int n) {
////
////        int[][] currentConstraints = new int[constraints.length][constraints[0].length];
////
////
////        //Making the new constraints table taking into account the decisions that were already made
////        for (int i = 0; i < constraints.length; i++) {
////
////            //all decisions subtract from the value of k for the corresponding constraint inequality.
////            int changeK = 0;
////            for (int j = 0; j < constraints[0].length - 1; j++) {
////                //if we have made a decision for that variable, we change the value of K.
////                //Otherwise we simply just make the constraints -1.
////                if (currentDecisions[j] != -1) {
////                    currentConstraints[i][j] = 0;
////                    changeK -= constraints[i][j] * currentDecisions[j];
////                } else {
////                    currentConstraints[i][j] = constraints[i][j];
////                }
////            }
////
////
////            currentConstraints[i][n] = constraints[i][n] + changeK;
////        }
////        return currentConstraints;
////    }}
////
//////import java.util.*;
//////
//////class Solution {
//////    /**
//////     * Returns the number of N-Queen solutions
//////     */
//////    public static int getNQueenSolutions(int n) {
//////        int k = n;
//////        int a = 0;
//////
//////        if(k % 2 == 0) {
//////            a = 1;
//////        }
//////
//////        int[][] constraints = new int[k * 6 + a][k * k + 1];
//////        for(int i = 0; i < k; i++) {
//////            for(int j = 0; j < k; j++) {
//////                constraints[i][i * k + j] = 1;
//////                constraints[i + k][ j * k +i] = 1;
//////
//////            }
//////            constraints[i][k * k] = 1;
//////            constraints[i + k][ k * k] = 1;
//////        }
//////
//////
//////        Set<Integer> edges = new HashSet<>();
//////
//////        for(int i = 0; i < k; i++) {
//////            edges.add(i);
//////            edges.add(i * k);
//////        }
//////
//////        Iterator iterator = edges.iterator();
//////        for(int i = 0; i < edges.size(); i++) {
//////            int j = (int) iterator.next();
//////            while(j < (k * k)) {
//////
//////                constraints[i + k * 2][j] = 1;
//////                if((j + 1) % k == 0) {
//////                    break;
//////                }
//////                j += k + 1;
//////
//////            }
//////            constraints[i + k * 2][k * k] = 1;
//////        }
//////
//////        Set<Integer> edges2 = new HashSet<>();
//////
////////        int[] edges2 = new int[]{1,2,3,4,5,6,7,15,23,31, 39, 47, 55};
//////        for(int i = 0; i < k; i++) {
//////            edges2.add(i);
//////            edges2.add(k * i - 1);
//////        }
//////        Iterator iterator2 = edges2.iterator();
//////
//////
//////        for(int i = 0; i < edges2.size(); i++) {
//////            int j = (int) iterator2.next();
//////            while(j < (k  * k) && j >= 0) {
//////
//////                constraints[i + k * 2 + edges.size()][j] = 1;
//////                if(j % k == 0) {
//////                    break;
//////                }
//////                j += k -1;
//////
//////            }
//////            constraints[i + k * 2 + edges.size()][k * k] = 1;
//////        }
//////
//////
//////        for(int i = 0; i < (k * k) + 1; i++) {
//////            constraints[constraints.length - 2][i] = 1;
//////            constraints[constraints.length - 1][i] = -1;
//////        }
//////        constraints[constraints.length - 2][k * k] = k;
//////        constraints[constraints.length - 1][k * k] = -k;
//////
//////        int answer = 0;
//////        if(k % 2 == 0) {
//////            for(int i = 0; i < k / 2; i++) {
//////                constraints[constraints.length - 3][i] = 1;
//////            }
//////            constraints[constraints.length - 3][k * k] = 0;
//////            ArrayList<Boolean[]> solutions = Solver.solve(constraints);
//////            answer = solutions.size() * 2;
//////
//////        } else {
//////            ArrayList<Boolean[]> solutions = Solver.solve(constraints);
//////            answer = solutions.size();
//////        }
//////        return answer;
//////
//////
//////    }
//////}
//////
//////class Solver {
//////    //Copy your solver here
//////
//////
//////    public static ArrayList<Boolean[]> solve(int[][] constraints) {
//////
//////
//////
//////        int n = constraints[0].length - 1;
//////
//////        Map<Integer, int[]> dlMap = new HashMap<>();
//////
//////        ArrayList<Boolean[]> solutions = new ArrayList<>();
//////
//////        Map<Integer, Integer> pastDecisions = new HashMap<>();
//////        int dl = 0;
//////
//////        int[] decisionLevel0 = new int[n];
//////        for(int i = 0; i < n; i++) {
//////            decisionLevel0[i] = -1;
//////        }
//////
//////        dlMap.put(0, decisionLevel0);
//////
//////        Map<Integer, ArrayList<Integer>> constraintsMap = new HashMap<>();
//////
//////        for(int i = 0; i < constraints.length; i++) {
//////            for (int j = 0; j < n; j++) {
//////                if(constraintsMap.containsKey(j)) {
//////                    constraintsMap.get(j).add(i);
//////                } else {
//////                    constraintsMap.put(j, new ArrayList<>());
//////                    constraintsMap.get(j).add(i);
//////                }
//////            }
//////        }
//////
//////        while(true) {
//////
//////            Queue<Integer> changedDecisions = new LinkedList<>();
//////            if(dl ==-1) {
//////                break;
//////            }
//////
//////            if(!dlMap.containsKey(dl)) {
//////                int a = 2;
//////            }
//////
//////            int[] currentDL = dlMap.get(dl).clone();
//////            int decisionsLeft = 0;
//////            for(int i = 0; i < n; i++) {
//////                if(currentDL == null) {
//////                    int a = 2;
//////                }
//////                if(currentDL[i] == -1) {
//////                    decisionsLeft++;
//////                }
//////            }
//////            if(decisionsLeft == 0) {
//////                if(test(constraints, currentDL)) {
//////
//////                    Boolean[] solution = new Boolean[n];
//////
//////                    for (int i = 0; i < n; i++) {
//////                        if (currentDL[i] == 1) {
//////                            solution[i] = true;
//////                        } else {
//////                            solution[i] = false;
//////                        }
//////                    }
//////
//////                    solutions.add(solution);
//////                }
//////                dlMap.remove(dl);
//////                if(!pastDecisions.containsKey(dl)) {
//////                    return solutions;
//////                }
//////                int pastDecision = pastDecisions.remove(dl);
//////
//////                int[] pastMap = dlMap.remove(dl -1);
//////                pastMap[pastDecision] = 0;
//////                dlMap.put(dl-1, pastMap);
//////                dl--;
//////                continue;
//////
//////            }
//////
//////            dl++;
//////
//////            int decisionVariable = -1;
//////            for(int i = 0; i < n; i++) {
//////                if(currentDL[i] == -1) {
//////                    decisionVariable = i;
//////                    break;
//////                }
//////            }
//////
//////
//////
//////            pastDecisions.put(dl, decisionVariable);
//////
//////            currentDL[decisionVariable] = 1;
//////            changedDecisions.add(decisionVariable);
//////
//////            int[][] currentConstraints = getConstraints(currentDL, constraints, n);
//////
//////
////////            propogate info
//////            boolean ranIntoProblem = false;
//////
//////            while(!changedDecisions.isEmpty()) {
//////                int changedDecision = changedDecisions.poll();
//////
//////                for(int i : constraintsMap.get(changedDecision)) {
//////
//////                    int count_negatives = 0;
//////                    for(int j = 0; j < n; j++) {
//////                        if(currentConstraints[i][j] == -1) {
//////                            count_negatives++;
//////                        }
//////                    }
//////
////////                not enough negatives
//////                    if(count_negatives < -1 * currentConstraints[i][n]) {
//////                        ranIntoProblem = true;
//////                        break;
//////                    }
//////
//////                    if(count_negatives == -1 * currentConstraints[i][n]) {
//////                        for(int j = 0; j < n; j++) {
//////                            if(currentConstraints[i][j] == 1) {
//////                                changedDecisions.add(j);
//////                                currentDL[j] = 0;
//////                            } else if(currentConstraints[i][j] == -1) {
//////                                changedDecisions.add(j);
//////                                currentDL[j] = 1;
//////                            }
//////                        }
//////
//////                        currentConstraints = getConstraints(currentDL, constraints, n);
//////                    }
//////
//////                }
//////            }
//////
//////
//////            if(ranIntoProblem) {
//////                int pastDecision  = pastDecisions.remove(dl);
//////                int[] pastDL = dlMap.remove(dl - 1);
//////                pastDL[pastDecision] = 0;
//////
//////                int count_NotKnown = 0;
//////                for(int i = 0; i < n; i++) {
//////                    if(currentDL[i] == -1) {
//////                        count_NotKnown++;
//////                    }
//////                }
//////
//////                dlMap.put(dl - 1, pastDL);
//////                dl--;
//////                continue;
//////
//////            } else {
//////                dlMap.put(dl, currentDL);
//////            }
//////
//////
//////
//////
//////        }
//////
//////
//////        return solutions;
//////
//////
//////
//////
//////
//////    }
//////
//////    private static boolean test(int[][] constraints, int[] currentDL) {
//////
//////        for(int i = 0; i < constraints.length; i++) {
//////            int sum = 0;
//////            for(int j = 0; j < constraints[0].length - 1; j++) {
//////                sum += constraints[i][j] * currentDL[j];
//////            }
//////            if(sum > constraints[i][constraints[0].length - 1]) {
//////                return false;
//////            }
//////
//////        }
//////        return true;
//////
//////    }
//////
//////    private static int[][] getConstraints(int currentDecisions[], int[][] constraints, int n) {
//////
//////        int[][] currentConstraints = new int[constraints.length][constraints[0].length];
//////
//////
//////        //Making the new constraints table taking into account the decisions that were already made
//////        for (int i = 0; i < constraints.length; i++) {
//////
//////            //all decisions subtract from the value of k for the corresponding constraint inequality.
//////            int changeK = 0;
//////            for (int j = 0; j < constraints[0].length - 1; j++) {
//////                //if we have made a decision for that variable, we change the value of K.
//////                //Otherwise we simply just make the constraints -1.
//////                if (currentDecisions[j] != -1) {
//////                    currentConstraints[i][j] = 0;
//////                    changeK -= constraints[i][j] * currentDecisions[j];
//////                } else {
//////                    currentConstraints[i][j] = constraints[i][j];
//////                }
//////            }
//////
//////
//////            currentConstraints[i][n] = constraints[i][n] + changeK;
//////        }
//////        return currentConstraints;
//////    }
//////}
//////
//////// fdasfa
//////
//////
