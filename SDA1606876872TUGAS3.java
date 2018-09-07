import java.util.*;
import java.io.*;

class File implements Comparable<File> {
	String name;
	String type;
	int size;
	Folder parent;
	int id;

	public static int counter = 0;

	public File(String name, String type, int size, Folder parent, int id){
		this.name = name;
		this.type = type;
		this.size = size;
		this.parent = parent;
		this.id = id;// urutan masukin file ke files
	}

	@Override
	public int compareTo(File ot){
		if(this.name.equals(ot.name)) return this.id - ot.id;
		return this.name.compareTo(ot.name);
	}
}

class Folder implements Comparable<Folder> {
	String name;
	int sizeAll;
	boolean containsFile;
	TreeSet<Folder> folders;
	TreeSet<File> files;
	String type;
	Folder parent;

	public Folder(String name, Folder parent){
		this.name = name;
		this.sizeAll = 1;
		this.containsFile = true;
		this.folders = new TreeSet<>();
		this.files = new TreeSet<>();
		this.type = null;
		this.parent = parent;
	}

	public Folder selectFolder(String destinationName){
		// current folder
		if(name.equals(destinationName)) return this;
		if(!containsFile){
			// iterate all folders
			Iterator<Folder> iter = folders.iterator();
			while(iter.hasNext()){
				Folder result = iter.next().selectFolder(destinationName);
				if(result != null) return result;
			}
		}
		// not found
		return null;
	}

	public void addFolder(Folder folder){
		if(containsFile){
			// move all the files
			Iterator<File> iter = files.iterator();
			while(iter.hasNext()){
				folder.putFile(iter.next());//masukin dan cek file nya
			}
			// set contains folder
			containsFile = false;
			files.clear();
		}
		folder.parent = this;
		folders.add(folder);
	}

	public boolean eraseFolder(String destinationName){
		if(!containsFile){
			// iterate all folders
			Iterator<Folder> iter = folders.iterator();
			while(iter.hasNext()){
				Folder now = iter.next();
				if(destinationName.equals(now.name)){
					folders.remove(new Folder(destinationName, null));
					return true;
				}
				boolean removed = now.eraseFolder(destinationName);
				if(removed) return true;
			}
		}
		return false;
	}

	public Folder putFile(File file){
		if(!containsFile){// mengandung folder
			// iterate all folders
			Iterator<Folder> iter = folders.iterator();
			while(iter.hasNext()){
				Folder folder = iter.next().putFile(file);
				if(folder != null) return folder;
			}
			return null;
		} else {
			// insert file
			if(type == null || type.equals(file.type)){
				files.add(file);
				file.parent = this;
				type = file.type;
				return this;
			}
			return null;
		}
	}

	public Folder putFileParent(File file, Folder before){
		if(!containsFile){
			ArrayList<Folder> tempFolder = new ArrayList<>();

			Iterator<Folder> iter = folders.iterator();
			boolean found = false;
			while(iter.hasNext()){
				Folder now = iter.next();
				if(now.equals(before)){
					found = true;
					continue;
				}
				if(found){
					Folder folder = now.putFile(file);
					if(folder != null) return folder;
				} else {
					tempFolder.add(now);
				}
			}
			for(int i=0; i<tempFolder.size(); i++){
				Folder folder = tempFolder.get(i).putFile(file);
				if(folder != null) return folder;
			}
		} else {
			Folder folder = putFile(file);
			if(folder != null) return folder;
		}
		if(parent != null) return parent.putFileParent(file, this);
		return null;
	}

	public int eraseFile(String destinationName){
		int sum = 0;
		if(!containsFile){
			// iterate all folders
			Iterator<Folder> iter = folders.iterator();
			while(iter.hasNext()){
				sum += iter.next().eraseFile(destinationName);
			}
			return sum;
		}
		// iterate all files
		ArrayList<File> tempFile = new ArrayList<>();
		Iterator<File> iter = files.iterator();
		while(iter.hasNext()){
			File file = iter.next();
			if(destinationName.equals(file.name)) tempFile.add(file);//masukin semua file bernama destination tanpa perhatiin tipe
		}
		for(int i=0; i<tempFile.size(); i++){
			files.remove(tempFile.get(i));
			sum++;
		}
		return sum;
	}

	public StringBuilder search(String destinationName){
		return search(destinationName, 0);
	}

	public StringBuilder search(String destinationName, int depth){
		StringBuilder tempBuilder1 = new StringBuilder();
		StringBuilder tempBuilder2 = new StringBuilder();

		for(int i=0; i<depth; i++){
			tempBuilder1.append("  ");
		}
		tempBuilder1.append("> " + name + "\n");

		if(containsFile){
			// iterate all files
			Iterator<File> iter = files.iterator();
			while(iter.hasNext()){
				File file = iter.next();
				if(destinationName.equals(file.name)){
					for(int j=0; j<depth+1; j++){
						tempBuilder2.append("  ");
					}
					tempBuilder2.append("> " + file.name + "." + type + "\n");
				}
			}
		} else {
			// iterate all folders
			Iterator<Folder> iter = folders.iterator();
			while(iter.hasNext()){
				Folder folder = iter.next();
				StringBuilder result = folder.search(destinationName, depth+1);
				if(destinationName.equals(folder.name) || result != null){
					tempBuilder2.append(result);
				}
			}
		}
		// check if found or not
		if(tempBuilder2.length() == 0){
			tempBuilder1 = null;
		} else {
			tempBuilder1.append(tempBuilder2);
		}
		return tempBuilder1;
	}

	public int updateSize(){
		int sum = 0;
		if(containsFile){
			// iterate all files
			Iterator<File> iter = files.iterator();
			while(iter.hasNext()){
				sum += iter.next().size;
			}
		} else {
			// iterate all folders
			Iterator<Folder> iter = folders.iterator();
			while(iter.hasNext()){
				sum += iter.next().updateSize();
			}
		}
		return sizeAll = sum + 1;
	}

	public StringBuilder getStructure(){
		updateSize();
		return getStructure(0);
	}

	public StringBuilder getStructure(int depth) {
		StringBuilder tempBuilder = new StringBuilder();

		// print name
		for(int i=0; i<depth; i++){
			tempBuilder.append("  ");
		}
		tempBuilder.append("> " + name + " " + sizeAll + "\n");

		if(containsFile){
			// iterate all files
			Iterator<File> iter = files.iterator();
			while(iter.hasNext()){
				File file = iter.next();
				for(int j=0; j<depth+1; j++){
					tempBuilder.append("  ");
				}
				tempBuilder.append("> " + file.name + "." + file.type + " " + file.size + "\n");
			}
		} else {
			// iterate all folders
			Iterator<Folder> iter = folders.iterator();
			while(iter.hasNext()){
				StringBuilder result = iter.next().getStructure(depth+1);
				tempBuilder.append(result);
			}
		}

		return tempBuilder; 
	}


	@Override
	public int compareTo(Folder ot){
		return this.name.compareTo(ot.name);
	}
}
	
	
public class SDA1606876872TUGAS3 {
	public static void main(String args[]) throws IOException {

		BufferedReader in = new BufferedReader(new InputStreamReader( System.in));
		BufferedWriter out = new BufferedWriter(new OutputStreamWriter(System.out));
		
		// create default folder
		Folder root = new Folder("root", null);
		
		while(true){
			// parse the input
			String temp = in.readLine();
			if(temp == null) break;
			String order[] = temp.split(" ");

			if(order[0].equalsIgnoreCase("add")){
				// parse the data
				String name = order[1];
				String destinationName = order[2];

				// get the folder and insert
				Folder destination = root.selectFolder(destinationName);
				destination.addFolder(new Folder(name, null));
			} else if(order[0].equalsIgnoreCase("insert")){
				// parse the data
				String title = order[1];
				String name = title.split("\\.")[0];
				String type = title.split("\\.")[1];
				int size = Integer.parseInt(order[2]);
				String destinationName = order[3];

				// get the folder and insert
				Folder destination = root.selectFolder(destinationName);
				Folder folder = destination.putFileParent(new File(name, type, size, null, File.counter++), null);
				if(folder != null) out.write(title + " added to " +  folder.name + "\n");
			} else if(order[0].equalsIgnoreCase("remove")){
				String name = order[1];

				// remove folder
				boolean removed = root.eraseFolder(name);
				if(removed) out.write("Folder " + name + " removed\n");

				// remove file
				int result = root.eraseFile(name);
				out.write(result + " File " + name + " removed\n");
			} else if(order[0].equalsIgnoreCase("search")){
				String name = order[1];

				// search file or folder
				out.write(root.search(name).toString());
			} else if(order[0].equalsIgnoreCase("print")){
				String destinationName = order[1];

				// get the folder
				Folder destination = root.selectFolder(destinationName);
				out.write(destination.getStructure().toString());
			}
			out.flush();
		}

	}
}





