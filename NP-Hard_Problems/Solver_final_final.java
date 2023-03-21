


import java.util.ArrayList;
import java.util.HashMap;
import java.util.*;
import java.math.*;


public class Solver_final_final {



    /**
     * Returns the filled in sudoku grid.
     * @param grid the partially filled in grid. unfilled positions are -1.
     * @return the fully filled sudoku grid.
     */
    public static int[][] solve(int[][] grid) {
        //Call your solver here

        HashMap<Integer, ArrayList<Integer>> domainMap = new HashMap<>();
        ArrayList<Integer> domain = new ArrayList<Integer>();

        for(int i = 1; i <= n; i++){
            domain.add(i);
        }

        for( int i = 0; i < n; i++){
            domainMap.put(i, (ArrayList) domain.clone());
        }

        ArrayList<Solver.Constraint> constraints = new ArrayList<Solver.Constraint>();
        for(int i = 0; i < n; i++){
            for( int j = i + 1; j < n; j++){
                constraints.add(new Solver.Constraint(i, j, 2));
            }
        }

        
        
        /////


        int sqrt = (int) Math.sqrt(n);


        for(int blockRow = 0; blockRow < sqrt; blockRow++){
            for(int blockCol = 0; blockCol < sqrt; blockCol++){


                int block = blockRow* sqrt *n + blockCol*sqrt;

                for(int row = 0; row < sqrt;row++) {
                    for (int col = 0; col < sqrt; col++) {
                        int squareIndex = block + row*n +col;
                        for(int row2 = 0; row2 < sqrt;row2++) {
                            for (int col2 = 0; col2 < sqrt; col2++){
                                int neighbourIndex = block + row2*n + col2;
                                if(squareIndex != neighbourIndex){
                                    constraints.add(new Solver.Constraint(squareIndex, neighbourIndex, 2));

                                }

                            }
                        }



                    }
                }

            }

        }
        
        /////
        
        
        
        

        //ArrayList<Boolean[]> solution = Solver.solve( new int[1][n+ 1]);
        List solution = Solver.solve( constraints, domainMap, n);

        //Call your solver here
        ArrayList<int[]> finalsolution = new ArrayList<>();

        //Integer not int array
        return solution;

        ///////////////

        int n = grid.length;
        int sqrt = Math.sqrt(n);
        HashMap<Integer, ArrayList<Integer>> domainMap = new HashMap<>();
        ArrayList<Integer> domain = new ArrayList<Integer>();
        for(int i = 1; i <= n; i++){
            domain.add(i);
        }
        //The method solve(ArrayList<Solver.Constraint>, HashMap<Integer,ArrayList<Integer>>, int) (ArrayList<Solver.Constraint>, Map<Integer,ArrayList>, int)


        for(int i = 0; i < n;i++){
            for(int j = 0; j < n;j++){
                if(grid[i][j] != -1){
                    domainMap.put( i * n + j, new ArrayList<>(Arrays.asList(grid[i][j])));
                } else {
                    domainMap.put( i * n + j, (ArrayList) domain.clone());
                }
            }
        }

        ArrayList<Solver.Constraint> constraints = new ArrayList<Solver.Constraint>();


        for(int i = 0; i < n; i++){
            for( int j = 0; j < n; j++){
                for( int k = j + 1; k < n; k++){
                    //rows not equal
                    constraints.add(new Solver.Constraint(i*n + j,i*n + k, 2));
                    // columns not equal
                    constraints.add(new Solver.Constraint(i + j * n, i + k*n, 2));
                }
            }
        }
        for(int i = 0; i < sqrt; i++){
            for(int j = 0; j < sqrt; j++)
        }

        ArrayList<int[]> solution = Solver.solve( new ArrayList<Solver.Constraint>(), domainMap, n);
        int[] firstSolution = solution.get(0);

        int[][] finalGrid = new int[n][n];

        int index = 0;
        for(int i = 0; i < n; i++){
            for( int j = 0; j < n; j++){
                finalGrid[i][j] = firstSolution[index];
                index++;
            }
        }








        return grid;
    }
//    public static List<int[]> getSubsets(int n) {
//        Map<Integer, ArrayList> domainMap = new HashMap<>();
//        ArrayList<Integer> domain = new ArrayList<Integer>();
//
//        for(int i = 1; i <= n; i++){
//            domain.add(i);
//        }
//
//        for( int i = 0; i < n; i++){
//            domainMap.put(i, (ArrayList) domain.clone());
//        }
//
//        ArrayList<Constraint> constraints = new ArrayList<Constraint>();
//        for(int i = 0; i < n; i++){
//            for( int j = i + 1; j < n; j++){
//                constraints.add(new Constraint(i, j, 2));
//            }
//        }
//
//        //ArrayList<Boolean[]> solution = Solver.solve( new int[1][n+ 1]);
//        List solution = Solver.solve( constraints, domainMap, n);
//
//        //Call your solver here
//        ArrayList<int[]> finalsolution = new ArrayList<>();
//
//        //Integer not int array
//        return solution;
//    }

    public static ArrayList<Integer[]> solve(ArrayList<Constraint> constraints, HashMap<Integer, ArrayList<Integer>> domain , int n) {

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