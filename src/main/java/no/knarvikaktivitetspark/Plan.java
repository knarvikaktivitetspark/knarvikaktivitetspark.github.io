package no.knarvikaktivitetspark;

import java.util.List;

public record Plan(List<Entry> progress, List<Entry> plan) {

    public record Entry(String month, String[] entries) {}

}
