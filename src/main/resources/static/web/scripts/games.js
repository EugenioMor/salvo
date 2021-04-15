fetch("http://localhost:8080/api/games")
    .then(function (response) {
        return response.json();
    })
    .then(function (data) {
        app.games = data
        app.leaderBoard();
    })


var app = new Vue({
    el: "#app",
    data: {
        games: [],
        leaderboard: [],

    },

    methods: {
        getPlayers: function () {
            var mailsArray = [];

            for (var i = 0; i < app.games.length; i++) {
                for (var j = 0; j < app.games[i].GamePlayers.length; j++) {
                    if (!mailsArray.includes(app.games[i].GamePlayers[j].player.email)) {
                        mailsArray.push(app.games[i].GamePlayers[j].player.email)
                    }
                }
            }
            return mailsArray;
        },

        totalScore: function (email) {
            var score = 0;

            for (var i = 0; i < app.games.length; i++) {
                for (var j = 0; j < app.games[i].GamePlayers.length; j++) {
                    if (email == app.games[i].GamePlayers[j].player.email) {
                        score += app.games[i].GamePlayers[j].score
                    }
                }
            }
            return score;
        },

        countWins: function (email) {
            var wins = 0

            for (var i = 0; i < app.games.length; i++) {
                for (var j = 0; j < app.games[i].GamePlayers.length; j++) {
                    if (email == app.games[i].GamePlayers[j].player.email) {
                        wins += app.games[i].GamePlayers[j].score == 1.0
                    }
                }
            }
            return wins;
        },

        countLosses: function (email) {
            var losses = 0

            for (var i = 0; i < app.games.length; i++) {
                for (var j = 0; j < app.games[i].GamePlayers.length; j++) {
                    if (email == app.games[i].GamePlayers[j].player.email) {
                        losses += app.games[i].GamePlayers[j].score == 0
                    }
                }
            }
            return losses;
        },

        countTies: function (email) {
            var ties = 0

            for (var i = 0; i < app.games.length; i++) {
                for (var j = 0; j < app.games[i].GamePlayers.length; j++) {
                    if (email == app.games[i].GamePlayers[j].player.email) {
                        ties += app.games[i].GamePlayers[j].score == 0.5
                    }
                }
            }
            return ties;
        },

        leaderBoard: function () {
            let players = app.getPlayers()

            for (i = 0; i < players.length; i++) {
                var total = app.totalScore(players[i])
                var wins = app.countWins(players[i])
                var losses = app.countLosses(players[i])
                var ties = app.countTies(players[i])

                var newPlayerScore = {
                    email: players[i],
                    wins: wins,
                    losses: losses,
                    ties: ties,
                    total: total

                }

                app.leaderboard.push(newPlayerScore)
            }
        }

    }
})