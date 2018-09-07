import java.io.*;
import java.util.*;



public class SDA1606876872TUGAS2 {	
	private  static HashMap<Position, ArrayList<Pahlawan>> positionPahlawanList;
	public Stack<Position> gudakoPosition;
    public static ArrayList<Pahlawan> gudakoPahlawan = new ArrayList<>();// tempat menyimpan pahlawan yg dpt mengikuti gudako
	public static HashMap<Position, Dungeon> positionDungeonList;
	public static int gudakoLevel = 1;
	public static int time =0;
	
	public static void main(String args[]) throws IOException{
		
		BufferedReader masukan = new BufferedReader(new InputStreamReader( System.in));
		
		String read = masukan.readLine();
		String[] inputAwal = read.split(" ");
		
		int sumHero = Integer.parseInt(inputAwal[0]);
		int sumSummon = Integer.parseInt(inputAwal[1]);
		int sumDungeon = Integer.parseInt(inputAwal[2]);
		int firstMana = Integer.parseInt(inputAwal[3]);
		int row = Integer.parseInt(inputAwal[4]);
		int col= Integer.parseInt(inputAwal[5]);

		
		// variabel u store data
        HashMap<String, Pahlawan> pahlawanName = new HashMap<>();
		HashMap<Position, ArrayList<Pahlawan>> positionPahlawanList = new HashMap<>();
        HashMap<Position, Dungeon> positionDungeonList = new HashMap<>();
		char gudakoMap[][] = new char[120][120];
		
		
		// read pahlawan
		for(int i =0; i< sumHero; i++){
			String readNext = masukan.readLine();
			String[] inputNext = readNext.split(";");
			
			String name = inputNext[0];
			int mana = Integer.parseInt(inputNext[1]);
			long power = Long.parseLong(inputNext[2]);
			int weapon = Pahlawan.convertWeapon(inputNext[3]);
			
			pahlawanName.put(name, new Pahlawan(name, mana, power, weapon));// masukin nama hero serta objek hero nya
			
		}
		
		
		//read summon
		for(int i =0; i< sumSummon; i++){
			String readNext = masukan.readLine();
			String[] inputNext = readNext.split(";");
			
			int tRow = Integer.parseInt(inputNext[0]);
			int tCol = Integer.parseInt(inputNext[1]);
			String listStringHero = inputNext[2];
			
			Position tPos = new Position(tRow, tCol);
			ArrayList<Pahlawan> pahlawanList = new ArrayList<>();
			
            StringTokenizer token = new StringTokenizer(listStringHero, ","); //split nama-nama hero
            while(token.hasMoreTokens()){
                pahlawanList.add(pahlawanName.get(token.nextToken()));// ngambil pahlawanName yg sesuai inputan dari yg udh disimpen 
            }
			
            Collections.sort(pahlawanList);// sort nama pahlawan di pahlawanList
            positionPahlawanList.put(tPos, pahlawanList);// masukin sesuai input row dan col yg bersesuaian
		}
		
		
		//read dungeon
		for(int i=0; i< sumDungeon; i++){
			String readNext = masukan.readLine();
			String[] inputNext = readNext.split(";");
			
			int tRow = Integer.parseInt(inputNext[0]);
			int tCol = Integer.parseInt(inputNext[1]);
			long power = Long.parseLong(inputNext[2]);
			int level = Integer.parseInt(inputNext[3]);
			int weapon = Pahlawan.convertWeapon(inputNext[4]);
			int max_hero = Integer.parseInt(inputNext[5]);
			
	        // mendapat array dungeon dari current position
			Position tPos = new Position (tRow, tCol);
			positionDungeonList.put(tPos, new Dungeon(power, level, weapon, max_hero));
			
		}
		
		
		//jalannya gudako
		Stack<Position> gudakoPosition = new Stack<>();
		int stepRow[] = {0, 1, 0, -1};// prioritas step, karena stack LIFO
		int stepCol[] = {-1, 0, 1, 0};
		
		for(int i =1; i<= row; i++){
			for(int j=1; j<= col; j++){
				gudakoMap[i][j] = (char) masukan.read();// baca row dan col dari input
				if(gudakoMap[i][j]== 'M') gudakoPosition.push(new Position (i,j));// cek titik awal
			}
			masukan.readLine();
		}
		
		while(!gudakoPosition.isEmpty()){
			Position now = gudakoPosition.pop();
			int nRow = now.row;
			int nCol = now.col;
			
			if(gudakoMap[nRow][nCol] == 'X') continue;
			
	        if(gudakoMap[nRow][nCol]=='S'){
	            ArrayList<Pahlawan> nowHero = positionPahlawanList.get(now);
	        	int tempMana = firstMana;
	        	Summon(nRow, nCol, tempMana,nowHero);

	        }
	        
	        if(gudakoMap[nRow][nCol]=='D'){
                Dungeon dungeonNow = positionDungeonList.get(now);
	        	Dungeon(nRow, nCol, dungeonNow);

	        }
	        
	        // menandai petak yang sudah dilalui
	        gudakoMap[nRow][nCol] = 'X';

	        // melakukan pengecekan sesuai prioritas untuk petak selanjutnya
	        for(int i=0; i<4; i++){

	            if(nRow+stepRow[i]>=1 && nRow+stepRow[i]<=row && nCol+stepCol[i]>=1 && nCol+stepCol[i]<=col &&
	               gudakoMap[nRow+stepRow[i]][nCol+stepCol[i]]!='X' && gudakoMap[nRow+stepRow[i]][nCol+stepCol[i]]!='#'){

	                gudakoPosition.push(new Position(nRow+stepRow[i], nCol+stepCol[i]));
	            }
	        }
	        
	        
		}
		
		System.out.println("Akhir petualangan Gudako");
		System.out.println("Level Gudako: " + gudakoLevel);
		System.out.println("Level pahlawan:");
		
		//urutin pahlawan gudako dan levelnya
        Collections.sort(gudakoPahlawan, new GudakoComparatorLevel());
        for(int i=0; i<gudakoPahlawan.size(); i++){
        	System.out.println(gudakoPahlawan.get(i).name + ": " + gudakoPahlawan.get(i).level);
        }
	}

	public static void Summon(int row,int col,int mana, ArrayList<Pahlawan> h){
        ArrayList<String> heroListName = new ArrayList<>(); // heroes yg mengikuti gudako
        
        //get hero
        for(int i=0; i<h.size(); i++){
            if(mana >= h.get(i).mana){
                h.get(i).time = time; // set time, time nya buat hero baru yg masuk ke petak
                gudakoPahlawan.add(h.get(i)); // menambahkan pahlawan yg dpt ikut gudako
                mana-= h.get(i).mana; // mengurangi mana
                heroListName.add(h.get(i).name); // simpen nama pahlawan yg ikut gudako
            }else break;
        }
        
        //print
        if(!heroListName.isEmpty()){
        	System.out.print(row + "," + col + " Pahlawan yang ikut:");
            for(int i=0; i<heroListName.size(); i++){
            	if(i<heroListName.size()-1 && i>=0){
                	System.out.print(heroListName.get(i) + "," );
            	}else{
            		System.out.print(heroListName.get(i));
            	}
            }
            System.out.print("\n");
        }else{
        	System.out.println(row + "," + col + " tidak ada pahlawan yang ikut");
        }
        time++;// time hero baru nambah kalo masuk petak S
	}
	
	public static void Dungeon(int row,int col,Dungeon d){
        Collections.sort(gudakoPahlawan, new GudakoComparatorDungeon(d.weapon));
        
        long sumPower =0;// menyimpan kekuatan pahlawan yg ikut gudako
        ArrayList<String> heroListName = new ArrayList<>(); //heroes yang battle 
        
        // get hero
        for(int i=0; i<gudakoPahlawan.size() && i<d.max_hero; i++){
            sumPower += gudakoPahlawan.get(i).realPower(d.weapon);
            heroListName.add(gudakoPahlawan.get(i).name);
        }
        
        // print
        if(sumPower>= d.power){
        	System.out.print(row + "," + col + " BATTLE, kekuatan: " + sumPower + ", pahlawan: ");
        	for(int i =0; i<heroListName.size(); i++){
            	if(i< heroListName.size()-1 && i>=0){
                	System.out.print(heroListName.get(i) + "," );
            	}else{
            		System.out.print(heroListName.get(i));
            	}
            	gudakoPahlawan.get(i).level += d.level;
        	}
        	gudakoLevel += ((int) heroListName.size())*d.level;
        	System.out.print("\n");
        } else {
            System.out.println(row + "," + col + " RUN, kekuatan maksimal sejumlah: " + sumPower);
        }
         
	}
}


class Position {
	public int row;
	public int col;
	
	public Position(int row, int col) {
		super();
		this.row = row;
		this.col = col;
	}

    // function u cek apakah objeknya sama  
	@Override
	public boolean equals(Object other) {
		Position ot = (Position) other;
		return (this.row==ot.row && this.col==ot.col);
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(row, col);
	}
}


class Pahlawan implements Comparable<Pahlawan>{
	public String name;
	public int mana;
	public long power;
	public int weapon; // 0 (pedang), 1 (panah)
	public int level;
	public int time; // lama ikut gudako
	
    public Pahlawan(String name, int mana, long power, int weapon) {
		super();
		this.name = name;
		this.mana = mana;
		this.power = power;
		this.weapon = weapon;
		this.level = 1 ;
/*		this.time = time;*/
	}

	public static int convertWeapon(String weapon){// mengetahui jenis senjata
        return weapon.equalsIgnoreCase("pedang")?0:1;
    }
    
    public long realPower(int dungeonWeapon){// update power ketika memasuki dungeon
        if(this.weapon > dungeonWeapon) return 2*power; 
        else if(this.weapon < dungeonWeapon) return power/2;
        else return power;
    }

	@Override
	public int compareTo(Pahlawan other) {
		if(this.power!= other.power) return (other.power< this.power ?-1:1);// yang diperhatikan this.power, yang diinginkan descending. jadi this.power berada di -1
		else if(this.mana!= other.mana) return (this.mana< other.mana?-1:1);// yg dilihat this.mana, urutan ascending. jadi this.mana berada di -1
		else return this.name.compareTo(other.name);// urutin nama
	}
}

class GudakoComparatorDungeon implements Comparator<Pahlawan>{
	public int dungeonWeapon;
	
	public GudakoComparatorDungeon(int dungeonWeapon) {
		super();
		this.dungeonWeapon = dungeonWeapon;
	}

	@Override
	public int compare(Pahlawan p1, Pahlawan p2) {
		long power1 = p1.realPower(dungeonWeapon);
		long power2 = p2.realPower(dungeonWeapon);
		
		if(power1 != power2) return (power2 <power1 ? -1:1);// urutan descending
/*		else if(p1.time != p2.time) return (p2.time<p1.time? -1:1);*/
		else if(p1.time != p2.time) return p1.time - p2.time;// urutan descending
		else return p1.compareTo(p2);
	}
}

class GudakoComparatorLevel implements Comparator<Pahlawan>{

	@Override
	public int compare(Pahlawan p1, Pahlawan p2) {
		if(p1.level!=p2.level) return (p2.level<p1.level?-1:1);// urutan descending
        else if(p1.time!=p2.time) return p1.time - p2.time;/*
		else if(p1.time != p2.time) return (p2.time<p1.time? -1:1);*/// urutan descending
		else return p1.compareTo(p2);
	}
}

class Dungeon{
    public long power;
    public int level;
    public int weapon;// 0 (pedang), 1 (panah)
    public int max_hero;
    
	public Dungeon(long power, int level, int weapon, int max_hero) {
		super();
		this.power = power;
		this.level = level;
		this.weapon = weapon;
		this.max_hero = max_hero;
	}
}




