package com.codeoftheweb.salvo;

import org.hibernate.annotations.GenericGenerator;
import javax.persistence.*;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import static java.util.stream.Collectors.toList;

@Entity
public class Player {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
    @GenericGenerator(name = "native", strategy = "native")
    private long id;

    @OneToMany (mappedBy = "player", fetch = FetchType.EAGER)
    private Set<GamePlayer> gamePlayers;

    @OneToMany(mappedBy = "player", fetch = FetchType.EAGER)
    private  Set<Score> scores = new HashSet<>();

    private String userName;

    public Player() {}

    public Player(String userName) {

        this.userName = userName;
    }

    public void addGamePlayer (GamePlayer gamePlayer) {
        gamePlayer.setPlayer(this);
        gamePlayers.add(gamePlayer);
    }


    public List<Game> getGames() {

        return gamePlayers.stream().map(sub -> sub.getGame()).collect(toList());
    }

    public String getUserName() {

        return userName;
    }

    public Optional<Score> getScore (Game game) {
        return scores.stream().filter(el -> el.getGame().equals(game)).findFirst();
    }

    public void setUserName(String userName) {

        this.userName = userName;
    }

    public String toString() {

        return userName;
    }

    public long getId() {

        return id;
    }

    public void setId(Long id) {

        this.id = id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Set<GamePlayer> getGamePlayers() {
        return gamePlayers;
    }

    public void setGamePlayers(Set<GamePlayer> gamePlayers) {
        this.gamePlayers = gamePlayers;
    }

    public Set<Score> getScores() {
        return scores;
    }

    public void setScores(Set<Score> scores) {
        this.scores = scores;
    }
}
