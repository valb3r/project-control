package com.valb3r.projectcontrol.service.analyze;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
public class CommitRanges {

    private final List<Range> ranges;

    public void addAnalyzedCommit(String commitId) {
        if (ranges.size() == 0) {
            ranges.add(new Range(commitId, commitId));
            return;
        }

        var range = ranges.get(ranges.size() - 1);
        if (null == range.getStart()) {
            range.setStart(commitId);
        }
        range.setEnd(commitId);
    }

    public String toString() {
        var result = new StringBuilder();
        for (var range : ranges) {
            if (null == range.getStart()) {
                break;
            }
            result.append(range.getStart());
            result.append(":");
            result.append(range.getEnd());
            result.append(";");
        }
        result.deleteCharAt(result.length() - 1);
        return result.toString();
    }

    public static CommitRanges fromString(String serialized) {
        if (null == serialized || serialized.isEmpty()) {
            return new CommitRanges(new ArrayList<>(List.of(new Range(null, null))));
        }

        List<Range> ranges = new ArrayList<>();
        for (var part : serialized.split(";")) {
            var rangePart = part.split(":");
            ranges.add(new Range(rangePart[0], rangePart[1]));
        }

        return new CommitRanges(ranges);
    }

    @Getter
    @Setter
    @AllArgsConstructor
    private static class Range {
        private String start;
        private String end;
    }
}
