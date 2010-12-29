/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package movies.db;

import javax.swing.undo.AbstractUndoableEdit;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;


/**
 *
 * @author thrasher
 */
public class UnReUpdate  extends AbstractUndoableEdit {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public Movie newMovie,previousMovie;
    public int row;
    public DataBaseTable dataBase;

    public UnReUpdate(DataBaseTable dataBase,Movie newDisc, Movie previousDisc, int row) {
        this.newMovie=newDisc;
        this.previousMovie=previousDisc;
        this.row=row;
        this.dataBase=dataBase;
    }

    @Override
    public void undo( ) throws CannotUndoException {
        super.undo();
        dataBase.updateMovie(previousMovie,row);
    }

    @Override
    public void redo( ) throws CannotRedoException {
        super.redo();
        dataBase.updateMovie(newMovie,row);
    }
}
