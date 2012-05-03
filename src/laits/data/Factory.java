package laits.data;

import laits.comm.CommException;
import laits.comm.DataArchive;
import laits.comm.Database;

/**
 * Factory
 *
 * @author Javier Gonzalez Sanchez
 * @version 20101114
 */
public abstract class Factory {

  protected Database database;
  protected DataArchive archive;

  public Factory() throws CommException {
    this.database = Database.getInstance();
    this.archive = DataArchive.getInstance();
  }

}
