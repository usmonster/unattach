package app.unattach.model;

import java.io.File;
import java.util.SortedMap;

public record ProcessSettings(ProcessOption processOption, File targetDirectory,
                              String filenameSchema, boolean addMetadata, SortedMap<String, String> idToLabel) {}
