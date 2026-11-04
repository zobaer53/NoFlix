# App-specific R8 rules. Libraries (Retrofit, kotlinx-serialization, Room, Hilt, WorkManager,
# the ad-filter modules) ship their own consumer rules; add a rule here only for code that
# is reached by name at runtime (reflection, JNI, JS bridges).

# Readable line numbers in release stack traces (deobfuscate them with mapping.txt).
-keepattributes SourceFile,LineNumberTable
-renamesourcefileattribute SourceFile

# Retrofit services return ZedMoviesResult<T> (suspend functions see it through Continuation's
# signature) and ResultAdapterFactory reads T by reflection. R8 full mode strips the generic
# signature of a type no keep rule covers, so T became Void and every body came back null.
-keep,allowobfuscation,allowshrinking class com.zobaer53.zedmovies.data.common.result.ZedMoviesResult
