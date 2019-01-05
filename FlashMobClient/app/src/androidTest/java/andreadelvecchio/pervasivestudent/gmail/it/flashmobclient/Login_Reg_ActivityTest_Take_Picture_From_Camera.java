package andreadelvecchio.pervasivestudent.gmail.it.flashmobclient;


import android.app.Activity;
import android.app.Instrumentation;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import androidx.test.InstrumentationRegistry;
import androidx.test.espresso.DataInteraction;
import androidx.test.espresso.ViewInteraction;
import androidx.test.espresso.intent.rule.IntentsTestRule;
import androidx.test.filters.LargeTest;
import androidx.test.rule.ActivityTestRule;
import androidx.test.rule.GrantPermissionRule;
import androidx.test.runner.AndroidJUnit4;

import static android.content.Context.MODE_PRIVATE;
import static androidx.test.core.app.ApplicationProvider.getApplicationContext;
import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.intent.Intents.intending;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasAction;
import static androidx.test.espresso.intent.matcher.IntentMatchers.toPackage;
import static androidx.test.espresso.matcher.RootMatchers.withDecorView;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.isEnabled;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static androidx.test.platform.app.InstrumentationRegistry.getInstrumentation;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.anything;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class Login_Reg_ActivityTest_Take_Picture_From_Camera {

//    @Rule
//    public ActivityTestRule<Login_Reg_Activity> mActivityTestRule = new ActivityTestRule<>(Login_Reg_Activity.class);

    @Rule
    public IntentsTestRule<CameraActivity> intentsRule = new IntentsTestRule<>(CameraActivity.class,true,false); ;

    @Rule
    public GrantPermissionRule mGrantPermissionRule =
            GrantPermissionRule.grant(
                    "android.permission.WRITE_EXTERNAL_STORAGE");

//    @Rule
//    public ActivityTestRule<CameraActivity> mCameraActivityTestRule = new ActivityTestRule<>(CameraActivity.class);

    @BeforeClass
    public static void beforeExec(){
        Context appContext = InstrumentationRegistry.getTargetContext();

        SharedPreferences preferences = appContext.getSharedPreferences("Login_Reg_Activity", MODE_PRIVATE);

        SharedPreferences.Editor editor = preferences.edit();

        editor.putString("username", "a");
        editor.putString("password", "a");
        editor.apply();



        getInstrumentation().getUiAutomation().executeShellCommand(
                "pm grant " + getApplicationContext().getPackageName()
                        + " android.permission.WRITE_EXTERNAL_STORAGE");
    }




//    @BeforeClass
//    @AfterClass
//    public static void resetSharedPref(){
//        Context appContext = InstrumentationRegistry.getTargetContext();
//
//        SharedPreferences preferences = appContext.getSharedPreferences("Login_Reg_Activity", MODE_PRIVATE);
//
//        SharedPreferences.Editor editor = preferences.edit();
//        editor.clear();
//        editor.commit();
//    }



    @Test
    public void login_Reg_ActivityTest_Take_Picture_From_Camera() {
//        ViewInteraction appCompatEditText = onView(
//                allOf(withId(R.id.userText),
//                        childAtPosition(
//                                childAtPosition(
//                                        withId(android.R.id.content),
//                                        0),
//                                1),
//                        isDisplayed()));
//        appCompatEditText.perform(click());
//
//        ViewInteraction appCompatEditText2 = onView(
//                allOf(withId(R.id.userText),
//                        childAtPosition(
//                                childAtPosition(
//                                        withId(android.R.id.content),
//                                        0),
//                                1),
//                        isDisplayed()));
//        appCompatEditText2.perform(replaceText("a"), closeSoftKeyboard());
//
//        ViewInteraction appCompatEditText3 = onView(
//                allOf(withId(R.id.passText),
//                        childAtPosition(
//                                childAtPosition(
//                                        withId(android.R.id.content),
//                                        0),
//                                3),
//                        isDisplayed()));
//        appCompatEditText3.perform(replaceText("a"), closeSoftKeyboard());
//
//        ViewInteraction appCompatButton = onView(
//                allOf(withId(R.id.button4), withText("Accedi"),
//                        childAtPosition(
//                                childAtPosition(
//                                        withId(android.R.id.content),
//                                        0),
//                                5),
//                        isDisplayed()));
//        appCompatButton.perform(click());
//
//        DataInteraction linearLayout = onData(anything())
//                .inAdapterView(allOf(withId(R.id.list),
//                        childAtPosition(
//                                withId(R.id.layout_for_list),
//                                0)))
//                .atPosition(0);
//        linearLayout.perform(click());
//
//        intentsRule = new IntentsTestRule<>(CameraActivity.class);
//
//        ViewInteraction appCompatButton2 = onView(
//                allOf(withId(R.id.post), withText("Scatta Foto"),
//                        childAtPosition(
//                                childAtPosition(
//                                        withId(android.R.id.content),
//                                        0),
//                                1),
//                        isDisplayed()));
//        appCompatButton2.perform(click());

        Intent intent = new Intent();
        intent.putExtra("FlashMobName", "FM_For_EspressoTest");
        intentsRule.launchActivity(intent);

        // Create a bitmap we can use for our simulated camera image
        Bitmap icon = BitmapFactory.decodeResource(
                getInstrumentation().getTargetContext().getResources(),
                R.drawable.refresh);


        // Build a result to return from the Camera app

        Intent resultData = new Intent();
        resultData.setAction(MediaStore.ACTION_IMAGE_CAPTURE);

        File photoFile = null;
        try {
            photoFile = intentsRule.getActivity().createImageFile();
            FileOutputStream out = new FileOutputStream(photoFile);
            icon.compress(Bitmap.CompressFormat.PNG, 100, out);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Uri outputUri = FileProvider.getUriForFile(
                intentsRule.getActivity(),
                BuildConfig.APPLICATION_ID + ".provider",
                photoFile);
        resultData.putExtra(MediaStore.EXTRA_OUTPUT, outputUri);
        resultData.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
        resultData.putExtra("data", icon);
        Instrumentation.ActivityResult result = new Instrumentation.ActivityResult(Activity.RESULT_OK, resultData);



        // Stub out the Camera. When an intent is sent to the Camera, this tells Espresso to respond
        // with the ActivityResult we just created
        //intending(toPackage("com.android.camera2")).respondWith(result);
        intending(hasAction(MediaStore.ACTION_IMAGE_CAPTURE)).respondWith(result);

        ViewInteraction appCompatButton3 = onView(
                allOf(withText("Scatta Foto"),
                        childAtPosition(
                                childAtPosition(
                                        withId(android.R.id.content),
                                        0),
                                2),
                        isDisplayed()));
        appCompatButton3.perform(click());



        intentsRule.getActivity().setmImageFileLocation(photoFile.getAbsolutePath());

        onView(withId(R.id.butLoad)).check(matches(isEnabled()));

        ViewInteraction appCompatButton4 = onView(
                allOf(withId(R.id.butLoad), withText("Upload Photo"),
                        childAtPosition(
                                childAtPosition(
                                        withId(android.R.id.content),
                                        0),
                                0),
                        isDisplayed()));
        appCompatButton4.perform(click());

        onView(withText("Upload OK")).inRoot(withDecorView(not(intentsRule.getActivity().getWindow().getDecorView()))).check(matches(isDisplayed()));


    }

    private static Matcher<View> childAtPosition(
            final Matcher<View> parentMatcher, final int position) {

        return new TypeSafeMatcher<View>() {
            @Override
            public void describeTo(Description description) {
                description.appendText("Child at position " + position + " in parent ");
                parentMatcher.describeTo(description);
            }

            @Override
            public boolean matchesSafely(View view) {
                ViewParent parent = view.getParent();
                return parent instanceof ViewGroup && parentMatcher.matches(parent)
                        && view.equals(((ViewGroup) parent).getChildAt(position));
            }
        };
    }
}
