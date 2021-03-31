fetch("http://localhost:8080/api/games")
    .then(function (response) {
        return response.json();
    })
    .then(function (data) {
     app.games = data
     })

var app = new Vue({
    el: "#app",
    data: {
        games: []

        }
})
