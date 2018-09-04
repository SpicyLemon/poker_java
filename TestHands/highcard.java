class highcard {
    public static void main(String [] Args) {
	card [] a = new card[7];
	a[0] = new card(1,1);
	a[1] = new card(2,2);
	a[2] = new card(3,3);
	a[3] = new card(4,1);
	a[4] = new card(11,4);
	a[5] = new card(9,1);
	a[6] = new card(8,3);
	hand h = new hand(a);
	h.printhandarray();
	h.showhandstrength();
    }
}
