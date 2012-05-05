package laits.data;

import laits.comm.CommException;
import laits.comm.DataArchive;


/**
 * Factory
 *
 * @author Javier Gonzalez Sanchez
 * @version 20101114
 */
public abstract class Factory {

  protected DataArchive archive;

  public Factory() throws CommException {   
    this.archive = DataArchive.getInstance();
  }

}
