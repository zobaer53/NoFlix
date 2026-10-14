#include <jni.h>
#include <cstdio>
#include "third-party/ad-block/ad_block_client.h"

extern "C"
JNIEXPORT jlong
JNICALL
Java_io_github_edsuns_adblockclient_AdBlockClient_createClient(JNIEnv *env,
                                                               jobject) {
    auto *client = new AdBlockClient();
    return (long) client;
}

extern "C"
JNIEXPORT void
JNICALL
Java_io_github_edsuns_adblockclient_AdBlockClient_releaseClient(JNIEnv *env,
                                                                jobject,
                                                                jlong clientPointer,
                                                                jlong rawDataPointer,
                                                                jlong processedDataPointer) {
    auto *client = (AdBlockClient *) clientPointer;
    delete client;

    char *rawData = (char *) rawDataPointer;
    delete[] rawData;

    char *processedData = (char *) processedDataPointer;
    delete[] processedData;
}

extern "C"
JNIEXPORT jboolean JNICALL
Java_io_github_edsuns_adblockclient_AdBlockClient_isGenericElementHidingEnabled(JNIEnv *env,
                                                                                jobject /* this */,
                                                                                jlong clientPointer) {
    auto *client = (AdBlockClient *) clientPointer;
    return client->isGenericElementHidingEnabled;
}

extern "C"
JNIEXPORT void JNICALL
Java_io_github_edsuns_adblockclient_AdBlockClient_setGenericElementHidingEnabled(JNIEnv *env,
                                                                                 jobject /* this */,
                                                                                 jlong clientPointer,
                                                                                 jboolean enabled) {
    auto *client = (AdBlockClient *) clientPointer;
    client->isGenericElementHidingEnabled = enabled;
}

extern "C"
JNIEXPORT jlong
JNICALL
Java_io_github_edsuns_adblockclient_AdBlockClient_loadBasicData(JNIEnv *env,
                                                                jobject,
                                                                jlong clientPointer,
                                                                jbyteArray data,
                                                                jboolean preserveRules) {
    int dataLength = env->GetArrayLength(data);
    // parse() reads a NUL-terminated string; without the terminator it reads past
    // the buffer and crashes on large filter lists.
    char *dataChars = new char[dataLength + 1];
    env->GetByteArrayRegion(data, 0, dataLength, reinterpret_cast<jbyte *>(dataChars));
    dataChars[dataLength] = '\0';

    auto *client = (AdBlockClient *) clientPointer;
    client->parse(dataChars, preserveRules);

    return (long) dataChars;
}

extern "C"
JNIEXPORT jlong
JNICALL
Java_io_github_edsuns_adblockclient_AdBlockClient_loadProcessedData(JNIEnv *env,
                                                                    jobject /* this */,
                                                                    jlong clientPointer,
                                                                    jbyteArray data) {
    int dataLength = env->GetArrayLength(data);
    char *dataChars = new char[dataLength];
    env->GetByteArrayRegion(data, 0, dataLength, reinterpret_cast<jbyte *>(dataChars));

    auto *client = (AdBlockClient *) clientPointer;
    client->deserialize(dataChars);

    // We cannot delete dataChars here as adblock keeps a ptr to it.
    // Instead we send back a ptr ref so we can delete it later in the release method
    return (long) dataChars;
}

extern "C"
JNIEXPORT jbyteArray
JNICALL
Java_io_github_edsuns_adblockclient_AdBlockClient_getProcessedData(JNIEnv *env,
                                                                   jobject /* this */,
                                                                   jlong clientPointer) {
    auto *client = (AdBlockClient *) clientPointer;

    int size;
    char *data = client->serialize(&size, false);

    jbyteArray dataBytes = env->NewByteArray(size);
    env->SetByteArrayRegion(dataBytes, 0, size, reinterpret_cast<jbyte *>(data));

    delete[] data;
    return dataBytes;
}

// The *File variants read and write the data directly, so a filter list (tens of MB once
// processed) never has to fit in the Java heap.

// Reads the whole file into a new NUL-terminated buffer, or returns nullptr.
static char *readFile(JNIEnv *env, jstring path) {
    const char *pathChars = env->GetStringUTFChars(path, nullptr);
    FILE *file = fopen(pathChars, "rb");
    env->ReleaseStringUTFChars(path, pathChars);
    if (file == nullptr) return nullptr;
    char *data = nullptr;
    long length;
    if (fseek(file, 0, SEEK_END) == 0 && (length = ftell(file)) >= 0
        && fseek(file, 0, SEEK_SET) == 0) {
        data = new char[length + 1];
        if (fread(data, 1, length, file) == (size_t) length) {
            data[length] = '\0';
        } else {
            delete[] data;
            data = nullptr;
        }
    }
    fclose(file);
    return data;
}

extern "C"
JNIEXPORT jlong
JNICALL
Java_io_github_edsuns_adblockclient_AdBlockClient_loadBasicDataFile(JNIEnv *env,
                                                                    jobject,
                                                                    jlong clientPointer,
                                                                    jstring path,
                                                                    jboolean preserveRules) {
    char *dataChars = readFile(env, path);
    if (dataChars == nullptr) return 0;
    auto *client = (AdBlockClient *) clientPointer;
    client->parse(dataChars, preserveRules);
    return (long) dataChars;
}

extern "C"
JNIEXPORT jlong
JNICALL
Java_io_github_edsuns_adblockclient_AdBlockClient_loadProcessedDataFile(JNIEnv *env,
                                                                        jobject /* this */,
                                                                        jlong clientPointer,
                                                                        jstring path) {
    char *dataChars = readFile(env, path);
    if (dataChars == nullptr) return 0;
    auto *client = (AdBlockClient *) clientPointer;
    client->deserialize(dataChars);
    // Kept until releaseClient, like loadProcessedData's buffer.
    return (long) dataChars;
}

extern "C"
JNIEXPORT jboolean
JNICALL
Java_io_github_edsuns_adblockclient_AdBlockClient_saveProcessedData(JNIEnv *env,
                                                                    jobject /* this */,
                                                                    jlong clientPointer,
                                                                    jstring path) {
    auto *client = (AdBlockClient *) clientPointer;
    int size;
    char *data = client->serialize(&size, false);

    const char *pathChars = env->GetStringUTFChars(path, nullptr);
    FILE *file = fopen(pathChars, "wb");
    env->ReleaseStringUTFChars(path, pathChars);
    bool written = false;
    if (file != nullptr) {
        written = fwrite(data, 1, size, file) == (size_t) size;
        written = fclose(file) == 0 && written;
    }
    delete[] data;
    return written;
}

extern "C"
JNIEXPORT jint JNICALL
Java_io_github_edsuns_adblockclient_AdBlockClient_getFiltersCount(JNIEnv *env, jobject /* this */,
                                                                  jlong clientPointer) {
    auto *client = (AdBlockClient *) clientPointer;
    int count = client->numFilters
                + client->numCosmeticFilters
                + client->numHtmlFilters
                + client->numScriptletFilters
                + client->numExceptionFilters
                + client->numNoFingerprintFilters
                + client->numNoFingerprintExceptionFilters
                + client->numNoFingerprintDomainOnlyFilters
                + client->numNoFingerprintAntiDomainOnlyFilters
                + client->numNoFingerprintDomainOnlyExceptionFilters
                + client->numNoFingerprintAntiDomainOnlyExceptionFilters
                + client->numHostAnchoredFilters
                + client->numHostAnchoredExceptionFilters;
    return count;
}

extern "C"
JNIEXPORT jobject JNICALL
Java_io_github_edsuns_adblockclient_AdBlockClient_matches(JNIEnv *env, jobject /* this */,
                                                          jlong clientPointer, jstring url,
                                                          jstring firstPartyDomain,
                                                          jint filterOption) {
    jboolean isUrlCopy;
    const char *urlChars = env->GetStringUTFChars(url, &isUrlCopy);

    jboolean isDocumentCopy;
    const char *firstPartyDomainChars = env->GetStringUTFChars(firstPartyDomain, &isDocumentCopy);

    auto *client = (AdBlockClient *) clientPointer;

    Filter *matchedFilter;
    Filter *matchedExceptionFilter;
    bool shouldBlock = client->matches(urlChars, (FilterOption) filterOption, firstPartyDomainChars,
                                       &matchedFilter, &matchedExceptionFilter);

    char *matchedRule = matchedFilter ? matchedFilter->ruleDefinition : nullptr;
    char *matchedExceptionRule = matchedExceptionFilter ?
                                 matchedExceptionFilter->ruleDefinition : nullptr;

    // create java MatchResult
    jclass match_result_class = env->FindClass("io/github/edsuns/adblockclient/MatchResult");
    jmethodID init_id = env->GetMethodID(match_result_class, "<init>",
                                         "(ZLjava/lang/String;Ljava/lang/String;)V");
    jobject matchResult = env->NewObject(match_result_class, init_id,
                                         shouldBlock,
                                         env->NewStringUTF(matchedRule),
                                         env->NewStringUTF(matchedExceptionRule));

    env->ReleaseStringUTFChars(url, urlChars);
    env->ReleaseStringUTFChars(firstPartyDomain, firstPartyDomainChars);

    return matchResult;
}

// replacement for NewStringUTF()
// won't throw JNI ERROR: input is not valid Modified UTF-8
jstring bytesToStringUTF(JNIEnv *env, const char *src) {

    if (!src) {
        return nullptr;
    }
    jsize len = strlen(src);
    jstring encoding = env->NewStringUTF("UTF-8");
    jclass stringCls = env->FindClass("java/lang/String");
    jmethodID methodId = env->GetMethodID(stringCls, "<init>", "([BLjava/lang/String;)V");
    jbyteArray bytes = env->NewByteArray(len);
    env->SetByteArrayRegion(bytes, 0, len, (jbyte *) src);

    return (jstring) env->NewObject(stringCls, methodId, bytes, encoding);
}

extern "C"
JNIEXPORT jstring JNICALL
Java_io_github_edsuns_adblockclient_AdBlockClient_getElementHidingSelectors(JNIEnv *env,
                                                                            jobject /* this */,
                                                                            jlong clientPointer,
                                                                            jstring url) {
    jboolean isUrlCopy;
    const char *urlChars = env->GetStringUTFChars(url, &isUrlCopy);

    auto *client = (AdBlockClient *) clientPointer;
    const char *selectors = client->getElementHidingSelectors(urlChars);

    env->ReleaseStringUTFChars(url, urlChars);

    return bytesToStringUTF(env, selectors);
}

jobjectArray toStringArray(JNIEnv *env, const LinkedList<std::string> *rules) {
    if (!rules) {
        return nullptr;
    }
    auto array = env->NewObjectArray(rules->length(),
                                     env->FindClass("java/lang/String"), nullptr);
    int i = 0;
    for (auto r : *rules) {
        env->SetObjectArrayElement(array, i, env->NewStringUTF(r.c_str()));
        i++;
    }
    return array;
}

extern "C"
JNIEXPORT jobjectArray JNICALL
Java_io_github_edsuns_adblockclient_AdBlockClient_getExtendedCssSelectors(JNIEnv *env,
                                                                          jobject /* this */,
                                                                          jlong clientPointer,
                                                                          jstring url) {
    jboolean isUrlCopy;
    const char *urlChars = env->GetStringUTFChars(url, &isUrlCopy);

    auto *client = (AdBlockClient *) clientPointer;
    const LinkedList<std::string> *rules = client->getExtendedCssSelectors(urlChars);

    env->ReleaseStringUTFChars(url, urlChars);

    return toStringArray(env, rules);
}

extern "C"
JNIEXPORT jobjectArray JNICALL
Java_io_github_edsuns_adblockclient_AdBlockClient_getCssRules(JNIEnv *env,
                                                              jobject /* this */,
                                                              jlong clientPointer,
                                                              jstring url) {
    jboolean isUrlCopy;
    const char *urlChars = env->GetStringUTFChars(url, &isUrlCopy);

    auto *client = (AdBlockClient *) clientPointer;
    const LinkedList<std::string> *rules = client->getCssRules(urlChars);

    env->ReleaseStringUTFChars(url, urlChars);

    return toStringArray(env, rules);
}

extern "C"
JNIEXPORT jobjectArray JNICALL
Java_io_github_edsuns_adblockclient_AdBlockClient_getScriptlets(JNIEnv *env,
                                                                jobject /* this */,
                                                                jlong clientPointer,
                                                                jstring url) {
    jboolean isUrlCopy;
    const char *urlChars = env->GetStringUTFChars(url, &isUrlCopy);

    auto *client = (AdBlockClient *) clientPointer;
    const LinkedList<std::string> *rules = client->getScriptlets(urlChars);

    env->ReleaseStringUTFChars(url, urlChars);

    return toStringArray(env, rules);
}
