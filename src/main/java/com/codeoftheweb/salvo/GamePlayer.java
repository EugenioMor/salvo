package com.codeoftheweb.salvo;

import org.hibernate.annotations.GenericGenerator;
import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
public class GamePlayer {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
    @GenericGenerator(name = "native", strategy = "native")
    private long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "player_id")
    private Player player;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "game_id")
    private Game game;

    @OneToMany (fetch = FetchType.EAGER, mappedBy = "gamePlayer")
    private Set <Ship> ships = new HashSet<>();

    @OneToMany (fetch = FetchType.EAGER, mappedBy = "gamePlayer")
    private Set <Salvo> salvos = new HashSet<>();


    private LocalDateTime date;

    public GamePlayer() {}

    public GamePlayer (LocalDateTime date, Game game, Player player) {
        this.date = date;
        this.player = player;
        this.game = game;
    }

    public void AddShip(Ship ship) {
        ship.setGamePlayer(this);
        ships.add(ship);
    }


    public void AddSalvo(Salvo salvo) {
        salvo.setGamePlayer(this);
        salvos.add(salvo);
    }

    public Set<Salvo> getSalvos() {
        return salvos;
    }

    public void setSalvos(Set<Salvo> salvos) {
        this.salvos = salvos;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        id = id;
    }

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public Game getGame() {
        return game;
    }

    public void setGame(Game game) {
        this.game = game;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public Set<Ship> getShips() {return ships;}

    public void setShips(Set<Ship> ships) {
        this.ships = ships;
    }

}