package com.codeoftheweb.salvo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.*;

import static java.util.stream.Collectors.toList;

@RestController
@RequestMapping("/api")
public class SalvoController {
    @Autowired
    private GameRepository gameRepository;

    @Autowired
    private GamePlayerRepository gamePlayerRepository;

    @Autowired
    private PlayerRepository playerRepository;

    @Autowired
    private SalvoRepository salvoRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping("/game_view/{gamePlayerId}")
    public ResponseEntity<Map<String, Object>> getGameView(@PathVariable long gamePlayerId, Authentication authentication) {

        if (isGuest(authentication)) {
            return new ResponseEntity<>(makeMap("error", "You must be logged in!"), HttpStatus.UNAUTHORIZED);
        }
        Optional<GamePlayer> gamePlayer = gamePlayerRepository.findById(gamePlayerId);
        Player player = playerRepository.findByUserName(authentication.getName());

        if (gamePlayer.isEmpty()) {
            return new ResponseEntity<>(makeMap("error", "The game does not exist"), HttpStatus.FORBIDDEN);
        }
        if (gamePlayer.get().getPlayer().getId() != player.getId()) {
            return new ResponseEntity<>(makeMap("error", "this game don't belong you!"), HttpStatus.UNAUTHORIZED);
        }
        return new ResponseEntity<>(makeGameViewDTO(gamePlayer.get()), HttpStatus.OK);
    }

    @PostMapping("/players")
    public ResponseEntity<Object> register(
            @RequestParam String username, @RequestParam String password) {

        if (username.isEmpty() || password.isEmpty()) {
            return new ResponseEntity<>("Missing data", HttpStatus.FORBIDDEN);
        }

        if (playerRepository.findByUserName(username) != null) {
            return new ResponseEntity<>("Name already in use", HttpStatus.FORBIDDEN);
        }
        playerRepository.save(new Player(username, passwordEncoder.encode(password)));
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @PostMapping("/games")
    public ResponseEntity<Map<String, Object>> createGame(Authentication authentication) {
        if (isGuest(authentication)) {
            return new ResponseEntity<>(makeMap("error", "You must be logged in"), HttpStatus.UNAUTHORIZED);
        } else {
            Player player = playerRepository.findByUserName(authentication.getName());
            Game game = gameRepository.save(new Game(LocalDateTime.now()));
            GamePlayer gamePlayer = gamePlayerRepository.save(new GamePlayer(LocalDateTime.now(), game, player));
            return new ResponseEntity<>(makeMap("gpid", gamePlayer.getId()), HttpStatus.CREATED);
        }
    }

    @PostMapping("/games/{gameId}/players")
    public ResponseEntity<Map<String, Object>> joinGame(Authentication authentication, @PathVariable long gameId) {
        if (!isGuest(authentication)) {
            Optional<Game> game = gameRepository.findById(gameId);

            if (game.isEmpty()) {
                return new ResponseEntity<>(makeMap("error", "Game not found"), HttpStatus.NOT_FOUND);
            }

            Player player = playerRepository.findByUserName(authentication.getName());
            if (game.get().getGamePlayers().stream().anyMatch(gamePlayer -> gamePlayer.getPlayer().getId() == player.getId())) {
                return new ResponseEntity<>(makeMap("error", "You are already playing this game"), HttpStatus.FORBIDDEN);

            } else {
                if (game.get().getGamePlayers().size() == 1) {
                    GamePlayer gamePlayer = new GamePlayer(LocalDateTime.now(), game.get(), player);

                    gamePlayerRepository.save(gamePlayer);
                    return new ResponseEntity<>(makeMap("gpid", gamePlayer.getId()), HttpStatus.CREATED);

                } else {
                    return new ResponseEntity<>(makeMap("error", "The game is full"), HttpStatus.FORBIDDEN);
                }
            }
        } else {
            return new ResponseEntity<>(makeMap("error", "You are not logged in"), HttpStatus.UNAUTHORIZED);
        }
    }

    @PostMapping("games/players/{gamePlayerId}/ships")
    public ResponseEntity<Map<String, Object>> addShips(Authentication authentication, @PathVariable long gamePlayerId, @RequestBody List<Ship> ships) {
        if (isGuest(authentication)) {
            return new ResponseEntity<>(makeMap("error", "You have to logged in."), HttpStatus.UNAUTHORIZED);

        } else {
            Optional<GamePlayer> gamePlayer = gamePlayerRepository.findById(gamePlayerId);
            Player player = playerRepository.findByUserName(authentication.getName());
            if (gamePlayer.isEmpty()) {
                return new ResponseEntity<>(makeMap("error", "Game not found"), HttpStatus.NOT_FOUND);

            } else if (gamePlayer.get().getPlayer().getId() != player.getId()) {
                return new ResponseEntity<>(makeMap("error", "This is not your game"), HttpStatus.UNAUTHORIZED);

            } else if (gamePlayer.get().getShips().size() > 0) {
                return new ResponseEntity<>(makeMap("error", "all ships placed"), HttpStatus.FORBIDDEN);

            } else if (ships.size() != 5) {
                return new ResponseEntity<>(makeMap("error", "you must have 5 ships"), HttpStatus.FORBIDDEN);

            } else {
                for (Ship ship : ships) {
                    gamePlayer.get().AddShip(ship);
                }
                gamePlayerRepository.save(gamePlayer.get());
                return new ResponseEntity<>(makeMap("done!", "You have added the ships"), HttpStatus.CREATED);
            }
        }
    }

    @PostMapping("games/players/{gamePlayerId}/salvos")
    public ResponseEntity<Map<String, Object>> addSalvos(Authentication authentication, @PathVariable long gamePlayerId, @RequestBody Salvo salvoes) {
        if (isGuest(authentication)) {
            return new ResponseEntity<>(makeMap("error", "You have to logged in."), HttpStatus.UNAUTHORIZED);

        } else {
            Optional<GamePlayer> gamePlayer = gamePlayerRepository.findById(gamePlayerId);
            Player player = playerRepository.findByUserName(authentication.getName());
            Optional<GamePlayer> opponent = gamePlayer.get().getGame().getGamePlayers().stream().filter(x -> x.getId() != gamePlayer.get().getId()).findFirst();

            if (gamePlayer.isEmpty()) {
                return new ResponseEntity<>(makeMap("error", "Game not found"), HttpStatus.NOT_FOUND);

            }
            if (gamePlayer.get().getPlayer().getId() != player.getId()) {
                return new ResponseEntity<>(makeMap("error", "This is not your game"), HttpStatus.UNAUTHORIZED);

            }
            if (gamePlayer.get().getSalvos().size() + 1 != salvoes.getTurn()) {
                return new ResponseEntity<>(makeMap("error", "You can't skip turns"), HttpStatus.UNAUTHORIZED);

            }
            if (gamePlayer.get().getSalvos().size() == salvoes.getTurn()) {
                return new ResponseEntity<>(makeMap("error", "you can't shoot any more salvoes"), HttpStatus.UNAUTHORIZED);

            }
            if (salvoes.getLocations().size() != 5) {
                return new ResponseEntity<>(makeMap("error", "you have to shot 5 salvoes"), HttpStatus.UNAUTHORIZED);

            }
            if (opponent.isPresent()) {
                if (gamePlayer.get().getSalvos().size() - opponent.get().getSalvos().size() >= 1) {
                    return new ResponseEntity<>(makeMap("error", "You can't skip turns, cheater!"), HttpStatus.UNAUTHORIZED);
                } else {
                    Salvo salvo = new Salvo(salvoes.getTurn(), gamePlayer.get(), salvoes.getLocations());
                    salvoRepository.save(salvo);
                    return new ResponseEntity<>(makeMap("done!", "You have fired the salvoes"), HttpStatus.CREATED);
                }
            } else {
                return new ResponseEntity<>(makeMap("error!", "the opponent doesn't exist"), HttpStatus.NOT_FOUND);
            }
        }
    }

    @GetMapping("/games")
    public Map<String, Object> getGames(Authentication authentication) {
        Map<String, Object> dto = new LinkedHashMap<String, Object>();
        if (!isGuest(authentication)) {
            Player player = playerRepository.findByUserName(authentication.getName());
            dto.put("player", makePlayerDTO(player));
        } else {
            dto.put("player", null);
        }
        dto.put("games", gameRepository.findAll().stream().map(this::makeGameDTO).collect(toList()));
        return dto;
    }

    @GetMapping("/players")
    public List<Map<String, Object>> getPlayers() {
        return playerRepository.findAll().stream().map(this::makePlayerDTO).collect(toList());
    }

    @GetMapping("/gamePlayers")
    public List<Map<String, Object>> getGamePlayers() {
        return gamePlayerRepository.findAll().stream().map(this::makeGamePlayerDTO).collect(toList());
    }

    public Map<String, Object> makeGameDTO(Game game) {
        Map<String, Object> dto = new LinkedHashMap<String, Object>();
        dto.put("id", game.getId());
        dto.put("date", game.getDate());
        dto.put("GamePlayers", game.getGamePlayers().stream().map(this::makeGamePlayerDTO).collect(toList()));
        return dto;
    }

    public Map<String, Object> makePlayerDTO(Player player) {
        Map<String, Object> dto = new LinkedHashMap<String, Object>();
        dto.put("id", player.getId());
        dto.put("email", player.getUserName());
        return dto;
    }

    public Map<String, Object> makeGamePlayerDTO(GamePlayer gamePlayer) {
        Map<String, Object> dto = new LinkedHashMap<String, Object>();
        dto.put("id", gamePlayer.getId());
        dto.put("player", makePlayerDTO(gamePlayer.getPlayer()));
        dto.put("score", gamePlayer.getScore().map(Score::getScore).orElse(null));
        return dto;
    }

    public Map<String, Object> makeShipDTO(Ship ship) {
        Map<String, Object> dto = new LinkedHashMap<String, Object>();
        dto.put("type", ship.getType());
        dto.put("locations", ship.getLocations());
        return dto;
    }

    public Map<String, Object> makeSalvoDTO(Salvo salvo) {
        Map<String, Object> dto = new LinkedHashMap<String, Object>();
        dto.put("player", salvo.getGamePlayer().getPlayer().getId());
        dto.put("turn", salvo.getTurn());
        dto.put("locations", salvo.getLocations());
        return dto;
    }


    public Map<String, Object> makeGameViewDTO(GamePlayer gamePlayer) {
        Map<String, Object> dto = new LinkedHashMap<String, Object>();
        dto.put("id", gamePlayer.getGame().getId());
        dto.put("created", gamePlayer.getGame().getDate());
        dto.put("gamePlayers", gamePlayer.getGame().getGamePlayers().stream().map(this::makeGamePlayerDTO).collect(toList()));
        dto.put("ships", gamePlayer.getShips().stream().map(this::makeShipDTO).collect(toList()));
        dto.put("salvos", gamePlayer.getGame().getGamePlayers().stream().flatMap((a) -> a.getSalvos().stream().map(this::makeSalvoDTO)));
        return dto;
    }


    // private methods
    private boolean isGuest(Authentication authentication) {
        return authentication == null || authentication instanceof AnonymousAuthenticationToken;
    }

    private Map<String, Object> makeMap(String key, Object value) {
        Map<String, Object> map = new HashMap<>();
        map.put(key, value);
        return map;
    }
}







