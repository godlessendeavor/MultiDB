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
public class UnReUpdate  extends AbstractUndoableEdit {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public Video newVid,prevVid;
    public int row;
    public DataBaseTable dataBase;

    public UnReUpdate(DataBaseTable dataBase,Video newVid, Video prevVid, int row) {
        this.newVid=newVid;
        this.prevVid=prevVid;
        this.row=row;
        this.dataBase=dataBase;
    }

    @Override
    public void undo( ) throws CannotUndoException {
        super.undo();
        dataBase.updateVideo(prevVid,row);

    }

    @Override
    public void redo( ) throws CannotRedoException {
        super.redo();
        dataBase.updateVideo(newVid,row);
    }
}
