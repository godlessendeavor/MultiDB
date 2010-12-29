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
public class UnReDelete  extends AbstractUndoableEdit {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public Doc doc;
    public DataBaseTable dataBase;
    public TabMod tabModel;

    public UnReDelete(DataBaseTable dataBase,Doc doc,TabMod tabModel) {
        this.doc=doc;
        this.dataBase=dataBase;
        this.tabModel=tabModel;
    }

    @Override
    public void undo( ) throws CannotUndoException {
        super.undo();
        dataBase.insertNewDoc(doc);
    }

    @Override
    public void redo( ) throws CannotRedoException {
        super.redo();
        int row=tabModel.searchDoc(doc.id);
        dataBase.deleteDoc(row);
    }

   
}
