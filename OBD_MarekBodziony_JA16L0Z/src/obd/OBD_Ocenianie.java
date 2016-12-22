package obd;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Scanner;

/**
 * 	Praca domow z przedmiotu OBD
 * 	Grupa: JA16L0Z
 * 	@author Marek Bodziony
 *	
 */

public class OBD_Ocenianie {

	public static void main(String[] args) {
				
		// Laduj sterownik JDBC
//		try {
//			Class c = Class.forName("oracle.jdbc.driver.OracleDriver");
//			System.out.println("OK! Zaladowano sterownik JDBC");
//		} catch (ClassNotFoundException e) {
//			System.out.println("BLAD! Nie zaladowano sterownika JDBC!");
//			e.printStackTrace();
//			return;
//		}
		
		// Test polaczenia z baza danych Oracle
//		try {
//			Connection conn = DriverManager.getConnection(Namiary.URL, Namiary.USER, Namiary.PASSWORD);
//			System.out.println("OK! Polaczono z baza danych Oracle");
//			conn.close();
//			System.out.println("OK! Rozlaczono z baza danych Oracle");
//		} catch (SQLException e) {
//			System.out.println("BLAD! Nie polaczono z baza danych Oracle");
//			e.printStackTrace();
//		}
		
		
		try {
						
			Connection conn = DriverManager.getConnection(Namiary.URL, Namiary.USER, Namiary.PASSWORD);
			Statement polecenie = conn.createStatement();
			
			// tworzy odpowiednie tabele w bazie danych i uzupelnie je przyk³adowymi danymi, JESLI NIE BYLO ICH WCZESNIEJ
			createTableIfDontExists(polecenie);
			
			// Program wyswietli powitanie oraz dostepne opcje dla uzytkownika
			System.out.println("\n\t\t>>>\tWitaj w programie \"Ocenianie\"\t<<<\n");
			wyswietlDostepneOpcje();
			
			// Program czeka i odpowiednio reaguje na polecenia uzytkowniaka
			String input = "";
			Scanner scanner = new Scanner(System.in);
			
			while (!input.equals("x")){
				
				System.out.print(">> ");
				input = scanner.nextLine();
				
				if (input.equals("n")) wyswietlListeNauczycieli(polecenie);
				if (input.equals("u")) wyswietlListeUczniow(polecenie);
				if (input.equals("p")) wyswietlListePrzedmiotow(polecenie);
				if (input.equals("s")) wyswietlListeOcen(polecenie);
				if (input.equals("o")) wyswietlListeOcenionychUczniow(polecenie);
				if (input.equals("h")) wyswietlDostepneOpcje();
				if (input.equals("d")) wyswietlInfoOceniania();
				if (input.startsWith("ocena")) ocenianieUcznia(polecenie, input);
			}
			
			scanner.close();
			polecenie.close();
			conn.close();
		}
		catch (SQLException e){
			e.printStackTrace();
			return;
		}
					
		System.out.println(" - koniec programu -");		
	}

	
	// prywatna metoda wyswietli dostepne opcje 
	private static void wyswietlDostepneOpcje() {
		
		System.out.println("  Wybierz odpowiednia opcje :");
		System.out.println("   n - wyswietl liste nauczycieli");
		System.out.println("   u - wyswietl liste uczniow");
		System.out.println("   p - wyswietl liste przedmiotow");
		System.out.println("   s - wyswietl liste dostepnych ocen");
		System.out.println("   o - wyswietl liste ocen przydzielonych uczniom");
		System.out.println("   d - DOKONAJ OCENY UCZNIA");
		System.out.println("   h - wyswietl ponownie dostepne opcje (help)");
		System.out.println("   x - zakoncz dzialanie programu (exit)\n");		
	}
	
	// wyswietl wszystkich nauczycieli
	public static void wyswietlListeNauczycieli(Statement s) throws SQLException{
		ResultSet rs = s.executeQuery("SELECT * FROM nauczyciel");
		System.out.println("  Lista nauczycieli:");
		while (rs.next()){
			System.out.println("  " + rs.getInt(1) + ".\t" + rs.getString(2) + " " + rs.getString(3));
		}
		System.out.println("");
		rs.close();
	}
	
	// wyswietl wszystkich uczniow
	private static void wyswietlListeUczniow(Statement s) throws SQLException{
		ResultSet rs = s.executeQuery("SELECT * FROM uczen");
		System.out.println("  Lista ucziow:");
		while (rs.next()){
			System.out.println("  " + rs.getInt(1) + ".\t" + rs.getString(2) + " " + rs.getString(3));
		}
		System.out.println("");
		rs.close();
	}
	// wyswietl wszystkich przedmiotow
	private static void wyswietlListePrzedmiotow(Statement s) throws SQLException{
		ResultSet rs = s.executeQuery("SELECT * FROM przedmiot");
		System.out.println("  Lista przedmiotow:");
		while (rs.next()){
			System.out.println("  " + rs.getInt(1) + ".\t" + rs.getString(2));
		}
		System.out.println("");
		rs.close();
	}
	//wyswietl wszystkie dostepne oceny
	private static void wyswietlListeOcen(Statement s) throws SQLException{
		ResultSet rs = s.executeQuery("SELECT * FROM ocena");
		System.out.println("  Lista dostepnych ocen:");
		while (rs.next()){
			System.out.println("  " + rs.getInt(1) + ".\t" + rs.getString(2) + " " + rs.getFloat(3));
		}
		System.out.println("");
		rs.close();
	}
	//wyswietl wszystkie oceny przydzielone uczniom
	private static void wyswietlListeOcenionychUczniow(Statement s) throws SQLException{
		ResultSet rs = s.executeQuery("SELECT * FROM ocenianie");
		boolean isAtLeastOne = false;
		System.out.println("  Lista ocen przydzielonych uczniom:");
		while (rs.next()){
			System.out.println("  " + rs.getString(1));
			isAtLeastOne = true;
		}
		if (!isAtLeastOne) System.out.println("  -brak danych-");
		System.out.println("");
		rs.close();
	}
		
		
	// wyswietla informacje o procesie oceniania ucznia
	private static void wyswietlInfoOceniania(){
		System.out.println("  Dokonaj oceny ucznia podajac : slowo kluczowe \"ocena\" oraz id nauczyciela, id ocenianego ucznia, id przedmiotu, ocene, typ oceny (C - czastkowa, S - semestralna).");
		System.out.println("  np.: ocena,1,2,4,3,C");
	}
	
	// ocenianie ucznia
	private static void ocenianieUcznia(Statement s, String in){	
		
		// pobierz dane wywolania, jesli skladnia poprawna
		String [] inputs = pobierzDaneWywolaniaJesliPoprawne(in);	
		
		// jesli skladnia bledna, wyswietl blad i przerwij ocenianie
		if(inputs == null) { System.out.println("  ERROR! Nie dokonano oceny!!! Bledna skladnia wywolania! "); return;}	
		
		// dokonaj oceny ucznia, jesli podano poprawne dane (id nauczyciela, id ucznia, id przedmiotu, id oceny, typ oceny)
		dokonajOcenyUcznia(inputs[1],inputs[2],inputs[3],inputs[4],inputs[5],s);
		
	}
	
	// metoda pobiera dane wywolania, jesli podano poprawna skladnie
	private static String[] pobierzDaneWywolaniaJesliPoprawne(String input){
		String [] inputs = input.split(",");
		if (!inputs[0].equals("ocena")) return inputs = null;								// podano bledne slowo kluczowe - poprawne to "ocena"
		else if(inputs.length != 6) return inputs = null;									// podano zbyt malo lub zbyt wiele wartosci wywolania - powinno byc 6
		else if (!inputs[5].equals("C") && !inputs[5].equals("S")) return inputs = null;	// podano bledny typ oceny - poprawne to "C" i "S"
		
		try{
			for (int i = 1; i < inputs.length - 1; i++){									// gdy podano bledny format id (nauczyciela, ucznia, przedmiotu lub oceny) 
				Integer.parseInt(inputs[i]); 
			}
		}
		catch (NumberFormatException e){ return inputs = null;}
		
		return inputs;		// skladnia wywolania poprawna, zwraca dane wywolania w  formie tablicy String[]
	}
	
	// metoda dokonuje oceny ucznia, jesli podano poprawne dane (id nauczyciela, id ucznia, id przedmiotu, id oceny, typ oceny)
	private static void dokonajOcenyUcznia(String n, String u, String p, String o, String t, Statement s){
		
		int idn = Integer.parseInt(n);
		int idu = Integer.parseInt(u);
		int idp = Integer.parseInt(p);
		int ido = Integer.parseInt(o);
				
		try {
			if(s.executeUpdate("SELECT idn FROM nauczyciel WHERE idn = " + idn) == 0) {				
				System.out.println("  BLAD! Nie ma nauczyciela o idn = " + idn + ".");
				System.out.println("  Ps. Pamietaj, ze zawsze mozesz wyswietlic liste nauczycieli wybierajac 'n'");
				return;
			}
			if(s.executeUpdate("SELECT idu FROM uczen WHERE idu = " + idu) == 0) {
				System.out.println("  BLAD! Nie ma ucznia o idu = " + idu + ".");
				System.out.println("  Ps. Pamietaj, ze zawsze mozesz wyswietlic liste uczniow wybierajac 'u'");
				return;
			}
			if(s.executeUpdate("SELECT idp FROM przedmiot WHERE idp = " + idp) == 0) {
				System.out.println("  BLAD! Nie ma przedmiotu o idp = " + idp + ".");
				System.out.println("  Ps. Pamietaj, ze zawsze mozesz wyswietlic liste przedmiotow wybierajac 'p'");
				return;
			}
			if(s.executeUpdate("SELECT ido FROM ocena WHERE ido = " + ido) == 0) {
				System.out.println("  BLAD! Nie ma oceny o ido = " + ido + ".");
				System.out.println("  Ps. Pamietaj, ze zawsze mozesz wyswietlic liste dostepnych ocen wybierajac 's'");
				return;
			}
			// jesli typ oceny to "S", sprawdz czy ocena semestralna nie zostala juz przydzielona uczniowi
			if(t.equals("S") && s.executeUpdate("SELECT * FROM ocenianie WHERE rodzaj_oceny = '" + idn + "; " + idu + "; " + idp + "; " + ido + "; " + t + ";'") == 1){
				System.out.println("  BLAD! Ocena semestralna dla ucznia z tego przedmiotu zostala juz wystawiona.");
				System.out.println("  Ps. Pamietaj, ze zawsze mozesz wyswietlic liste wystawionych ocen wybierajac 'o'");
				return;
			}
						
			// jesli dane poprawne OCEN UCZNIA (tj. dodaj do tabeli 'ocenianie' dopowiedni rekord)
			s.executeUpdate("INSERT INTO ocenianie VALUES ('" + idn + "; " + idu + "; " + idp + "; " + ido + "; " + t + ";')");
			System.out.println("  OK! Poprawnie dokonano oceny ucznia!");
						
		}
		catch(SQLException e){
			System.out.println("  BLAD! Cos poszlo nie tak! Nie udalo sie ocenic ucznia!");
			e.printStackTrace();
		}
	}

	// prywatna metoda do tworzenia odpowiednich tabel i uzupelniania ich danymi (w przypadku braku tych tabel w bazie danych)
	private static void createTableIfDontExists(Statement s) throws SQLException {
		
		boolean isTableNauczycielCreated 	= false;
		boolean isTableUczenCreated 		= false;
		boolean isTablePrzedmiotCreated 	= false;
		boolean isTableOcenaCreated 		= false;
		boolean isTableOcenianieCreated		= false;
				
		ResultSet rs = s.executeQuery("SELECT table_name FROM user_tables");		// pobiera nazwy wszystkich tabel z bazy danych		
		
		// sprawdza czy odpowiednie Tabele sa utworzone w bazie danych
		while (rs.next()){
			if (rs.getString(1).equals("NAUCZYCIEL")) isTableNauczycielCreated = true;
			else if (rs.getString(1).equals("UCZEN")) isTableUczenCreated = true;
			else if (rs.getString(1).equals("PRZEDMIOT")) isTablePrzedmiotCreated = true;
			else if (rs.getString(1).equals("OCENA")) isTableOcenaCreated = true;
			else if (rs.getString(1).equals("OCENIANIE")) isTableOcenianieCreated = true;
		}
		// jesli nie ma odpowiednich tabel w bazie danych to je tworzy i wypelnia danymi
		if (isTableNauczycielCreated == false) {
			s.execute("CREATE TABLE nauczyciel (idn INTEGER NOT NULL, nazwisko CHAR(30) NOT NULL, imie CHAR(20) NOT NULL)");
			s.executeUpdate("INSERT INTO nauczyciel VALUES (1,'Adamski','Karol')");
			s.executeUpdate("INSERT INTO nauczyciel VALUES (2,'Baranowski','Michal')");
			s.executeUpdate("INSERT INTO nauczyciel VALUES (3,'Kucharska','Anna')");
			s.executeUpdate("INSERT INTO nauczyciel VALUES (4,'Zawadzki','Robert')");
			System.out.println("Ok! Stworzono tabele NAUCZYCIEL i uzupelniono ja danymi");
		}
		if (isTableUczenCreated == false) {
			s.execute("CREATE TABLE uczen (idu INTEGER NOT NULL, nazwisko CHAR(30) NOT NULL, imie CHAR(20) NOT NULL)");
			s.executeUpdate("INSERT INTO uczen VALUES (1,'Borowski','Szczepan')");
			s.executeUpdate("INSERT INTO uczen VALUES (2,'Centkiewicz','Michalina')");
			s.executeUpdate("INSERT INTO uczen VALUES (3,'Domaniuk','Katarzyna')");
			s.executeUpdate("INSERT INTO uczen VALUES (4,'Holland','Jan')");
			s.executeUpdate("INSERT INTO uczen VALUES (5,'Michalik','Dorota')");
			System.out.println("Ok! Stworzono tabele UCZEN i uzupelniono ja danymi");
		}
		if (isTablePrzedmiotCreated == false) {
			s.execute("CREATE TABLE przedmiot (idp INTEGER NOT NULL, nazwa CHAR(20) NOT NULL)");
			s.executeUpdate("INSERT INTO przedmiot VALUES (1,'Fizyka')");
			s.executeUpdate("INSERT INTO przedmiot VALUES (2,'Chemia')");
			s.executeUpdate("INSERT INTO przedmiot VALUES (3,'Biologia')");
			s.executeUpdate("INSERT INTO przedmiot VALUES (4,'Matematyka')");
			s.executeUpdate("INSERT INTO przedmiot VALUES (5,'J.Polski')");
			System.out.println("Ok! Stworzono tabele PRZEDMIOT i uzupelniono ja danymi");
		}
		if (isTableOcenaCreated == false) {
			s.execute("CREATE TABLE ocena (ido INTEGER NOT NULL, wartosc_opis CHAR(20) NOT NULL, wartosc_num FLOAT NOT NULL)");
			s.executeUpdate("INSERT INTO ocena VALUES (1,'ndst', 1.0)");
			s.executeUpdate("INSERT INTO ocena VALUES (2,'dop', 2.0)");
			s.executeUpdate("INSERT INTO ocena VALUES (3,'dst', 3.0)");
			s.executeUpdate("INSERT INTO ocena VALUES (4,'db', 4.0)");
			s.executeUpdate("INSERT INTO ocena VALUES (5,'bdb', 5.0)");
			s.executeUpdate("INSERT INTO ocena VALUES (6,'cel', 6.0)");
			System.out.println("Ok! Stworzono tabele OCENA i uzupelniono ja danymi");
		}
		if (isTableOcenianieCreated == false) {
			s.execute("CREATE TABLE ocenianie (rodzaj_oceny CHAR(20) NOT NULL)");
			System.out.println("Ok! Stworzono tabele OCENIANIE\n\n");
		}
	}

}
