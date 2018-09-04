/****************************************\
* headsup.java                           *
* By Daniel Wedul                        *
* February 2003				     *
>****************************************<
* This will be a runnable java program   *
* that simulates a number of heads-up    *
* texas hold-em hands                    *
>****************************************<
* It will use command line arguments as  *
* follows:                               *
* java headsup #1 #2 -3 -4 -5 -6         *
* all inputs are optional                *
* #1 is the number of rounds to deal     *
* #2 is the either 1 or 0 for the number *
*    of jokers to use in the deck        *
* -3, -4 are one set of pocket cards     *
* -5, -6 are another set of pocket cards *
>****************************************<
* This program will take the two sets of *
* pocket cards out of the deck and deal  *
* #1 boards and test which hand wins the *
* most.                                  *
\****************************************/

public class headsup {
    public static void main (String [] args) {
	int iter = 10000;
	int joker = 0;
	card [] keep = new card[4];
	card [] pocket1 = new card[2];
	card [] pocket2 = new card[2];
	
	int argsgiven = args.length;
	if (argsgiven > 6) argsgiven = 6;

	int cardsgiven = argsgiven - 2;
	if (cardsgiven < 0) cardsgiven = 0;

	int cardsneeded = 4 - cardsgiven;

	switch (argsgiven) {
	    case (6): {
			char [] k = args[5].toCharArray();
			keep[3] = new card(k[0],k[1]);
	    }
	    case (5): {
			char [] k = args[4].toCharArray();
			keep[2] = new card(k[0],k[1]);
          }
	    case (4): {
			char [] k = args[3].toCharArray();
			keep[1] = new card(k[0],k[1]);
	    }
	    case (3): {
			char [] k = args[2].toCharArray();
			keep[0] = new card(k[0],k[1]);
	    }
	    case (2): { joker = Integer.parseInt(args[1].trim()); }
	    case (1): { iter = Integer.parseInt(args[0].trim()); }
	    case (0): { break;  }
        }
	
	card [] leftover = new card[53];
	deck a = new deck(joker == 1);
	int numfound = 0;
	int count = 0;
	card temp;
	//pull the cards in question
	while(numfound < cardsgiven) {
	    temp = a.draw();
	    boolean wanted = false;	
	    for (int i=0;i<cardsgiven;i++) {
			if (temp.issameas(keep[i]) ) wanted = true;
	    }
	    if (wanted) {
			numfound++;
	    }
	    else {
			leftover[count] = temp;
			count++;
	    }
	}
	while(count>0) {
	    count--;
	    a. returncard(leftover[count]);
	    leftover[count]=null;
	}
	
	count = 0;
	int p1win = 0;
	int p2win = 0;
	//main for loop
	for (int i=0;i<iter;i++) {
	    a.shuffle();
	    a.cut();
	
	    //get the rest of the needed cards
	    switch (cardsneeded) {
			case (4): { 
			    pocket1[0] = a.draw();
			    pocket1[1] = a.draw();
			    pocket2[0] = a.draw();
			    pocket2[1] = a.draw();
			    break;
			}
			case (3): {
			    pocket1[0] = keep[0];
			    pocket1[1] = a.draw();
			    pocket2[0] = a.draw();
			    pocket2[1] = a.draw();
			    break;
			}
			case (2): { 
			    pocket1[0] = keep[0];
			    pocket1[1] = keep[1];
			    pocket2[0] = a.draw();
			    pocket2[1] = a.draw();
			    break;
			}
			case (1): { 
			    pocket1[0] = keep[0];
			    pocket1[1] = keep[1];
			    pocket2[0] = keep[2];
			    pocket2[1] = a.draw();
			    break;
			}
			case (0): { 
			    pocket1[0] = keep[0];
			    pocket1[1] = keep[1];
			    pocket2[0] = keep[2];
			    pocket2[1] = keep[3];
			    break; 
			}
	    }

	    //deal the board
	    card [] board = new card[5];
	    for (int j=0;j<5;j++) {
			board[j] = a.draw();
	    }

	    //set up the hands
	    hand player1 = new hand(pocket1[0], pocket1[1], board);
	    hand player2 = new hand(pocket2[0], pocket2[1], board);

	    //count wins
	    int p1rank = player1.getrank();
	    int p2rank = player2.getrank();
	    if     (p1rank < p2rank) p1win++;
	    else if (p1rank > p2rank) p2win++;

	    //put cards back
	    for (int j=0;j<5;j++) {
			a.returncard(board[j]);
	    }
	    switch (cardsneeded) {
			case (4): { a.returncard(pocket1[0]); }
			case (3): { a.returncard(pocket1[1]); }
			case (2): { a.returncard(pocket2[0]); }
			case (1): { a.returncard(pocket2[1]); }
			case (0): { break; }
	    }
	   
	} // end main for loop

	System.out.println("");
	if (cardsgiven > 0) pocket1[0].printcard();
	else System.out.print ("xx "); 
	if (cardsgiven > 1) pocket1[1].printcard();
	else System.out.print ("xx "); 
	System.out.println("won "+p1win+"/"+iter+" hands.");	
	if (cardsgiven > 2) pocket2[0].printcard();
	else System.out.print ("xx "); 
	if (cardsgiven > 3) pocket2[1].printcard();
	else System.out.print ("xx "); 
	System.out.println("won "+p2win+"/"+iter+" hands.");	
	System.out.println("They split "+(iter-p1win-p2win)+"/"+iter+" hands.");
	System.out.println("");
    }//end main
}//end class headsup
