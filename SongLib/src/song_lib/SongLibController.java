//Haoran Yu, Zikang Xia
package song_lib;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import storage.SongList;
import storage.SongList.Action;
import storage.SongList.Song;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.value.ObservableBooleanValue;

import java.util.List; 

public class SongLibController {
    @FXML
    private TableView<SongList.Song> songTable;
    @FXML
    private TableColumn<SongList.Song, String> nameC;
    @FXML
    private TableColumn<SongList.Song, String> artistC;

    @FXML
    private Label nameL;
    @FXML
    private Label artistL;
    @FXML
    private Label albumL;
    @FXML
    private Label yearL;
    
    @FXML
    private Button addB;
    @FXML
    private Button deleteB;
    @FXML
    private Button editB;
    @FXML
    private Button cancelB;
    @FXML
    private Button confirmB;
    
    private IntegerProperty actionDone = new SimpleIntegerProperty(-1);
    private IntegerProperty isDelete = new SimpleIntegerProperty(-1);
    
    @FXML
    private TextField nameF;
    @FXML
    private TextField artistF;
    @FXML
    private TextField albumF;
    @FXML
    private TextField yearF;
    
    private SongList songList;
    
    private SongList.Action prevAction;
    
    public SongLibController() {
    }
    
    @FXML
    private void initialize() {
    	
    	// Initialize the person table with the two columns.
    	nameC.setCellValueFactory(cellData -> cellData.getValue().getNameL());
        artistC.setCellValueFactory(cellData -> cellData.getValue().getArtistL());
        songTable.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> showSongList(newValue));
        addB.disableProperty().bind(actionDone.greaterThan(0));
        deleteB.disableProperty().bind(actionDone.greaterThan(0));
        editB.disableProperty().bind(actionDone.greaterThan(0));
        BooleanBinding b1 = artistF.textProperty().isEmpty().or(nameF.textProperty().isEmpty());
        BooleanBinding b = b1.and(this.isDelete.lessThan(0));
        confirmB.disableProperty().bind(actionDone.lessThan(0).or(b));
        cancelB.disableProperty().bind(actionDone.lessThan(0));
        nameF.visibleProperty().bind(actionDone.greaterThan(0).and(this.isDelete.lessThan(0)));
        artistF.visibleProperty().bind(actionDone.greaterThan(0).and(this.isDelete.lessThan(0)));
        albumF.visibleProperty().bind(actionDone.greaterThan(0).and(this.isDelete.lessThan(0)));
        yearF.visibleProperty().bind(actionDone.greaterThan(0).and(this.isDelete.lessThan(0)));
    }
    
    @FXML
    private void delete() {
    	int selectedIndex = songTable.getSelectionModel().getSelectedIndex();
    	if (selectedIndex < 0 ) {
			Alert alert = new Alert(Alert.AlertType.ERROR);
			alert.setTitle("ERROR!");
			alert.setHeaderText("No Song to Delete");
			alert.setContentText("Songlist is empty, no song to be deleted!");
			alert.showAndWait();
    		return;
    	}
    	actionDone.set(1);
    	isDelete.set(1);
    	prevAction = SongList.Action.DELETE;  	
    }
    
    @FXML 
    private void add() {
    	addSong(songList.emptySong());
    	actionDone.set(1);
    	isDelete.set(-1);
    	prevAction = SongList.Action.ADD;
    }
    
    @FXML
    private void edit() {
    	int selectedIndex = songTable.getSelectionModel().getSelectedIndex();
		if (selectedIndex < 0 ) {
			Alert alert = new Alert(Alert.AlertType.ERROR);
			alert.setTitle("ERROR!");
			alert.setHeaderText("No Song to Edit");
			alert.setContentText("Songlist is empty, no song to be edited!");
			alert.showAndWait();
			return;
		}
		SongList.Song song = songTable.getItems().get(selectedIndex);
    	addSong(song);
    	
    	actionDone.set(1);
    	isDelete.set(-1);
    	prevAction = SongList.Action.EDIT;
    	
    }
    
    @FXML
    private void cancel() {
    	actionDone.set(-1);
    	isDelete.set(-1);
    }
    
    @FXML
    private void confirm() {
    	String albumText, yearText;
		if(albumF.textProperty().isEmpty().get()) {
			albumText = ", ";
		} else {
			albumText = "," + albumF.getText();
		}
		if(yearF.textProperty().isEmpty().get()) {
			yearText  = ", ";
		} else {
			yearText = "," + yearF.getText();
		}
    	switch(prevAction) {
    	case ADD: 
    		if(this.songList.addSongList(nameF.getText() + "," + artistF.getText() + albumText + yearText, yearF.getText())) {
    			if(!yearCheck(yearF.getText())){
        			Alert alert = new Alert(Alert.AlertType.ERROR);
            		alert.setTitle("ERROR!");
            		alert.setHeaderText("Year is not a positive integer");
            		alert.setContentText("Year must be a positive integer, add failed!");
            		alert.showAndWait();
        		} else {
        			int index = this.songList.getIndex();
        			songTable.getSelectionModel().select(index);
        		}
    		} else {
    			Alert alert = new Alert(Alert.AlertType.ERROR);
    			alert.setTitle("ERROR!");
    			alert.setHeaderText("Song already exists");
    			alert.setContentText("Song already exists, add failed!");
    			alert.showAndWait();
    		}
    		break;
    	case EDIT:
    		if(this.songList.editSongList(songTable.getSelectionModel().getSelectedIndex(),
    				nameF.getText() + "," + artistF.getText() + "," + albumF.getText() + "," + yearF.getText(), yearF.getText())) {
    			if(!yearCheck(yearF.getText())){
        			Alert alert = new Alert(Alert.AlertType.ERROR);
            		alert.setTitle("ERROR!");
            		alert.setHeaderText("Year is not a positive integer");
            		alert.setContentText("Year must be a positive integer, edit failed!");
            		alert.showAndWait();
        		} else {
        			int index = this.songList.getIndex();
        			songTable.getSelectionModel().select(index);
        		}
    		} else {
    			Alert alert = new Alert(Alert.AlertType.ERROR);
    			alert.setTitle("ERROR!");
    			alert.setHeaderText("Song already exists");
    			alert.setContentText("Song already exists, edit failed!");
    			alert.showAndWait();
    		}
    		break;
    	case DELETE:
    		int selectedIndex = songTable.getSelectionModel().getSelectedIndex();
    		this.songList.deleteSongList(selectedIndex);
        	if (this.songList.getSongList().size()> selectedIndex ) {
        		songTable.getSelectionModel().select(selectedIndex);
        	} else if (this.songList.getSongList().size()> 0 ) {
        		songTable.getSelectionModel().select(selectedIndex - 1);
        	} 
        	break;
    	}
    	songList.writeToFile();
    	actionDone.set(-1);
    	isDelete.set(-1);
    	
    	
    }
    
	public boolean yearCheck(String year) {
		if(!year.isEmpty()) {
			for (int i = 0; i < year.length(); i++){
				if(!Character.isDigit(year.charAt(i))) {
					return false;
				}
			}
			if (Integer.parseInt(year) < 0) {
				return false;
			}
		}
		return true;
	}
    
    public void addSong(SongList.Song song) {
        nameF.setText(song.getName());
        artistF.setText(song.getArtist());
        albumF.setText(song.getAlbum());
        yearF.setText(song.getYear());
    }
    
    private void showSongList(SongList.Song song) {
        if (song != null) {

        	nameL.setText(song.getName());
            artistL.setText(song.getArtist());
            albumL.setText(song.getAlbum());
            yearL.setText(song.getYear());

        } else {
            return;
        }
    }
    
    public void setMainApp(SongLib songLib) {
        this.songList = songLib.getSongList();
        songTable.setItems(this.songList.getSongList());
        if(this.songList.getSongList().size() > 0) {
        	songTable.getSelectionModel().select(0);
        }
    }
}