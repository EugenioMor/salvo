const urlParams = new URLSearchParams(window.location.search);
const gamePlayerId = urlParams.get('gp');

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
        salvoes: {
            turn: 0,
            locations: []
        },
        locationSalvos: [],
        statusGame: "",
        created: null,
    },
    methods: {

        //pinta las naves
        paintShips: function () {
            for (let i = 0; i < app.gameView.ships.length; i++) {
                for (let j = 0; j < app.gameView.ships[i].locations.length; j++) {
                    document.getElementById(app.gameView.ships[i].locations[j]).className = "ship" + i;
                }
            }
        },

        viewer: function () {
            for (let i = 0; i < app.gameView.gamePlayers.length; i++) {
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

            for (let i = 0; i < salvosPlayer1.length; i++) {

                for (let j = 0; j < salvosPlayer1[i].locations.length; j++) {
                    document.getElementById(salvosPlayer1[i].locations[j].toLowerCase()).className = "salvo1";
                    document.getElementById(salvosPlayer1[i].locations[j].toLowerCase()).innerHTML = salvosPlayer1[i].turn;
                }
            }

            for (let i = 0; i < salvosPlayer2.length; i++) {
                for (let j = 0; j < salvosPlayer2[i].locations.length; j++) {
                    document.getElementById(salvosPlayer2[i].locations[j]).className = "salvo2";
                    document.getElementById(salvosPlayer2[i].locations[j]).innerHTML = salvosPlayer2[i].turn;
                }
            }
        },

        match: function () {
            var salvosPlayer2 = app.gameView.salvos.filter(element => element.player == app.secondPlayer.id)

            for (let i = 0; i < salvosPlayer2.length; i++) {
                for (let j = 0; j < salvosPlayer2[i].locations.length; j++) {
                    for (let k = 0; k < app.gameView.ships.length; k++) {
                        if (app.gameView.ships[k].locations.includes(salvosPlayer2[i].locations[j])) {
                            document.getElementById(salvosPlayer2[i].locations[j].toUpperCase()).className = "hit";
                            document.getElementById(salvosPlayer2[i].locations[j].toUpperCase()).innerHTML = salvosPlayer2[i].turn;
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
                    timer: 2000
                })

            }
        },

        shipSend: function (row, column) {
            var ships = []

            if (app.orientation == "vertical") {
                for (let j = 0; j < app.shipSelected.size; j++) {
                    for (let i = 0; i < app.rows.length; i++) {
                        if (app.rows[i] == row) {
                            ships.push(app.rows[i + j] + column)
                        }
                    }
                }

                if (!app.shipsArray.some(r => ships.some(z => r.locations.includes(z))) && (app.rows.indexOf(row) + app.shipSelected.size) < 11) {
                    for (let h = 0; h < app.shipsArray.length; h++) {
                        if (app.shipsArray[h].type == app.shipSelected.type) {
                            for (let j = 0; j < app.shipsArray[h].locations.length; j++) {

                                document.getElementById(app.shipsArray[h].locations[j]).className = ""
                            }
                            app.shipsArray.find(e => e.type == app.shipSelected.type).locations = []
                            for (let j = 0; j < ships.length; j++) {
                                app.shipsArray[h].locations.push(ships[j])
                                document.getElementById(ships[j]).className = "paint";
                            }
                        }
                    }
                }
            } else {
                for (let j = 0; j < app.shipSelected.size; j++) {
                    ships.push(row + (parseInt(column) + j))
                }
                if (!app.shipsArray.some(t => ships.some(v => t.locations.includes(v))) && (Number(column) + Number(app.shipSelected.size)) <= 11) {
                    for (let h = 0; h < app.shipsArray.length; h++) {
                        if (app.shipsArray[h].type == app.shipSelected.type) {
                            for (let j = 0; j < app.shipsArray[h].locations.length; j++) {

                                document.getElementById(app.shipsArray[h].locations[j]).className = ""
                            }
                            app.shipsArray.find(e => e.type == app.shipSelected.type).locations = []
                            for (let j = 0; j < ships.length; j++) {
                                app.shipsArray[h].locations.push(ships[j])
                                document.getElementById(ships[j]).className = "paint";
                            }
                        }
                    }
                }
            }
        },

        salvoPost: function () {

            $.post({
                    url: "/api/games/players/" + gamePlayerId + "/salvos",
                    data: JSON.stringify(app.salvoes),
                    dataType: "text",
                    contentType: "application/json"
                })
                .done(function () {
                    location.reload();
                })
                .fail(function (response) {
                    Swal.fire({
                        position: 'center',
                        icon: 'error',
                        title: JSON.parse(response.responseText).error,
                        showConfirmButton: false,
                        timer: 2000
                    })
                })
        },

        salvoSend: function (row, column) {

            var turn = app.gameView.salvos.filter(el => el.player == app.mainPlayer.id).length + 1
            var location = row.toUpperCase() + column
            var salvosId = app.gameView.salvos.filter(el => el.player == app.mainPlayer.id)

            if (!app.locationSalvos.includes(location) && app.salvoes.locations.length <= 4 && !salvosId.some(x => x.locations.includes(location))) {

                document.getElementById(location.toLowerCase()).className = "paint"
                app.salvoes.locations.push(location)
                app.salvoes.turn = turn
                app.locationSalvos.push(location)

            } else {
                if (app.salvoes.locations.length <= 5 && app.locationSalvos.includes(location)) {
                    document.getElementById(location.toLowerCase()).className = ""
                    app.salvoes.locations.splice(app.salvoes.locations.indexOf(location), 1)
                    app.locationSalvos.splice(app.locationSalvos.indexOf(location), 1)
                }
            }
        },

        hits: function () {
            for (let i = 0; i < app.gameView.hits.length; i++) {
                for (let j = 0; j < app.gameView.hits[i].damagedLocations.length; j++) {
                    document.getElementById(app.gameView.hits[i].damagedLocations[j].toLowerCase()).className = "hit";
                }
            }
            for (let i = 0; i < app.gameView.opponentHits.length; i++) {
                for (let j = 0; j < app.gameView.opponentHits[i].damagedLocations.length; j++) {
                    document.getElementById(app.gameView.opponentHits[i].damagedLocations[j]).className = "hit";
                }
            }
        },

        sunks: function () {
            for (let i = 0; i < app.gameView.sunks.length; i++) {
                for (let j = 0; j < app.gameView.sunks[i].sunkenShips.length; j++) {
                    for (let k = 0; k < app.gameView.sunks[i].sunkenShips[j].locations.length; k++) {
                        document.getElementById(app.gameView.sunks[i].sunkenShips[j].locations[k].toLowerCase()).className = "sunk";
                    }
                }
            }
            for (let i = 0; i < app.gameView.opponentSunks.length; i++) {
                for (let j = 0; j < app.gameView.opponentSunks[i].sunkenShips.length; j++) {
                    for (let k = 0; k < app.gameView.opponentSunks[i].sunkenShips[j].locations.length; k++) {
                        document.getElementById(app.gameView.opponentSunks[i].sunkenShips[j].locations[k]).className = "sunk";
                    }
                }
            }
        },

        statusChange: function () {
            let status = {
                PLACE_SHIPS: "Place your ships",
                WAIT_OPPONENT: "Waiting for the opponent...",
                WAIT_OPPONENT_SHIPS: "Waiting for the opponent to place their ships...",
                WAIT_OPPONENT_SALVOS: "Waiting for the opponent's salvoes...",
                PLACE_SALVOS: "Place your salvoes",
                TIE: "Has been a draw!",
                WIN: " You win!",
                LOSE: "You lost!"
            }
            app.statusGame = status[app.gameView.gameState]
        },

        reloadStatus: function () {
            if (this.gameView.gameState == "WAIT_OPPONENT", "WAIT_OPPONENT_SHIPS", "WAIT_OPPONENT_SALVOS") {
                console.log("wait opponent")
                setTimeout(this.getData, 2000)
            }
        },

        getData: function () {
            var url = "/api/game_view/" + gamePlayerId;

            fetch(url)
                .then(function (response) {
                    return response.json();
                })
                .then((data) => {
                    this.gameView = data;
                    if (this.gameView != null) {
                        this.paintShips();
                        this.viewer();
                        this.salvos();
                        this.match();
                        this.hits();
                        this.sunks();
                        this.statusChange();
                        this.reloadStatus();
                    }
                })
        },
    },

    mounted: function () {
        this.getData();
    }
})