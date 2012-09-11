/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package musicmovies.db;

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
	public Video vid;
    public DataBaseTable dataBase;
    public TabMod tabModel;

    public UnReDelete(DataBaseTable dataBase,Video vid,TabMod tabModel) {
        this.vid=vid;
        this.dataBase=dataBase;
        this.tabModel=tabModel;
    }

    @Override
    public void undo( ) throws CannotUndoException {
        super.undo();
        dataBase.insertNewVideo(vid);
    }

    @Override
    public void redo( ) throws CannotRedoException {
        super.redo();
        int row=tabModel.searchVideo(vid.id.intValue());
        dataBase.deleteVideo(row);
    }

   
}
