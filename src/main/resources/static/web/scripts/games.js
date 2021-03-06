var app = new Vue({
    el: "#app",
    data: {
        games: [],
        leaderboard: [],
        username: "",
        password: "",
        currentUser: null,
    },

    methods: {
        gamesData: function () {
            fetch("/api/games")
                .then(function (response) {
                    return response.json();
                })
                .then((data) => {
                    this.currentUser = data.player;
                    this.games = data.games;
                    this.leaderBoard();
                })
        },

        //Obtención de los jugadores
        getPlayers: function () {
            var mailsArray = [];

            for (let i = 0; i < app.games.length; i++) {
                for (let j = 0; j < app.games[i].GamePlayers.length; j++) {
                    if (!mailsArray.includes(app.games[i].GamePlayers[j].player.email)) {
                        mailsArray.push(app.games[i].GamePlayers[j].player.email)
                    }
                }
            }
            return mailsArray;
        },
        //Puntaje total
        totalScore: function (email) {
            var score = 0;

            for (let i = 0; i < app.games.length; i++) {
                for (let j = 0; j < app.games[i].GamePlayers.length; j++) {
                    if (email == app.games[i].GamePlayers[j].player.email) {
                        score += app.games[i].GamePlayers[j].score
                    }
                }
            }
            return score;
        },
        //Victorias de cada jugador
        countWins: function (email) {
            var wins = 0

            for (let i = 0; i < app.games.length; i++) {
                for (let j = 0; j < app.games[i].GamePlayers.length; j++) {
                    if (email == app.games[i].GamePlayers[j].player.email) {
                        wins += app.games[i].GamePlayers[j].score == 1.0
                    }
                }
            }
            return wins;
        },
        //Derrotas de cada jugador
        countLosses: function (email) {
            var losses = 0

            for (let i = 0; i < app.games.length; i++) {
                for (let j = 0; j < app.games[i].GamePlayers.length; j++) {
                    if (email == app.games[i].GamePlayers[j].player.email) {
                        losses += app.games[i].GamePlayers[j].score == 0
                    }
                }
            }
            return losses;
        },
        //Empates de cada jugador
        countTies: function (email) {
            var ties = 0

            for (let i = 0; i < app.games.length; i++) {
                for (let j = 0; j < app.games[i].GamePlayers.length; j++) {
                    if (email == app.games[i].GamePlayers[j].player.email) {
                        ties += app.games[i].GamePlayers[j].score == 0.5
                    }
                }
            }
            return ties;
        },
        //
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
        },

        //función para loguearse
        login: function () {
            $.post("/api/login", {
                    username: app.username,
                    password: app.password
                })
                .done(function () {
                    location.reload();
                })
                .fail(function (response) {
                    Swal.fire({
                        position: 'center',
                        icon: 'error',
                        title: JSON.parse(response.responseText).error,
                        timer: 2000
                    })
                })
        },
        //función para registrarse
        signup: function () {
            $.post("/api/players", {
                    username: app.username,
                    password: app.password
                })
                .done(function () {
                    app.login();
                    Swal.fire({
                        position: 'center',
                        icon: 'success',
                        title: JSON.parse(response.responseText).error,
                        showConfirmButton: false,
                        timer: 2000
                    })
                })
                .fail(function () {
                    Swal.fire({
                        icon: 'error',
                        title: 'Oops...',
                        text: 'Cannot create user!',
                        footer: '<a href>Why do I have this issue?</a>'
                    })
                })

        },
        //función para cerrar sesión
        logout: function () {
            $.post("/api/logout")
                .done(function () {
                    app.currentUser = null;
                    location.reload();
                })
                .fail(function (response) {
                    Swal.fire({
                        position: 'center',
                        icon: 'error',
                        title: JSON.parse(response.responseText).error,
                        timer: 2000
                    })
                })
        },
        //función para crear juego
        createGame: function () {
            $.post("/api/games")
                .done(function (data) {
                    location.href = "/web/game.html?gp=" + data.gpid
                })
        },
        //unirse a un juego
        joinGame: function (gameId) {
            $.post("/api/games/" + gameId + "/players")
                .done(function (data) {
                    location.href = "/web/game.html?gp=" + data.gpid;
                })
        },
    },

    mounted: function () {
        this.gamesData();
    }
})