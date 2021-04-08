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

        },
    methods: {

    paintShips: function () {
        for(i = 0; i < app.gameView.ships.length; i++) {
           for(j = 0; j < app.gameView.ships[i].locations.length; j++) {
                document.getElementById(app.gameView.ships[i].locations[j]).className="ship" + i;
           }
       }
    },

       viewer: function () {
            for(var i = 0; i < app.gameView.gamePlayers.length; i++) {
              if(app.gameView.gamePlayers[i].id == gamePlayerId) {
                    app.mainPlayer = app.gameView.gamePlayers[i].player.email
              }
              else {
                     app.secondPlayer = app.gameView.gamePlayers[i].player.email
              }
           }
       }

    }
})






