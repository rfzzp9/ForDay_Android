# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile

# ===================================
# General Android
# ===================================
-dontusemixedcaseclassnames
-dontskipnonpubliclibraryclasses
-verbose

-keepattributes *Annotation*
-keepattributes Signature
-keepattributes InnerClasses
-keepattributes EnclosingMethod

# ===================================
# Kotlin
# ===================================
-keep class kotlin.Metadata { *; }
-keep class kotlin.reflect.** { *; }

-keepclassmembers class **$WhenMappings {
    <fields>;
}

# Kotlin Result
-keep class kotlin.Result { *; }

# ===================================
# Kotlinx Serialization
# ===================================
-keepattributes *Annotation*, InnerClasses
-dontnote kotlinx.serialization.AnnotationsKt

-keepclassmembers class kotlinx.serialization.json.** {
    *** Companion;
}
-keepclasseswithmembers class kotlinx.serialization.json.** {
    kotlinx.serialization.KSerializer serializer(...);
}

# 직렬화 가능한 클래스 보존
-keep,includedescriptorclasses class com.forday.app.**$$serializer { *; }
-keepclassmembers class com.forday.app.** {
    *** Companion;
}
-keepclasseswithmembers class com.forday.app.** {
    kotlinx.serialization.KSerializer serializer(...);
}

# @Serializable 어노테이션이 붙은 모든 클래스 보존
-keep @kotlinx.serialization.Serializable class ** { *; }

# ===================================
# 프로젝트 데이터 모델 - 반드시 보존
# ===================================
# Keep entire model classes to prevent ClassCastException
-keep class com.forday.app.remote.model.request.** { *; }
-keep class com.forday.app.remote.model.response.** { *; }
-keep class com.forday.app.data.model.** { *; }
-keep class com.forday.app.domain.model.** { *; }

# Keep model field names (request/response) to avoid release-only API failures due to obfuscated JSON keys
-keepclassmembers class com.forday.app.remote.model.request.** { <fields>; }
-keepclassmembers class com.forday.app.remote.model.response.** { <fields>; }
-keepclassmembers class com.forday.app.data.model.** { <fields>; }
-keepclassmembers class com.forday.app.domain.model.** { <fields>; }

# ===================================
# Navigation 파라미터로 사용되는 UI 모델 - 필드명 보존 필수
# ===================================
-keep class com.forday.app.presentation.mypage.routinedetail.RoutineRecordDetailUiModel { *; }
-keep class com.forday.app.presentation.mypage.routinedetail.RoutineReactionUiModel { *; }
-keep class com.forday.app.presentation.mypage.routinedetail.RoutineUserReactionUiModel { *; }
-keep class com.forday.app.presentation.modifyhobby.screen.HobbyModifyParams { *; }

-keepclassmembers class com.forday.app.presentation.mypage.routinedetail.RoutineRecordDetailUiModel { <fields>; }
-keepclassmembers class com.forday.app.presentation.mypage.routinedetail.RoutineReactionUiModel { <fields>; }
-keepclassmembers class com.forday.app.presentation.mypage.routinedetail.RoutineUserReactionUiModel { <fields>; }
-keepclassmembers class com.forday.app.presentation.modifyhobby.screen.HobbyModifyParams { <fields>; }

# ===================================
# Navigation 파라미터로 사용되는 Enum - 직렬화 필수
# ===================================
-keep enum com.forday.app.presentation.onboarding.timeselect.ScreenMode { *; }
-keepclassmembers enum com.forday.app.presentation.onboarding.timeselect.ScreenMode { <fields>; }

# ===================================
# NavKey 구현체 필드명 보존 (파라미터가 있는 라우트)
# ===================================
-keepclassmembers class com.forday.app.presentation.record.navigation.RecordRoutine { <fields>; }
-keepclassmembers class com.forday.app.presentation.mypage.routinedetail.navigation.RoutineDetail { <fields>; }
-keepclassmembers class com.forday.app.presentation.modifyroutine.navigation.ModifyRoutine { <fields>; }
-keepclassmembers class com.forday.app.presentation.inputhobbyroutines.navigation.InputRoutine { <fields>; }
-keepclassmembers class com.forday.app.presentation.onboarding.timeselect.navigation.SelectPerTime { <fields>; }
-keepclassmembers class com.forday.app.presentation.onboarding.frequencyselect.navigation.SelectPerWeek { <fields>; }
-keepclassmembers class com.forday.app.presentation.onboarding.periodselect.navigation.SelectPeriod { <fields>; }
-keepclassmembers class com.forday.app.core.navigation.LoadingRoutines { <fields>; }
-keepclassmembers class com.forday.app.core.navigation.RoutineAiRecommend { <fields>; }

# ===================================
# Repository & UseCase
# ===================================
-keep class com.forday.app.domain.repository.** { *; }
-keep class com.forday.app.domain.usecase.** { *; }
-keep class com.forday.app.data.repository.** { *; }

# ===================================
# Retrofit
# ===================================
-keepattributes Signature, InnerClasses, EnclosingMethod
-keepattributes RuntimeVisibleAnnotations, RuntimeVisibleParameterAnnotations
-keepattributes AnnotationDefault

# Retrofit with R8 full mode
-if interface * { @retrofit2.http.* <methods>; }
-keep,allowobfuscation interface <1>
-keep,allowobfuscation,allowshrinking class kotlin.coroutines.Continuation
-if interface * { @retrofit2.http.* public *** *(...); }
-keep,allowoptimization,allowshrinking,allowobfuscation class <3>
-keep,allowobfuscation,allowshrinking class retrofit2.Response

-keepclassmembers,allowshrinking,allowobfuscation interface * {
    @retrofit2.http.* <methods>;
}

-dontwarn org.codehaus.mojo.animal_sniffer.IgnoreJRERequirement
-dontwarn javax.annotation.**
-dontwarn kotlin.Unit

-keep class retrofit2.** { *; }
-keepclasseswithmembers class * {
    @retrofit2.http.* <methods>;
}

# ===================================
# OkHttp
# ===================================
-dontwarn okhttp3.**
-dontwarn okio.**
-dontwarn javax.annotation.**
-keepnames class okhttp3.internal.publicsuffix.PublicSuffixDatabase

# https://github.com/square/okhttp/pull/6792
-dontwarn org.conscrypt.**
-dontwarn org.bouncycastle.**
-dontwarn org.openjsse.**
-dontwarn org.bouncycastle.jsse.**

# ===================================
# Hilt
# ===================================
-dontwarn com.google.errorprone.annotations.**
-keep class dagger.** { *; }
-keep class javax.inject.** { *; }

-keepclassmembers,allowobfuscation class * {
    @javax.inject.* *;
    @dagger.* *;
}

-keep class dagger.hilt.** { *; }
-keep class * extends dagger.hilt.internal.GeneratedComponent { *; }

# ===================================
# Coroutines
# ===================================
-keepnames class kotlinx.coroutines.internal.MainDispatcherFactory {}
-keepnames class kotlinx.coroutines.CoroutineExceptionHandler {}
-keepclassmembers class kotlinx.coroutines.** {
    volatile <fields>;
}
-keepclassmembers class kotlin.coroutines.SafeContinuation {
    volatile <fields>;
}
-dontwarn kotlinx.coroutines.**

# ===================================
# Jetpack Compose
# ===================================
-keep class androidx.compose.runtime.** { *; }
-keep class androidx.compose.ui.** { *; }
-keepclassmembers class androidx.compose.** {
    <methods>;
}

# ===================================
# Firebase
# ===================================
-keep class com.google.firebase.** { *; }
-keep class com.google.android.gms.** { *; }
-dontwarn com.google.firebase.**
-dontwarn com.google.android.gms.**

# Crashlytics - keep exception info for better crash reports
-keepattributes *Annotation*
-keepattributes SourceFile,LineNumberTable
-keep public class * extends java.lang.Exception

# ===================================
# DataStore
# ===================================
-keep class androidx.datastore.*.** {*;}

# ===================================
# Coil (Image Loading)
# ===================================
-keep class coil.** { *; }
-dontwarn coil.**

# ===================================
# Navigation
# ===================================
-keep class androidx.navigation.** { *; }
-keepnames class androidx.navigation.**
-keepclassmembers class androidx.navigation.** {
    <methods>;
}

# ===================================
# Kakao SDK
# ===================================
-keep class com.kakao.sdk.**.model.* { <fields>; }
-keep class * extends com.google.gson.TypeAdapter

# ===================================
# Kotlinx DateTime
# ===================================
-keep class kotlinx.datetime.** { *; }

# ===================================
# Kotlinx Immutable Collections
# ===================================
-keep class kotlinx.collections.immutable.** { *; }

# ===================================
# Enum
# ===================================
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

# ===================================
# Parcelable
# ===================================
-keep class * implements android.os.Parcelable {
    public static final android.os.Parcelable$Creator *;
}

# ===================================
# Serializable
# ===================================
-keepclassmembers class * implements java.io.Serializable {
    static final long serialVersionUID;
    private static final java.io.ObjectStreamField[] serialPersistentFields;
    private void writeObject(java.io.ObjectOutputStream);
    private void readObject(java.io.ObjectInputStream);
    java.lang.Object writeReplace();
    java.lang.Object readResolve();
}

# ===================================
# Glance (App Widgets)
# ===================================
-keep class androidx.glance.** { *; }

# ===================================
# Timber (Logging)
# ===================================
-dontwarn org.jetbrains.annotations.**

# ===================================
# Lottie
# ===================================
-keep class com.airbnb.lottie.** { *; }

# ===================================
# R8 Optimization
# ===================================
-allowaccessmodification
-repackageclasses

-keep class com.forday.app.core.navigation.** { *; }
-keep class com.forday.app.presentation.**.navigation.** { *; }

# Navigator 및 NavigationState 보존
-keep class com.forday.app.presentation.main.Navigator { *; }
-keep class com.forday.app.presentation.main.MainNavigationState { *; }

# NavKey 구현체 보존
-keep class * implements androidx.navigation3.runtime.NavKey { *; }

# Navigation 패키지 전체 보존
-keep class com.forday.app.core.navigation.** { *; }
-keep class com.forday.app.presentation.**.navigation.** { *; }

# BottomNavItem 보존
-keep class com.forday.app.presentation.main.BottomNavItem { *; }

# ===================================
# Navigation 파라미터로 사용되는 Serializable 클래스
# ===================================
-keep class com.forday.app.presentation.inputhobbyroutines.AiRoutineItemState { *; }
-keepclassmembers class com.forday.app.presentation.inputhobbyroutines.AiRoutineItemState { <fields>; }

# ===================================
# 도메인/프레젠테이션 Enum 보존
# ===================================
-keep enum com.forday.app.domain.model.VisibilityType { *; }
-keepclassmembers enum com.forday.app.domain.model.VisibilityType { <fields>; }

-keep enum com.forday.app.presentation.onboarding.purposeselect.Purpose { *; }
-keepclassmembers enum com.forday.app.presentation.onboarding.purposeselect.Purpose { <fields>; }