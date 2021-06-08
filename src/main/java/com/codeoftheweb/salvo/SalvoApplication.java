package com.codeoftheweb.salvo;

import com.codeoftheweb.salvo.models.Player;
import com.codeoftheweb.salvo.repositories.PlayerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.GlobalAuthenticationConfigurerAdapter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.WebAttributes;
import org.springframework.security.web.authentication.logout.HttpStatusReturningLogoutSuccessHandler;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;


@SpringBootApplication
public class SalvoApplication extends SpringBootServletInitializer {

    /*@Autowired
    private PasswordEncoder passwordEncoder;
    */
    public static void main(String[] args) {
        SpringApplication.run(SalvoApplication.class, args);
    }

    /*@Bean
    public CommandLineRunner initData(
            PlayerRepository playerRepository,
            GameRepository gameRepository,
            GamePlayerRepository gamePlayerRepository,
            ShipRepository shipRepository,
            SalvoRepository salvoRepository,
            ScoreRepository scoreRepository) {

        return (args) -> {

            Game game1 = new Game(LocalDateTime.now());
            Game game2 = new Game(LocalDateTime.now());
            Game game3 = new Game(LocalDateTime.now());
            Game game4 = new Game(LocalDateTime.now());

            gameRepository.save(game1);
            gameRepository.save(game2);
            gameRepository.save(game3);
            gameRepository.save(game4);


            Player player1 = new Player("j.bauer@ctu.gov", passwordEncoder.encode("24"));
            Player player2 = new Player("c.obrian@ctu.gov", passwordEncoder.encode("42"));
            Player player3 = new Player("kim_bauer@gmail.com", passwordEncoder.encode("kb"));
            Player player4 = new Player("t.almeida@ctu.gov", passwordEncoder.encode("mole"));

            playerRepository.save(player1);
            playerRepository.save(player2);
            playerRepository.save(player3);
            playerRepository.save(player4);


            GamePlayer gamePlayer1 = new GamePlayer(LocalDateTime.now(), game1, player1);
            GamePlayer gamePlayer2 = new GamePlayer(LocalDateTime.now(), game1, player2);
            GamePlayer gamePlayer3 = new GamePlayer(LocalDateTime.now(), game2, player3);
            GamePlayer gamePlayer4 = new GamePlayer(LocalDateTime.now(), game2, player4);
            GamePlayer gamePlayer5 = new GamePlayer(LocalDateTime.now(), game3, player2);
            GamePlayer gamePlayer6 = new GamePlayer(LocalDateTime.now(), game3, player1);
            GamePlayer gamePlayer7 = new GamePlayer(LocalDateTime.now(), game4, player1);

            gamePlayerRepository.save(gamePlayer1);
            gamePlayerRepository.save(gamePlayer2);

            gamePlayerRepository.save(gamePlayer3);
            gamePlayerRepository.save(gamePlayer4);
            gamePlayerRepository.save(gamePlayer5);
            gamePlayerRepository.save(gamePlayer6);
            gamePlayerRepository.save(gamePlayer7);


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


            Salvo salvo1 = new Salvo(1, gamePlayer1, List.of("H5", "C5", "F1"));
            Salvo salvo2 = new Salvo(2, gamePlayer1, List.of("B3", "B5", "B8"));
            Salvo salvo3 = new Salvo(1, gamePlayer2, List.of("F9", "D1", "A7"));
            Salvo salvo4 = new Salvo(2, gamePlayer2, List.of("E7", "H2", "B9"));
            Salvo salvo5 = new Salvo(1, gamePlayer3, List.of("H1", "F5", "A3"));

            salvoRepository.save(salvo1);
            salvoRepository.save(salvo2);
            salvoRepository.save(salvo3);
            salvoRepository.save(salvo4);
            salvoRepository.save(salvo5);


            Score score1 = new Score(0.5, LocalDateTime.now(), game1, player1);
            Score score2 = new Score(0.5, LocalDateTime.now(), game1, player2);
            Score score3 = new Score(1, LocalDateTime.now(), game2, player3);
            Score score4 = new Score(0, LocalDateTime.now(), game2, player4);
            Score score5 = new Score(1, LocalDateTime.now(), game3, player2);
            Score score6 = new Score(0, LocalDateTime.now(), game3, player1);

            scoreRepository.save(score1);
            scoreRepository.save(score2);
            scoreRepository.save(score3);
            scoreRepository.save(score4);
            scoreRepository.save(score5);
            scoreRepository.save(score6);
        };
    }*/
}

@Configuration
class WebSecurityConfiguration extends GlobalAuthenticationConfigurerAdapter {

    @Autowired
    PlayerRepository playerRepository;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    @Override
    public void init(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(inputUserName -> {
            Player player = playerRepository.findByUserName(inputUserName);
            if (player != null) {
                return new User(player.getUserName(), player.getPassword(),
                        AuthorityUtils.createAuthorityList("USER"));
            } else {
                throw new UsernameNotFoundException("Unknown user: " + inputUserName);
            }
        });
    }
}

@Configuration
@EnableWebSecurity
class WebSecurityConfig extends WebSecurityConfigurerAdapter {
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
                .antMatchers("/web/game.html", "/api/game_view/**").hasAuthority("USER")
                .antMatchers("/rest/**").hasAuthority("USER")
                .antMatchers("/**").permitAll()
                .and();

        http.formLogin()
                .usernameParameter("username")
                .passwordParameter("password")
                .loginPage("/api/login");
        http.logout().logoutUrl("/api/logout");

        http.headers().frameOptions().disable();
        http.csrf().disable();
        http.exceptionHandling().authenticationEntryPoint((req, res, exc) -> res.sendError(HttpServletResponse.SC_UNAUTHORIZED));
        http.formLogin().successHandler((req, res, auth) -> clearAuthenticationAttributes(req));
        http.formLogin().failureHandler((req, res, exc) -> res.sendError(HttpServletResponse.SC_UNAUTHORIZED));
        http.logout().logoutSuccessHandler(new HttpStatusReturningLogoutSuccessHandler());
    }

    private void clearAuthenticationAttributes(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.removeAttribute(WebAttributes.AUTHENTICATION_EXCEPTION);
        }
    }
}


