package com.codeoftheweb.salvo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.annotations.GenericGenerator;
import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import static java.util.stream.Collectors.toList;

@Entity
public class Game {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
    @GenericGenerator(name = "native", strategy = "native")
    private long id;

    private LocalDateTime date;

    @OneToMany(mappedBy = "game", fetch = FetchType.EAGER)
    Set<GamePlayer> gamePlayers;

    public Game() {}

    public Game (LocalDateTime date) {
        this.date = date;
    }

    public void addGamePlayer(GamePlayer gamePlayer) {
        gamePlayer.setGame(this);
        gamePlayers.add(gamePlayer);
    }

    @JsonIgnore
    public List<Player> getPlayers() {
        return gamePlayers.stream().map(sub -> sub.getPlayer()).collect(toList());
    }

    public long getId() {

        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public LocalDateTime getDate() {

        return date;
    }

    public void setDate(LocalDateTime date) {

        this.date = date;
    }
    @JsonIgnore
    public Set<GamePlayer> getGamePlayers() {

        return gamePlayers;
    }

    public void setGamePlayers(Set<GamePlayer> gamePlayers) {

        this.gamePlayers = gamePlayers;
    }

}


