package com.codeoftheweb.salvo;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.time.LocalDateTime;
import java.util.List;

@SpringBootApplication
public class SalvoApplication {

    public static void main(String[] args) {
        SpringApplication.run(SalvoApplication.class, args);
    }

    @Bean
    public CommandLineRunner initData(
            PlayerRepository playerRepository,
            GameRepository gameRepository,
            GamePlayerRepository gamePlayerRepository,
            ShipRepository shipRepository,
            SalvoRepository salvoRepository) {

        return (args) -> {

            Game game1 = new Game(LocalDateTime.now());
            Game game2 = new Game(LocalDateTime.now());
            Game game3 = new Game(LocalDateTime.now());

            Player player1 = new Player("j.bauer@ctu.gov");
            Player player2 = new Player("c.obrian@ctu.gov");
            Player player3 = new Player("kim_bauer@gmail.com");
            Player player4 = new Player("t.almeida@ctu.gov");

            GamePlayer gamePlayer1 = new GamePlayer(LocalDateTime.now(), game1, player1);
            GamePlayer gamePlayer2 = new GamePlayer(LocalDateTime.now(), game1, player2);
            GamePlayer gamePlayer3 = new GamePlayer(LocalDateTime.now(), game2, player3);
            GamePlayer gamePlayer4 = new GamePlayer(LocalDateTime.now(), game2, player4);
            GamePlayer gamePlayer5 = new GamePlayer(LocalDateTime.now(), game3, player4);

            Ship ship1 = new Ship("Carrier", gamePlayer1, List.of("B3", "B4", "B5", "B6", "B7"));
            Ship ship2 = new Ship("Battleship", gamePlayer1, List.of("C1", "C2", "C3", "C4"));
            Ship ship3 = new Ship("Submarine", gamePlayer1, List.of("A9", "B9", "C9"));
            Ship ship4 = new Ship("Destroyer", gamePlayer1, List.of("D1", "D2", "D3"));
            Ship ship5 = new Ship("Patrol Boat", gamePlayer1, List.of("J3", "J4"));
            Ship ship6 = new Ship("Carrier", gamePlayer2, List.of("E2", "E3", "E4", "E5", "E6"));
            Ship ship7 = new Ship("Battleship", gamePlayer2, List.of("H2", "H3", "H4", "H5"));
            Ship ship8 = new Ship("Submarine", gamePlayer2, List.of("C7", "C8", "C9"));
            Ship ship9 = new Ship("Destroyer", gamePlayer2, List.of("B2", "C2", "D2"));
            Ship ship10 = new Ship("Patrol Boat", gamePlayer2, List.of("B3", "C3"));


            Salvo salvo1 = new Salvo("1", gamePlayer1, List.of("H5", "C5", "F1"));
            Salvo salvo2 = new Salvo("2", gamePlayer1, List.of("B3", "B5", "B8"));
            Salvo salvo3 = new Salvo("1", gamePlayer2, List.of("F9", "D1"));
            Salvo salvo4 = new Salvo("2", gamePlayer2, List.of("E7", "H2", "B9"));
            Salvo salvo5 = new Salvo("3", gamePlayer3, List.of("H1", "F5", "A3"));

            playerRepository.save(player1);
            playerRepository.save(player2);
            playerRepository.save(player3);
            playerRepository.save(player4);

            gameRepository.save(game1);
            gameRepository.save(game2);
            gameRepository.save(game3);

            gamePlayerRepository.save(gamePlayer1);
            gamePlayerRepository.save(gamePlayer2);
            gamePlayerRepository.save(gamePlayer3);
            gamePlayerRepository.save(gamePlayer4);
            gamePlayerRepository.save(gamePlayer5);

            shipRepository.save(ship1);
            shipRepository.save(ship2);
            shipRepository.save(ship3);
            shipRepository.save(ship4);
            shipRepository.save(ship5);
            shipRepository.save(ship6);
            shipRepository.save(ship7);
            shipRepository.save(ship8);
            shipRepository.save(ship9);
            shipRepository.save(ship10);

            salvoRepository.save(salvo1);
            salvoRepository.save(salvo2);
            salvoRepository.save(salvo3);
            salvoRepository.save(salvo4);
            salvoRepository.save(salvo5);


        };
    }
}
