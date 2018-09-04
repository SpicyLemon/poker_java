//This takes seven cards and outputs all of the possible
// hands that it can make.
// It is used to test the validity of the program.
public class rank {
    public static void main(String [] args) {
		//check if it was called right
		if (args.length != 7) {
		    System.out.println("\nImproper usage:");
		    System.out.println("Seven cards must be specified.");
		    System.out.println("You gave "+args.length+" cards to use.\n");
		    System.out.println("correct ussage is");
		    System.out.println("java rank c1 c2 c3 c4 c5 c6 c7");
		    System.out.println("where c1 through c7 is a two character card code chosen from below:");
		    deck a = new deck(true);
		    a.printdeck();
		    System.out.println("terminating program.\n");
		    System.exit(1);
		}
		//it was called right, create the cards
		card [] a = new card[7];
		for (int i=0;i<7;i++) {
		    char[] k = args[i].toCharArray();
		    a[i] = new card(k[0],k[1]);
		}
		//check all cards for errors
		for (int i=0;i<7;i++) {
		    card bogus = new card(-1,-1);
		    if (a[i].issameas(bogus)) {
				System.out.println("\nError:");
				System.out.println("The card code " + args[i] + " is not recognized.");
				System.out.println("The recognized codes are");
				deck d = new deck(true);
				d.printdeck();
				System.out.println("terminating program.\n");
				System.exit(1);
		    }
		}
		//check for doubles
		for (int i=0;i<6;i++) {
		    for (int j=i+1;j<7;j++) {
				if (a[i].issameas(a[j])) {
				    System.out.println("\nError:");
				    System.out.println("The card " + args[i] + " was specified twice.");
				    System.out.println("terminating program.\n");
				    System.exit(1);
				}
		    }
		}

		//if it got this far, everything is ok
		hand b = new hand(a);
		b.showhandstrength();
    }
}
