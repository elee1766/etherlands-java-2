package etherlandscore.etherlandscore.readonly;

import etherlandscore.etherlandscore.state.Plot;

public class ReadPlot {
  private final Plot plot;

  public ReadPlot(Plot plot){
    this.plot = plot;
  }

  public ReadPlot(Object obj){
    this.plot = (Plot) obj;
  }

}
