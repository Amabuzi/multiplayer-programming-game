package reorder;

import java.util.Vector;

import javax.swing.table.DefaultTableModel;

@SuppressWarnings("serial")
public class ReorderableTableModel extends DefaultTableModel {

	// Inherit the constructor of DefaultTableModel:
	public ReorderableTableModel(Object[][] objects, String[] strings) {
		super(objects, strings);
	}

	// Make each cell non-editable:
	@Override
	public boolean isCellEditable(int row, int column) {
		return false;
	}

	// Method to move a row from one index to another:
	public void reorder(int fromIndex, int toIndex) {

		// If the row is being moved down the table, take one away from toIndex
		// as removing the row would decrease the index of every row after it by 1:
		if (fromIndex < toIndex) {
			toIndex -= 1;
		}

		Vector<?> row = getDataVector().remove(fromIndex); // Remove the row at fromIndex and store it.
		getDataVector().add(toIndex, row); // Add the stored row to toIndex.
	}

}
