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

  /**
   * The constructor for Factory. This creates an instance of database and archive
   * that it's child classes use. 
   * @throws CommException
   */
  public Factory() throws CommException {
    this.database = Database.getInstance();
    this.archive = DataArchive.getInstance();
  }

}
