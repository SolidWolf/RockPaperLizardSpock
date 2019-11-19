import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Dialog;
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
	EventHandler<ActionEvent> quitButtonHandler;
	EventHandler<ActionEvent> playAgainHandler;
	
	GameInfo gameState = new GameInfo();
	
	String ip;
	int port;
	int playerID;

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
								for(GameInfo.PlayerInfo i : gameState.playerinfo) {
									String ID = Integer.toString(i.clientID);
									if(clientList.getItems().contains("Player " + ID)) {
										continue;							//Editor: Luis
									} 
									clientList.getItems().add("Player " + ID);
								} 
							}

							//Check if the given gameState includes a message
							if (gameState.isMessage == true) {
								actionList.getItems().add(gameState.message);		//Add the message to the listView
								playerID = gameState.playerID;							//Get what the id of the user is 
							}

							//Check if a player disconnected
							else if(gameState.p1Disconnected == true || gameState.p2Disconnected == true) {
									playAgainScene = getPlayAgainScene("Opponent has disconnected!");
									primaryStage.setScene(playAgainScene);
							}	
							else {
								//Check if the GUI should be updated
								if (gameState.updateClientUI == true) {
									if(gameState.winnerFound == false) {
										updateGUI();		//Update the GUI
									}
									else {
										
										String winnerMessage = "N/A";
										
										//Check if the player won or loss and set the message
										if (playerID == 0) {
											if (gameState.gameWinner.equals("p1")) 
												winnerMessage = "You Won!";
											else 
												winnerMessage = "You Loss!";
										}
										else if (playerID == 1) {
											if (gameState.gameWinner.equals("p2")) 
												winnerMessage = "You Won!";
											else 
												winnerMessage = "You Loss!";
										}
									
										//set the scene as the play again scene
										playAgainScene = getPlayAgainScene(winnerMessage);
										primaryStage.setScene(playAgainScene);
									}
								}
							}
						});
						},ip, port);
					clientConnection.start();
				} catch(Exception e) {
					
				}
			}
 		});
		
		this.playSelect = new EventHandler<MouseEvent> () {

			public void handle(MouseEvent event) {
				
				if (gameState.have2players == false) {
					actionList.getItems().add("Please wait for opponent to connect");
				}
				else {
					rockImage.setDisable(true);
					paperImage.setDisable(true);
					scissorsImage.setDisable(true);
					lizardImage.setDisable(true);
					spockImage.setDisable(true);

					ImageView imageview = (ImageView) event.getSource();
					actionList.getItems().add("You chose to play " + imageview.getId());
				    playerPlayed.setImage(new Image(imageview.getId() + ".jpg",150,150,false,false));
				    playerPlayed.setVisible(true);
				    
				    if (playerID == 0) {
				    	gameState.p1Plays = imageview.getId();
				    	gameState.p1Played = true;
				    }
				    else if (playerID == 1) {
				    	gameState.p2Plays = imageview.getId();
				    	gameState.p2Played = true;
				    }
				    clientConnection.send(gameState);	
				}
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
		
		//Event handler for the play again button
		this.playAgainHandler = new EventHandler<ActionEvent>() {
			public void handle(ActionEvent event) {
				
				//Tell the server, client chose to play again
				if (playerID == 0) 
					gameState.p1PlayAgain = true;
				else if (playerID == 1)
					gameState.p2PlayAgain = true;
				
				playingScene = getPlayingScene();
				primaryStage.setScene(playingScene);	
				clientConnection.send(gameState);
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
		this.actionList = new ListView<String>();
		this.actionList.setMaxHeight(200);
		this.actionList.getItems().add("Welcome to RPLS");
		this.actionList.getItems().add("IP: " + ip + " and port: "+ port);
		
		this.playerText = new Text("Player Played     Points:0");
		this.playerText.setStyle("-fx-font: 20 arial");
		this.playerPlayed = new ImageView(new Image("rock.jpg",150,150,false,false));
		this.playerPlayed.setVisible(false);
		this.playerInfo = new VBox();
		this.playerInfo.getChildren().addAll(playerText, playerPlayed);
		
		this.opponentText = new Text("Opponent Played   Points:0");
		this.opponentText.setStyle("-fx-font: 20 arial");
		this.opponentPlayed = new ImageView(new Image("rock.jpg",150,150,false,false));
		this.opponentPlayed.setVisible(false);
		this.opponentInfo = new VBox();
		this.opponentInfo.getChildren().addAll(opponentText, opponentPlayed);
		
		this.screenInfo = new VBox();										//Editor: Luis
		this.screenInfo.getChildren().addAll(actionList, clientList);
		
		//Add elements to the borderpane
		this.playingScreen.setTop(playingTitle);
		this.playingScreen.setBottom(playMenu);
		this.playingScreen.setRight(screenInfo);    //Editor: Luis
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
		BorderPane.setMargin(actionList, new Insets(0,10,10,0));	
		BorderPane.setMargin(playerInfo, new Insets(20,0,0,20));
		BorderPane.setMargin(opponentInfo, new Insets(20,0,0,80));
		
		return new Scene(playingScreen,900,600);
	}

	public Scene getPlayAgainScene(String message) {
		
		Text displayWinOrDisconnect = new Text(message);
		if (message.equals("Opponent has disconnected!")) {
			VBox.setMargin(displayWinOrDisconnect, new Insets(20,0,0,60));
			displayWinOrDisconnect.setStyle("-fx-font: 20 arial");
		}
		else {
			VBox.setMargin(displayWinOrDisconnect, new Insets(20,0,0,100));
			displayWinOrDisconnect.setStyle("-fx-font: 30 arial");
		}
		
		Text promptPlayAgain = new Text("Play Again?");
		promptPlayAgain.setStyle("-fx-font: 30 arial");
		this.playAgainButton = new Button("Play Again");
		this.playAgainButton.setOnAction(playAgainHandler);
		this.quitButton = new Button("Quit");
		this.quitButton.setOnAction(quitButtonHandler);
		this.playAgainOptions = new HBox();
		this.playAgainOptions.getChildren().addAll(playAgainButton, quitButton);
		
		//Set margins for the HBox
		HBox.setMargin(playAgainButton, new Insets(0,0,0,35));
		HBox.setMargin(quitButton, new Insets(0,0,0,75));
		
		//Set margins for the VBox
		VBox.setMargin(promptPlayAgain, new Insets(30,0,0,90));
		VBox.setMargin(playAgainOptions, new Insets(75,0,0,50));
		
		this.playAgainScreen = new VBox();
		this.playAgainScreen.getChildren().addAll(displayWinOrDisconnect,promptPlayAgain,playAgainOptions);
		
		return new Scene(playAgainScreen, 350, 250);
	}
	
	public void updateGUI() {
		rockImage.setDisable(false);
		paperImage.setDisable(false);
		scissorsImage.setDisable(false);
		lizardImage.setDisable(false);
		spockImage.setDisable(false);
		
		//Check which ID is the player
		if (playerID == 0) {
			actionList.getItems().add("Opponent played " + gameState.p2Plays);
			playerText.setText("Player Played     Points:" + gameState.p1Points);
			opponentText.setText("Opponent Played   Points:" + gameState.p2Points);
			opponentPlayed.setImage(new Image( gameState.p2Plays + ".jpg",150,150,false,false));
			if (gameState.roundWinner.equals("p1")) {
				actionList.getItems().add("You won the round");
			}
			else if (gameState.roundWinner.equals("p2")) {
				actionList.getItems().add("Opponent won the round");
			}
			else {
				actionList.getItems().add("The round ended in a draw");
			}
		}
		else if (playerID == 1) {
			actionList.getItems().add("Opponent played " + gameState.p1Plays);
			playerText.setText("Player Played     Points:" + gameState.p2Points);
			opponentText.setText("Opponent Played   Points:" + gameState.p1Points);
			opponentPlayed.setImage(new Image(gameState.p1Plays + ".jpg",150,150,false,false));
			if (gameState.roundWinner.equals("p2")) {
				actionList.getItems().add("You won the round");
			}
			else if (gameState.roundWinner.equals("p1")) {
				actionList.getItems().add("Opponent won the round");
			}
			else {
				actionList.getItems().add("The round ended in a draw");
			}
		}

		opponentPlayed.setVisible(true);
	}

}
