package org.miniforecat.ranker;

import java.util.ArrayList;
import java.util.List;

import org.miniforecat.suggestions.SuggestionsInput;
import org.miniforecat.suggestions.SuggestionsOutput;
import org.miniforecat.utils.Quicksort;

/**
 * Chooses the closest suggestions
 * 
 * @author Daniel Torregrosa
 * 
 */
public class RankerPosition extends RankerShared {

	private static final long serialVersionUID = 185266502718735714L;

	@Override
	public List<SuggestionsOutput> rankerService(SuggestionsInput rankInp, List<SuggestionsOutput> input) {
		ArrayList<SuggestionsOutput> outputSuggestionsList = new ArrayList<SuggestionsOutput>();
		ArrayList<Integer> sortList = new ArrayList<Integer>();
		SuggestionsOutput so;

		for (int index = 0; index < input.size(); index++) {
			sortList.add(index);
			so = input.get(index);
			so.setSuggestionFeasibility(Math.abs(so.getWordPosition() - rankInp.getPosition()));
		}
		Quicksort q = new Quicksort();
		q.sort(sortList, input);

		for (int index = 0; index < maxSuggestions && index < input.size(); index++) {
			outputSuggestionsList.add(input.get(sortList.get(index)));
		}

		return outputSuggestionsList;
	}
}
