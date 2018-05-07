
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.Random;

public class board 
{

	public board()
	{
		boardstate = new int[]{3,3,3,3,3,3,0,3,3,3,3,3,3,0};
	}
	
	int[] getboard()
	{
		return boardstate;
	}
	
	void printboard(int[] board)
	{
		int i = 0;
		while(i < 6) //print north side (in reverse)
		{
			System.out.print(board[12-i] + " ");
			i++;
		}
		
		System.out.println();
		System.out.println(board[13] + "         " + board[6]); //north goal
		
		i = 0;
		while(i < 6) //print south side
		{
			System.out.print(board[i] + " ");
			i++;
		}
		System.out.println();
		System.out.println();
	}
	
	boolean victory(int[] boardstate)
	{
		int i = 0;
	    southsidesum = 0;
	    northsidesum = 0;
	    
		while(i < 6) //check for victory
		{
			southsidesum = southsidesum + boardstate[i];
			northsidesum = northsidesum + boardstate[i+7];
			i++;
		}
		int totalSouthSum = southsidesum + boardstate[6];
		int totalNorthSum = northsidesum + boardstate[13];
		if(southsidesum == 0 || northsidesum == 0)
		{
			
			if(totalSouthSum > totalNorthSum)
			{
				System.out.println("South victory!");
				System.out.println("North total: " + totalNorthSum + "\nSouth total: " + totalSouthSum );
				totalSouthWins++;
				southWins++;
				return true;
			}
			else if (totalSouthSum < totalNorthSum)
			{
				System.out.println("North victory!");
				System.out.println("North total: " + totalNorthSum + "\nSouth total: " + totalSouthSum );
				totalNorthWins++;
				northWins++;
				return true;
			}
			else
			{
				System.out.println("Tie!");
				ties++;
				return true;
			}
		}
		//System.out.println("South total: " + totalSouthSum + "\nNorth total: " + totalNorthSum);
		return false;
	}
	
	boolean illegalMove(int p, boolean SMove, int[] board)
	{
		if(board[0] == -1)
		{
			return true;
		}
		
		else
		{
			int[] newBoard = Arrays.copyOf(board, board.length);
			if (p == 6 || p == 13)
			{
				System.out.println("Cannot sow goal pits");
				return true;
			}
			else if (p < 0 || p > 13)
			{
				System.out.println("Pit out of bounds");
				return true;
			}
			else if (newBoard[p] == 0)
			{
				System.out.println("Cannot sow empty pits");
				return true;
			}
			else if (SMove == true && p > 6)
			{
				System.out.println("Cannot sow from North's pits");
				return true;
			}
			else if (SMove == false && p < 7)
			{
				System.out.println("Cannot sow from South's pits");
				return true;
			}
			return false;
		}
	}
	
	void checkForCaptures(int pit, int seeds, int numberOfSows)
	{
		int lastpit = (pit+numberOfSows) % 14;
		if (boardstate[lastpit] == 1) //check for captures
		{
			if (lastpit < 6 && southturn)
			{
				seeds = boardstate[lastpit+2*(6-lastpit)];
				if(seeds != 0)
				{
					boardstate[lastpit+2*(6-lastpit)] = 0;
					boardstate[6] = boardstate[6] + seeds;
					System.out.println("South captured!");
				}
			}
			else if (lastpit > 6 && !southturn && lastpit < 13)
			{
				seeds = boardstate[lastpit+2*(6-lastpit)];
				if(seeds != 0)
				{
					boardstate[lastpit+2*(6-lastpit)] = 0;
					boardstate[13] = boardstate[13] + seeds;
					System.out.println("North captured!");
				}
			}
		}
	}
	
	int[] computerCheckForCaptures(boolean south,int pit, int seeds, int numberOfSows, int[] board)
	{
		int lastpit = (pit+numberOfSows) % 14;
		if (board[lastpit] == 1) //check for captures
		{
			if (lastpit < 6 && south)
			{
				seeds = board[lastpit+2*(6-lastpit)];
				if(seeds != 0)
				{
					board[lastpit+2*(6-lastpit)] = 0;
					board[6] = board[6] + seeds;
					//System.out.println("GENERATED South captured!");
				}
			}
			else if (lastpit > 6 && !south && lastpit < 13)
			{
				seeds = board[lastpit+2*(6-lastpit)];
				if(seeds != 0)
				{
					board[lastpit+2*(6-lastpit)] = 0;
					board[13] = board[13] + seeds;
					//System.out.println("GENERATED North captured!");
				}
			}
		}
		return board;
	}
	
	int takeUserInput()
	{
		int i = 0;
		do 	//take in user input 
		{
			System.out.print(northPrompt);
			i = input.nextInt();
		}while(illegalMove(i,southturn,boardstate));
		return i;
	}
	
	List<int[]> generateMoves(int[] currBoard,boolean southturn)
	{
		List<int[]> newMoves = new ArrayList<int[]>();
		int[] invalid = {-1};
		if(southturn)	//generate south's moves
		{
			for(int i = 0; i < 6; i++)	//for all of south's pits
			{
				if(!illegalMove(i, southturn,currBoard))	//if it is a valid move, add it to the list
				{
					newMoves.add(computerSow(i, currBoard, southturn));
				}
				else
				{
					newMoves.add(invalid);
				}
			}
		}
		else if (!southturn)	//generate north's moves 
		{
			for(int i = 7; i < 13; i++)	//for all of north's pits 
			{
				if(!illegalMove(i,southturn, currBoard))	
				{
					newMoves.add(computerSow(i, currBoard, southturn));
				}
				else
				{
					newMoves.add(invalid);
				}
			}
		}
		return newMoves;
	}
	
	void sow(int pit)
	{	
		if (!illegalMove(pit,southturn,boardstate))
		{
			//System.out.println("Sowing pit " + pit);	
			
			int seeds = boardstate[pit];
			boardstate[pit] = 0;
			
			int i = 0;		
			int j = 0;
			boolean done = false;
			while(!done) //sow around the board
			{
				if(southturn && ((pit+i+1) % 14) != 13)	//skip North's goal pit 
				{
					if(((pit+i+1) % 14) != 13)
						boardstate[(pit+i+1) % 14]++;
					j++;
					if(j > (seeds - 1))
						done = true;
				}
				else if(!southturn && ((pit+i+1) % 14) != 6) //skip South's goal pit 
				{
					boardstate[(pit+i+1) % 14]++;	
					j++;
					if(j > (seeds - 1))
						done = true;
				}
				i++;
			}				
			checkForCaptures(pit, seeds, i);		
		}
	}
	
	int[] computerSow(int pit, int[] board, boolean south) 	//generate a new move and its resulting board 
	{
		int[] newBoard = Arrays.copyOf(board, board.length);
			
		int seeds = newBoard[pit];
		newBoard[pit] = 0;
			
		int i = 0;
		int j = 0;
		boolean done = false;
		while(!done) //sow around the board
		{
			if(south && ((pit+i+1) % 14) != 13)	//skip North's goal pit 
			{
				if(((pit+i+1) % 14) != 13)
					newBoard[(pit+i+1) % 14]++;
				j++;
				if(j > (seeds - 1))
					done = true;
			}
			else if(!south && ((pit+i+1) % 14) != 6) //skip South's goal pit 
			{
				newBoard[(pit+i+1) % 14]++;	
				j++;
				if(j > (seeds - 1))
					done = true;
			}
			i++;
		}		
		newBoard = computerCheckForCaptures(south,pit, seeds, i, newBoard);
		return newBoard; 
	}
	
	int[] minimax(int depth, boolean south, int[] board)
	{
		int i = 0;
		int bestVal = 0;
		int currVal = 0; 
		int pit = -1;
		List<int[]> newMoves = generateMoves(board,south);
		if(depth == 0)
		{
			leaves++;
			//bestVal = evaluateBoard(board,south);
		}
		else 
		{
			if(south)	//SOUTH'S TURN 
			{
				bestVal = Integer.MIN_VALUE;
				pit = -1;
				i = 0;
				for(int[] move : newMoves)
				{
					if(move != null)
					{
						currVal = minimax(depth - 1,!south,move)[0];
						if(currVal > bestVal)
						{
							bestVal = currVal;
							pit = i;
						}	
					}
					i++;
				}
			}
			else		//NORTH'S TURN 
			{
				bestVal = Integer.MAX_VALUE;
				pit = -1;
				i = 0;
				for(int[] move : newMoves)
				{
					if(move != null)
					{
						currVal = minimax(depth - 1,south,move)[0];
						if(currVal < bestVal)
						{
							bestVal = currVal;
							pit = i + 7;
						}
					}
					i++;
				}
			}
		}
		return new int[] {bestVal, pit};
	}
	
	double[] minimaxAlphaBetaPruining(int depth, boolean south, int[] board, double alpha, double beta, double[] nWeights)
	{	
		int i = 0;
		double currVal = 0; 
		double pit = -1;
		List<int[]> newMoves = generateMoves(board,south);
		boolean allInvalid = true;
		
		for(int j = 0; j < 6; j++)
		{
			if(newMoves.get(j)[0] != -1)
			{
				allInvalid = false;
				break;
			}
		}
		if(depth == 0 || allInvalid)
		{
			leaves++;
			currVal = evaluateBoard2(board, south);
			return new double[] {currVal, pit};
		}
		else 
		{
			if(south)	//SOUTH'S TURN 
			{
				i = 0;
				for(int[] move : newMoves)
				{
					if(move[0] != -1)
					{
						currVal =  minimaxAlphaBetaPruining(depth - 1,!south,move,alpha, beta, nWeights)[0];
						if(currVal > alpha)
						{
							alpha = currVal;
							pit = i;
						}	
						if(beta <= alpha) break;
					}
					i++;
				}
				return new double[] {alpha, pit};
			}
			else		//NORTH'S TURN 
			{
				//currVal = Integer.MAX_VALUE;
				//pit = -1;
				i = 0;
				for(int[] move : newMoves)
				{
					if(move[0] != -1)
					{
						currVal =  minimaxAlphaBetaPruining(depth - 1,!south,move, alpha, beta, nWeights)[0];
						if(currVal < beta)
						{
							beta = currVal;
							pit = i + 7;
						}
						if(beta <= alpha) break;
					}
					i++;
				}
					return new double[] {beta, pit};
			}
		}	
	}
	
	double[] minimaxAlphaBetaPruining2(int depth, boolean south, int[] board, double alpha, double beta, double[] nWeights, double[] sWeights)
	{	
		int i = 0;
		double currVal = 0; 
		double pit = -1;
		List<int[]> newMoves = generateMoves(board,south);
		boolean allInvalid = true;
		
		for(int j = 0; j < 6; j++)
		{
			if(newMoves.get(j)[0] != -1)
			{
				allInvalid = false;
				break;
			}
		}
		if(depth == 0 || allInvalid)
		{
			leaves++;
			currVal = evaluateBoard2(board,south);
			return new double[] {currVal, pit};
		}
		else 
		{
			if(south)	//SOUTH'S TURN 
			{
				i = 0;
				for(int[] move : newMoves)
				{
					if(move[0] != -1)
					{
						currVal =  minimaxAlphaBetaPruining2(depth - 1,!south,move,alpha, beta, nWeights, sWeights)[0];
						if(currVal > alpha)
						{
							alpha = currVal;
							pit = i;
						}	
						else if(currVal == alpha)
						{
							Random r = new Random();	
							if(r.nextInt(2) == 1)
							{
								pit = i;
							}
							
						}
						if(beta <= alpha) break;
					}
					i++;
				}
				return new double[] {alpha, pit};
			}
			else		//NORTH'S TURN 
			{
				//currVal = Integer.MAX_VALUE;
				//pit = -1;
				i = 0;
				for(int[] move : newMoves)
				{
					if(move[0] != -1)
					{
						currVal =  minimaxAlphaBetaPruining2(depth - 1,!south,move, alpha, beta, nWeights, sWeights)[0];
						if(currVal < beta)
						{
							beta = currVal;
							pit = i + 7;
						}
						else if(currVal == beta)
						{
							Random r = new Random();	
							if(r.nextInt(2) == 1)
							{
								pit = i + 7;
							}
						}
						if(beta <= alpha) break;
					}
					i++;
				}
					return new double[] {beta, pit};
			}
		}	
	}
	
	double evaluateBoard(int[] board, boolean south, double[] nWeights, double[] sWeights)
	{
		double fitness = 0;
		double pit1 = 0;
		double wpit1 = 0;
		double pit2 = 0;
		double wpit2 = 0;
		double pit3 = 0;
		double wpit3 = 0;
		double goal = 0;
		double wgoal = 0;
		double empty = 0;
		double wempty = 0;
		double highest = 0;
		double whighest = 0;
		double capture = 0;
		double wcapture = 0;
		double sum = 0;
		double wsum = 0;
		
		if (south == true)
		{
			wgoal = sWeights[0];
			wempty = sWeights[1];
			whighest = sWeights[2];
			wcapture = sWeights[3];
			//goal pit
			goal += board[6];
			fitness += goal*wgoal;
			//# of empty pits
			for(int i = 0; i < 6; i++)
			{
				if ((double)board[i] == 0)
					{
						empty += 1;
					}
				
				if((double)board[i] > highest)
				{
					highest = (double)board[i];
				}
				
				if((double)board[i+2*(6-i)] == 0)
				{
					capture += (double)board[i];
				}
			}
			fitness += empty*wempty + highest*whighest + capture*wcapture;

		}
			else
			{
				wgoal = nWeights[0];
				wsum = nWeights[1];
				whighest = nWeights[2];
				wcapture = nWeights[3];
				wpit1 = nWeights[4];
				wpit2 = nWeights[5];
				wpit3 = nWeights[6];
				
				pit1 += board[10];
				pit2 += board[11];
				pit3 += board[12];
				goal += board[13];
				
				fitness += goal*wgoal + pit1*wpit1 + pit2*wpit2 + pit3*wpit3;
				
				fitness += empty * wempty;
				//highest pit
				for(int i = 7; i < 13; i++)
				{
					if ((double)board[i] == 0)
					{
						empty += 1;
					}
		
					if((double)board[i] > highest)
					{
						highest = (double)board[i];
					}
				
					if((double)board[i - 2*(i-6)] == 0 && (double)board[i] > 3)
					{
						capture += (double)board[i];
					}
					sum += board[i];
				}
		
				fitness += sum*wsum + empty*wempty + highest*whighest + capture*wcapture;
			}
			return roundToDecimals(fitness);
	}
	
	double evaluateBoard2(int[] board, boolean south)
	{
		int i = 0;

	    int southSum = 0;
	    int northSum = 0;
		while(i < 6) //sum both sides 
		{
			southSum = southSum + board[i];
			northSum = northSum + board[i+7];
			i++;
		}
		if(south)
		{
			return ((southSum + board[6]) - (northSum + board[13]));
			
		}
		else
		{
			return ((northSum + board[13]) - (southSum + board[13]));
		}	
	}
	
	double evaluateBoard3(int[] board, double[] nWeights, boolean south)
	{
		double wgoal = nWeights[0];
		double wsum = nWeights[1];
		double wcapture = nWeights[2];
		double wpit1 = nWeights[3];
		double wpit2 = nWeights[4];
		double wpit3 = nWeights[5];
		
		double pit1 = 0;
		double pit2 = 0;
		double pit3 = 0;
		double goal = 0;
		double sum = 0;
		double capture = 0;
		double fitness = 0;
		
		if(south)
		{
			pit1 = board[4];
			pit2 = board[5];
			pit3 = board[6];
			goal = board[7];
			for(int i = 0; i < 7; i++)
			{
				if((double)board[i - 2*(i-6)] == 0 && (double)board[i] > 3)
				{
					capture += (double)board[i];
				}
				sum += board[i];
			}
		}
		
		else
		{
			pit1 = board[10];
			pit2 = board[11];
			pit3 = board[12];
			goal = board[13];
			for(int i = 7; i < 13; i++)
			{
				if((double)board[i - 2*(i-6)] == 0 && (double)board[i] > 3)
				{
					capture += (double)board[i];
				}
				sum += board[i];
			}
		}
		
		fitness += goal*wgoal + pit1*wpit1 + pit2*wpit2 + pit3*wpit3 + sum*wsum + capture*wcapture;	
		return roundToDecimals(fitness);
	}
	
	void play(double[] nWeights)
	{	
		do
		{
			southturn = true; //south's turn

			leaves = 0;
			double[] southMove = minimaxAlphaBetaPruining(17,southturn,Arrays.copyOf(boardstate, boardstate.length),Integer.MIN_VALUE,Integer.MAX_VALUE, nWeights);
			sow((int)southMove[1]);
			System.out.println("SOUTH'S best move: " + southMove[1]);
			printboard(boardstate);
			
			southturn = false; //north's turn

			leaves = 0;
			double[] northMove = minimaxAlphaBetaPruining(17,southturn,Arrays.copyOf(boardstate, boardstate.length),Integer.MIN_VALUE,Integer.MAX_VALUE, nWeights);
			sow((int)northMove[1]);
			System.out.println("NORTH'S best move: " + northMove[1]);
			printboard(boardstate);
				
		}while(!victory(boardstate));
		System.out.println("\nGame Over...");
	}
	
	void play2(double[] nWeights)
	{	
		do
		{	
			southturn = false; //north's turn
			leaves = 0;
			int pit = takeUserInput();
			sow(pit);
			System.out.println("Player's move: " + pit);
			printboard(boardstate);
			
			southturn = true; //south's turn
			double[] southMove = minimaxAlphaBetaPruining(17,southturn,Arrays.copyOf(boardstate, boardstate.length),Integer.MIN_VALUE,Integer.MAX_VALUE, nWeights);
			sow((int)southMove[1]);
			System.out.println("SOUTH'S best move: " + southMove[1]);
			printboard(boardstate);
			

			

			

			
		}while(!victory(boardstate));
		System.out.println("\nGame Over...");
	}
	
	public static double roundToDecimals(double d) 
	{   
		int temp = (int)(d * Math.pow(10 , 2));  
		return (double)((temp)/Math.pow(10 , 2));  
	}
	
	String northPrompt = "North player, enter move: ";
	String southPrompt = "South player, enter move: ";
	boolean southturn; //determine whose turn it is
	boolean victoryN = false;
	boolean victoryS = false;
	int southsidesum = 0;
	int northsidesum = 0;
	static int totalSouthWins = 0;
	static int totalNorthWins = 0;
	static int southWins = 0;
	static int northWins = 0;
	static int ties = 0;
	int leaves = 0; 
	int[] boardstate;
	Scanner input = new Scanner(System.in);
	
	public static void main(String[] args)
	{	
		Random r = new Random();
		double[] northWeights = {2.45, 2.73, 1.29, 2.46, 1.42, 2.76};
		double[] southWeights = {66.0, 98.27, 20.6, 46.11, 77.93, 0.0, 0.0};
		double[] bestWeights = {0, 0, 0, 0, 0, 0, 0};
		
		board Oware = new board(); //initialize board
		System.out.println("Initial board:");
		Oware.printboard(Oware.boardstate);
		
		//Oware.play2(northWeights, southWeights);
		Oware.play2(northWeights);
	}
}