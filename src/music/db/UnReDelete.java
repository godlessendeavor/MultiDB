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
public class UnReDelete  extends AbstractUndoableEdit {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public Disc disc;
    public DataBaseTable dataBase;
    public DiscTableModel tabModel;

    public UnReDelete(DataBaseTable dataBase,Disc disc,DiscTableModel tabModel) {
        this.disc=disc;
        this.dataBase=dataBase;
        this.tabModel=tabModel;
    }

    @Override
    public void undo( ) throws CannotUndoException {
        super.undo();
        dataBase.insertNewDisc(disc);
    }

    @Override
    public void redo( ) throws CannotRedoException {
        super.redo();
        int row=tabModel.searchDisc(disc.id);
        dataBase.deleteDisc(row);
    }

   
}
