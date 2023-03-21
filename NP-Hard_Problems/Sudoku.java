//import java.util.*;
//
//public class Sudoku {
//
//    /**
//     * Returns the number of N-Queen solutions
//     */
//    public static int getNSudokuSolutions(int n) {
//        int k = n;
//        int a = 0;
//
//        if(k % 2 == 0) {
//            a = 1;
//        }
//
//        int[][] constraints = new int[n * n + 3 * n][n * n * n + 1];
//
//        // uniqueness
//        for(int i = 0; i < n * n;i++){
//            for(int j = 0; j < n ;j++){
//                constraints[i][i * n + j] = 1;
//            }
//            constraints[i][n*n*n] = 1;
//        }
//
//        int afterUniqueness = n * n;
//
//        for(int i = 0; i < n; i++) {
//
//
//            // rows contains n numbers
//            for(int j = 0; j < n * n; j++) {
//
//                constraints[afterUniqueness + i][i * n + j] = 1;
//
//
//
//
//            }
//            constraints[afterUniqueness + i][ n * n * n] = n;
//
//            // columns contains n numbers
//            for(int j = 0; j < n * n * n; j = j + n) {
//
//                constraints[afterUniqueness + n + i][i * n + j] = 1;
//
//
//            }
//            constraints[afterUniqueness + n + i][ n * n * n] = n;
//
//        }
//
//
//
//
//
//
//
//
////
////        for(int i = 0; i < k; i++) {
////            for(int j = 0; j < k; j++) {
////                constraints[i][i * k + j] = 1;
////                constraints[i + k][ j * k +i] = 1;
////
////            }
////            constraints[i][k * k] = 1;
////            constraints[i + k][ k * k] = 1;
////        }
////
////
////        Set<Integer> edges = new HashSet<>();
////
////        for(int i = 0; i < k; i++) {
////            edges.add(i);
////            edges.add(i * k);
////        }
////
////        Iterator iterator = edges.iterator();
////        for(int i = 0; i < edges.size(); i++) {
////            int j = (int) iterator.next();
////            while(j < (k * k)) {
////
////                constraints[i + k * 2][j] = 1;
////                if((j + 1) % k == 0) {
////                    break;
////                }
////                j += k + 1;
////
////            }
////            constraints[i + k * 2][k * k] = 1;
////        }
////
////        Set<Integer> edges2 = new HashSet<>();
////
//////        int[] edges2 = new int[]{1,2,3,4,5,6,7,15,23,31, 39, 47, 55};
////        for(int i = 0; i < k; i++) {
////            edges2.add(i);
////            edges2.add(k * i - 1);
////        }
////        Iterator iterator2 = edges2.iterator();
////
////
////        for(int i = 0; i < edges2.size(); i++) {
////            int j = (int) iterator2.next();
////            while(j < (k  * k) && j >= 0) {
////
////                constraints[i + k * 2 + edges.size()][j] = 1;
////                if(j % k == 0) {
////                    break;
////                }
////                j += k -1;
////
////            }
////            constraints[i + k * 2 + edges.size()][k * k] = 1;
////        }
////
////
////        for(int i = 0; i < (k * k) + 1; i++) {
////            constraints[constraints.length - 2][i] = 1;
////            constraints[constraints.length - 1][i] = -1;
////        }
////        constraints[constraints.length - 2][k * k] = k;
////        constraints[constraints.length - 1][k * k] = -k;
////
////        int answer = 0;
////        if(k % 2 == 0) {
////            for(int i = 0; i < k / 2; i++) {
////                constraints[constraints.length - 3][i] = 1;
////            }
////            constraints[constraints.length - 3][k * k] = 0;
////            ArrayList<Boolean[]> solutions = Solver.solve(constraints);
////            answer = solutions.size() * 2;
////
////        } else {
////            ArrayList<Boolean[]> solutions = Solver.solve(constraints);
////            answer = solutions.size();
////        }
//
//        return answer;
//
//
//    }
//}
//
//
//class Solver {
//    //Copy your solver here
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
//// fdasfa
//
//
