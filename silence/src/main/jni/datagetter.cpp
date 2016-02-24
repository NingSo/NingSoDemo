#include "com_ningso_jni_HttpUtils.h"
#include "curl.h"
#include <stdlib.h>
#include <vector>
#include <map>
#include <string>

#include <android/log.h>

#undef DEBUG
#define DEBUG

#ifdef DEBUG

#define LOG_TAG    "Bytelee_JNI"
#undef  LOG
#define LOGD(...)  __android_log_print(ANDROID_LOG_DEBUG,LOG_TAG,__VA_ARGS__)
#define LOGI(...)  __android_log_print(ANDROID_LOG_INFO,LOG_TAG,__VA_ARGS__)
#define LOGW(...)  __android_log_print(ANDROID_LOG_WARN,LOG_TAG,__VA_ARGS__)
#define LOGE(...)  __android_log_print(ANDROID_LOG_ERROR,LOG_TAG,__VA_ARGS__)
#define LOGF(...)  __android_log_print(ANDROID_LOG_FATAL,LOG_TAG,__VA_ARGS__)

#else
#undef LOG
#define LOGD(...)
#define LOGI(...)
#define LOGW(...)
#define LOGE(...)
#define LOGF(...)

#endif


using namespace std;

std::string jstring2str(JNIEnv* env, jstring jstr);

void write_data(void *ptr, size_t size, size_t nmemb, void *stream)
{
    //LOGD("Recv Data...\n");
	std::vector<char> *recvBuffer = (std::vector<char>*)stream;
	size_t sizes = size * nmemb;
	recvBuffer->insert(recvBuffer->end(), (char*)ptr, (char*)ptr+sizes);
}

map<string, string>processWithRequestInfo(JNIEnv* env, jobjectArray keys, jobjectArray values)
{
    //LOGD("Process With Request Info Method\n");
    
    int key_len = env->GetArrayLength(keys);
    int value_len = env->GetArrayLength(values);
    int len = key_len<value_len?key_len:value_len;
    
    //LOGD("process len:%d\n", len);
    
    map<string, string> params;
    for(int i = 0; i < len; i ++)
    {
        jstring key = (jstring)env->GetObjectArrayElement(keys, i);
        string keyInfo("");
        if(key)
        {
            const char* chars = (const char*)env->GetStringUTFChars(key, NULL);
            keyInfo += chars;
            env->ReleaseStringUTFChars(key, chars);
        }
        
        jstring value = (jstring)env->GetObjectArrayElement(values, i);
        string valInfo("");
        if(value)
        {
            const char* chars = (const char*)env->GetStringUTFChars(value, NULL);
            valInfo += chars;
            env->ReleaseStringUTFChars(value, chars);
        }
        //LOGD("Insert Param: %s === %s\n", keyInfo.c_str(), valInfo.c_str());
        params.insert(pair<string, string>(keyInfo, valInfo));
    }
    
    return params;
}

string mergeParams(map<string, string>params)
{
    //LOGD("Merge Params Method\n");
    
    map<string, string>::iterator iter;
    string payload("");
    
    for(iter = params.begin(); iter != params.end(); iter++)
    {
        if (iter != params.begin())
        {
            payload += "&";
        }
        payload += iter->first;
        payload += "=";
        payload += iter->second;
    }
    
    //LOGD("Merged: %s\n", payload.c_str());
    return payload;
}

bool configureCURL(CURL *handle)
{
	if (!handle) 
	{
	       	return false;
       	}
       	int32_t code;
       	code = curl_easy_setopt(handle, CURLOPT_TIMEOUT, 15000);//3 seconds
       	if (code != CURLE_OK)
       	{
	       	return false;
       	}
       	code = curl_easy_setopt(handle, CURLOPT_CONNECTTIMEOUT, 15000);//15 seconds
       	if (code != CURLE_OK)
       	{
	       	return false;
       	} 
	return true;
}

int processPostTask(const char* url, const char* payload, size_t payload_size, void* stream)
{
    CURLcode code = CURL_LAST;
    CURL *curl = curl_easy_init();
    
    do {
        if (!configureCURL(curl))
        {
            break;
        }
        code = curl_easy_setopt(curl, CURLOPT_URL, url);
        if (code != CURLE_OK)
        {
            break;
        }
        code = curl_easy_setopt(curl, CURLOPT_WRITEFUNCTION, write_data);
        if (code != CURLE_OK)
        {
            break;
        }
        code = curl_easy_setopt(curl, CURLOPT_WRITEDATA, stream);
        if (code != CURLE_OK)
        {
            break;
        }
        
        code = curl_easy_setopt(curl, CURLOPT_POST, 1);
        if (code != CURLE_OK)
        {
            break;
        }
        
        code = curl_easy_setopt(curl, CURLOPT_POSTFIELDS, payload);
        if (code != CURLE_OK)
        {
            break;
        }
        
        code = curl_easy_setopt(curl, CURLOPT_POSTFIELDSIZE, payload_size);
        if (code != CURLE_OK)
        {
            break;
        }
        
        //LOGD("Perform POST\n");
        code = curl_easy_perform(curl);
        if (code != CURLE_OK)
        {
            //LOGD("Perform POST Failed\n");
            break;
        }
        else
        {
            //LOGD("Perform POST Done\n");
        }
        
        int32_t errorCode;
        code = curl_easy_getinfo(curl, CURLINFO_RESPONSE_CODE, &errorCode);
        if (code != CURLE_OK || errorCode != 200)
        {
            code = CURLE_HTTP_RETURNED_ERROR;
        }
    }while (0);
    
    if (curl)
    {
        curl_easy_cleanup(curl);
    }
    return (code == CURLE_OK ? 0 : 1);
}

int  processGetTask(const char *url, void *stream)
{
	CURLcode code = CURL_LAST;
	CURL *curl = curl_easy_init();
	do {
        if (!configureCURL(curl))
		{
            break;
        }
		code = curl_easy_setopt(curl, CURLOPT_URL, url);
        if (code != CURLE_OK)
		{
            break;
        }
		code = curl_easy_setopt(curl, CURLOPT_FOLLOWLOCATION, url);
        if (code != CURLE_OK)
        {
            break;
        }
		code = curl_easy_setopt(curl, CURLOPT_WRITEFUNCTION, write_data);
        if (code != CURLE_OK)
		{
            break;
        }
		code = curl_easy_setopt(curl, CURLOPT_WRITEDATA, stream);
        if (code != CURLE_OK)
		{
            break;
        }
		code = curl_easy_perform(curl);
	       	if (code != CURLE_OK) 
		{
		       	break;
        }
	    
        int32_t errorCode;
        code = curl_easy_getinfo(curl, CURLINFO_RESPONSE_CODE, &errorCode);
		if (code != CURLE_OK || errorCode != 200) 
		{
		       	code = CURLE_HTTP_RETURNED_ERROR;
	       	
        }
    }while (0);

	if (curl) 
	{
	       	curl_easy_cleanup(curl);
    }
    return (code == CURLE_OK ? 0 : 1);
}

//ttp://aassa?appInfo=

string getURL(const char* url)
{
//    LOGD("GET URL: %s\n", url);
    
	std::vector<char> recvBuffer;
	if(processGetTask(url, &recvBuffer))
    {
        string value = std::string(recvBuffer.begin(), recvBuffer.end());
//        LOGD("GET URL Done: %s\n", value.c_str());
        return value;
    }
//    LOGD("GET URL Done: null\n");
    return string("");
}

string postURL(const char* url, map<string, string>params)
{
//    LOGD("POST URL: %s\n", url);
   	std::vector<char> recvBuffer;
    string payload = mergeParams(params);
    
	if(processPostTask(url, payload.c_str(), payload.size(), &recvBuffer))
    {
        string retVal = std::string(recvBuffer.begin(), recvBuffer.end());
//        LOGD("POST URL Done: %s\n", retVal.c_str());
        return retVal;
    }
//    LOGD("POST URL Done: null\n");
    return string("");
}

/*
 * Class:     com_ningso_jni_HttpUtils
 * Method:    getAppData
 * Signature: ([Ljava/lang/String;[Ljava/lang/String;)Ljava/lang/String;
 * Get url: http://push.nbpush.com/push-api/clientApi
 */
JNIEXPORT jstring JNICALL Java_com_ningso_jni_HttpUtils_getAppData
(JNIEnv *env, jclass cls, jobjectArray keys, jobjectArray values)
{
//    LOGD("Get Method\n");
    
    string url = "http://api.zitiguanjia.com/ad/getAdOne";
    map<string, string>params = processWithRequestInfo(env, keys, values);
    
    url += "?";
    url += mergeParams(params);
    
    string retVal = getURL(url.c_str());
    jstring val = env->NewStringUTF(retVal.c_str());
    return val;
}

/*
 * Class:     com_ningso_jni_HttpUtils
 * Method:    postAppData
 * Signature: ([Ljava/lang/String;[Ljava/lang/String;)Ljava/lang/String;
 * Post url: http://push.nbpush.com/push-api/clientApi
 */
JNIEXPORT jstring JNICALL Java_com_ningso_jni_HttpUtils_postAppData
(JNIEnv *env, jclass cls, jobjectArray keys, jobjectArray values)
{
   string url = "http://api.zitiguanjia.com/ad/getAdOne";
    map<string, string>params = processWithRequestInfo(env, keys, values);
    
    string retVal = postURL(url.c_str(), params);
    jstring val = env->NewStringUTF(retVal.c_str());
    return val;
}

/*
 * Class:     com_ningso_jni_HttpUtils
 * Method:    postLog
 * Signature: ([Ljava/lang/String;[Ljava/lang/String;)V
 * Post url: http://push.nbpush.com/push-api/stat
 */
JNIEXPORT jstring JNICALL Java_com_ningso_jni_HttpUtils_postLog
(JNIEnv *env, jclass cls, jobjectArray keys, jobjectArray values)
{
/*
#?packageKey=sByXii1W&categoryKey=KeT3axbI&ResourcesTypeKey=NHwpZYrz&adType=image
**/

    string url = "http://api.zitiguanjia.com/ad/getAdOne";
    map<string, string>params = processWithRequestInfo(env, keys, values);
    
    string retVal = postURL(url.c_str(), params);
    jstring val = env->NewStringUTF(retVal.c_str());
    return val;
}

/*
 * Class:     com_ningso_jni_HttpUtils
 * Method:    postActive
 * Signature: ([Ljava/lang/String;[Ljava/lang/String;)V
 * Post url: http://push.nbpush.com/push-api/channelSplit
 */
JNIEXPORT jstring JNICALL Java_com_ningso_jni_HttpUtils_postActive
(JNIEnv *env, jclass cls, jobjectArray keys, jobjectArray values)
{
    string url = "http://api.zitiguanjia.com/ad/getAdOne";
    map<string, string>params = processWithRequestInfo(env, keys, values);
    string retVal = postURL(url.c_str(), params);
    jstring val = env->NewStringUTF(retVal.c_str());
    return val;
}

/*
 * Class:     com_ningso_jni_HttpUtils
 * Method:    getData
 * Signature: (Ljava/lang/String;[Ljava/lang/String;[Ljava/lang/String;)Ljava/lang/String;
 */
JNIEXPORT jstring JNICALL Java_com_ningso_jni_HttpUtils_getData
  (JNIEnv *env, jclass cls, jstring j_str, jobjectArray keys, jobjectArray values){
   string url = "http://api.zitiguanjia.com/";
  url += jstring2str(env,j_str);
   map<string, string>params = processWithRequestInfo(env, keys, values);

      url += "?";
      url += mergeParams(params);

      string retVal = getURL(url.c_str());
      jstring val = env->NewStringUTF(retVal.c_str());
      return val;
  }

/*
 * Class:     com_ningso_jni_HttpUtils
 * Method:    postData
 * Signature: (Ljava/lang/String;[Ljava/lang/String;[Ljava/lang/String;)Ljava/lang/String;
 */
JNIEXPORT jstring JNICALL Java_com_ningso_jni_HttpUtils_postData
  (JNIEnv *env, jclass cls, jstring j_str, jobjectArray keys, jobjectArray values)
  {
      string url = "http://api.zitiguanjia.com/";
      url += jstring2str(env,j_str);
      map<string, string>params = processWithRequestInfo(env, keys, values);
      string retVal = postURL(url.c_str(), params);
      jstring val = env->NewStringUTF(retVal.c_str());
      return val;
  }


std::string jstring2str( JNIEnv* env, jstring jstr )
{
	char		* rtn		= NULL;
	jclass		clsstring	= env->FindClass( "java/lang/String" );
	jstring		strencode	= env->NewStringUTF( "GB2312" );
	jmethodID	mid		= env->GetMethodID( clsstring, "getBytes",
							    "(Ljava/lang/String;)[B" );
	jbyteArray	barr	= (jbyteArray) env->CallObjectMethod( jstr, mid, strencode );
	jsize		alen	= env->GetArrayLength( barr );
	jbyte		* ba	= env->GetByteArrayElements( barr, JNI_FALSE );
	if ( alen > 0 )
	{
		rtn = (char *) malloc( alen + 1 );
		memcpy( rtn, ba, alen );
		rtn[alen] = 0;
	}
	env->ReleaseByteArrayElements( barr, ba, 0 );
	std::string stemp( rtn );

	free( rtn );
	return(stemp);
}