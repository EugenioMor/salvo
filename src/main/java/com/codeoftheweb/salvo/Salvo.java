package com.codeoftheweb.salvo;

import org.hibernate.annotations.GenericGenerator;
import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import static java.util.stream.Collectors.toList;


@Entity
public class Salvo {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
    @GenericGenerator(name = "native", strategy = "native")
    private long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "gamePlayer_id")
    private GamePlayer gamePlayer;

    private int turn;

    @ElementCollection
    @Column(name = "locations")
    private List<String> locations = new ArrayList<>();

    public Salvo() {
    }

    public Salvo(int turn, GamePlayer gamePlayer, List<String> locations) {
        this.turn = turn;
        this.gamePlayer = gamePlayer;
        this.locations = locations;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public GamePlayer getGamePlayer() {
        return gamePlayer;
    }

    public void setGamePlayer(GamePlayer gamePlayer) {
        this.gamePlayer = gamePlayer;
    }

    public int getTurn() {
        return turn;
    }

    public void setTurn(int turn) {
        this.turn = turn;
    }

    public List<String> getLocations() {
        return locations;
    }

    public void setLocations(List<String> locations) {
        this.locations = locations;
    }


    public List<String> getHits() {
        List<String> hits = new ArrayList<>();
        if (this.gamePlayer.getOpponent().isPresent()) {
            hits = this.locations.stream().filter(location -> this.gamePlayer.getOpponent().get().getShips().stream().anyMatch(ship -> ship.getLocations().contains(location))).collect(toList());
        }
        return hits;
    }

    public List<Ship> getSunks() {
        List<Ship> sunks = new ArrayList<>();
        List<String> hitLocations = this.gamePlayer.getSalvos().stream().filter(salvo -> salvo.getTurn() <= this.getTurn()).flatMap(salvo -> salvo.getHits().stream()).collect(toList());

        if (this.gamePlayer.getOpponent().isPresent()) {
            sunks = this.gamePlayer.getOpponent().get().getShips().stream().filter(ship -> hitLocations.containsAll(ship.getLocations())).collect(toList());
        }
        return sunks;
    }
}