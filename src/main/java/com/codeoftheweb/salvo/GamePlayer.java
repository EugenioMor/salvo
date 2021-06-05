package com.codeoftheweb.salvo;

import org.hibernate.annotations.GenericGenerator;
import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static java.util.stream.Collectors.toList;

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

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "gamePlayer", cascade = CascadeType.ALL)
    private Set<Ship> ships = new HashSet<>();

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "gamePlayer")
    private Set<Salvo> salvos = new HashSet<>();


    private LocalDateTime date;

    public GamePlayer() {
    }

    public GamePlayer(LocalDateTime date, Game game, Player player) {
        this.date = date;
        this.player = player;
        this.game = game;
    }

    public Optional<Score> getScore() {
        return player.getScore(game);
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
        this.id = id;
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

    public Set<Ship> getShips() {
        return ships;
    }

    public void setShips(Set<Ship> ships) {
        this.ships = ships;
    }

    public Optional<GamePlayer> getOpponent() {
        Optional<GamePlayer> opponent = getGame().getGamePlayers().stream().filter(x -> x.getId() != getId()).findFirst();
        return opponent;
    }

    public GameStatus gameStatus() {
        if (this.getShips().isEmpty()) {
            return GameStatus.PLACE_SHIPS;
        } else {
            if (this.getOpponent().isPresent()) {
                if (this.getOpponent().get().getShips().isEmpty()) {
                    return GameStatus.WAIT_OPPONENT_SHIPS;
                } else {
                    if (this.getSalvos().stream().noneMatch(em -> em.getTurn() == this.getSalvos().size())) {
                        return GameStatus.PLACE_SALVOS;
                    } else {
                        if (this.getOpponent().get().getSalvos().stream().noneMatch(em -> em.getTurn() == this.getSalvos().size())) {
                            return GameStatus.WAIT_OPPONENT_SALVOS;
                        } else if (this.getSalvos().size() == this.getOpponent().get().getSalvos().size()) {
                            List<Long> mySunks = this.getSalvos().stream().filter(x -> x.getTurn() == this.getSalvos().size()).flatMap(x -> x.getSunks().stream()).map(Ship::getId).collect(toList());
                            List<Long> opponentSunks = this.getOpponent().get().getSalvos().stream().filter(x -> x.getTurn() == this.getSalvos().size()).flatMap(x -> x.getSunks().stream()).map(Ship::getId).collect(toList());

                            if (mySunks.size() == 5 && opponentSunks.size() == 5) {
                                return GameStatus.TIE;
                            } else if (mySunks.size() == 5) {
                                return GameStatus.WIN;
                            } else if (opponentSunks.size() == 5) {
                                return GameStatus.LOSE;
                            } else {
                                return GameStatus.PLACE_SALVOS;
                            }
                        } else {
                            return GameStatus.PLACE_SALVOS;
                        }
                    }
                }
            } else {
                return GameStatus.WAIT_OPPONENT;
            }
        }
    }
}

