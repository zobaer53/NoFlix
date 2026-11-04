# Native code looks this class up by name (FindClass) and calls this constructor, so
# neither the class name nor the constructor may be renamed or removed.
-keep class io.github.edsuns.adblockclient.MatchResult { <init>(boolean,java.lang.String,java.lang.String); }
