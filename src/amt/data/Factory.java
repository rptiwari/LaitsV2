package amt.data;

import amt.comm.CommException;
import amt.comm.DataArchive;
import amt.comm.Database;

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
