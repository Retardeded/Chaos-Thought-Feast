package com.knowledge.testapp.activity

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.knowledge.testapp.data.Game
import com.knowledge.testapp.data.GameState
import com.knowledge.testapp.data.GameStatus


class QueueActivity : AppCompatActivity() {

    private var gameId: String? = null // To store the game ID

    lateinit var tvGameStatus:TextView
    lateinit var tvGameId:TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        /*
        binding = ActivityQueueBinding.inflate(layoutInflater)
        setContentView(binding.root)

        tvGameStatus = binding.tvGameStatus
        tvGameId = binding.tvGameId


        val gamesRef = FirebaseDatabase.getInstance().getReference("games")
        val auth = FirebaseAuth.getInstance()
        val currentUser: FirebaseUser? = auth.currentUser

        if (currentUser != null) {
            val userId = currentUser.uid

            // Get the latest game node
            gamesRef.limitToLast(1).addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    //need to check player ID??
                    if (dataSnapshot.hasChildren()) {
                        val latestGameSnapshot = dataSnapshot.children.iterator().next()
                        val latestGame = latestGameSnapshot.getValue(Game::class.java)
                        if (latestGame != null && latestGame.players.isNotEmpty()) {
                            val updatedPlayers = latestGame.players + mapOf(userId to true)
                            latestGame.players = updatedPlayers
                            latestGameSnapshot.ref.setValue(latestGame)
                            latestGame.game_status = GameStatus.Game_On
                            latestGameSnapshot.ref.setValue(latestGame)
                            gameId = latestGameSnapshot.key ?: ""
                        } else {
                            // Create a new game since no games exist
                            val newGameKey = gamesRef.push().key

                            val newGame = Game(
                                players = mapOf(userId to true),
                                current_turn = userId,
                                game_state = GameState(), // Replace GameState() with your game state initialization
                                game_status = GameStatus.GETTING_READY
                            )
                            gamesRef.child(newGameKey!!).setValue(newGame)
                            gameId = newGameKey ?: ""
                            tvGameStatus.text = "Game Status: ${newGame.game_status}"
                            tvGameId.text = "Game ID: $gameId"
                        }
                    } else {
                        // Create a new game since no games exist
                        val newGameKey = gamesRef.push().key

                        val newGame = Game(
                            players = mapOf(userId to true),
                            current_turn = userId,
                            game_state = GameState(), // Replace GameState() with your game state initialization
                            game_status = GameStatus.GETTING_READY
                        )
                        gamesRef.child(newGameKey!!).setValue(newGame)
                        gameId = newGameKey ?: ""
                        tvGameStatus.text = "Game Status: ${newGame.game_status}"
                        tvGameId.text = "Game ID: $gameId"
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    // Handle errors
                }
            })
        }

        listenForGameStatusChanges()
    }

    private fun listenForGameStatusChanges() {
        if(gameId == null) {
            return
        }
        val gameRef = FirebaseDatabase.getInstance().getReference("games").child(gameId!!)

        val gameStatusListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val game = dataSnapshot.getValue(Game::class.java)
                if (game != null && game.game_status == GameStatus.Game_On) {
                    //val intent = Intent(this@QueueActivity, MultiplayerGameActivity::class.java)
                    intent.putExtra("gameId", gameId)
                    startActivity(intent)
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Handle errors
            }
        }

        gameRef.addValueEventListener(gameStatusListener)

         */
    }
}