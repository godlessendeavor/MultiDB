/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package docs.db;

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
	public Doc newDoc,previousDoc;
    public int row;
    public DataBaseTable dataBase;

    public UnReUpdate(DataBaseTable dataBase,Doc newDoc, Doc previousDoc, int row) {
        this.newDoc=newDoc;
        this.previousDoc=previousDoc;
        this.row=row;
        this.dataBase=dataBase;
    }

    @Override
    public void undo( ) throws CannotUndoException {
        super.undo();
        dataBase.updateDoc(previousDoc,row);
    }

    @Override
    public void redo( ) throws CannotRedoException {
        super.redo();
        dataBase.updateDoc(newDoc,row);
    }
}
