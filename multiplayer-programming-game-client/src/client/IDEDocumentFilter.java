package client;

import java.awt.Color;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import javax.swing.JTextPane;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultHighlighter.DefaultHighlightPainter;
import javax.swing.text.Highlighter.Highlight;
import javax.swing.text.Document;
import javax.swing.text.DocumentFilter;
import javax.swing.text.MutableAttributeSet;
import javax.swing.text.StyleConstants;

public class IDEDocumentFilter extends DocumentFilter {
	
	JTextPane textPane; // Used to access the JTextPane that this document filter is assigned to.
	
	// Create a set of keywords:
	private Set<String> keywords = new HashSet<String>(Arrays.asList(new String[]{
			"False", "None", "True", "and", "as", "assert", "break", "class", "continue", "def", "del",
			"elif", "else", "except", "finally", "for", "from", "global", "if", "import", "in", "is",
			"lambda", "nonlocal", "not", "or", "pass", "raise", "return", "try", "while", "with", "yield"
		}));
	
	// Create Color objects for the code, keyword and occurrence highlighting colours:
	private Color codeColour = Color.black;
	private Color keywordColour = Color.blue;
	private Color occurrenceHighlightingColour = Color.yellow;
	
	private DefaultHighlightPainter highlightPainter = new DefaultHighlightPainter(occurrenceHighlightingColour); // Create a highlight painter for occurrence highlighting.
	
	private int lastAutoAddedOffset = -1; // Used to store the offset of the last automatically added character.
	
	// Create variables for toggling automatic indentation, bracket closing and string closing:
	private boolean doAutoIndentation = true;
	private boolean doAutoBracketClosing = true;
	private boolean doAutoStringClosing = true;
	
	// Create variables for accessing the filter bypass and attribute set:
	private FilterBypass filterBypass;
	private AttributeSet attributeSet;
	
	
	// Constructor:
	public IDEDocumentFilter(JTextPane textPane) {
		this.textPane = textPane; // Store the (pointer to the) text pane that this document filter is assigned to.
		
		// Add a caret listener to the text pane to handle occurrence highlighting:
		textPane.addCaretListener(new CaretListener() {
			
			// Method called when a caret update occurs:
			@Override
			public void caretUpdate(CaretEvent event) {
				
				Highlight[] highlights = textPane.getHighlighter().getHighlights(); // Get all of the highlights in the text pane.
				
				// Iterate through each highlight:
				for (Highlight highlight: highlights) {
					
					// If the highlight is made by highlightPainter, remove it:
					if (highlight.getPainter().equals(highlightPainter)) {
						textPane.getHighlighter().removeHighlight(highlight);
					}
				}
				
				// Get the left and right parts of the selected text:
				int left = event.getMark();
				int right = event.getDot();
				
				// If there is no text selected, return:
				if (left == right) {
					return;
				}
				
				if (left > right) {
					int temp = left;
					left = right;
					right = temp;
				}
				
				// Get the document from the text pane:
				Document doc = textPane.getDocument();
				
				
				try { // The following code may throw an exception which must be caught.
					
					// If there is a word character on the left side of the selection, the selection is not a word so return:
					if (left > 0 && doc.getText(left - 1, 1).matches("[^\\W]+")) {
						return;
					}
					
					// If there is a word character on the right side of the selection, the selection is not a word so return:
					if ((right < doc.getLength() && doc.getText(right, 1).matches("[^\\W]+"))) {
						return;
					}
					
					// If there are non-word characters within the selection, the selection is not a word so return:
					for (int i = left; i < right; i++) {
						if (!doc.getText(i, 1).matches("[^\\W]+")) {
							return;
						}
					}

					String word = doc.getText(left, right-left); // Get the word from the document.
					
					String allText = doc.getText(0, doc.getLength()); // Get all text from the document.
					
					int i = allText.indexOf(word); // Create a variable which stores the index of an occurrence of the word.
					
					// While there is an occurrence of the word:
					while (i != -1) {
						
						// If the left of the occurrence is a non-word character:
						if (i == 0 || !doc.getText(i - 1, 1).matches("[^\\W]+")) {
							
							// If the right of the occurrence is a non-word character:
							if (i + word.length() == doc.getLength() || !doc.getText(i + word.length(), 1).matches("[^\\W]+")) {
								
								// Highlight the occurrence of the word:
								textPane.getHighlighter().addHighlight(i, i + word.length(), highlightPainter);
							}
						}
						
						// Get the index of the next occurrence:
						i = allText.indexOf(word, i + 1);
					}
					
				}
				
				// If a bad location exception occurred, print the stack trace:
				catch (BadLocationException e) {
					e.printStackTrace();
				}
			}
		});
	}

	// Method called when a string is being added to the document:
	@Override
	public void replace(FilterBypass filterBypass, int offset, int length, String string, AttributeSet attributeSet) throws BadLocationException {
		this.filterBypass = filterBypass;
		this.attributeSet = attributeSet;
		
		StyleConstants.setForeground((MutableAttributeSet) attributeSet, codeColour); // Reset the text colour.
		lastAutoAddedOffset = -1; // Reset lastAutoAddedOffset.
		
		// If a newline character is being added to the document:
		if (string.equals("\n") && doAutoIndentation) {
			
			String character = ""; // Create a string to store a character.
			
			// Iterate from the current offset backwards:
			for (int i = offset - 1; i >= 0; i--) {
				
				character = filterBypass.getDocument().getText(i, 1); // Get the character at position i.
				
				// If the character is a newline:
				if (character.equals("\n")) {
					
					// Iterate forward from the position of the newline character:
					for (int j = i + 1; j < offset; j++) {
						
						character = filterBypass.getDocument().getText(j, 1); // Get the character at position j.
						
						// If the character is a tab character, add a tab after the string to be added to the document.
						if (character.equals("\t")) {
							string = string + "\t";
						} else break;
					}
					
					break; // Break out of the loop so that lines before the previous line do not affect the current line's indentation.
				}
			}
			
			character = filterBypass.getDocument().getText(offset - 1, 1); // Get the last character of the previous line.
			
			// If the last character of the previous line is a colon, add another tab character to the string to be added:
			if (character.equals(":")) {
				string = string + "\t";
			}
			
			// Add the string to the document:
			filterBypass.getDocument().insertString(offset, string, attributeSet); // Insert the string into the document.
		}
		
		// If the string to be added should have another character automatically added to it, add the character and store its offset:
		else if (string.equals("\"") && doAutoStringClosing) {
			filterBypass.getDocument().insertString(offset, "\"\"", attributeSet);
			lastAutoAddedOffset = offset;
		}
		else if (string.equals("(") && doAutoBracketClosing) {
			filterBypass.getDocument().insertString(offset, "()", attributeSet);
			lastAutoAddedOffset = offset;
		}
		else if (string.equals("[") && doAutoBracketClosing) {
			filterBypass.getDocument().insertString(offset, "[]", attributeSet);
			lastAutoAddedOffset = offset;
		}
		else if (string.equals("{") && doAutoBracketClosing) {
			filterBypass.getDocument().insertString(offset, "{}", attributeSet);
			lastAutoAddedOffset = offset;
		}
		
		else {
			filterBypass.getDocument().insertString(offset, string, attributeSet); // Insert the string into the document.
		}
		
		highlightKeywords(filterBypass, offset, string.length()); // Highlight words that are adjacent to the offset.
		textPane.setCaretPosition(offset+string.length()); // Move the caret position to the end of the string to be added.
	}
	
	
	// Method called when text is being removed from the document:
	@Override
	public void remove(FilterBypass filterBypass, int offset, int length) throws BadLocationException {
		
		// If the current offset is the offset of the last automatically added character's offset, remove it:
		if (offset == lastAutoAddedOffset) {
			super.remove(filterBypass, lastAutoAddedOffset, 1);
		}
		
		super.remove(filterBypass, offset, length); // Remove the character(s) at the offset.
		highlightKeywords(filterBypass, offset, -length); // Highlight words that are adjacent to the offset.
		textPane.setCaretPosition(offset); // Move the caret to the offset's position.
	}
	
	
	// Method to highlight keywords:
	public void highlightKeywords(FilterBypass filterBypass, int offset, int length) throws BadLocationException {
		
		System.out.println(offset);
		System.out.println(length);
		
		Document doc = filterBypass.getDocument();// Get the document of the text area. 
		
		int left = offset - 1; // Create a left pointer which starts at the character before the offset.
		
		// Move the left pointer left until it is not in a word.
		while (left >= 0 && doc.getText(left, 1).matches("[^\\W]+")) {
			left--;
		}
		
		// Increment the left pointer so that it is in the first character of the left word.
		left++;
		
		int right = left; // Create a right pointer which starts at the left pointer.
		
		// Increase right until it is outside of the document (or until the loop is forcefully broken):
		for (; right < doc.getLength(); right++) {
			
			// If the right pointer is not in a word:
			if (!doc.getText(right, 1).matches("[^\\W]+")) {
				
				// Highlight the word bounded by the left and right pointers:
				highlightWord(filterBypass, textPane.getParagraphAttributes(), left, right);
				
				left = right + 1; // Set the left pointer to the character after the right pointer.
				
				// If the right pointer has gone past the original end of the string to be added,
				// break out of the loop as the rest of the document should be unchanged:
				if (right > offset + length) {
					break;
				}
			}
		}
		
		// If the right pointer has reached the end of the document, highlight the last word:
		if (right >= doc.getLength()) {
			highlightWord(filterBypass, textPane.getParagraphAttributes(), left, right);
		}
	}
	
	
	// Method to highlight a word:
	private void highlightWord(FilterBypass filterBypass, AttributeSet attributeSet, int left, int right) throws BadLocationException {
		// Get the substring between the left and right pointer and store it:
		String word = filterBypass.getDocument().getText(left, right-left);
		
		super.remove(filterBypass, left, right-left); // Remove the word.
		
		// If the word is a keyword, modify the attribute set so that the text is the keyword colour:
		if (keywords.contains(word)) {
			StyleConstants.setForeground((MutableAttributeSet) attributeSet, keywordColour);
		}
		
		// If the word is not a keyword, modify the attribute set so that the text is the code colour:
		else {
			StyleConstants.setForeground((MutableAttributeSet) attributeSet, codeColour);
		}
		
		// Add the word to the document with the new attribute set:
		filterBypass.getDocument().insertString(left, word, attributeSet);
	}
	
	
	// Method to change the code colour:
	public void setCodeColour(int rgb) {
		codeColour = new Color(rgb);
		updateColours();
	}
	
	
	// Method to change the keyword colour:
	public void setKeywordColour(int rgb) {
		keywordColour = new Color(rgb);
		updateColours();
	}
	
	
	// Method to change the occurrence highlighting colour:
	public void setOccurrenceHighlightingColour(int rgb) {
		occurrenceHighlightingColour = new Color(rgb);
		highlightPainter = new DefaultHighlightPainter(occurrenceHighlightingColour);
	}
	
	
	// Method to toggle automatic indentation:
	public void doAutoIndentation(boolean doAutoIndentation) {
		this.doAutoIndentation = doAutoIndentation;
	}
	
	
	// Method to toggle automatic bracket closing:
	public void doAutoBracketClosing(boolean doAutoBracketClosing) {
		this.doAutoBracketClosing = doAutoBracketClosing;
	}
	
	
	// Method to toggle automatic string closing:
	public void doAutoStringClosing(boolean doAutoStringClosing) {
		this.doAutoStringClosing = doAutoStringClosing;
	}
	
	
	// Method to update the colours of the existing text when they are changed:
	private void updateColours() {
		
		Document doc = textPane.getDocument(); // Get the JTextPane's document.
		if (doc.getLength() == 0) return; // If the document is empty, return as there are no colours to update.
		
		// The following code may throw an exception which must be caught:
		try {
			
			int left = 0; // Create a left pointer which starts at the beginning of the document.
			int right = left; // Create a right pointer which starts at the left pointer.
			
			// Increase right until it is outside of the document (or until the loop is forcefully broken):
			for (; right < doc.getLength(); right++) {
				
				// If the right pointer is not in a word:
				if (!doc.getText(right, 1).matches("[^\\W]+")) {
					
					String word = doc.getText(left, right-left); // Get and store the word bounded between the left and right pointers.
					
					filterBypass.remove(left, right-left); // Remove the word from the document.
					
					// If the word is a keyword, modify the attribute set so that the text is the keyword colour:
					if (keywords.contains(word)) {
						StyleConstants.setForeground((MutableAttributeSet) attributeSet, keywordColour);
					}
					
					// If the word is not a keyword, modify the attribute set so that the text is the code colour:
					else {
						StyleConstants.setForeground((MutableAttributeSet) attributeSet, codeColour);
					}
					
					// Add the word to the document with the new attribute set:
					doc.insertString(left, word, attributeSet);
					
					
					left = right + 1; // Set the left pointer to the character after the right pointer.
					
					// If the right pointer has gone past the original end of the string to be added,
					// break out of the loop as the rest of the document should be unchanged:
					if (right > doc.getLength()) {
						break;
					}
				}
			}
			
			// If the right pointer has reached the end of the document, highlight the last word:
			if (right >= doc.getLength()) {
				
				String word = doc.getText(left, right-left); // Get and store the word bounded between the left and right pointers.
				
				filterBypass.remove(left, right-left); // Remove the word from the document.
				
				// If the word is a keyword, modify the attribute set so that the text is the keyword colour:
				if (keywords.contains(word)) {
					StyleConstants.setForeground((MutableAttributeSet) attributeSet, keywordColour);
				}
				
				// If the word is not a keyword, modify the attribute set so that the text is the code colour:
				else {
					StyleConstants.setForeground((MutableAttributeSet) attributeSet, codeColour);
				}
				
				// Add the word to the document with the new attribute set:
				doc.insertString(left, word, attributeSet);
			}
		}
		
		// If an exception occurs, print the stack trace and continue:
		catch (BadLocationException e) {
			e.printStackTrace();
		}
	}
}
