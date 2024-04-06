//Haoran Yu, Zikang Xia
package song_lib;


import java.io.IOException;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import storage.SongList;
public class SongLib extends Application {

    private Stage primaryStage;
    private SongList songList = new SongList("songlist.txt");

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;

        showSongLib();
        primaryStage.show();
    }

    public void showSongLib() {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("songlib.fxml"));
            AnchorPane layout = (AnchorPane) loader.load();

            primaryStage.setScene(new Scene(layout));
            primaryStage.setTitle("Song Library");
            SongLibController controller = loader.getController();
            controller.setMainApp(this);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public SongList getSongList() {
    	return this.songList;
    }

    public static void main(String[] args) {
        launch(args);
    }
}