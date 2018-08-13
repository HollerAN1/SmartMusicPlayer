package com.smartmusic.android.smartmusicplayer;

/**
 * Instrumentation Tests for DatabaseService
 * Created by holle on 8/6/2018.
 */

import android.content.Intent;
import android.os.IBinder;
import android.support.test.InstrumentationRegistry;
import android.support.test.rule.ServiceTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.concurrent.TimeoutException;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsInstanceOf.any;
import static org.junit.Assert.assertThat;

@RunWith(AndroidJUnit4.class)
public class DatabaseServiceTests {

    @Rule
    public final ServiceTestRule mServiceRule = new ServiceTestRule();
    public SPDatabaseService mService;

    @org.junit.Before
    public void setUp() throws TimeoutException{
        testWithBoundService();
    }


    @Test
    public void initalizeDatabase() {

    }

    @Test
    public void readIntoDatabase(){

    }

    @Test
    public void testWithStartedService() {
        try {
            mServiceRule.startService(
                    new Intent(InstrumentationRegistry.getTargetContext(), SPDatabaseService.class));
        } catch (TimeoutException e){

        }
        //do something
    }

    @Test
    public void testWithBoundService() throws TimeoutException {
        // Create the service Intent.
        Intent serviceIntent =
                new Intent(InstrumentationRegistry.getTargetContext(),
                        SPDatabaseService.class);

        // Data can be passed to the service via the Intent.
//        serviceIntent.putExtra(LocalService.SEED_KEY, 42L);

        // Bind the service and grab a reference to the binder.
        IBinder binder = mServiceRule.bindService(serviceIntent);

        // Get the reference to the service, or you can call
        // public methods on the binder directly.
        mService =
                ((SPDatabaseService.SPDatabaseBinder) binder).getService();

        // Verify that the service is working correctly.
        assertThat(mService.getRandomInt(), is(any(Integer.class)));
    }
}
