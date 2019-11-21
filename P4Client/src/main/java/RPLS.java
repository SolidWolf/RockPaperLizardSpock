import javafx.animation.PauseTransition;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.util.Duration;

public class RPLS extends Application {
	
	Client clientConnection;
	BorderPane startScreen;
	BorderPane playingScreen;
	
	HBox playerView;
	HBox ipAndPortMenu;
	HBox playSelection;
	HBox playAgainOptions;
	
	//VBoxs
	VBox playAgainScreen;
	VBox playMenu;
	VBox playerInfo;
	VBox opponentInfo;
	VBox screenInfo;
	VBox lobbyScreen;
	VBox clientChallenge;
	
	//Text displayed on the screen
	Text title;
	Text playingTitle;
	Text promptIPAndPort;
	Text promptWhatToPlay;
	Text playerText;
	Text opponentText;
	Text clientListText;
	
	//User inputs
	TextField portField;
	TextField ipField;
	
	//ListViews to store the players and the actions
	ListView<String> actionList;
	ListView<String> clientList;
	
	Button submitButton;
	Button playAgainButton;
	Button quitButton;
	
	Scene lobbyScene;
	Scene startScene;
	Scene playingScene;
	Scene playAgainScene;
	
	//Create ImageView to show play choices
	ImageView rockImage;
	ImageView paperImage;
	ImageView scissorsImage;
	ImageView lizardImage;
	ImageView spockImage;
	ImageView playerPlayed;
	ImageView opponentPlayed;
	
	//EventHandlers
	EventHandler<MouseEvent> playSelect;
	EventHandler<MouseEvent> challengeSelect;
	EventHandler<ActionEvent> quitButtonHandler;
	
	GameInfo gameState = new GameInfo();
	
	PauseTransition pause = new PauseTransition(Duration.seconds(2));
	
	String ip;
	int port;
	int playerID;
	int sentForID = 0;

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		launch(args);
	}

	//feel free to remove the starter code from this method
	@Override
	public void start(Stage primaryStage) throws Exception {
		// TODO Auto-generated method stub
		primaryStage.setTitle("RPLS Client");
		
		this.startScreen = new BorderPane();			//Initialize borderpane for scene
		
		//Initialize the text for ip address and port number 
		this.promptIPAndPort = new Text("Enter an IP address and port number");
		this.promptIPAndPort.setStyle("-fx-font: 12 arial");
		
		//Initialize start button
		this.submitButton = new Button("Start");	
		
		//Initialize and style text for port and ip
		this.portField = new TextField();
		this.portField.setPromptText("Port Number");
		this.portField.setMaxWidth(100);
		this.ipField = new TextField();
		this.ipField.setPromptText("IP Address");
		this.ipField.setMaxWidth(100);
		
		this.ipAndPortMenu = new HBox();
		ipAndPortMenu.getChildren().addAll(promptIPAndPort,ipField, portField, submitButton);
		this.startScreen.setCenter(ipAndPortMenu);

		//Initialize text for the title and set it at the top
		this.title = new Text("Rock, Paper, Scissors, Spock, Lizard Client");
		this.title.setStyle("-fx-font: 26 arial");
		this.startScreen.setTop(title);
		
		//Set margins for borderpane elements
		BorderPane.setMargin(title, new Insets(50,0,0,35));
		BorderPane.setMargin(ipAndPortMenu, new Insets(200,0,0,50));
		
		//Set margins for the HBox
		HBox.setMargin(promptIPAndPort, new Insets(5,0,0,0));
		HBox.setMargin(ipField, new Insets(0,0,0,10));
		HBox.setMargin(portField, new Insets(0,0,0,10));
		HBox.setMargin(submitButton, new Insets(0,0,0,10));

		//Set the background for the scene
		this.startScreen.setStyle("-fx-background-color:#eff0e9");
		
		this.startScene = new Scene(startScreen,600,400);
		primaryStage.setScene(startScene);
		primaryStage.show();
		
		this.submitButton.setOnAction(new EventHandler<ActionEvent>(){
			public void handle(ActionEvent action) {
				try {
					port = Integer.parseInt(portField.getText());
					ip = ipField.getText();
					lobbyScene = getLobbyScene();
					primaryStage.setScene(lobbyScene);
					clientConnection = new Client(data->{
						Platform.runLater(()->{
							gameState = (GameInfo) data;

							//Update the clients that user can challenge
							if(gameState.newPlayer == true) {
								playerID = gameState.playerID;							//Get what the id of the user is 
								for(GameInfo.PlayerInfo i : gameState.playerinfo) {
									String ID = Integer.toString(i.clientID);
									if(clientList.getItems().contains("Player " + ID)) {
										continue;							
									} 
									clientList.getItems().add("Player " + ID);
								} 
							}
							else if (gameState.isDisconnect == true) {
								
								actionList.getItems().add("Player " + gameState.disconnectID + " disconnected!");
																
								//Remove the ID from the list of clients
								for (int i = 0; i < clientList.getItems().size(); i++) {
									if (clientList.getItems().get(i).equals("Player " + gameState.disconnectID)) {
										clientList.getItems().remove(i);
										break;
									}
								}
								
								if (gameState.disconnectID == gameState.sentFor) {
									primaryStage.setScene(lobbyScene);
								}
							}
							//Check if it is a challenge
							else if (gameState.challengeAccepted == true) {
								if (gameState.sentBy == playerID || gameState.sentFor == playerID) {
									if (sentForID == 0)
										sentForID = gameState.sentBy;
									playingScene = getPlayingScene();
									primaryStage.setScene(playingScene);
								}
							}
							//Check if the given gameState includes a message
							else if (gameState.isMessage == true) {
								actionList.getItems().add(gameState.message);		//Add the message to the listView
								playerID = gameState.playerID;							//Get what the id of the user is 
							}
							else if (gameState.updateClientUI == true) {
								updateGUI();
								pause.setOnFinished(event -> {
									if (gameState.sentBy == playerID || gameState.sentFor == playerID) {
										primaryStage.setScene(lobbyScene);
									}
								});
								pause.play();
							}
						
						});
						},ip, port);
					clientConnection.start();
				} catch(Exception e) {
					
				}
			}
 		});
		
		
		//event handler for the client list                        //Edit: Angel
        this.challengeSelect = new EventHandler<MouseEvent> () {

	        @Override
	        public void handle(MouseEvent event) {

	        	String[] senterID = clientList.getSelectionModel().getSelectedItem().split(" ", 2);
	        	int challengeForIndex = 0;
	        	
	        	for (int i = 0; i < gameState.playerinfo.size(); i++) {
					if (gameState.playerinfo.get(i).clientID == Integer.parseInt(senterID[1])) 
						challengeForIndex = i;
	        	}
	        	if (Integer.parseInt(senterID[1]) == playerID) {
	        		actionList.getItems().add("You cannot challenge yourself!");
	        	}
	        	else if (gameState.playerinfo.get(challengeForIndex).isPlaying == true) {
	        		actionList.getItems().add("Playing is already playing");
	        	}
	        	else {
	        		gameState.isChallenge = true;
	        		gameState.sentBy = playerID;
		        	gameState.sentFor = Integer.parseInt(senterID[1]);
		        	sentForID = Integer.parseInt(senterID[1]);
		        	clientConnection.send(gameState);
	        	}
	        }
        };
		
        //Event handler for when user choose what to play
		this.playSelect = new EventHandler<MouseEvent> () {

			public void handle(MouseEvent event) {
					
				//Disable the buttons
				rockImage.setDisable(true);
				paperImage.setDisable(true);
				scissorsImage.setDisable(true);
				lizardImage.setDisable(true);
				spockImage.setDisable(true);

				//Set the image for the player 
				ImageView imageview = (ImageView) event.getSource();
				playerPlayed.setImage(new Image(imageview.getId() + ".jpg",150,150,false,false));
				playerPlayed.setVisible(true);

				//get location of player in the list
				int playerChoiceIndex = 0;
				for (int i = 0; i < gameState.playerinfo.size(); i++) {
					if (gameState.playerinfo.get(i).clientID == playerID) 
						playerChoiceIndex = i;
		        }
				    
				//Get the string of what the client played
				gameState.playerinfo.get(playerChoiceIndex).playerPlayed = imageview.getId();
				    
				//Set who its sent by and who it is sent for
				gameState.sentBy = playerID;
				gameState.sentFor = sentForID;
				gameState.isChallenge = false;
				gameState.isPlayed = true;
				    
				//Set the client index in the playerinfo arraylist as played
				gameState.playerinfo.get(playerChoiceIndex).hasPlayed = true;
				    
				//Send the object to the server
				clientConnection.send(gameState);	
			}
		};
		
		
		primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent t) {
                Platform.exit();
                System.exit(0);
            }
        });
		
		//Event handler for the quit button
		this.quitButtonHandler = new EventHandler<ActionEvent>() {
			public void handle(ActionEvent event) {
				Platform.exit();
                System.exit(0);
			}
		}; 
	}
	
	
	public Scene getLobbyScene() {
		
		this.lobbyScreen = new VBox();				//Initialize VBox
		this.playerView = new HBox();				//Initialize HBox
		this.clientChallenge = new VBox();			//Initialize HBox
		
		//Initialize list for clients
		this.clientList = new ListView<String>();
		this.clientList.setMaxHeight(250);
		
		//Add event handler
		this.clientList.setOnMouseClicked(challengeSelect);
		
		//Initialize text and set style
		this.clientListText = new Text("Players to challenge");
		this.clientListText.setStyle("-fx-font: 16 arial");
		
		this.clientChallenge.getChildren().addAll(clientListText,clientList);
		
		//Initialize actionlist and set starting text
		this.actionList = new ListView<String>();
		this.actionList.setMaxHeight(250);
		this.actionList.getItems().add("Welcome to RPLS");
		this.actionList.getItems().add("IP: " + ip + " and port: "+ port);
		
		this.playerView.getChildren().addAll(clientChallenge, actionList);
		this.lobbyScreen.getChildren().addAll(title, playerView);
		
		//Set margins for the VBox
		HBox.setMargin(actionList, new Insets(17,0,0,40));
		HBox.setMargin(clientChallenge, new Insets(0,0,0,30));
		
		//Set margins for the HBox
		VBox.setMargin(title, new Insets(20,0,0,40));
		VBox.setMargin(playerView, new Insets(30,0,0,0));
		VBox.setMargin(clientListText, new Insets(0,0,0,50));
		
		return new Scene (lobbyScreen, 600, 400);
	} 
	
	public Scene getPlayingScene() {
		
		//Initialize playing screen and set background
		this.playingScreen = new BorderPane();
		this.playingScreen.setStyle("-fx-background-color:#eff0e9");
		
		this.playingTitle = new Text("RPLS Client");
		this.playingTitle.setStyle("-fx-font: 30 arial");
		
		//Create the image for each option
		this.rockImage = new ImageView(new Image("rock.jpg",150,150,false,false));
		this.paperImage = new ImageView(new Image("paper.jpg",150,150,false,false));
		this.scissorsImage = new ImageView(new Image("scissors.jpg",150,150,false,false));
		this.lizardImage = new ImageView(new Image("lizard.jpg",150,150,false,false));
		this.spockImage = new ImageView(new Image("spock.jpg",150,150,false,false));
		
		//Set ids for the images
		this.rockImage.setId("rock");
		this.paperImage.setId("paper");
		this.scissorsImage.setId("scissors");
		this.lizardImage.setId("lizard");
		this.spockImage.setId("spock");
		
		//Attach mouse events
		this.rockImage.setOnMouseClicked(playSelect);
		this.paperImage.setOnMouseClicked(playSelect);
		this.scissorsImage.setOnMouseClicked(playSelect);
		this.lizardImage.setOnMouseClicked(playSelect);
		this.spockImage.setOnMouseClicked(playSelect);

		//Create hbox and add all the images
		this.playSelection = new HBox();
		playSelection.getChildren().addAll(rockImage,paperImage,scissorsImage,lizardImage,spockImage);
		
		//Create prompt to ask user to 
		this.promptWhatToPlay = new Text("What would you like to play?");
		this.promptWhatToPlay.setStyle("-fx-font: 24 arial");

		this.playMenu = new VBox();
		playMenu.getChildren().addAll(promptWhatToPlay,playSelection);
		
		//Initialize actionlist and set starting text
		//this.actionList = new ListView<String>();
		//this.actionList.setMaxHeight(400);
		
		//this.actionList.getItems().add("Welcome to RPLS");
		//this.actionList.getItems().add("IP: " + ip + " and port: "+ port);
		
		this.playerText = new Text("      Player Played     ");
		this.playerText.setStyle("-fx-font: 20 arial");
		this.playerPlayed = new ImageView(new Image("rock.jpg",150,150,false,false));
		this.playerPlayed.setVisible(false);
		this.playerInfo = new VBox();
		this.playerInfo.getChildren().addAll(playerText, playerPlayed);
		
		this.opponentText = new Text("     Opponent Played   ");
		this.opponentText.setStyle("-fx-font: 20 arial");
		this.opponentPlayed = new ImageView(new Image("rock.jpg",150,150,false,false));
		this.opponentPlayed.setVisible(false);
		this.opponentInfo = new VBox();
		this.opponentInfo.getChildren().addAll(opponentText, opponentPlayed);
		
		//Add elements to the borderpane
		this.playingScreen.setTop(playingTitle);
		this.playingScreen.setBottom(playMenu);
		//this.playingScreen.setRight(actionList);    
		this.playingScreen.setLeft(playerInfo);
		this.playingScreen.setCenter(opponentInfo);
		
		//Set margins for the Vbox
		VBox.setMargin(promptWhatToPlay, new Insets(0,0,15,235));
		VBox.setMargin(opponentPlayed, new Insets(40,0,0,40));
		VBox.setMargin(playerPlayed, new Insets(40,0,0,40));
		
		//Set margins for the play menu HBox
		HBox.setMargin(rockImage, new Insets(0,10,0,0));
		HBox.setMargin(paperImage, new Insets(0,10,0,0));
		HBox.setMargin(scissorsImage, new Insets(0,10,0,0));
		HBox.setMargin(lizardImage, new Insets(0,10,0,0));
		HBox.setMargin(spockImage, new Insets(0,10,0,0));

		//Set margins for the playingScreen borderpane
		BorderPane.setMargin(playingTitle, new Insets(20,0,0,50));
		BorderPane.setMargin(playMenu, new Insets(0,0,20,55));
		//BorderPane.setMargin(actionList, new Insets(0,10,10,0));	
		BorderPane.setMargin(playerInfo, new Insets(20,0,0,20));
		BorderPane.setMargin(opponentInfo, new Insets(20,0,0,80));
		
		return new Scene(playingScreen,900,600);
	}
	
	public void updateGUI() {
		
		//print out the choices of the 2 players
		int challengeForIndex = 0;
		int sentFromIndex = 0;
		
		//Get the ID from the playerinfo arrayList
		for (int i = 0; i < gameState.playerinfo.size(); i++) {
			if (gameState.playerinfo.get(i).clientID == gameState.sentFor)
				challengeForIndex = i;
			else if (gameState.playerinfo.get(i).clientID == gameState.sentBy)
				sentFromIndex = i;
		}
		
		opponentPlayed.setImage(new Image(gameState.playerinfo.get(challengeForIndex).playerPlayed + ".jpg",150,150,false,false));
		opponentPlayed.setVisible(true);
		
		//Add what each user played to the listview
		actionList.getItems().add("Player " + gameState.sentFor + " has chosen: " + gameState.playerinfo.get(challengeForIndex).playerPlayed);
		actionList.getItems().add("Player " + gameState.sentBy + " has chosen: " + gameState.playerinfo.get(sentFromIndex).playerPlayed);
		
		//print out the result of the game
		if(gameState.roundWinner.equals("draw")) {
			actionList.getItems().add("The game of Player " + gameState.sentFor + " and" + " Player " + gameState.sentBy +  " has ended in a draw");
		}
		else {
			actionList.getItems().add("Player " + gameState.roundWinner + " won the game.");
		}
		actionList.getItems().add("");
	}

}
