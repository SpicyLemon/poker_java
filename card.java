/****************************************************\
* card.java                                          *
* by Daniel Wedul                                    *
* Last modified 5/27/02                              *
>****************************************************<
* This is a class that describes a playing card.     *
* There are two things that are fundamental to the   *
*  identity of a card, its suit (Hearts, etc.)       *
*  and it's value (2, king, etc.)		             *
>****************************************************<
* There are no set methods in this class.            *
* This is to prevent a card from being changed after *
*  it has been created.                              *
* The get suit/value name routines are for output    *
>****************************************************<
* suites:                                            *
* 1 - Diamonds            the values are strait      *
* 2 - Hearts              forward. 1=ace, 2=2, ...   *
* 3 - Spades              11=jack, 12=queen, 13=king *
* 4 - Clubs               15=joker                   *
>****************************************************<
* joker:                                             *
* the joker will be treated as greater than all      *
*  other cards                                       *
*  it will also be treated as the same suit as       *
*  all the other cards                               *
\****************************************************/

class card
{
    private int value = -1;
    private int suit = -1;

    //constructor
    // a card must be given its suit and value
    // This is the only place were they can be set
    public card(int v, int s)
    {
		//make sure suit and value are both withing proper bounds
		//otherwise leave the card at -1, -1
		if (v > 0 && v <= 15  && s > 0 && s <= 5)
		{
		    value = v;
		    if (value == 14) value = 1;
		    suit = s;
		}
    } // end constructor

    public card(char v, char s)
    {
		switch (v)
		{
		    case('A'): value = 1; break;
		    case('2'): value = 2; break;
		    case('3'): value = 3; break;
		    case('4'): value = 4; break;
		    case('5'): value = 5; break;
		    case('6'): value = 6; break;
		    case('7'): value = 7; break;
		    case('8'): value = 8; break;
		    case('9'): value = 9; break;
		    case('T'): value = 10; break;
		    case('J'): value = 11; break;
		    case('Q'): value = 12; break;
		    case('K'): value = 13; break;
		    case('R'): value = 15; break;
		    default: value = -1;
		}
		switch (s)
		{
		    case('D'): suit = 1; break;
		    case('H'): suit = 2; break;
		    case('S'): suit = 3; break;
		    case('C'): suit = 4; break;
		    case('R'): suit = 5; break;
		    default: suit = -1;
		}
		if ((v == 'R') || (s == 'R')) 
		{
		    value = 15;
		    suit = 5;
		}
		if ((suit == -1) || (value == -1)) 
		{
		    suit = -1;
		    value = -1;
		}
    }//end constructor

    //a method to get the value
    public int getvalue()
    {
		return value;
    } //end getvalue

    //a method to get a one character name of each value
    public char getvaluename()
    {
	char retval = 'x';
		switch(value)
		{
		    case(1): retval = 'A'; break;
		    case(2): retval = '2'; break;
		    case(3): retval = '3'; break;
		    case(4): retval = '4'; break;
		    case(5): retval = '5'; break;
		    case(6): retval = '6'; break;
		    case(7): retval = '7'; break;
		    case(8): retval = '8'; break;
		    case(9): retval = '9'; break;
		    case(10): retval = 'T'; break;
		    case(11): retval = 'J'; break;
		    case(12): retval = 'Q'; break;
		    case(13): retval = 'K'; break; 
		    case(15): retval = 'R'; break;
		}
		return retval;
    } //end getvaluename

    // a method to get the suit number
    public int getsuit()
    {
		return suit;
    } //end getsuit
   
    //a method to return a character value of the suit
    public char getsuitname()
    {
		char retval = 'x';
		switch(suit)
		{
		    case(1): retval = 'D'; break;
		    case(2): retval = 'H'; break;
		    case(3): retval = 'S'; break;
		    case(4): retval = 'C'; break;
 		    case(5): retval = 'R'; break;
		}
		return retval;
    } //end getsuitname

    /************************\
    *   BEGIN TEST METHODS   *
    \************************/

    //quick note: these test methods assume that suit doesn't
    //	matter when ranking a card.
    //also, an ace will return both less than a two and greater
    // than a king

    //is greater than
    public boolean isgt(card a)
    {
		return (this.isge(a) && !this.iseq(a));
    } // end isgt

    //is greater than or equal to
    public boolean isge(card a)
    {
		boolean retval = (this.getvalue() > a.getvalue());
		if ((this.getvalue() == 1) && (a.getvalue() != 15))
		    retval = true;
		return retval;
    } //end isge

    //is equal to
    public boolean iseq(card a)
    {
		boolean retval = (this.getvalue() == a.getvalue());
		if ( ((this.getvalue() == 15) && (a.getvalue() == 1)) ||
		     ((a.getvalue() == 15) && (this.getvalue() == 15))   )
		{
		    retval = true;
		}
		return retval;
    }// end iseq

    //is less than
    public boolean islt(card a)
    {
		return (!this.isge(a));
    } // end islt

    //is less than or equal to
    public boolean isle(card a)
    {
		return (!this.isge(a) || this.iseq(a));
    } // end isle

    //is same as (same value and suit)
    public boolean issameas(card a)
    {
		return ( (this.getvalue() == a.getvalue()) 
		            && (this.getsuit() == a.getsuit()) );
	} // end issameas

    //is suited with (same suit)
    public boolean issuitedwith(card a)
    {
		boolean retval = (this.getsuit() == a.getsuit());
		if (this.getsuit() == 5 || a.getsuit() == 5) retval = true;
		return retval;
    } // end issuitedwith

    /************************\
    *    END TEST METHODS    *
    \************************/

    // a method to print the value and suit of the card
    public void printcard()
    {
		System.out.print ("" + this.getvaluename() + this.getsuitname()+ " "); 
    } // end printcard

} // end class card


// end card.java
