import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class RPLS extends Application {
	
	BorderPane startScreen;
	
	TextField portField;
	Button submitPortButton;
	Text promptPort;
	Text title;
	Text displayPlayersConnected;
	
	HBox portAndSubmit;
	
	VBox waitingScreen;
	VBox serverScreen;

	//Listview to store all the actions
	ListView<String> serverListView;
	
	Scene startScene;
	Scene serverScene;
	Scene waitingScene;
	
	GameInfo gameState = new GameInfo();
	Server serverConnection;


	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		
		primaryStage.setTitle("RPLS Server");		//Set title for the scene
		
		this.startScreen = new BorderPane();			//Initialize borderpane for scene
		
		//Initialize the text for port number 
		this.promptPort = new Text("Enter a port number");
		this.promptPort.setStyle("-fx-font: 12 arial");

		this.submitPortButton = new Button("Start");	
		this.portField = new TextField();
		this.portAndSubmit = new HBox();
		portAndSubmit.getChildren().addAll(promptPort, portField, submitPortButton);
		this.startScreen.setCenter(portAndSubmit);

		//Initialize text for the title and set it at the top
		this.title = new Text("Rock, Paper, Scissors, Spock, Lizard Server");
		this.title.setStyle("-fx-font: 26 arial");
		this.startScreen.setTop(title);
		
		//Set margins for borderpane elements
		BorderPane.setMargin(title, new Insets(50,0,0,35));
		BorderPane.setMargin(portAndSubmit, new Insets(200,0,0,120));
		
		//Set margins for the HBox
		HBox.setMargin(promptPort, new Insets(5,0,0,0));
		HBox.setMargin(portField, new Insets(0,0,0,10));
		HBox.setMargin(submitPortButton, new Insets(0,0,0,10));

		//Set the background for the scene
		this.startScreen.setStyle("-fx-background-color:#eff0e9");
		
		this.startScene = new Scene(startScreen,600,400);
		
		primaryStage.setScene(startScene);
		primaryStage.show();
		
		submitPortButton.setOnAction(new EventHandler<ActionEvent>() {
			
			public void handle(ActionEvent event) {
				
				int portNum = Integer.parseInt(portField.getText());		//Get the port number
				serverScene = getServerScene();								//Get a new server scene
				primaryStage.setScene(serverScene);
				serverConnection = new Server(data -> {
					Platform.runLater(()->{
						gameState = (GameInfo) data;
						
						//Check if a player disconnected
						if (gameState.isDisconnect == true) {
							serverListView.getItems().add("Player " + gameState.disconnectID + " disconnected!");
							displayPlayersConnected.setText("Players Connected:" + gameState.playerCount);
						}
						//Check if a new player connected
						else if (gameState.newPlayer == true) {
							displayPlayersConnected.setText("Players Connected:" + gameState.playerCount);
							serverListView.getItems().add("Player " + gameState.playerID + " has connected");

						}
						//Check if any of the players are playing again 
						else if (gameState.p1PlayAgain == true || gameState.p2PlayAgain == true) {
							
							if (gameState.p1PlayAgain == true && gameState.p2PlayAgain == true) {
								serverListView.getItems().add("Both players are playing again");
								serverListView.getItems().add("Both players are playing again");
							}
							else if (gameState.p1PlayAgain == true) 
								serverListView.getItems().add("Player 1 is playing again");
							else 
								serverListView.getItems().add("Player 2 is playing again");
						}
						else if(gameState.playerCount >= 2) {
							
							//Update the GUI
							if (gameState.updateServerUI == true) {
								
								//Add what each player played to the listViews
								serverListView.getItems().add("Player 1 played " +  gameState.p1Plays);
								serverListView.getItems().add("Player 2 played " + gameState.p2Plays);
								
								//Update listview with who won and who loss
								if (gameState.roundWinner == "p1") {
									serverListView.getItems().add("Player 1 won the round");
									serverListView.getItems().add("Player 2 loss the round");
								}
								else if (gameState.roundWinner == "p2") {
									serverListView.getItems().add("Player 1 loss the round");
									serverListView.getItems().add("Player 2 won the round");
								}
								else {
									serverListView.getItems().add("The round ended in a draw");
								}
								
								//Add the points of each player to the listView
								serverListView.getItems().add("Player 1 has " + gameState.p1Points + " points");
								serverListView.getItems().add("Player 2 has " + gameState.p2Points + " points");
								
								//Add who won and who loss to the listView
								if (gameState.winnerFound == true) {
									if (gameState.gameWinner.equals("p1")) {
										serverListView.getItems().add("Player 1 wins!");
										serverListView.getItems().add("Player 2 loses!");
									}
									else {
										serverListView.getItems().add("Player 1 loses!");
										serverListView.getItems().add("Player 2 wins!");
									}
								}
							}			
						}
						
					});
				},portNum); 	
			}
		});
		
		primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent t) {
                Platform.exit();
                System.exit(0);
            }
        });
		
	}
	
	//make the server scene
	public Scene getServerScene() {
		
		//Create the borderpane and set style 
		this.serverScreen = new VBox();
		this.serverScreen.setStyle("-fx-background-color:#eff0e9");
		this.title.setStyle("-fx-font: 30 arial");
		
		//Create listview for the server
		this.serverListView = new ListView<String>();
		this.serverListView.setMaxWidth(700);

		//Display information on how many players are connected
		this.displayPlayersConnected = new Text("Players Connected:0");
		this.displayPlayersConnected.setStyle("-fx-font: 18 arial");

		//Add the title and listview to the VBox
		this.serverScreen.getChildren().addAll(title, displayPlayersConnected, serverListView);
		
		//Add margins for the VBox
		VBox.setMargin(title, new Insets(10,0,0,150));
		VBox.setMargin(displayPlayersConnected, new Insets(15,0,0,350));
		VBox.setMargin(serverListView, new Insets(20,0,0,100));

		

		
		return new Scene(serverScreen, 900,600);
	}
	
	/*	NOT USED
	public Scene getWaitingScene(int port) {
		
		this.waitingScreen = new VBox();
		this.waitingScreen.setStyle("-fx-background-color:#eff0e9");
		
		//Set the text for waiting for players to connect and style it
		Text displayWaiting = new Text("Waiting for players to connect to port " + port);
		displayWaiting.setStyle("-fx-font: 20 arial");
		
		//Set the text for players connected and style it
		this.displayPlayersConnected = new Text("Players Connected:0");
		this.displayPlayersConnected.setStyle("-fx-font: 20 arial");
		
		//Add the text to the Vbox
		waitingScreen.getChildren().addAll(displayWaiting,displayPlayersConnected);
		
		//Set margins for elements in the vbox
		VBox.setMargin(displayWaiting, new Insets(30,0,0,60));
		VBox.setMargin(displayPlayersConnected, new Insets(30,0,0,150));
		
		return new Scene(waitingScreen,500,150);
	}
	*/

}
