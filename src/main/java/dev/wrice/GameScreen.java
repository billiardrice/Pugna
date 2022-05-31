package dev.wrice;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Scanner;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import Pugna.Team;
import Pugna.Map.GameMap;
import Pugna.Map.Territories.Connection;
import Pugna.Map.Territories.Territory;
import Pugna.Troops.BasicTroop;
import Pugna.Troops.Troop;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.StrokeLineJoin;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;

public class GameScreen implements Initializable {

  enum Phase {
    DRAFT,
    ATTACK,
    FORTIFY,
    REINFORCE
  }

  private Phase phase = Phase.DRAFT;

  private Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
  private List<PolyTerrain> territories = new LinkedList<>();
  private List<Team> teams = new LinkedList<>();

  @FXML
  Pane mapPane;

  @FXML
  Text teamName;
  int curTeam = 0;
  PolyTerrain curPolyTerrain;

  @FXML
  Slider sliderVal;

  @Override
  public void initialize(URL arg0, ResourceBundle arg1) {
    fillMap();
  }

  public void fillMap() {
    try {

      FileChooser fileChooser = new FileChooser();
      fileChooser.setTitle("Open Resource File");
      fileChooser.getExtensionFilters().addAll(
          new ExtensionFilter("All Files", "*.*"));
      File selectedFile = fileChooser.showOpenDialog(App.stage);
      System.out.println(selectedFile);

      Scanner json = new Scanner(selectedFile);

      String temp = "";
      while (json.hasNextLine()) {
        temp += json.nextLine().replaceAll("\t|\n", "");
      }
      JSONObject jsonObject = (JSONObject) JSONValue.parse(temp);
      json.close();

      GameMap g = new GameMap(jsonObject);

      for (Territory t : g.getTerritories()) {

        PolyTerrain p = new PolyTerrain(t);
        territories.add(p);

        for (int i = 0; i < t.getPoints().size(); i++) {
          p.getPoints()
              .add(Double.parseDouble(t.getPoints().get((Object) Integer.valueOf(i).toString())));
        }

        p.setTId(t.getName());
        p.setFill(t.getRegion().getColor());
        p.setStroke(Color.BLACK);
        p.setStrokeLineJoin(StrokeLineJoin.BEVEL);
        p.setStrokeWidth(3);
        p.addEventHandler(
            MouseEvent.MOUSE_CLICKED,
            event -> {
              switch (phase) {
                case DRAFT:
                  if (p.getTroops().size() == 0) {
                    p.addTroop(new BasicTroop(teams.get((curTeam - 1) % teams.size()), p.getTerritory()));
                    Boolean noneLeft = true;
                    for (PolyTerrain poly : territories) {
                      if (poly.getTroops().size() == 0) {
                        noneLeft = false;
                      }
                    }
                    if (noneLeft) {
                      curTeam = 0;
                      endturn();
                      phase = Phase.REINFORCE;
                      reinforce();
                      curTeam = 0;
                      endturn();
                    } else {
                      endturn();
                    }
                  }
                  break;

                case ATTACK:
                  sliderVal.setMax(p.getTroops().size());

                  if (curPolyTerrain != null) {
                    if (curPolyTerrain == p) {
                      curPolyTerrain = null;
                      curTeam--;
                      endturn();
                      break;
                    }

                    if (p.getConnections().contains(curPolyTerrain)) {
                      if (p.getTroops().size() == 0 || p.getTroops().get(0).getTeam() == teams.get(curTeam - 1)) {
                        // MOVE
                        List<Troop> tempTroops = new LinkedList<>(curPolyTerrain.getTerritory().getMobileTroops());
                        for (Troop troop : tempTroops) {
                          troop.move(p.getTerritory());
                        }
                        p.updateLable();
                        curPolyTerrain.updateLable();
                        curPolyTerrain = null;
                        curTeam--;
                        endturn();
                      } else {
                        // ATTACK
                        System.out.println(p.getTroops().size());
                        curPolyTerrain.getTerritory().move((int) sliderVal.getValue(), p.getTerritory());
                        p.updateLable();
                        curPolyTerrain.updateLable();
                        curPolyTerrain = null;
                        curTeam--;
                        endturn();
                      }
                    } else {
                      break;
                    }

                  } else {
                    if (p.getTroops().get(0).getTeam() == teams.get((curTeam - 1) % teams.size())) {
                      curPolyTerrain = p;
                      p.setFill(p.getTerritory().getRegion().getColor().desaturate());
                      for (PolyTerrain poly : p.getConnections()) {
                        poly.setFill(Color.WHITE);
                      }
                    }
                  }

                default:
                  break;
              }

            });

        mapPane.getChildren().add(p);
        mapPane.getChildren().addAll(p.addLabel());

      }

      for (PolyTerrain p : territories) {

        List<PolyTerrain> tempPList = new LinkedList<>();

        for (Connection t : g.getConnections().get(p.getTerritory())) {
          for (PolyTerrain poly : territories) {
            if (poly.getTerritory() == t.getDestination()) {
              tempPList.add(poly);
            }
          }
        }

        p.setConnections(tempPList);
      }

    } catch (Exception e) {
      e.printStackTrace();
      LOGGER.log(Level.WARNING, "Error Filling Map");
    }

    try {
      LOGGER.log(Level.INFO, "Opening Tean Config");
      Pane teamMenu = (Pane) App.loadFXML("Teams");
      ((Pane) mapPane.getParent()).getChildren().add(teamMenu);
      teamMenu.setVisible(true);
      teamMenu.setTranslateX(1080 / 2 - 600 / 2);
      teamMenu.setTranslateY(720 / 2 - 400 / 2);
      VBox teamsBox = (VBox) teamMenu.getChildren().get(1);
      Button end = (Button) teamMenu.getChildren().get(2);
      end.setOnAction(event -> {

        for (Node team : teamsBox.getChildren()) {

          if (((TextField) ((HBox) team).getChildren().get(0)).getText() != null) {
            teams.add(new Team(((TextField) ((HBox) team).getChildren().get(0)).getText()));
          }

        }

        teamName.setText(teams.get(curTeam++).getName());

        ((Pane) mapPane.getParent()).getChildren().remove(teamMenu);

      });
    } catch (IOException e) {
      LOGGER.log(Level.WARNING, "Error Creating Team Config");
    }
  }

  public void exit() throws IOException {
    App.setRoot("mainMenu");
  }

  @FXML
  public void reinforce() {
    for (PolyTerrain p : territories) {
      if (p.getTroops().size() >= 1) {
        p.reinforce();
      }
    }
    phase = Phase.ATTACK;
  }

  public void movePhase(Team team) {
    for (PolyTerrain p : territories) {
      try {
        if (p.getTroops().get(0).getTeam() == team) {
          p.setFill(p.getTerritory().getRegion().getColor());
        } else {
          p.setFill(p.getTerritory().getRegion().getColor().darker().darker());
        }
      } catch (Exception e) {
        p.setFill(p.getTerritory().getRegion().getColor().darker().darker());
      }
    }
  }

  @FXML
  public void endturn() {
    if (curTeam > 2 && phase != Phase.DRAFT) {
      curTeam = 0;
      reinforce();
    }
    teamName.setText(teams.get(curTeam++ % teams.size()).getName());
    curPolyTerrain = null;
    Set<Team> tempTeams = new HashSet<>();
    for (PolyTerrain p : territories) {
      p.updateLable();
      try {
        tempTeams.add(p.getTroops().get(0).getTeam());
      } catch (Exception e) {
      }
    }
    switch (phase) {
      case ATTACK:
      if (tempTeams.size() == 1) {
        try {
          Pane teamMenu = (Pane) App.loadFXML("win");
          ((Pane) mapPane.getParent()).getChildren().add(teamMenu);
          teamMenu.setVisible(true);
          teamMenu.setTranslateX(1080 / 2 - 600 / 2);
          teamMenu.setTranslateY(720 / 2 - 400 / 2);
          Text text = (Text) teamMenu.getChildren().get(0);
          text.setText(((Team) tempTeams.toArray()[0]).getName() + "\nWON!!!");
          Button exit = (Button) teamMenu.getChildren().get(1);
          exit.setOnAction(event -> {
            try {
              App.setRoot("mainMenu");
            } catch (IOException e) {
            }
          });
        } catch (IOException e) {
        }
      }
        movePhase(teams.get((curTeam - 1) % teams.size()));

      default:
        break;
    }
  }

  @FXML
  public void changeTool() {

  }
}
