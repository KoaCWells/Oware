Authors: Koa Wells, Henri Thomas, Henry Thomas
CSCE A411 - Artificial Intelligence

----Oware----
This is a program designed to play against an AI opponent at the table game Oware.  In order to change the difficulty, increase the first parameter passed into miniMaxAlphaBetaPruining at line 709.  This increases the amount of moves that AI player is able to look ahead.  The heuristic used for the AI opponent is as followed: weight_goal*goal + weight_pit1*pit1 + weight_pit2*pit2 + weight_pit3*pit3 + weight_sum_of_seeds*sum_of_seeds + weight_of_capture*capture.  Optimal weights were found using hill-climbing.

The player goes first using the following entries as moves: 7, 8, 9, 10, 11, 12

There is currently a bug that does not allow for the player to move.  The game thinks that the player is trying to sow an empty pit.