import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.*;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.EnumSet;

public class RockPaperScissors {
  private static final int VICTORIES_LIMIT = 3;
  private static final int PLAYERS = 3;

  private static int roundCounter = 1;

  private static List<Player> players = new ArrayList<Player>();
  private static volatile Player winner;
  private static Boolean keepPlaying = true;

  public static void main(String[] args) throws InterruptedException {
    ExecutorService es = Executors.newFixedThreadPool(PLAYERS);

    for (int i=0; i<PLAYERS; i++) {
      Player player = new Player("P" + (i + 1));
      players.add(player);
    }

    while (keepPlaying) {
      System.out.format("\n---- ROUND %d\n", roundCounter);
      List<Future<Map<Player, Weapon>>> results = es.invokeAll(players);

      winner = null;

      try {
        for (Future<Map<Player, Weapon>> result : results) {
          for (Player player : result.get().keySet()) {
            if (isWinner(player, result.get())) {
              winner = player;
            }
          }
        }
      } catch (ExecutionException e) {
        System.out.println(e);
      }

      if (winner != null) {
        winner.incrementVictories();
        if (winner.victories >= VICTORIES_LIMIT) {
          keepPlaying = false;
          System.out.format("\n%s reaches the maximum amount of victories! Congratulations!", winner.name);
        }
      }

      roundCounter++;
    }
  } 


  public static Boolean isWinner(Player player, Map<Player, Weapon> playerWeaponMap) {
    Boolean isWinner = false;
    int points = 0;

    for (Map.Entry<Player, Weapon> playerWeapon : playerWeaponMap.entrySet()) {
      if (!player.equals(playerWeapon.getKey())
      && !playerWeaponMap.values().containsAll(EnumSet.allOf(Weapon.class))
      && !player.weapon.equals(playerWeapon.getValue())) {
        if ((playerWeapon.getValue().equals(Weapon.ROCK) && player.weapon.equals(Weapon.PAPER))
        || (playerWeapon.getValue().equals(Weapon.PAPER) && player.weapon.equals(Weapon.SCISSORS))
        || (playerWeapon.getValue().equals(Weapon.SCISSORS) && player.weapon.equals(Weapon.ROCK))) {
          points++;
        }
      }
    }

    if (points == PLAYERS-1) {
      isWinner = true;
    }
    return isWinner;
  }
}

public class Player implements Callable<Map<Player, Weapon>> {
  public int victories = 0;
  public int VICTORIES_LIMIT = 3;
  public String name;
  public Weapon weapon;
  public static Map<Player, Weapon> playerWeaponMap = new HashMap<Player, Weapon>();

  public Player(String name) {
    this.name = name;
  }

  @Override
  public Map<Player, Weapon> call() throws Exception {

    try {
      Thread.sleep(ThreadLocalRandom.current().nextInt(100, 500));
      weapon = pickRandomWeapon();
      playerWeaponMap.put(this, weapon);
    } catch (Exception e) {
    }
    
    return playerWeaponMap;
  }
  
  public Weapon pickRandomWeapon() {
    weapon = Weapon.getRandomWeapon();
    System.out.format("%s picks %s; victories so far: %d\n",  name, weapon, victories);
    return weapon;
  }

  public void incrementVictories() {
    victories++;
    System.out.format("%s wins!\n",  name);
  }
}

public enum Weapon {
  ROCK, PAPER, SCISSORS;

  private static Random rand = new Random();

  public static Weapon getRandomWeapon() {
    Weapon weapons[] = values();
    return weapons[rand.nextInt(weapons.length)];
  }
}
