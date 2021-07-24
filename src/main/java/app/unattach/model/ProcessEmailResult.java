package app.unattach.model;

import java.util.Set;

public record ProcessEmailResult(String newId, Set<String> filenames) {}
