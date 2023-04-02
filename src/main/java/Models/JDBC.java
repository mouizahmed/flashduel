package Models;


import java.io.IOException;
import java.math.BigInteger;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.URI;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.Arrays;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

import com.fasterxml.jackson.databind.ObjectMapper;

public class JDBC {
	private static Connection conn;
	private static Statement stmt;
	private static Statement stmt2;
	private static Statement stmt3;
	private static ResultSet rs;
	private static ResultSet rs1;
	private static ResultSet rs2;
	private static ResultSet rs3;
	private static String sql;
	private static String sql2;
	private static String sql3;
	private static String query1;
	private PasswordUtilities passwordUtilities = new PasswordUtilities();
	
	

	
	public JDBC() throws IOException, InterruptedException {
		String url = "jdbc:mysql://us-cdbr-east-06.cleardb.net:3306/heroku_957a5ec054245a7?reconnect=true";
		String user = "b94e112144658f";
		String password = "7af4eb5c";
		
//		ObjectMapper mapper = new ObjectMapper();
//		
//		String input = """
//				{
//					"model": "text-davinci-003",
//					"prompt": "Please create a simple multiple-choice question using the following term and answer: [TERM]: [USER ANSWER]. \\nYour response should have the following format: Question: [TERM] [QUESTION TEXT] Options: A) [USER ANSWER] B) [OPTION B] C) [OPTION C] D) [OPTION D]. The answer provided should be randomly assigned to options A, B, C, or D. One of the options must be the answer provided by the user and the correct answer must be the answer provided. I want the answer to always be one of the options at face value. The other 3 options must only be variations of the answer provided. You can replace [TERM] and [ANSWER] with the actual term and answer provided by the user. Term: [element] Answer: [substance that CANNOT be chemically broken down into simpler substances]\\n",
//					"temperature": 0.7,
//					"max_tokens": 256,
//					"top_p": 1,
//					"frequency_penalty": 0,
//					"presence_penalty": 0
//				}
//				""";
//		
//		 try {
//			 HttpRequest request = HttpRequest.newBuilder()
//		                .uri(URI.create("https://api.openai.com/v1/completions"))
//		                .header("Content-Type", "application/json")
//		                .header("Authorization","Bearer " + "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")
//		                .POST(HttpRequest.BodyPublishers.ofString(input))
//		                .build();
//			 
//			 HttpClient client = HttpClient.newHttpClient();
//			 var response = client.send(request, HttpResponse.BodyHandlers.ofString());
//			 
//			 GPTResponse response1 = mapper.readValue(response.body(), GPTResponse.class);
//			 
//		        // Retrieve the "choices" array
//		        String choices = response1.getChoices().get(0).getText();
//		        choices = choices.replaceAll("\\n", "");
//		        
//		        System.out.println(choices);
//		        
//		        String question = choices.split("Question: ")[1].split("Options: ")[0].trim();
//		        System.out.println(question);
////		        
//		        String optionsString = choices.split("Options: ")[1];
//		        System.out.println(optionsString);
//		        
//		        String[] options = optionsString.split("[A-D]\\) ");
//		        
//		        for (String option: options) {
//		        	System.out.println(option);
//		        }
//		        
//		        
//		        
//		        
//		        
//		        //String question = choices.get(0).getText().split("Question: ")[1].split("Options:")[0].trim();
////		        //System.out.println(question);
////		        System.out.println(choices.get(0).getText());
////		        String optionsString = choices.get(0).getText().split("Options: ")[1].split("Type of question:")[0].trim();
////		       
////		        optionsString = optionsString.replaceAll("\\n", "");
////		        
////		        String[] options = choices.get(0).getText().split("[A-D]\\)");
////		       
////		        
////		        //System.out.println(options[1]);
////		      
////		        
////		        String questionType = choices.get(0).getText().split("Type of question: ")[1].trim();
//		     
//
////	     
//		            
//		  
//			// System.out.println(response.body());
//		 } catch (IOException e) {
//			 e.printStackTrace();
//		 } catch (InterruptedException e) {
//			 e.printStackTrace();
//		 }
		
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
			conn = DriverManager.getConnection(url, user, password);
			if (conn != null) {
				//initialize();
				System.out.println("Connected to the database");
			} else {
				System.out.println("Failed to make connection!");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	
	public User createNewUser(String username, String email, String password, String confirmPassword) {
		User user = null;
		
		try {
			LocalDate currentDate = LocalDate.now();
			String hashedPassword = PasswordUtilities.hashPassword(password);
			user = new User(username, email, hashedPassword, currentDate);
			stmt = conn.createStatement();
			stmt.executeUpdate("INSERT INTO Users (username, email, password, regDate) VALUES ('" + user.getUsername() + "', '" + user.getEmail() + "', '" + user.getPassword() + "', '" + currentDate + "');");
			return user;
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
			return null;
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			return null;
		} catch (InvalidKeySpecException e) {
			e.printStackTrace();
			return null;
		}
	
	}
	
	
	public User verifyUser(String username, String password) {
		User user = null;
		ArrayList<Deck> decks = new ArrayList<>();
		try {
			stmt = conn.createStatement();
			stmt2 = conn.createStatement();
			stmt3 = conn.createStatement();
			sql = "SELECT * FROM Users WHERE username='" + username + "';";
			sql2 = "SELECT * FROM Decks WHERE createdBy='" + username +"';";
			
			rs1 = stmt.executeQuery(sql);
			if (rs1.next()) {
				if (PasswordUtilities.validate(password, rs1.getString("password"))) {
					rs2 = stmt2.executeQuery(sql2);
					
					while (rs2.next()) {
						System.out.println(rs2.getString("deckTitle"));
						ArrayList<Flashcard> flashcards = new ArrayList<>();
						sql3 = "SELECT * FROM Flashcards WHERE deckID='" + rs2.getString("deckID") + "'";
						rs3 = stmt3.executeQuery(sql3);
						while (rs3.next()) {
							flashcards.add(new Flashcard(rs3.getString("question"), rs3.getString("answer"), rs3.getString("createdBy"), rs3.getString("deckID")));
						}
						
						decks.add(new Deck(rs2.getString("deckTitle"), flashcards, rs2.getString("createdBy"), rs2.getBoolean("public"), rs2.getString("deckID"), rs2.getString("school"), rs2.getString("faculty"), rs2.getString("description"), rs2.getString("courseName")));
					}
					
					
					user = new User(rs1.getString("username"), rs1.getString("email"), rs1.getString("password"), rs1.getDate("regDate").toLocalDate());
					
					user.setUserDeckList(decks);
					
					
					return user;
				} else {
					throw new IllegalArgumentException("Incorrect Password");
				}
			} else {
				throw new SQLException("User does not exist");
			}
		} catch (SQLException e) {
			e.printStackTrace();
			return user;
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return user;
		} catch (InvalidKeySpecException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return user;
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
			return user;
		}
		
	}
	
	public Deck addDeckToProfile(Deck deck, User currentUser) {
		// TODO Auto-generated method stub
		ArrayList<Flashcard> flashcardsCopy = new ArrayList<>();
		Deck deckCopy = deck;
		deckCopy.setDeckID();
		deckCopy.setCreatedBy(currentUser.getUsername());
		
		for (int i = 0; i < deck.getAllFlashcards().size(); i++) {
			Flashcard flashcardCopy = deck.getAllFlashcards().get(i);
			flashcardCopy.setNewFlashcardID();
			flashcardCopy.setDeckID(deckCopy.getDeckID());
			flashcardCopy.setNewUser(currentUser.getUsername());
			flashcardsCopy.add(flashcardCopy);
		}
		
		deckCopy.replaceFlashcards(flashcardsCopy);
		this.createDeck(deckCopy.getDeckTitle(), flashcardsCopy, false, currentUser, deckCopy.getDeckID(), deckCopy.schoolName, deckCopy.facultyName, deckCopy.description, deckCopy.courseName);
		return deckCopy;
		
		
		
	}
	
	public Deck createDeck(String deckTitle, ArrayList<Flashcard> flashcards, boolean publicDeck, User currentUser, String deckID, String schoolName, String facultyName, String description, String courseName) {
		Deck deck = null;
		String addFlashcardsQuery = "INSERT INTO Flashcards (createdBy, flashcardID, deckID, question, answer) VALUES (?, ?, ?, ?, ?)";
		String addDeckQuery = "INSERT INTO Decks (createdBy, deckID, deckTitle, public, school, faculty, description, courseName) VALUES (?, ?, ?, ?, ?, ?, ?, ?);";
		try {
			
			PreparedStatement stmt1 = conn.prepareStatement(addDeckQuery);
			stmt1.setString(1, currentUser.getUsername());
			
			System.out.println(deckID);
			stmt1.setString(2, deckID);
			stmt1.setString(3, deckTitle);
			stmt1.setBoolean(4, publicDeck);
			stmt1.setString(5, schoolName);
			stmt1.setString(6, facultyName);
			stmt1.setString(7, description);
			stmt1.setString(8, courseName);
			System.out.println(stmt1);
			int rowsInserted1 = stmt1.executeUpdate();
			
			PreparedStatement stmt2 = conn.prepareStatement(addFlashcardsQuery);
			for (int i = 0; i < flashcards.size(); i++) {
				stmt2.setString(1, flashcards.get(i).createdBy);
				stmt2.setString(2, flashcards.get(i).flashcardID);
				stmt2.setString(3, flashcards.get(i).getDeckID());
				stmt2.setString(4, flashcards.get(i).getQuestion().trim());
				stmt2.setString(5, flashcards.get(i).getAnswer().trim());
				int rowsInserted2 = stmt2.executeUpdate();
			}
			//String schoolName, String facultyName, String description, String courseName
			deck = new Deck(deckTitle, flashcards, currentUser.getUsername(), publicDeck, deckID, schoolName, facultyName, description, courseName);
			return deck;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return deck;
	}
	

	
	public void createQuiz(QuizSession quizSession) {
		String addQuizQuery = "INSERT INTO Quizzes (quizID, deckID, avgScore, quizTakenBy) VALUES (?, ?, ?, ?);";
		
		try {
			PreparedStatement stmt1 = conn.prepareStatement(addQuizQuery);
			stmt1.setString(1, quizSession.getQuizID());
			stmt1.setString(2, quizSession.getDeck().getDeckID());
			stmt1.setString(3, Double.toString(quizSession.getAvgScore()));
			stmt1.setString(4, quizSession.getUser().getUsername());
			int rowsInserted = stmt1.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	
	public void setFlashcardDifficulty(Flashcard flashcard, String colour) {
		String updateFlashcard = "UPDATE Flashcards SET difficultyColor=? WHERE flashcardID=?";
		
		try {
			PreparedStatement stmt1 = conn.prepareStatement(updateFlashcard);
			stmt1.setString(1, colour);
			stmt1.setString(2, flashcard.getFlashcardID());
			int rowsUpdated = stmt1.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public Leaderboard getQuizLeaderboard(Deck deck) {
		
		Leaderboard leaderboard = new Leaderboard(deck);
		
		sql = "SELECT quizTakenBy, MAX(avgScore) as avgScore FROM Quizzes WHERE deckID='" + deck.getDeckID() + "' GROUP BY quizTakenBy ORDER BY avgScore DESC;";
		
		try {
			stmt = conn.createStatement();
			rs1 = stmt.executeQuery(sql);
			
			while(rs1.next()) {
				Player player = new Player(deck, rs1.getString("quizTakenBy"), rs1.getDouble("avgScore"));
				leaderboard.addPlayer(player);
			}
			return leaderboard;
		} catch (SQLException e) {
			e.printStackTrace();
			return leaderboard;
		}
		
	}
	
	
	public int quizAttempts(Deck deck, User user) {
		int userAttempts = 0;
		
		sql = "SELECT COUNT(*) FROM Quizzes WHERE deckID='" + deck.getDeckID() + "' AND quizTakenBy='" + user.getUsername() + "';";
		
		try {
			stmt = conn.createStatement();
			rs1 = stmt.executeQuery(sql);
			
			rs1.next();
			userAttempts = rs1.getInt(1);
			return userAttempts;
		} catch (SQLException e) {
			e.printStackTrace();
			return userAttempts;
		}
	}
	

	
	
	public ArrayList<Deck> publicDeckList() {
		ArrayList<Deck> publicDecks = new ArrayList<>();
		
		sql = "SELECT * FROM Decks WHERE public='1'";
		
				
		try {
			stmt = conn.createStatement();
			stmt2 = conn.createStatement();
			rs1 = stmt.executeQuery(sql);
			while (rs1.next()) {
				System.out.println(rs1.getString("deckTitle"));
				ArrayList<Flashcard> flashcards = new ArrayList<>();
				sql2 = "SELECT * FROM Flashcards WHERE deckID='" + rs1.getString("deckID") + "'";
				rs2 = stmt2.executeQuery(sql2);
				while (rs2.next()) {
					flashcards.add(new Flashcard(rs2.getString("question").trim(), rs2.getString("answer").trim(), rs2.getString("createdBy"), rs2.getString("deckID"), rs2.getString("flashcardID"), rs2.getString("difficultyColor")));
					
				}
				
				publicDecks.add(new Deck(rs1.getString("deckTitle"), flashcards, rs1.getString("createdBy"), rs1.getBoolean("public"), rs1.getString("deckID"), rs1.getString("school"), rs1.getString("faculty"), rs1.getString("description"), rs1.getString("courseName")));
			}
			
			return publicDecks;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return publicDecks;
		}
		
		
	}
	
	public ArrayList<Deck> searchPublicDeckQuery(String query) {
		ArrayList<Deck> searchedDecks = new ArrayList<>();
		
		sql = "SELECT * FROM Decks WHERE deckTitle LIKE '%" + query + "%' AND public='1'";
		
		try {
			stmt = conn.createStatement();
			stmt2 = conn.createStatement();
			rs1 = stmt.executeQuery(sql);
			
			while (rs1.next()) {
				ArrayList<Flashcard> flashcards = new ArrayList<>();
				sql2 = "SELECT * FROM Flashcards WHERE deckID='" + rs1.getString("deckID") + "'";
				rs2 = stmt2.executeQuery(sql2);
				while (rs2.next()) {
					flashcards.add(new Flashcard(rs2.getString("question"), rs2.getString("answer"), rs2.getString("createdBy"), rs2.getString("deckID")));
				}
				
				searchedDecks.add(new Deck(rs1.getString("deckTitle"), flashcards, rs1.getString("createdBy"), rs1.getBoolean("public"), rs1.getString("deckID"), rs1.getString("school"), rs1.getString("faculty"), rs1.getString("description"), rs1.getString("courseName")));
			}
			
			return searchedDecks;
		} catch (SQLException e) {
			e.printStackTrace();
			return searchedDecks;
		}
	}
	
	public Deck updateDefinition(Deck deck, Flashcard flashcard, String updatedDefinition) {
		sql = "UPDATE Flashcards SET answer= ? WHERE flashcardID = ? AND deckID = ?";
		try {
			PreparedStatement stmt = conn.prepareStatement(sql);
			stmt.setString(1, updatedDefinition);
			stmt.setString(2, flashcard.getFlashcardID());
			stmt.setString(3, flashcard.getDeckID());
			int rowsUpdated = stmt.executeUpdate();
			flashcard.setAnswer(updatedDefinition);
			int index = deck.getAllFlashcards().indexOf(flashcard);
			if (index != -1) {
				deck.getAllFlashcards().set(index, flashcard);
			}
			
			return deck;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
		

		
		
	}
	
	public void updateFlashcard(Flashcard flashcard) {
		sql = "UPDATE Flashcards SET question = ?, answer = ? WHERE flashcardID = ? AND deckID = ?";
		
		try {
			PreparedStatement stmt = conn.prepareStatement(sql);
			stmt.setString(1, flashcard.getQuestion());
			stmt.setString(2, flashcard.getAnswer());
			stmt.setString(3, flashcard.getFlashcardID());
			stmt.setString(4, flashcard.getDeckID());
			int rowsUpdated = stmt.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void deleteFlashcard(Flashcard flashcard) {
		sql = "DELETE FROM Flashcards WHERE flashcardID = ? AND deckID = ?";
		try {
			PreparedStatement stmt = conn.prepareStatement(sql);
			stmt.setString(1, flashcard.getFlashcardID());
			stmt.setString(2, flashcard.getDeckID());
			int rowsUpdated = stmt.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void addFlashcard(Flashcard flashcard, String deckID, String user) {
		sql = "INSERT INTO Flashcards (createdBy, flashcardID, deckID, question, answer) VALUES (?, ?, ?, ?, ?)";
		
		try {
			PreparedStatement stmt = conn.prepareStatement(sql);
			stmt.setString(1, user);
			stmt.setString(2, flashcard.getFlashcardID());
			stmt.setString(3, deckID);
			stmt.setString(4, flashcard.getQuestion());
			stmt.setString(5, flashcard.getAnswer());
			int rowsUpdated = stmt.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void updateDeckInfo(Deck deck) {
		sql = "UPDATE Decks SET deckTitle = ?, public = ?, school = ?, faculty = ?, courseName = ?, description = ? WHERE deckID = ?";
		
		try {
			PreparedStatement stmt = conn.prepareStatement(sql);
			stmt.setString(1, deck.getDeckTitle());
			stmt.setBoolean(2, deck.getPublicity());
			stmt.setString(3, deck.getSchoolName());
			stmt.setString(4, deck.getFaultyName());
			stmt.setString(5, deck.getCourseName());
			stmt.setString(6, deck.getDescription());
			stmt.setString(7, deck.getDeckID());
			int rowsUpdated = stmt.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public ArrayList<Deck> searchUserDeckQuery(String query, String user) {
		ArrayList<Deck> searchedDecks = new ArrayList<>();
		
		sql = "SELECT * FROM Decks WHERE deckTitle LIKE '%" + query + "%' AND createdBy='" + user + "'";
		
		try {
			stmt = conn.createStatement();
			stmt2 = conn.createStatement();
			rs1 = stmt.executeQuery(sql);
			
			while (rs1.next()) {
				ArrayList<Flashcard> flashcards = new ArrayList<>();
				sql2 = "SELECT * FROM Flashcards WHERE deckID='" + rs1.getString("deckID") + "'";
				rs2 = stmt2.executeQuery(sql2);
				while (rs2.next()) {
					flashcards.add(new Flashcard(rs2.getString("question"), rs2.getString("answer"), rs2.getString("createdBy"), rs2.getString("deckID")));
				}
				// String schoolName, String facultyName, String description, String courseName
				searchedDecks.add(new Deck(rs1.getString("deckTitle"), flashcards, rs1.getString("createdBy"), rs1.getBoolean("public"), rs1.getString("deckID"), rs1.getString("school"), rs1.getString("faculty"), rs1.getString("description"), rs1.getString("courseName")));
			}
			
			return searchedDecks;
		} catch (SQLException e) {
			e.printStackTrace();
			return searchedDecks;
		}
	}
	



	

}
