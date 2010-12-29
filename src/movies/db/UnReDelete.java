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
public class UnReDelete  extends AbstractUndoableEdit {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public Movie movie;
    public DataBaseTable dataBase;
    public TabMod tabModel;

    public UnReDelete(DataBaseTable dataBase,Movie movie,TabMod tabModel) {
        this.movie=movie;
        this.dataBase=dataBase;
        this.tabModel=tabModel;
    }

    @Override
    public void undo( ) throws CannotUndoException {
        super.undo();
        dataBase.insertNewMovie(movie);
    }

    @Override
    public void redo( ) throws CannotRedoException {
        super.redo();
        int row=tabModel.searchMovie(movie.id);
        dataBase.deleteMovie(row);
    }

   
}
