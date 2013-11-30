package main.utils;

public class LevenshteinDistance {
	
	public static final int MED_THRESHOLD=6;
	public static final int STRICT_THRESHOLD=2;
	public static final int LOW_THRESHOLD=9;
	private int threshold=3;

	//****************************
	  // Get minimum of three values
	  //****************************
	  private int Minimum (int a, int b, int c) {
		  int mi;

		  mi = a;
		  if (b < mi) {
		      mi = b;
		  }
		  if (c < mi) {
		     mi = c;
		  }
		  return mi;

	}

	  //*****************************
	  // Compute Levenshtein distance
	  //*****************************

	  private int getLD (String s, String t) {
		  int d[][]; // matrix
		  int n; // length of s
		  int m; // length of t
		  int i; // iterates through s
		  int j; // iterates through t
		  char s_i; // ith character of s
		  char t_j; // jth character of t
		  int cost; // cost

		    // Step 1
		  	if (s==null) return 1000;
		  	if (t==null) return 1000;
		    n = s.length ();
		    m = t.length ();
		    if (n == 0) {
		      return 1000;
		    }
		    if (m == 0) {
		      return 1000;
		    }
		    d = new int[n+1][m+1];

		    // Step 2
		    for (i = 0; i <= n; i++) {
		      d[i][0] = i;
		    }
		    for (j = 0; j <= m; j++) {
		      d[0][j] = j;
		    }

		    // Step 3
		    for (i = 1; i <= n; i++) {
		      s_i = s.charAt (i - 1);
		      
		    // Step 4
		      for (j = 1; j <= m; j++) {
		        t_j = t.charAt (j - 1);

		        // Step 5
		        if (s_i == t_j) cost = 0;
		        else cost = 1;

		        // Step 6
		        d[i][j] = Minimum (d[i-1][j]+1, d[i][j-1]+1, d[i-1][j-1] + cost);

		      }
		    }

		    // Step 7
		    return d[n][m];

		  }
	  
	    public boolean compare(String s, String t){
	    	//System.out.println("comparing "+s+" with "+t);
	    	//System.out.println(getLD(s,t));
	    	if (getLD(s,t)>=this.threshold) return false;
	    	else return true;
	    }
	  
		public int getThreshold() {
			return threshold;
		}

		public void setThreshold(int threshold) {
			this.threshold = threshold;
		}
	  

	}