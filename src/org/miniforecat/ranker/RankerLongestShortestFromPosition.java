package org.miniforecat.ranker;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import org.miniforecat.suggestions.SuggestionsInput;
import org.miniforecat.suggestions.SuggestionsOutput;

/**
 * Chooses the longest and shortest suggestion of each position, starting with the closest position.
 * 
 * @author Daniel Torregrosa
 * 
 */
public class RankerLongestShortestFromPosition extends RankerShared {

	private static final long serialVersionUID = -6208413797820197425L;
	RankerShared applyBefore = null;

	public class SuggestionsOutputLengthComparator implements Comparator<SuggestionsOutput> {
		@Override
		public int compare(SuggestionsOutput o1, SuggestionsOutput o2) {
			return o1.getSuggestionText().length() - o2.getSuggestionText().length();
		}
	}

	@Override
	public List<SuggestionsOutput> rankerService(SuggestionsInput rankInp, List<SuggestionsOutput> input) {
		ArrayList<SuggestionsOutput> outputSuggestionsList = new ArrayList<SuggestionsOutput>();
		HashMap<Integer, List<SuggestionsOutput>> all = new HashMap<Integer, List<SuggestionsOutput>>();
		int biggestList = 0;

		for (SuggestionsOutput so : input) {
			if (all.get(so.getWordPosition()) == null) {
				all.put(so.getWordPosition(), new ArrayList<SuggestionsOutput>());
				all.get(so.getWordPosition()).add(so);
			} else {
				all.get(so.getWordPosition()).add(so);
			}
			if (all.get(so.getWordPosition()).size() > biggestList) {
				biggestList = all.get(so.getWordPosition()).size();
			}
		}

		for (Integer i : all.keySet()) {
			Collections.sort(all.get(i), new SuggestionsOutputLengthComparator());
		}

		ArrayList<Integer> order = new ArrayList<Integer>();

		for (SuggestionsOutput so : input) {
			if (!order.contains(so.getWordPosition())) {
				order.add(so.getWordPosition());
			}
		}

		List<SuggestionsOutput> current;

		int iteration = 0;

		// Iteration controls what length rank to pick: iteration 1 will pick longest and shortest,
		// iteration 2 will pick 2nd longest and 2nd shortest...
		while ((iteration <= biggestList / 2) && outputSuggestionsList.size() < maxSuggestions) {
			for (Integer i : order) {
				current = all.get(i);
				if (iteration <= current.size() / 2) {
					// Check if we have enough suggestions
					if (outputSuggestionsList.size() >= maxSuggestions)
						break;
					// Add the iteration-th longest suggestion if it is not already on the result
					if (!outputSuggestionsList.contains(current.get(current.size()
							- (1 + iteration)))) {
						outputSuggestionsList.add(current.get(current.size() - (1 + iteration)));
					}
					// Check if we have enough suggestions
					if (outputSuggestionsList.size() >= maxSuggestions)
						break;
					// Add the iteration-th shortest suggestion if it is not already on the result
					if (!outputSuggestionsList.contains(current.get(iteration))) {
						outputSuggestionsList.add(current.get(iteration));
					}
				}
			}
			iteration++;
		}

		return outputSuggestionsList;
	}
}
