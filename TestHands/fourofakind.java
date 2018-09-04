class fourofakind {
    public static void main(String [] Args) {
	card [] a = new card[7];
	a[0] = new card(2,1);
	a[1] = new card(2,2);
	a[2] = new card(2,3);
	a[3] = new card(2,4);
	a[4] = new card(3,1);
	a[5] = new card(4,2);
	a[6] = new card(5,3);
	hand h = new hand(a);
	h.printhandarray();
	h.showhandstrength();
    }
}
