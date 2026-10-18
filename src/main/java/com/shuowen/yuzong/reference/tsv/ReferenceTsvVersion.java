package com.shuowen.yuzong.reference.tsv;

import java.time.Instant;

public record ReferenceTsvVersion(String id, String originalName, Instant uploadedAt,
                                  int rowCount, long size, String sha256, String name, boolean locked) {}
