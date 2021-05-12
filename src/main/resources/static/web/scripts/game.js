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
        shipsArray: [{
                type: "Battleship",
                size: 4,
                locations: []
            },
            {
                type: "Submarine",
                size: 3,
                locations: []
            },
            {
                type: "Destroyer",
                size: 3,
                locations: []
            },
            {
                type: "Patrol Boat",
                size: 2,
                locations: []
            },
            {
                type: "Carrier",
                size: 5,
                locations: []
            }
        ],

        orientation: "vertical",
        shipSelected: {},
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

        shipPost: function () {

            if (app.shipsArray[0].locations.length != 0 && app.shipsArray[1].locations.length != 0 && app.shipsArray[2].locations.length != 0 && app.shipsArray[3].locations.length != 0 && app.shipsArray[4].locations.length != 0) {
                $.post({
                        url: "/api/games/players/" + gamePlayerId + "/ships",
                        data: JSON.stringify(app.shipsArray),
                        dataType: "text",
                        contentType: "application/json"
                    })
                    .done(function () {
                        location.reload();
                    })
            } else {
                Swal.fire({
                    position: 'center',
                    icon: 'error',
                    title: 'You have to add 5 ships!',
                    showConfirmButton: false,
                    timer: 1000
                })
            }
        },

        shipSend: function (row, column) {
            var ships = []

            if (app.orientation == "vertical") {
                for (j = 0; j < app.shipSelected.size; j++) {
                    for (i = 0; i < app.rows.length; i++) {
                        if (app.rows[i] == row) {
                            ships.push(app.rows[i + j] + column)
                        }
                    }
                }

                if (!app.shipsArray.some(r => ships.some(z => r.locations.includes(z))) && (app.rows.indexOf(row) + app.shipSelected.size) < 11) {
                    for (h = 0; h < app.shipsArray.length; h++) {
                        if (app.shipsArray[h].type == app.shipSelected.type) {
                            for (j = 0; j < app.shipsArray[h].locations.length; j++) {

                                document.getElementById(app.shipsArray[h].locations[j]).className = ""
                            }
                            app.shipsArray.find(e => e.type == app.shipSelected.type).locations = []
                            for (j = 0; j < ships.length; j++) {
                                app.shipsArray[h].locations.push(ships[j])
                                document.getElementById(ships[j]).className = "paint";
                            }
                        }
                    }
                }
            } else {
                for (j = 0; j < app.shipSelected.size; j++) {
                    ships.push(row + (parseInt(column) + j))
                }
                if (!app.shipsArray.some(t => ships.some(v => t.locations.includes(v))) && (Number(column) + Number(app.shipSelected.size)) <= 11) {
                    for (h = 0; h < app.shipsArray.length; h++) {
                        if (app.shipsArray[h].type == app.shipSelected.type) {
                            for (j = 0; j < app.shipsArray[h].locations.length; j++) {

                                document.getElementById(app.shipsArray[h].locations[j]).className = ""
                            }
                            app.shipsArray.find(e => e.type == app.shipSelected.type).locations = []
                            for (j = 0; j < ships.length; j++) {
                                app.shipsArray[h].locations.push(ships[j])
                                document.getElementById(ships[j]).className = "paint";
                            }
                        }
                    }
                }
            }
        }
    }
})