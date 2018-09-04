/**********************************************************\
* hand.java                                                *
* by Daniel Wedul                                          *
* last modified 5/26/02                                    *
>**********************************************************< 
* This class defines a full hand in Texas Hold'em          *
* There are seven cards in each hand                       *
* only five are 'played'                                   *
* this class uses a 14x4 array to test hands,              *
* the 14 comes from the 13 cards plus one, since the ace   *
*   can be either high or low.                             *
>**********************************************************<
* Private in this class is the hand value testers          *
*   i.e. isroyalflush etc.                                 *
* note that they are listed in order, best hand first      *
>**********************************************************<
* the joker is good for aces straits and flushes           *
* technically speaking this means that it adds one to the  *
* number of aces, and it makes it so that only four cards  *
* are needed for straits or flushes.                       *
\**********************************************************/

class hand
{
    private card[] all = new card[7];
    private int rank = 0;
    private String handname = "";
    private int joker = 0;
    //variables to be set during construction so testing
    //  only needs to be done once a hand
    private boolean royalflush = false;
    private int straitflush = -1;
    private int fourofakind = -1;
    private int[] fullhouse = {-1,-1};
    private int flush = -1;
    private int strait = -1;
    private int threeofakind = -1;
    private int[] twopair = {-1,-1};
    private int pair = -1;
    private int[][] handarray = {	{0,0,0,0},
									{0,0,0,0},
									{0,0,0,0},
									{0,0,0,0},
									{0,0,0,0},
									{0,0,0,0},
									{0,0,0,0},
									{0,0,0,0},
									{0,0,0,0},
									{0,0,0,0},
									{0,0,0,0},
									{0,0,0,0},
									{0,0,0,0},
									{0,0,0,0}	};

    // all[] in this class is an image of the cards
    // rank is an integer value that is associated with the hand
    // handarray[] is the 14x4 array mentioned in the beginning comments

    // constructor, a hand must have 7 cards
    // with all seven cards
    public hand(card a, card b, card c, card d, card e, card f, card g)
    {
		all[0] = a;
		all[1] = b;
		all[2] = c;
		all[3] = d;
		all[4] = e;
		all[5] = f;
		all[6] = g;
		prepare();
    } // end constructor

    //constructor with two cards (the pocket) 
    //		and an array of five(the board)
    public hand(card a, card  b, card [] c)
    {
		for(int i=0;i<5;i++) all[i] = c[i];
		all[5] = a;
		all[6] = b;
		prepare();
    } // end constructor

    //constructor, with an array of seven cards
    public hand(card [] a)
    {
		for (int i=0;i<7;i++) all[i] = a[i];
		prepare();
    } // end constructor

    // a void called by the constuctor to prepare the arrays for use
    private void prepare()
    {
		// sort all[]
		sort();
		// prepare handarray
		for(int i=0;i<7;i++)
		{
		    if ((all[i].getvalue() == 15) || (all[i].getsuit() == 5))
				joker = 1;
		    else
				handarray[all[i].getvalue()-1][all[i].getsuit()-1] = 1;
		}
		// the aces go in two columns
		for(int j=0;j<4;j++)
		    handarray[13][j] = handarray[0][j];
		//do all of the testing (and sorting)
		pair = ispair();
		twopair = istwopair();
		threeofakind = isthreeofakind();
		strait = isstrait();
		flush = isflush();
		fullhouse = isfullhouse();
		fourofakind = isfourofakind();
		straitflush = isstraitflush();
		royalflush = isroyalflush();

		//calculate rank 
		rank = calculaterank();
    } // end void prepare

    // a void to get the hands rank
    public int getrank()
    {
		return rank;
    } // end getrank

    // A method to rank the hand
    // I will use a boolean variable 'done' to keep from having 
    // a huge if/elseif/elseif/... statement
    /*****************************************\
    * a hands rank is as follows              *
    *     royal flush: 1       - ~            *
    *    Strait Flush: 2       - 10           *
    *  Four of a Kind: 11      - 249          *
    *      Full House: 250     - 499          *
    *           Flush: 500     - 299,999      *
    *          Strait: 300,000 - 300,019      *
    * Three of a kind: 300,020 - 302,499      *
    *        Two pair: 302,500 - 304,999      *
    *            pair: 305,000 - 349,999      *
    *           other: 350,000 - ~            *
    * a hands rank also includes its kickers  *
    >*****************************************<
    * formula for calculating hands:          *
    * find number of values needed to         *
    *   distinguish one hand from another     *
    *   (include all kickers)                 *
    * look up formula below.                  *
    * 1: _			     m:14     *
    * 2: _+13*_			     m:196    *
    * 3: _+12*(_+13*_)		     m:2366   *
    * 4: _+11*(_+12*(_+13*_))	     m:26040  *
    * 5: _+10*(_+11*(_+12*(_+13*_))) m:260414 *
    * note: the m value is calculated by      *
    *   putting 14 in each blank, it is the   *
    *   maximum that each formula can have    *
    * insert important values in spaces with  *
    *   the least important number in the     *
    *   first space followed by the second    *
    *   etc. Call this number n.              *
    * finally use the first table and find    *
    * the beginning for the hand              *
    * for example, if it's two pair, the      *
    *   beginning is 302,500.                 *
    * finally,                                *
    *      retval = beginning + m - n         *
    \*****************************************/
    private int calculaterank()
    {
		int retval = 0;

		int a = 0;
		int [] b = {-1,-1};
		boolean done = false;

		//start with checking for a royal flush
		if (royalflush)  // 1
		{
		    retval = 1;
		    handname = "    Royal Flush";
		    done = true;
		}
		a = straitflush;
		if (!done && (a != -1)) // 2 - 10
		{
		    retval = 2 + 13 - a;
		    handname = "   Strait Flush";
		    done = true;
		}
		//four of a kind
		a = fourofakind;
		if (!done && (a != -1)) // 11 - 249
		{
		    //find the highest of the remaining 3 cards
		    int hi = 0;
		    for(int i=0;i<7;i++)
		    {
				int t = all[i].getvalue();
				if (t==1 || t==15) t = 14;
				if ((t != a) && (t > hi)) hi = t;
		    }
		    a = hi + 13 * a;
		    retval = 11 + 196 - a;
		    handname = " Four of a kind";
		    done = true;

		}
		//a full house
		b = fullhouse;
		if (!done && (b[0] != -1) && (b[1] != -1)) // 250 - 499
		{
		    a = 13*b[0] + b[1];
		    retval = 250 + 196 - a;
		    handname = "     Full House";
		    done = true;
		}
		//a flush
		a = flush;
		if (!done && (a != -1)) //500 - 299,999
		{
		    a = all[4].getvalue() + 10 * 
			(all[3].getvalue() + 11 * 
			 (all[2].getvalue() + 12 * 
			  (all[1].getvalue() + 13 * a )));
		    retval = 500 + 260414 - a;
		    handname = "          Flush";
		    done = true;
		}
		//a strait
		a = strait;
		if (!done && (a != -1)) //300,000 - 300,019
		{
		    retval = 300000 + 14 - a;
		    handname = "         Strait";
		    done = true;
		}
		//three of a kind
		a = threeofakind;
		if (!done && (a != -1)) // 300,020 - 302,499
		{
		    //find the other two cards that play
		    int index = 0;
		    int hi = all[0].getvalue();
		    int lo = all[1].getvalue();
		    for(int i=6;i>=0;i--) if (all[i].getvalue() == a) index = i;
		    if (index == 0)
		    {
				hi = all[3].getvalue();
				lo = all[4].getvalue();
		    }
		    if (index == 1)
		    {
				lo = all[4].getvalue();
		    }	   
		    if (hi == 1) hi = 14;
		    a = lo + 12 * (hi + 13 * a);
		    retval = 300020 + 2366 - a;
		    handname = "Three of a kind";
		    done = true;
		}
		//two pair
		b = twopair;
		if (!done && (b[0] != -1) && (b[1] != -1)) // 302,500 - 304,999
		{
		    //find the kicker
		    int kicker = 0;
		    for(int i=0;i<7;i++)
		    {
				int t = all[i].getvalue();
				if (t == 1) t=14;
				if ( (t != b[0]) && (t != b[1]) )
				{
				    kicker = t;
				    i = 7;
				}
		    }
		    a = kicker + 12 * (b[1] + 13 * b[0]);
		    retval = 302500 + 2366 - a;
		    handname = "       Two pair";
		    done = true;
		}
		//a pair
		a = pair;
		if (!done && (a != -1)) // 305,000 - 349,999
		{
		    //find the three kickers
		    int [] k = { all[0].getvalue() ,
				 all[1].getvalue() ,
				 all[2].getvalue()  };
		    int index = 0;
		    for(int i=6;i>=0;i--) if (all[i].getvalue() == a) index = i;
		    if (index == 0)
		    {
				k[0] = all[2].getvalue();
				k[1] = all[3].getvalue();
				k[2] = all[4].getvalue();
		    }
		    if (index == 1)
		    {
				k[1] = all[3].getvalue();
				k[2] = all[4].getvalue();
		    }
		    if (index == 2)
		    {
				k[2] = all[4].getvalue();
		    }
		    if (k[0] == 1) k[0] = 14;
		    a = k[2] + 11 * (k[1] + 12 * (k[0] + 13*a));
		    retval = 305000 + 26040 - a;
		    handname = "           Pair";
		    done = true;
		}
		//highcard
		if (!done) //350,000 - ~
		{
		    a = all[0].getvalue();
		    if (a==1) a = 14;
		    a = all[4].getvalue() + 10 * 
			(all[3].getvalue() + 11 * 
			 (all[2].getvalue() + 12 * 
			  (all[1].getvalue() + 13 * a )));
		    retval = 350000 + 260414 - a;
		    handname = "      High Card";
		}

		return retval;
    } // end calculaterank


    // a method to get the string name
    public String gethandname()
    {
		return handname;
    }

    // a method to print the hand array
    // this is mainly for debugging
    public void printhandarray()
    {
		for(int i=0;i<14;i++)
		{
		    for(int j=0;j<4;j++)
		    {
				System.out.print (" " + handarray[i][j]);
		    }
				System.out.println(" ");
		}
    }

    // a method to sort the seven cards from highest to lowest
    private void sort()
    {
		for(int i=0;i<6;i++)
		{
		    for (int j=i;j<7;j++)
		    {
				if (all[j].isgt(all[i]))
				{
				    card temp = all[i];
				    all[i] = all[j];
				    all[j] = temp;
				}
		    }
		}
    } // end sort

    // a method to print the cards in the hand
    public void printhand()
    {
		for(int i = 0;i<7;i++)
		{
		    all[i].printcard();
		    System.out.print (" ");
		}
    } // end printhand

    // a method to print the cards in the hand with carriage return
    public void printhandln()
    {
		printhand();
		System.out.println("");
    } // end printhand

    /**************************\
    *    BEGIN TEST METHODS    *
    \**************************/

    // royal flush, A, K, Q, J, T, of same suit
    // a royal flush is the same no matter what suit
    // for that reason it we only need a boolean to return it
    private boolean isroyalflush()
    {
		return (straitflush == 14);
    } // end boolean isroyalflush

    // strait flush five cards in a row of the same suit
    // i.e. 4H, 5H, 6H, 7H, 8H
    // this will return the value of the highest card in the strait
    private int isstraitflush()
    {
		int retval = -1;
		if (flush <= -1) return retval;
		int straitstart = strait - 1;
		if (straitstart <= -1) return retval;
	
		for (int j=0;j<4;j++)
		{
		    for (int i=13;i>=4;i--)
		    {
				int counter = 0;
				int tempjoker = joker;
				for (int k=0;k<5;k++)
				{
				    if ((handarray[i-k][j] == 1) || (tempjoker == 1)) 
				    {
						if (handarray[i-k][j] != 1) tempjoker = 0;
						counter++;
				    }
				    else 
				    {
						counter = 0;
						k = 6;
				    }
				}
				if (counter == 5)
				{
				    retval = i;
				    i = -1;
				    j = 5;
				}
		    } //end i loop
		} //end j loop

		if (retval != -1)
		    retval++;

		return retval;	
    } // end int isstraitflush

    // four of a kind, four cards with the same value
    // returns value of card in four of a kind
    private int isfourofakind()
    {
		int retval = -1;
		for(int i=13;i>=0;i--)
		{   
		    int counter = 0;
		    for(int j=0;j<4;j++) counter = counter + handarray[i][j];
		    if (counter == 4)
		    {
				retval = i;
				i = 0;
		    }
		} 
		if (retval != -1) retval++;
		return retval;
    } // end int isfourofakind

    // full house, three cards of one value and two of another
    // returns an array of size 2
    // retval[0] = value of three of a kind
    // retval[1] = value of pair
    private int[] isfullhouse()
    {
		int[] retval = {-1,-1};

		//find the three of a kind if there is one
		retval[0] = threeofakind;
		if (retval[0] == -1) return retval;

		//find the pair if there's a three of a kind
		//a full house is also two pair
		// the pair needed must be one of those pairs
		//return if there was no two pair
		if (twopair[0] == -1)
		{
		    retval[0] = -1;
		    return retval;
		}
		//find out which pair to use
		if (twopair[1] != retval[0]) retval[1] = twopair[1];
		else if (twopair[0] != retval[0]) retval[1] = twopair[0];
		else retval[0] = -1;

		return retval;
    } // end int isfullhouse

    // flush, five cards of the same suit
    // returns highest card of the flush in first element
    // This method also puts the top five cards of the flush
    //   in the first 5 elements of all[] (for ranking)
    private int isflush()
    {
		int suit = -1;
		for(int j=0;j<4;j++)
		{
		    int counter = 0;
		    for(int i=1;i<14;i++) counter = counter + handarray[i][j];
		    if (counter >= 5-joker)
		    {
				suit = j;
				j=4;
		    }
		}
		if (suit == -1) return -1;
		// we have a flush, find the five highest cards of it
		suit++;		//because card's suits go 1-4 not 0-3
		//create a new card that has the same suit as the flush
		card tempsuited = new card(2,suit);
		for(int i=0; i<5;i++)
		{
		    //find highest card of flush suit
		    int highindex = i;
		    while(!all[highindex].issuitedwith(tempsuited) && (highindex < 7)) highindex++;
		    for (int j=i+1;j<7;j++)
		    {
				if (all[j].issuitedwith(tempsuited))
		 		{
				    if ( all[j].isgt(all[highindex]) )
				    {
						highindex = j;
				    }
				}
		    }
		    //swap it to current position of needed
		    if (highindex != i)
		    {
				card temp = all[i];
				all[i] = all[highindex];
				all[highindex] = temp;
		    }
		}
	
		int retval = all[0].getvalue();
		//take care of the ace and joker;
		if ((retval == 1) || (retval == 15)) retval = 14;
		return retval;
    } // end int isflush

    // strait, five cards in a row
    // i.e. 9, T, J, Q, K
    // returns highest card of the strait
    private int isstrait()
    {
		int retval = -1;
		for(int i=13;i>=4;i--)
		{
		    int counter = 0;
		    boolean tempjoker = (joker == 1);
		    for (int j=0;j<5;j++)
		    {
				boolean cardfound = false;
				for(int k=0;k<4;k++) if (handarray[i-j][k]==1) cardfound = true;
				if (!cardfound && tempjoker)
				{
				    cardfound = true;
				    tempjoker = false;
				}
				if (cardfound) counter++;
		    }
		    if (counter == 5)
		    {
				retval = i;
				i = 0;
		    }
		}
		if (retval != -1) retval++;
		return retval;
    } // end int isstrait

    // three of a kind, three cards of the same value
    // returns value of the three of a kind
    private int isthreeofakind()
    {
		int retval = -1;
		int counter = joker;
		for(int i=13;i>0;i--)
		{
		    for(int j=0;j<4;j++) counter = counter + handarray[i][j];
		    if (counter >= 3)
		    {
				retval = i;
				i = 0;
		    }
		    counter = 0;
		} 	
		if (retval != -1) retval++;
		return retval;
    } // end int isthreeofakind

    // two pairs, 2 cards of one value and 2 cards of another value
    // returns an array of size 2 that is the value of each pair
    private int[] istwopair()
    {
		int [] retval = {-1,-1};
		int pairsfound = 0;
		int counter = joker;
		for(int i=13;i>0;i--)
		{
		    for(int j=0;j<4;j++) counter = counter + handarray[i][j];
		    if (counter >= 2)
		    {
				retval[pairsfound] = i;
				pairsfound++;
				if (pairsfound == 2) i = 0;
				else if (counter == 4)
				{
				    retval[pairsfound] = i;
				    pairsfound = 2;
				    i = 0;
				}
		    }
		    counter = 0;
		} 
		// take care of when only one pair is found
		if (retval[1] == -1) retval[0] = -1;
		if (retval[0] != -1) retval[0]++;
		if (retval[1] != -1) retval[1]++;
		return retval;
    } // end int[] istwopair

    // two cards of the same value
    // returns value of pair
    private int ispair()
    {
		int retval = -1;
		int counter = joker;
		for(int i=13;i>0;i--)
		{
		    for(int j=0;j<4;j++) counter = counter + handarray[i][j];
		    if (counter >= 2)
		    {
				retval = i;
				i = 0;
		    }
		    counter = 0;
		} 
		if (retval != -1) retval++;
		return retval;
    } // end int ispair

    /**************************\
    *     END TEST METHODS     *
    \**************************/

    // a method that runs all test methods on a hand and 
    // displayes each's results
    public void showhandstrength()
    {
		System.out.print ("\n ");
		printhand();
		System.out.println("\n");
		System.out.println("    Royal Flush: " + royalflush );
		System.out.println("   Strait Flush: " + straitflush );
		System.out.println(" Four of a kind: " + fourofakind );
		System.out.println("     Full house: " + fullhouse[0] + ", " + fullhouse[1] );
		System.out.println("          Flush: " + flush );
		System.out.println("         Strait: " + strait );
		System.out.println("Three of a kind: " + threeofakind );
		System.out.println("       Two pair: " + twopair[0] + ", " + twopair[1] );
		System.out.println("           Pair: " + pair );
		System.out.println(" ");
		System.out.println("           Rank: " + rank );
		System.out.println(" ");
    } // end showhandstrength	

} // end class hand



// end hand.java
