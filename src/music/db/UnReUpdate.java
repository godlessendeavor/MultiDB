/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package music.db;

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
	public Disc newDisc,previousDisc;
    public int row;
    public DataBaseTable dataBase;

    public UnReUpdate(DataBaseTable dataBase,Disc newDisc, Disc previousDisc, int row) {
        this.newDisc=newDisc;
        this.previousDisc=previousDisc;
        this.row=row;
        this.dataBase=dataBase;
    }

    @Override
    public void undo( ) throws CannotUndoException {
        super.undo();
        dataBase.updateDisc(previousDisc,row);

    }

    @Override
    public void redo( ) throws CannotRedoException {
        super.redo();
        dataBase.updateDisc(newDisc,row);
    }
}
