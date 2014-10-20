package com.adjust.sdk.test;

import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import android.net.Uri;
import android.os.SystemClock;
import android.test.ActivityInstrumentationTestCase2;

import com.adjust.sdk.ActivityHandler;
import com.adjust.sdk.ActivityKind;
import com.adjust.sdk.ActivityPackage;
import com.adjust.sdk.AdjustFactory;
import com.adjust.sdk.Constants;
import com.adjust.sdk.Logger.LogLevel;

public class TestActivityHandler extends ActivityInstrumentationTestCase2<UnitTestActivity> {

    protected MockLogger mockLogger;
    protected MockPackageHandler mockPackageHandler;
    protected UnitTestActivity activity;

    public TestActivityHandler(){
        super(UnitTestActivity.class);
    }

    public TestActivityHandler(Class<UnitTestActivity> mainActivity){
        super(mainActivity);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        mockLogger = new MockLogger();
        mockPackageHandler = new MockPackageHandler(mockLogger);

        AdjustFactory.setLogger(mockLogger);
        AdjustFactory.setPackageHandler(mockPackageHandler);

        activity = getActivity();
    }

    @Override
    protected void tearDown() throws Exception{
        super.tearDown();

        AdjustFactory.setPackageHandler(null);
        AdjustFactory.setLogger(null);
        AdjustFactory.setTimerInterval(-1);
        AdjustFactory.setSessionInterval(-1);
        AdjustFactory.setSubsessionInterval(-1);
    }

    public void testFirstSession() {
        Context context = activity.getApplicationContext();

        // deleting the activity state file to simulate a first session
        mockLogger.test("Was AdjustActivityState deleted? " + ActivityHandler.deleteActivityState(context));

        ActivityHandler activityHandler = new ActivityHandler(activity);
        // start the first session
        activityHandler.trackSubsessionStart();
        // it's necessary to sleep the activity for a while after each handler call
        // to let the internal queue act
        SystemClock.sleep(1000);

        // test if the environment was set to Sandbox from the bundle
        assertTrue(mockLogger.toString(),
            mockLogger.containsMessage(LogLevel.ASSERT, "SANDBOX: Adjust is running in Sandbox mode"));

        // test that the file did not exist in the first run of the application
        assertTrue(mockLogger.toString(),
            mockLogger.containsMessage(LogLevel.VERBOSE, "Activity state file not found"));

        // when a session package is being sent the package handler should resume sending
        assertTrue(mockLogger.toString(),
            mockLogger.containsTestMessage("PackageHandler resumeSending"));

        // if the package was build, it was sent to the Package Handler
        assertTrue(mockLogger.toString(),
                mockLogger.containsTestMessage("PackageHandler addPackage"));

        // after adding, the activity handler ping the Package handler to send the package
        assertTrue(mockLogger.toString(),
                mockLogger.containsTestMessage("PackageHandler sendFirstPackage"));

        // checking the default values of the first session package
        // should only have one package
        assertEquals(1, mockPackageHandler.queue.size());

        ActivityPackage activityPackage = mockPackageHandler.queue.get(0);

        // check the Sdk version is being tested
        assertEquals(activityPackage.getExtendedString(),
            "android3.6.1", activityPackage.getClientSdk());

        // check the server url
        assertEquals(Constants.BASE_URL, "https://app.adjust.io");

        Map<String, String> parameters = activityPackage.getParameters();

        // check appToken?
        // device specific attributes: setMacShortMd5, setMacSha1, setAndroidId, setUserAgent
        // how to test setFbAttributionId, defaultTracker?

        // session atributes
        // sessionCount 1, because is the first session
        assertEquals(activityPackage.getExtendedString(),
            1, Integer.parseInt(parameters.get("session_count")));
        // subSessionCount -1, because we didn't had any subsessions yet
        // because only values > 0 are added to parameters, therefore is not present
        assertNull(activityPackage.getExtendedString(),
            parameters.get("subsession_count"));
        // sessionLenght -1, same as before
        assertNull(activityPackage.getExtendedString(),
            parameters.get("session_length"));
        // timeSpent -1, same as before
        assertNull(activityPackage.getExtendedString(),
            parameters.get("time_spent"));
        // createdAt
        // test diff with current now?
        // lastInterval -1, same as before
        assertNull(activityPackage.getExtendedString(),
            parameters.get("last_interval"));
        // packageType should be SESSION_START
        assertEquals(activityPackage.getExtendedString(),
            "/startup", activityPackage.getPath());

        // TODO: test resetSessionAttributes ?

        // check that the activity state is written by the first session or timer
        assertTrue(mockLogger.toString(),
                mockLogger.containsMessage(LogLevel.DEBUG, "Wrote activity state"));

        // ending of first session
        assertTrue(mockLogger.toString(),
                mockLogger.containsMessage(LogLevel.INFO, "First session"));

        SystemClock.sleep(1000);
        // by this time also the timer should have fired
        assertTrue(mockLogger.toString(),
                mockLogger.containsTestMessage("PackageHandler sendFirstPackage"));
    }

    public void testSessions() {
        Context context = activity.getApplicationContext();

        // starting from a clean slate
        mockLogger.test("Was AdjustActivityState deleted? " + ActivityHandler.deleteActivityState(context));

        // adjust the intervals for testing
        AdjustFactory.setSessionInterval(2000);
        AdjustFactory.setSubsessionInterval(100);

        ActivityHandler activityHandler = new ActivityHandler(activity);

        // start the first session
        activityHandler.trackSubsessionStart();

        // wait enough to be a new subsession, but not a new session
        SystemClock.sleep(1500);

        activityHandler.trackSubsessionStart();
        // wait enough to be a new session
        SystemClock.sleep(4000);
        activityHandler.trackSubsessionStart();
        // test the subsession end
        activityHandler.trackSubsessionEnd();
        SystemClock.sleep(1000);

        // check that a new subsession was created
        assertTrue(mockLogger.toString(),
            mockLogger.containsMessage(LogLevel.INFO, "Started subsession 2 of session 1"));

        // check that it's now on the 2nd session
        assertTrue(mockLogger.toString(),
            mockLogger.containsMessage(LogLevel.DEBUG, "Session 2"));

        // check that 2 packages were added to the package handler
        assertEquals(2, mockPackageHandler.queue.size());

        // get the second session package and its parameters
        ActivityPackage activityPackage = mockPackageHandler.queue.get(1);
        Map<String, String> parameters = activityPackage.getParameters();

        // the session and subsession count should be 2
        assertEquals(activityPackage.getExtendedString(),
            2, Integer.parseInt(parameters.get("session_count")));
        assertEquals(activityPackage.getExtendedString(),
            2, Integer.parseInt(parameters.get("subsession_count")));

        // TODO test updated timeSpent and sessionLenght
        mockLogger.test("timeSpent " + parameters.get("time_spent"));
        mockLogger.test("sessionLength " + parameters.get("session_length"));

        // check that the package handler was paused
        assertTrue(mockLogger.toString(),
            mockLogger.containsTestMessage("PackageHandler pauseSending"));
    }

    public void testEventsBuffered() {
        Context context = activity.getApplicationContext();

        // starting from a clean slate
        mockLogger.test("Was AdjustActivityState deleted? " + ActivityHandler.deleteActivityState(context));

        ActivityHandler activityHandler = new ActivityHandler(activity, "123456789012", "sandbox", "verbose", true);

        // start the first session
        activityHandler.trackSubsessionStart();

        // construct the parameters of the the event
        Map<String, String> eventParameters = new HashMap<String, String>();
        eventParameters .put("key", "value");
        eventParameters .put("foo", "bar");

        // the first event has parameters, the second none
        activityHandler.trackEvent("abc123", eventParameters);
        activityHandler.trackRevenue(4.45, "abc123", eventParameters);
        SystemClock.sleep(2000);

        // check that event buffering is enabled
        assertTrue(mockLogger.toString(),
            mockLogger.containsMessage(LogLevel.INFO, "Event buffering is enabled"));

        // check that the package builder added the session, event and revenue package
        assertEquals(mockLogger.toString(),
            3, mockPackageHandler.queue.size());

        // check the first event
        ActivityPackage activityPackage = mockPackageHandler.queue.get(1);
        Map<String, String> packageParameters = activityPackage.getParameters();

        // check the event count in the package parameters
        assertEquals(activityPackage.getExtendedString(),
            1, Integer.parseInt(packageParameters.get("event_count")));

        // check the event token
        assertEquals(activityPackage.getExtendedString(),
            "abc123", packageParameters.get("event_token"));

        // check the injected parameters
        assertEquals(activityPackage.getExtendedString(),
            "eyJrZXkiOiJ2YWx1ZSIsImZvbyI6ImJhciJ9", packageParameters.get("params"));

        // check the event path
        assertEquals(activityPackage.getExtendedString(),
            "/event", activityPackage.getPath());

        // check the event suffix
        assertEquals(activityPackage.getExtendedString(),
            " 'abc123'", activityPackage.getSuffix());

        // check that the event was buffered
        assertTrue(mockLogger.toString(),
            mockLogger.containsMessage(LogLevel.INFO, "Buffered event  'abc123'"));

        // check the event count in the written activity state
        assertTrue(mockLogger.toString(),
            mockLogger.containsMessage(LogLevel.DEBUG, "Wrote activity state: ec:1"));

        // check the event count in the logger
        assertTrue(mockLogger.toString(),
            mockLogger.containsMessage(LogLevel.DEBUG, "Event 1"));

        // check the second event/ first revenue
        activityPackage = mockPackageHandler.queue.get(2);
        packageParameters = activityPackage.getParameters();

        // check the event count in the package parameters
        assertEquals(activityPackage.getExtendedString(),
            2, Integer.parseInt(packageParameters.get("event_count")));

        // check the amount, transforming cents into rounded decimal cents
        // note that the 4.45 cents ~> 45 decimal cents
        assertEquals(activityPackage.getExtendedString(),
            45, Long.parseLong(packageParameters.get("amount")));

        // check the event token
        assertEquals(activityPackage.getExtendedString(),
            "abc123", packageParameters.get("event_token"));

        // check the injected parameters
        assertEquals(activityPackage.getExtendedString(),
            "eyJrZXkiOiJ2YWx1ZSIsImZvbyI6ImJhciJ9", packageParameters.get("params"));

        // check the revenue path
        assertEquals(activityPackage.getExtendedString(),
            "/revenue", activityPackage.getPath());

        // check the revenue suffix
        // note that the amount was rounded to the decimal cents
        assertEquals(activityPackage.getExtendedString(),
            " (4.5 cent, 'abc123')", activityPackage.getSuffix());

        // check that the revenue was buffered
        assertTrue(mockLogger.toString(),
            mockLogger.containsMessage(LogLevel.INFO, "Buffered revenue  (4.5 cent, 'abc123')"));

        // check the event count in the written activity state
        assertTrue(mockLogger.toString(),
            mockLogger.containsMessage(LogLevel.DEBUG, "Wrote activity state: ec:2"));

        // check the event count in the logger
        assertTrue(mockLogger.toString(),
            mockLogger.containsMessage(LogLevel.DEBUG, "Event 2 (revenue)"));
    }

    public void testEventsNotBuffered() {
        Context context = activity.getApplicationContext();

        // starting from a clean slate
        mockLogger.test("Was AdjustActivityState deleted? " + ActivityHandler.deleteActivityState(context));

        ActivityHandler activityHandler = new ActivityHandler(activity);
        // start the first session
        activityHandler.trackSubsessionStart();

        // construct the parameters of the the event
        Map<String, String> eventParameters = new HashMap<String, String>();
        eventParameters .put("key", "value");
        eventParameters .put("foo", "bar");

        // the first event has parameters, the second none
        activityHandler.trackEvent("abc123", null);

        activityHandler.trackRevenue (0, null, null);
        SystemClock.sleep(2000);

        // check that the package added the session, event and revenue package
        assertEquals(mockLogger.toString(),
            3, mockPackageHandler.queue.size());

        // check the first event
        ActivityPackage activityPackage = mockPackageHandler.queue.get(1);
        Map<String, String> packageParameters = activityPackage.getParameters();

        // check that it contains the information of the tracking being enabled
        assertNotNull(activityPackage.getExtendedString(),
            packageParameters.get("tracking_enabled"));


        // check the event count in the package parameters
        assertEquals(activityPackage.getExtendedString(),
            1, Integer.parseInt(packageParameters.get("event_count")));

        // check the event token
        assertEquals(activityPackage.getExtendedString(),
            "abc123", packageParameters.get("event_token"));

        // check the that the parameters were not injected
        assertNull(activityPackage.getExtendedString(),
            packageParameters.get("params"));

        // check the event path
        assertEquals(activityPackage.getExtendedString(),
            "/event", activityPackage.getPath());

        // check the event suffix
        assertEquals(activityPackage.getExtendedString(),
            " 'abc123'", activityPackage.getSuffix());

        // check that the package handler was called
        assertTrue(mockLogger.toString(),
            mockLogger.containsTestMessage("PackageHandler sendFirstPackage"));

        // check the event count in the written activity state
        assertTrue(mockLogger.toString(),
            mockLogger.containsMessage(LogLevel.DEBUG, "Wrote activity state: ec:1"));

        // check the event count in the logger
        assertTrue(mockLogger.toString(),
            mockLogger.containsMessage(LogLevel.DEBUG, "Event 1"));

        // check the fourth event/ second revenue
        activityPackage = mockPackageHandler.queue.get(2);
        packageParameters = activityPackage.getParameters();

        // check the event count in the package parameters
        assertEquals(activityPackage.getExtendedString(),
            2, Integer.parseInt(packageParameters.get("event_count")));

        // check the amount, transforming cents into rounded decimal cents
        // note that the 4.45 cents ~> 45 decimal cents
        assertEquals(activityPackage.getExtendedString(),
            0, Long.parseLong(packageParameters.get("amount")));

        // check that the event token is null
        assertNull(activityPackage.getExtendedString(),
            packageParameters.get("event_token"));

        // check the that the parameters were not injected
        assertNull(activityPackage.getExtendedString(),
            packageParameters.get("params"));

        // check the revenue path
        assertEquals(activityPackage.getExtendedString(),
            "/revenue", activityPackage.getPath());

        // check the revenue suffix
        // note that the amount was rounded to the decimal cents
        assertEquals(activityPackage.getExtendedString(),
            " (0.0 cent)", activityPackage.getSuffix());

        // check that the package handler was called
        assertTrue(mockLogger.toString(),
            mockLogger.containsTestMessage("PackageHandler sendFirstPackage"));

        // check the event count in the written activity state
        assertTrue(mockLogger.toString(),
            mockLogger.containsMessage(LogLevel.DEBUG, "Wrote activity state: ec:2"));

        // check the event count in the logger
        assertTrue(mockLogger.toString(),
            mockLogger.containsMessage(LogLevel.DEBUG, "Event 2 (revenue)"));
    }

    public void testChecks() {
        Context context = activity.getApplicationContext();

        // starting from a clean slate
        mockLogger.test("Was AdjustActivityState deleted? " + ActivityHandler.deleteActivityState(context));

        // activity with null environment and app token
        ActivityHandler activityHandler = new ActivityHandler(activity, null, null, null, false);
        activityHandler.trackSubsessionStart();
        activityHandler.trackSubsessionEnd();
        activityHandler.trackEvent("123456", null);
        activityHandler.trackRevenue(0, null, null);
        SystemClock.sleep(1000);

        // check message for null environment
        assertTrue(mockLogger.toString(),
            mockLogger.containsMessage(LogLevel.ASSERT, "Missing environment"));
        // TODO test package with env UNKNOWN

        // check first missing app token message from init
        assertTrue(mockLogger.toString(),
            mockLogger.containsMessage(LogLevel.ERROR, "Missing App Token."));

        // check second missing app token message from track session start
        assertTrue(mockLogger.toString(),
                mockLogger.containsMessage(LogLevel.ERROR, "Missing App Token."));

        // check third missing app token message from track session end
        assertTrue(mockLogger.toString(),
                mockLogger.containsMessage(LogLevel.ERROR, "Missing App Token."));

        // check fourth missing app token message from track event
        assertTrue(mockLogger.toString(),
                mockLogger.containsMessage(LogLevel.ERROR, "Missing App Token."));

        // check fifth missing app token message from track revenue
        assertTrue(mockLogger.toString(),
                mockLogger.containsMessage(LogLevel.ERROR, "Missing App Token."));

        // activity with invalid app token and environment
        activityHandler = new ActivityHandler(activity, "12345678901", "notValid", "verbose", false);
        activityHandler.trackSubsessionStart();

        SystemClock.sleep(1000);

        assertTrue(mockLogger.toString(),
            mockLogger.containsMessage(LogLevel.ASSERT, "Malformed environment 'notValid'"));
        // TODO test package with env MALFORMED

        assertTrue(mockLogger.toString(),
                mockLogger.containsMessage(LogLevel.ERROR, "Malformed App Token '12345678901'"));

        // activity handler with production environment, invalid event and revenue
        activityHandler = new ActivityHandler(activity, "qwerty123456", "production", "verbose", false);
        activityHandler.trackSubsessionStart();
        activityHandler.trackEvent(null, null);
        activityHandler.trackRevenue(-0.1, null, null);
        activityHandler.trackEvent("12345", null);
        SystemClock.sleep(1000);

        assertTrue(mockLogger.toString(),
            mockLogger.containsMessage(LogLevel.ASSERT,
                "PRODUCTION: Adjust is running in Production mode. Use this setting only for the build that you want to publish. Set the environment to `sandbox` if you want to test your app!"));

        // check that event token can not be null for event
        assertTrue(mockLogger.toString(),
            mockLogger.containsMessage(LogLevel.ERROR, "Missing Event Token"));

        // check that revenue can not be less than 0
        assertTrue(mockLogger.toString(),
            mockLogger.containsMessage(LogLevel.ERROR, "Invalid amount -0.1"));

        // check the lenght of the event token
        assertTrue(mockLogger.toString(),
            mockLogger.containsMessage(LogLevel.ERROR, "Malformed Event Token '12345'"));

    }

    public void testDisable() {
        Context context = activity.getApplicationContext();

        // starting from a clean slate
        mockLogger.test("Was AdjustActivityState deleted? " + ActivityHandler.deleteActivityState(context));

        // set the timer for a shorter time for testing
        AdjustFactory.setTimerInterval(700);

        ActivityHandler activityHandler = new ActivityHandler(activity, "qwerty123456", "sandbox", "verbose", false);

        // verify the default value, when not started
        assertTrue(activityHandler.isEnabled());

        activityHandler.setEnabled(false);

        // verify the default value, when not started
        assertFalse(activityHandler.isEnabled());

        // start the first session
        activityHandler.trackSubsessionStart();
        activityHandler.trackEvent("123456", null);
        activityHandler.trackRevenue(0.1, null, null);
        activityHandler.trackSubsessionEnd();
        activityHandler.trackSubsessionStart();

        SystemClock.sleep(1000);

        // verify the changed value after the activity handler is started
        assertFalse(activityHandler.isEnabled());

        // making sure the first session was sent
        assertTrue(mockLogger.toString(),
            mockLogger.containsMessage(LogLevel.INFO, "First session"));

        // delete the first session package from the log
        assertTrue(mockLogger.toString(),
            mockLogger.containsTestMessage("PackageHandler sendFirstPackage"));

        // making sure the timer fired did not call the package handler
        assertFalse(mockLogger.toString(),
            mockLogger.containsTestMessage("PackageHandler sendFirstPackage"));

        // test if the event was not triggered
        assertFalse(mockLogger.toString(),
            mockLogger.containsMessage(LogLevel.DEBUG, "Event 1"));
        // test if the revenue was not triggered
        assertFalse(mockLogger.toString(),
            mockLogger.containsMessage(LogLevel.DEBUG, "Event 1 (revenue)"));

        // verify that the application was paused
        assertTrue(mockLogger.toString(),
            mockLogger.containsTestMessage("PackageHandler pauseSending"));
        // verify that it was not resumed
        assertFalse(mockLogger.toString(),
            mockLogger.containsTestMessage("PackageHandler resumeSending"));

        // enable again
        activityHandler.setEnabled(true);

        SystemClock.sleep(1000);

        // verify that the timer was able to resume sending
        assertTrue(mockLogger.toString(),
            mockLogger.containsTestMessage("PackageHandler resumeSending"));

        activityHandler.trackEvent("123456", null);
        activityHandler.trackRevenue(0.1, null, null);
        activityHandler.trackSubsessionEnd();
        activityHandler.trackSubsessionStart();
        SystemClock.sleep(1000);

        // verify the changed value, when the activity state is started
        assertTrue(activityHandler.isEnabled());

        // test that the event was triggered
        assertTrue(mockLogger.toString(),
            mockLogger.containsMessage(LogLevel.DEBUG, "Event 1"));
        // test that the revenue was triggered
        assertTrue(mockLogger.toString(),
            mockLogger.containsMessage(LogLevel.DEBUG, "Event 2 (revenue)"));

        // verify that the application was paused
        assertTrue(mockLogger.toString(),
            mockLogger.containsTestMessage("PackageHandler pauseSending"));
        // verify that it was also resumed
        assertTrue(mockLogger.toString(),
            mockLogger.containsTestMessage("PackageHandler resumeSending"));
            // */
    }

    public void testOpenUrl() {
        Context context = activity.getApplicationContext();

        // starting from a clean slate
        mockLogger.test("Was AdjustActivityState deleted? " + ActivityHandler.deleteActivityState(context));

        ActivityHandler activityHandler = new ActivityHandler(activity);
        activityHandler.trackSubsessionStart();

        Uri normal = Uri.parse("AdjustTests://example.com/path/inApp?adjust_foo=bar&other=stuff&adjust_key=value");
        Uri emptyQueryString = Uri.parse("AdjustTests://");
        Uri emptyString = Uri.parse("");
        Uri nullString = null;
        Uri single = Uri.parse("AdjustTests://example.com/path/inApp?adjust_foo");
        Uri prefix = Uri.parse("AdjustTests://example.com/path/inApp?adjust_=bar");
        Uri incomplete = Uri.parse("AdjustTests://example.com/path/inApp?adjust_foo=");

        activityHandler.readOpenUrl(normal);
        activityHandler.readOpenUrl(emptyQueryString);
        activityHandler.readOpenUrl(emptyString);
        activityHandler.readOpenUrl(nullString);
        activityHandler.readOpenUrl(single);
        activityHandler.readOpenUrl(prefix);
        activityHandler.readOpenUrl(incomplete);

        SystemClock.sleep(1000);

        // check that all supposed packages were sent
        // 1 session + 1 reattributions
        assertEquals(2, mockPackageHandler.queue.size());

        // check that the normal url was parsed and sent
        ActivityPackage activityPackage = mockPackageHandler.queue.get(1);

        // testing the activity kind is the correct one
        ActivityKind activityKind = activityPackage.getActivityKind();
        assertEquals(activityPackage.getExtendedString(),
            ActivityKind.REATTRIBUTION, activityKind);

        // testing the conversion from activity kind to string
        String activityKindString = activityKind.toString();
        assertEquals(activityPackage.getExtendedString(),
            "reattribution", activityKindString);

        // testing the conversion from string to activity kind
        activityKind = ActivityKind.fromString(activityKindString);
        assertEquals(activityPackage.getExtendedString(),
            ActivityKind.REATTRIBUTION, activityKind);

        // package type should be reattribute
        assertEquals(activityPackage.getExtendedString(),
            "/reattribute", activityPackage.getPath());

        // suffix should be empty
        assertEquals(activityPackage.getExtendedString(),
            "", activityPackage.getSuffix());

        Map<String,String> parameters = activityPackage.getParameters();

        // check that deep link parameters contains the base64 with the 2 keys
        assertEquals(activityPackage.getExtendedString(),
            "{\"foo\":\"bar\",\"key\":\"value\"}", parameters.get("deeplink_parameters"));

        // check that added and set both session and reattribution package
        assertTrue(mockLogger.toString(), mockLogger.containsTestMessage("PackageHandler addPackage"));
        assertTrue(mockLogger.toString(), mockLogger.containsTestMessage("PackageHandler sendFirstPackage"));
        assertTrue(mockLogger.toString(), mockLogger.containsTestMessage("PackageHandler addPackage"));
        assertTrue(mockLogger.toString(), mockLogger.containsTestMessage("PackageHandler sendFirstPackage"));

        // check that sent the reattribution package
        assertTrue(mockLogger.toString(),
            mockLogger.containsMessage(LogLevel.DEBUG, "Reattribution {key=value, foo=bar}"));
    }

    public void testFinishedTrackingActivity() {
        Context context = activity.getApplicationContext();

        // starting from a clean slate
        mockLogger.test("Was AdjustActivityState deleted? " + ActivityHandler.deleteActivityState(context));

        ActivityHandler activityHandler = new ActivityHandler(activity);
        activityHandler.trackSubsessionStart();

        activityHandler.finishedTrackingActivity(null, "testFinishedTrackingActivity://");

        SystemClock.sleep(1000);

        assertTrue(mockLogger.toString(), mockLogger.containsMessage(LogLevel.ERROR, "Unable to open deep link (testFinishedTrackingActivity://)"));

    }
}
