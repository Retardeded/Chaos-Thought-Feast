package com.knowledge.testapp.data

data class Game(
    var players: Map<String, Boolean> = mapOf(),
    var current_turn: String? = null,
    var game_state: GameState = GameState(),
    var game_status:GameStatus = GameStatus.GETTING_READY
)