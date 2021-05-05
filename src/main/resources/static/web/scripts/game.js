const urlParams = new URLSearchParams(window.location.search);
const gamePlayerId = urlParams.get('gp');

var url = "http://localhost:8080/api/game_view/" + gamePlayerId;

fetch(url)
    .then(function (response) {
        return response.json();
    })
    .then(function (data) {
        app.gameView = data;
        app.paintShips();
        app.viewer();
        app.salvos();
        app.match();
        app.shipJson();
    })

var app = new Vue({
    el: "#app",
    data: {
        gameView: [],
        columns: [1, 2, 3, 4, 5, 6, 7, 8, 9, 10],
        rows: ["A", "B", "C", "D", "E", "F", "G", "H", "I", "J"],
        rowsTwo: ["a", "b", "c", "d", "e", "f", "g", "h", "i", "j"],
        mainPlayer: [],
        secondPlayer: [],
        shipsArray: [],

    },
    methods: {
        //pinta las naves
        paintShips: function () {
            for (i = 0; i < app.gameView.ships.length; i++) {
                for (j = 0; j < app.gameView.ships[i].locations.length; j++) {
                    document.getElementById(app.gameView.ships[i].locations[j]).className = "ship" + i;
                }
            }
        },

        viewer: function () {
            for (var i = 0; i < app.gameView.gamePlayers.length; i++) {
                if (app.gameView.gamePlayers[i].id == gamePlayerId) {
                    app.mainPlayer = app.gameView.gamePlayers[i].player;
                } else {
                    app.secondPlayer = app.gameView.gamePlayers[i].player;
                }
            }
        },

        salvos: function () {
            var salvosPlayer1 = app.gameView.salvos.filter(element => element.player == app.mainPlayer.id)
            var salvosPlayer2 = app.gameView.salvos.filter(element => element.player != app.mainPlayer.id)

            for (i = 0; i < salvosPlayer1.length; i++) {
                for (j = 0; j < salvosPlayer1[i].locations.length; j++) {
                    document.getElementById(salvosPlayer1[i].locations[j].toLowerCase()).className = "salvo1";
                    document.getElementById(salvosPlayer1[i].locations[j].toLowerCase()).innerHTML = salvosPlayer1[i].turn;
                }
            }

            for (i = 0; i < salvosPlayer2.length; i++) {
                for (j = 0; j < salvosPlayer2[i].locations.length; j++) {
                    document.getElementById(salvosPlayer2[i].locations[j]).className = "salvo2";
                    document.getElementById(salvosPlayer2[i].locations[j]).innerHTML = salvosPlayer2[i].turn;
                }
            }
        },

        match: function () {
            var salvosPlayer2 = app.gameView.salvos.filter(element => element.player == app.secondPlayer.id)

            for (i = 0; i < salvosPlayer2.length; i++) {
                for (j = 0; j < salvosPlayer2[i].locations.length; j++) {
                    for (k = 0; k < app.gameView.ships.length; k++) {
                        if (app.gameView.ships[k].locations.includes(salvosPlayer2[i].locations[j])) {
                            document.getElementById(salvosPlayer2[i].locations[j]).className = "hit";
                            document.getElementById(salvosPlayer2[i].locations[j]).innerHTML = salvosPlayer2[i].turn;

                        }
                    }
                }
            }
        },

        logout: function () {
            $.post("/api/logout")
                .done(function () {
                    app.currentUser = null;
                    window.location.replace("games.html");
                })
        },

        shipJson: function () {
            for (let i = 0; i < app.gameView.ships.length; i++) {
                app.shipsArray.push({
                    "type": app.gameView.ships[i].type,
                    "locations": app.gameView.ships[i].locations
                })
            }
        },

        shipPost: function () {
            $.post({
                    url: "/api/games/players/" + gamePlayerId + "/ships",
                    data: JSON.stringify([{
                            "type": "Destroyer",
                            "locations": ["B3", "B4", "B5", "B6", "B7"]
                        },
                        {
                            "type": "Destroyer",
                            "locations": ["C1", "C2", "C3", "C4"]
                        },
                        {
                            "type": "Destroyer",
                            "locations": ["A9", "B9", "C9"]
                        },
                        {
                            "type": "Destroyer",
                            "locations": ["D1", "D2", "D3"]
                        },
                        {
                            "type": "Destroyer",
                            "locations": ["J3", "J4"]
                        }

                    ]),
                    dataType: "text",
                    contentType: "application/json"
                })
                .done(function (response, status, jqXHR) {
                    alert("Ship added: " + response), location.reload();
                })
                .fail(function (jqXHR, status, httpError) {
                    alert("Failed to add ship: " + textStatus + " " + httpError);
                })
        }
    }
})