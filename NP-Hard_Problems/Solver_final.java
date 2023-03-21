import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Solver_final {

    public static void main(String[] args) {


        int n = 3;
        int k = 2;

        int[][] constraints = new int[2 * k][n * k + 1];

        for(int i = 0; i < k; i++) {
            for(int j = 0; j < n ; j++) {
                constraints[i][i * n + j] = 1;
                constraints[i + k][i * n + j] = -1;

            }
            constraints[i][n * k] = 1;
            constraints[i + k][n * k] = -1;


        }

        ArrayList<Boolean[]> solutions = solve(constraints);
        ArrayList<int[]> results = new ArrayList<>();


        for(Boolean[] solution: solutions) {
            int[] result = new int[k];

            for(int i = 0; i < k; i++) {
                for(int j =0; j <n; j++) {
                    if(solution[i *n + j]) {
                        result[i] = j + 1;
                    }
                }
            }
            results.add(result);


        }






        int a = 2;
    }


    public static ArrayList<Boolean[]> solve(int[][] constraints) {

        int n = constraints[0].length - 1;

        Map<Integer, int[]> dlMap = new HashMap<>();

        ArrayList<Boolean[]> solutions = new ArrayList<>();

        Map<Integer, Integer> pastDecisions = new HashMap<>();
        int dl = 0;

        int[] decisionLevel0 = new int[n];
        for(int i = 0; i < n; i++) {
            decisionLevel0[i] = -1;
        }

        dlMap.put(0, decisionLevel0);
        boolean failed = false;

        while(true) {
            if(dl ==-1) {
                break;
            }

            if(!dlMap.containsKey(dl)) {
                int a = 2;
            }

            int[] currentDL = dlMap.get(dl).clone();
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
                dlMap.remove(dl);
                if(!pastDecisions.containsKey(dl)) {
                    return solutions;
                }
                int pastDecision = pastDecisions.remove(dl);

                int[] pastMap = dlMap.remove(dl -1);
                pastMap[pastDecision] = 1;
                dlMap.put(dl-1, pastMap);
                dl--;
                continue;

            }

            dl++;

            int decisionVariable = -1;
            for(int i = 0; i < n; i++) {
                if(currentDL[i] == -1) {
                    decisionVariable = i;
                    break;
                }
            }



            pastDecisions.put(dl, decisionVariable);
            currentDL[decisionVariable] = 0;
            int[][] currentConstraints = getConstraints(currentDL, constraints, n);


//            propogate info
            boolean ranIntoProblem = false;
            for(int i = 0; i < currentConstraints.length; i++) {

                int count_negatives = 0;
                for(int j = 0; j < n; j++) {
                    if(currentConstraints[i][j] == -1) {
                        count_negatives++;
                    }
                }

//                not enough negatives
                if(count_negatives < -1 * currentConstraints[i][n]) {
                    ranIntoProblem = true;
                    break;
                }

                if(count_negatives == -1 * currentConstraints[i][n]) {
                    boolean propogatedInformation = false;
                    for(int j = 0; j < n; j++) {
                        if(currentConstraints[i][j] == 1) {
                            propogatedInformation = true;
                           currentDL[j] = 0;
                        } else if(currentConstraints[i][j] == -1) {
                            propogatedInformation = true;
                            currentDL[j] = 1;
                        }
                    }

                    currentConstraints = getConstraints(currentDL, constraints, n);
                    if(propogatedInformation) {
                        i = -1;
                    }
                }

            }

            if(ranIntoProblem) {
                failed = true;
                int pastDecision  = pastDecisions.remove(dl);
                int[] pastDL = dlMap.remove(dl - 1);
                pastDL[pastDecision] = 1;

                int count_NotKnown = 0;
                for(int i = 0; i < n; i++) {
                    if(currentDL[i] == -1) {
                        count_NotKnown++;
                    }
                }

                dlMap.put(dl - 1, pastDL);
                dl--;
                continue;

            } else {
                dlMap.put(dl, currentDL);
                failed = false;
            }




        }


        return solutions;





    }

    private static boolean test(int[][] constraints, int[] currentDL) {

        for(int i = 0; i < constraints.length; i++) {
            int sum = 0;
            for(int j = 0; j < constraints[0].length - 1; j++) {
                sum += constraints[i][j] * currentDL[j];
            }
            if(sum > constraints[i][constraints[0].length - 1]) {
                return false;
            }

        }
        return true;

    }

    private static int[][] getConstraints(int currentDecisions[], int[][] constraints, int n) {
        int[][] currentConstraints = new int[constraints.length][constraints[0].length];


        //Making the new constraints table taking into account the decisions that were already made
        for (int i = 0; i < constraints.length; i++) {

            //all decisions subtract from the value of k for the corresponding constraint inequality.
            int changeK = 0;
            for (int j = 0; j < constraints[0].length - 1; j++) {
                //if we have made a decision for that variable, we change the value of K.
                //Otherwise we simply just make the constraints -1.
                if (currentDecisions[j] != -1) {
                    currentConstraints[i][j] = 0;
                    changeK -= constraints[i][j] * currentDecisions[j];
                } else {
                    currentConstraints[i][j] = constraints[i][j];
                }
            }


            currentConstraints[i][n] = constraints[i][n] + changeK;
        }
        return currentConstraints;
    }


}
