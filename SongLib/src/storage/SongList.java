//Haoran Yu, Zikang Xia
package storage;

import java.util.*; 
import java.io.*;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import song_lib.SongLibController;

public class SongList {
	public class Song {
		private String name;
		private String artist;
		private String album;
		private String year;
		private SimpleStringProperty namep;
		private SimpleStringProperty artistp;

		private Song(String s) {
			try {
				String[] splited = s.split(",");
				this.name = splited[0];
				this.artist = splited[1];
				this.album = splited[2];
				this.year = splited[3];
				
			} catch(Exception e) {
				this.album = "";
				this.year = "";
			}
			this.namep = new SimpleStringProperty(this.name);
			this.artistp = new SimpleStringProperty(this.artist);
		}
		
		public SimpleStringProperty getNameL() {
			return namep;
		}
		public SimpleStringProperty getArtistL() {
			return artistp;
		}
		
		public String getName() {
			return name;
		}
		public String getArtist() {
			return artist;
		}
		
		public String getAlbum() {
			return album;
		}
		
		public String getYear() {
			return year;
		}
		@Override
		public String toString() {
			String content = "";
			content += this.name;
			content += ",";
			content += this.artist;
			content += ",";
			content += this.album;
			content += ",";
			content += this.year;
			return content;
		}

		public boolean equals(Song song) {
			return this.name.equals(song.name) && this.artist.equals(song.artist);
		}
	}
	
	private static Comparator<Song> SORT_SONG = new Comparator<Song>() {
		@Override
		public int compare(Song song1, Song song2) {
			if (song1.name.toLowerCase().equals(song2.name.toLowerCase())) {
				return song1.artist.toLowerCase().compareTo(song2.artist.toLowerCase());
			} else {
				return song1.name.toLowerCase().compareTo(song2.name.toLowerCase());
			}
		}
	};
	
	public enum Action {
		ADD,
		DELETE,
		EDIT,
		NOACTION
	};
	private ObservableList<Song> songList = FXCollections.observableList(new ArrayList<>());
	private Action prevAction = Action.NOACTION;
	private int prevIndex;
	private Song prevSong;
	private String filePath;
	private int index = 0;
	private Song emptySong= new Song("");
	public SongList(String filePath) {
		//read the file and extract song list information from it.
		this.filePath = filePath;
		readFromFile();
	}
	
	public ObservableList<Song> getSongList() {
		return this.songList;
	}
	
	public int getIndex() {
		return index;
	}

	public Song emptySong() {
		return emptySong;
	}

	public boolean editSongList(int index, String newSong, String getYear) {
		Song curSong = new Song(newSong);		
		
		if (compareSonglist(curSong) || curSong.equals(this.songList.get(index))) {
			//New: check if year is a positive integer, if not, this method will add nothing but return true
			if(!getYear.isEmpty()) {
				for (int i = 0; i < getYear.length(); i++){
					if(!Character.isDigit(getYear.charAt(i))) {
						return true;
					}
				}
				if (Integer.parseInt(getYear) < 0) {
					return true;
				}
			}
			//End here
			
			this.prevSong = this.songList.get(index);
			this.songList.set(index, curSong);
			Collections.sort(this.songList, SORT_SONG);
			this.prevAction = Action.EDIT;
			this.prevIndex = this.songList.indexOf(curSong);
			this.index = this.prevIndex;
			return true;
		} else {
			return false;
		}
	}
	
	public boolean addSongList(String newSong, String getYear) {
		Song curSong = new Song(newSong);		
		
		if (compareSonglist(curSong)) {
			//New: check if year is a positive integer, if not, this method will add nothing but return true
			if(!getYear.isEmpty()) {
				for (int i = 0; i < getYear.length(); i++){
					if(!Character.isDigit(getYear.charAt(i))) {
						return true;
					}
				}
				if (Integer.parseInt(getYear) < 0) {
					return true;
				}
			}
			//End here
			
			this.prevSong = null;
			this.songList.add(curSong);
			Collections.sort(this.songList, SORT_SONG);
			this.prevAction = Action.ADD;
			this.prevIndex = this.songList.indexOf(curSong);
			this.index = this.prevIndex;
			return true;
		} else {
			return false;
		}
	}
	
	public void deleteSongList(int index) {
		this.prevAction = Action.DELETE;
		this.prevSong = this.songList.get(index);
		this.prevIndex = -1;
		this.songList.remove(index);
	}
	
	public void backToLastStep() {
		switch(prevAction) {
		case ADD:
			this.songList.remove(this.prevIndex);
			return;
		case DELETE:
			this.songList.add(this.prevSong);
			Collections.sort(this.songList, SORT_SONG);
			return;
		case EDIT:
			this.songList.remove(this.prevIndex);
			this.songList.add(this.prevSong);
			Collections.sort(this.songList, SORT_SONG);
			return;
		default:
			return;
		}
	}
	
	private boolean compareSonglist(Song curSong) {
		//New: Add case INsensitive function
		for (int i = 0; i < songList.size(); i++) {
			if(songList.get(i).name.toLowerCase().equals(curSong.name.toLowerCase()) 
					&& songList.get(i).artist.toLowerCase().equals(curSong.artist.toLowerCase())) {
				return false;
			}
		}
		return true;
	}
	
	public void writeToFile() {
		try {
			File file = new File(this.filePath);
			file.createNewFile();
			BufferedWriter writer;
			writer = new BufferedWriter(new FileWriter(this.filePath));
			String content = "";
			for (int i = 0; i < songList.size(); i++) {
				Song song = songList.get(i);
				content += song.toString();
				if(i != songList.size() - 1) {
					content += "\n";
				}
			}
			writer.write(content);
			writer.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void readFromFile() {
		try {
			File file = new File(this.filePath);
			if (!file.exists()) {
				return;
			}
			BufferedReader reader;
			reader = new BufferedReader(new FileReader(this.filePath));
			String line = reader.readLine();
			while(line!=null) {
				this.songList.add(new Song(line));
				line = reader.readLine();
			}
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}

