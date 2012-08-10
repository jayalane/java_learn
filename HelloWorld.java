enum logLevel { CRIT , EMERGE, WARN, INFO, DEBUG, DEBUG2 };

class inner_logger {

	private logLevel the_logLevel; // not what u think

	public inner_logger (logLevel l) {
		the_logLevel = l;
	}

	public void log (logLevel level, String msg) {
		if (level == the_logLevel) {
			System.out.println(msg);
		}
	}
}


class myLogger {

	private String file;
	private inner_logger[] loggers;
	private logLevel the_logLevel; // for this file

	public myLogger (String __file) {
		file = __file;
//		for (logLevel l; logLevel.values()) {
//			inner_loggers[l] = new inner_logger(l);
//		}
	}
	public void set_level(logLevel l) {
		the_logLevel = l;
	}
    public void log (String msg) { // ideally this won't be used
		System.out.println(msg);
	}
}	

class Fibs {

	private Thread[] threads;
	private volatile long[] answers;
	private long[] done;
	private final int max_n;

	Fibs (int n) {

	    int j;
		
		max_n = n;
		answers = new long[n + 1];
		done = new long[n + 1];
		threads = new Thread[n + 1];

		answers[0] = 0;
		answers[1] = 1;
		done[0] = 1;
		done[1] = 1;

		for (j = 2; j <= n; j++) {
			System.out.println("J is " + j);
			final int ind = j;
			
			done[j] = 0;
			threads[j] = new Thread (new Runnable() {
					@Override
					public void run() {
						System.out.println("Starting J " + ind);
						if (ind >= 3) {
							try {
								Fibs.this.threads[ind-1].start();
							} catch (IllegalThreadStateException ex) {
								// ok 
							}
						}
						if (ind >= 4) {
							try {
								Fibs.this.threads[ind-2].start();
							} catch (IllegalThreadStateException ex) {
								// ok 
							}
						}
						int done = 0;
						while (done == 0) {
							try {
								if (ind >= 4) {
									Fibs.this.threads[ind - 1].join();
									Fibs.this.threads[ind - 2].join();
								} else if (ind >= 3) {
									Fibs.this.threads[ind - 1].join();
								}
								done = 1;
							} catch (InterruptedException ex) {
								done = 0;
							}
						}
						Fibs.this.answers[ind] = Fibs.this.answers[ind-1] + Fibs.this.answers[ind-2];
						Fibs.this.done[ind] = 1;
						System.out.println("Finishing J " + ind + " got " + Fibs.this.answers[ind]);
					}
				});
		}
		
	}
	public void go () {

		if (max_n >= 2) {
			threads[max_n].start();
		}
		
	}
	public long fib (int n)
		{
			if (n == 0) {
				return 0;
			}
			if (n == 1) {
				return 1;
			}
			while (done[n] == 0) {
				try {
					threads[n].join();
				} catch (InterruptedException ex) {
					ex.printStackTrace();
					// oh well
				}
			}
			return answers[n];
		}
}

public class HelloWorld {

    public static void  main (String [] argv)
    {
		int i = 0;
		int j;

		myLogger ml = new myLogger(Thread.currentThread().getStackTrace()[1].getFileName());

		ml.log("argv length is " + argv.length);

		for (i = 0; i < argv.length; i++) {
			
			try {
				
				j = Integer.parseInt(argv[i]);
				Fibs the_fib = new Fibs(j);
				the_fib.go();
				ml.log(argv[i] + "th fibonacci number is " + the_fib.fib(j));

			}  catch (Exception ex) {
				ex.printStackTrace();
				ml.log(argv[i] + " is not an integer");

			}
			ml.log(argv[i] + "\n");
		}
    	ml.log("Hello World!"); // Display the string.
    }
}
