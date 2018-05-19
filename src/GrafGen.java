import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;

public class GrafGen {
	
	// Pomožni razredi
	static class Pair <L extends Comparable<L>, D extends Comparable<D>> implements Comparable<Pair<L, D>> {
		
		L l;
		D d;
		
		public Pair (L a, D b) {
			
			l = a;
			d = b;
		}
		
		public String toString() {
			
			return l.toString() + " " + d.toString();
		}
		
		public int compareTo(Pair<L, D> p) {
			
			// Leksikografska ureditev
			int ret = l.compareTo(p.l);
			
			if (ret == 0)
				ret = d.compareTo(p.d);
			
			return ret;
		}
		
	}
	
	// Algoritmi za generiranje
	private static void alg1 (int n, double p, ArrayList<Pair<Integer, Integer>> povezave) {
		
		// Privzeti algoritem, generira nepovezan graf
		for (int i = 0; i < n; i++)
			for (int j = i + 1; j < n; j++)
				if (Math.random() < p)
					povezave.add(new Pair<Integer, Integer>(i, j));
	}
	
	private static void alg2 (int n, double p, ArrayList<Pair<Integer, Integer>> povezave) {
		
		// Alternativni algoritem, generira povezan graf
		for (int i = 1; i < n; i++) {
			
			int prvaPovezava = (int) Math.floor(Math.random() * i);
			povezave.add(new Pair<Integer, Integer>(prvaPovezava, i));
			
			double noviP = (i * p - 1) / i;
			
			for (int j = 0; j < i; j++)
				if (j != prvaPovezava && Math.random() < noviP)
					povezave.add(new Pair<Integer, Integer>(i, j));
		}
	}
	
	// Pomožne metode
	@SuppressWarnings("unused")
	private static double fact (long n) { // Trenutno se ne uporablja
		
		if (n < 0) return 0; // Verjetno bi blo fajn kako izjemo vreč, ampak eh, whatevs
		
		double ret = 1;
		while (n > 1) {
			ret *= n;
			n--;
		}
		
		return ret;
	}
	
	private static void povezi (ArrayList<Pair<Integer, Integer>> p, int n) {
		
		// Vzame graf, definiran s "p", najde med seboj nepovezane množice in vstavi minimalno število povezav,
		// potrebnih, da postane graf povezan
		ArrayList<Pair<Integer, Integer>> extras = new ArrayList<Pair<Integer, Integer>>();
		
		int[] pointers = new int[n];
		for (int i = 0; i < n; i++)
			pointers[i] = i;
		
		// Tabela pointers vsebuje za vsako vozlišče i neko vozlišče j <= i, ki je povezano z i
		// Ideja je enostavna - želimo, da vsako vozlišče kaže na najmanjše vozlišče, povezano z njim
		// Najprej gremo čez vse povezave. Večjemu vozlišču priredimo indeks manjšega.
		// Večji ima lahko že prirejeno neko vrednost, zato se rekurzivno spuščamo do manjših indeksov
		
		Iterator<Pair<Integer, Integer>> it = p.iterator();
		while (it.hasNext()) {
			
			Pair<Integer, Integer> povezava = it.next();
			int manjsi = povezava.l < povezava.d ? povezava.l : povezava.d;
			int vecji = povezava.l < povezava.d ? povezava.d : povezava.l;
			
			while (vecji > manjsi) {
				if (pointers[vecji] == vecji) {
					
					// Nimamo še prirejene vrednosti -> končali smo
					pointers[vecji] = manjsi;
					vecji = manjsi;
				} else if (pointers[vecji] > manjsi) {
					
					// Vrednost pri večjem je večja od manjšega -> popravimo vrednost
					vecji = pointers[vecji];
				} else if (pointers[vecji] < manjsi) {
					
					// Vrednost pri večjem je manjša od manjšega -> popravimo pri manjšem
					int temp = pointers[vecji];
					vecji = manjsi;
					manjsi = temp;
				} else if (pointers[vecji] == manjsi) {
					
					// Ne delamo nič, zaključimo
					vecji = manjsi;
				} else {
					
					// Tu ne bi smeli biti!!
					System.err.println("Zgodila se je huda napaka!");
					System.exit(-1);
				}
			}
		}
		
		// Od manjših k večjim indeksom zdaj popravljamo tabelo - dobili bomo pointerje na najmanjše elemente
		
		for (int i = 0; i < n; i++)
			if (pointers[i] < i)
				pointers[i] = pointers[pointers[i]];
		
		// Zdaj naredimo seznam najmanjših indeksov nepovezanih množic
		
		LinkedList<Integer> mnozice = new LinkedList<Integer>();
		
		for (int i : pointers) // To bi se dalo bolj učinkovito narediti
			if (!mnozice.contains(i))
				mnozice.add(i);
		
		// Zdaj dodamo potrebne povezave
		
		int first = mnozice.pop();
		while (!mnozice.isEmpty()) {
			int second = mnozice.pop();
			extras.add(new Pair<Integer, Integer>(first, second));
			first = second;
		}
		
		// Združimo seznama
		
		extras.forEach(par -> p.add(par));
	}
	
	// Main
	public static void main (String [] args) {
		
		if (args.length < 1) {
			System.err.println("Premalo argumentov. Potrebujem vsaj število vozlišč.");
			System.exit(-1);
		}
		
		// Preberemo želeno število vozlišč grafa
		if (args[0].charAt(0) == '-') {
			System.err.println("Prvi argument mora nujno biti število vozlišč.");
			System.exit(-1);
		}
		
		int n = 0;
		try {
			n = Integer.parseInt(args[0]);
		} catch (NumberFormatException e) {
			System.err.println("Prvi argument mora nujno biti število vozlišč.");
			System.exit(-1);
		}
		
		int currArg = 1;
		
		String algoritem = "default"; // Kateri algoritem uporabljamo za tvorjenje grafa
		boolean moraBitiPovezan = true; // Ali mora tvorjeni graf biti povezan
		boolean moraSortiratiPovezave = true; // Ali morajo biti povezave leksikografsko sortirane
		int zelenoSteviloPovezav = 2 * n; // Želeno povprečno število povezav
		
		while (currArg < args.length) {
			
			// Beremo argumente, dane programu
			switch (args[currArg]) {
			
			case "-a":
			case "--algoritem":
				if (currArg >= args.length - 1 || args[currArg + 1].charAt(0) == '-') {
					System.err.println("Podati moraš želeno ime algoritma.");
					System.exit(-1);
				}
				algoritem = args[currArg + 1];
				currArg += 2;
				break;
				
			case "-p":
			case "--povezav":
				if (currArg >= args.length - 1 || args[currArg + 1].charAt(0) == '-') {
					System.err.println("Podati moraš želeno število povezav.");
					System.exit(-1);
				}
				zelenoSteviloPovezav = Integer.parseInt(args[currArg + 1]);
				currArg += 2;
				break;
				
			case "-np":
			case "--nepovezan":
				moraBitiPovezan = false;
				currArg++;
				break;
				
			case "-ns":
			case "--nesortiraj":
				moraSortiratiPovezave = false;
				currArg++;
				break;
				
			default:
				System.err.println("Neveljaven argument: " + args[currArg] + ".");
				System.exit(-1);
			}
		}
		
		// Izračun verjetnosti, da ostane povezava v grafu
		double p = 2 * zelenoSteviloPovezav / (double) (n * (n + 1));
		
		ArrayList<Pair<Integer, Integer>> povezave = new ArrayList<Pair<Integer, Integer>>(); // Zdrava zabava, emajrajt
		
		// Izbira algoritma
		switch (algoritem) {
		
		case "default":
			alg1(n, p, povezave);
			if (moraBitiPovezan)
				povezi(povezave, n);
			break;
			
		case "alt":
			if (moraBitiPovezan = false)
				System.err.println("Opozorilo: Alternativni algoritem vedno ustvari povezan graf.");
			alg2(n, p, povezave);
			break;
			
		default:
			System.err.println("Neveljaven algoritem: " + algoritem + ".");
			System.exit(-1);
		}
		
		// Sortiranje povezav
		if (moraSortiratiPovezave)
			povezave.sort(null);
		
		// Izpis
		System.out.println(n + " " + povezave.size());
		povezave.forEach(par -> System.out.println(par.toString())); // Uf, a so to lambdice?
	}
	
}
