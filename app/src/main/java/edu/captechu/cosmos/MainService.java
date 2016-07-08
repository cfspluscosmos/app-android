package edu.captechu.cosmos;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

/**
 * Created by luizr on 07/07/2016.
 */
public class MainService extends Service {
    int mStartMode;       // indicates how to behave if the service is killed
    IBinder mBinder;      // interface for clients that bind
    boolean mAllowRebind; // indicates whether onRebind should be used
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    int limit_light_min;
    int limit_light_max;
    int limit_temp_min;
    int limit_temp_max;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        DatabaseReference limitsRef = database.getReference("limits");
        limitsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                limit_light_min = Integer.parseInt(dataSnapshot.child("limit_light_min").getValue().toString());
                limit_light_max = Integer.parseInt(dataSnapshot.child("limit_light_max").getValue().toString());
                limit_temp_min = Integer.parseInt(dataSnapshot.child("limit_temp_min").getValue().toString());
                limit_temp_max = Integer.parseInt(dataSnapshot.child("limit_temp_max").getValue().toString());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        DatabaseReference dataRef = database.getReference("data");

        // Read from the database
        dataRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot snapshot, String previousChildKey) {

                if ((Float.parseFloat(snapshot.child("temp").getValue().toString()) < limit_temp_min ||
                        Float.parseFloat(snapshot.child("temp").getValue().toString()) > limit_temp_max) &&
                        snapshot.child("status").getValue().toString().equals("false")) {
                    Notification.Builder mBuilder =
                            new Notification.Builder(getBaseContext())
                                    .setSmallIcon(R.drawable.ic_announcement_black_24dp)
                                    .setContentTitle(snapshot.getKey().toString())
                                    .setContentText("Temperature is off limits")
                                    .setAutoCancel(true);
                    // Creates an explicit intent for an Activity in your app
                    Intent resultIntent = new Intent(getBaseContext(), MainActivity.class);

                    // The stack builder object will contain an artificial back stack for the
                    // started Activity.
                    // This ensures that navigating backward from the Activity leads out of
                    // your application to the Home screen.
                    TaskStackBuilder stackBuilder = TaskStackBuilder.create(getBaseContext());
                    // Adds the back stack for the Intent (but not the Intent itself)
                    stackBuilder.addParentStack(MainActivity.class);
                    // Adds the Intent that starts the Activity to the top of the stack
                    stackBuilder.addNextIntent(resultIntent);
                    PendingIntent resultPendingIntent =
                            stackBuilder.getPendingIntent(
                                    0,
                                    PendingIntent.FLAG_UPDATE_CURRENT
                            );
                    mBuilder.setContentIntent(resultPendingIntent);
                    NotificationManager mNotificationManager =
                            (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                    // mId allows you to update the notification later on.
                    mNotificationManager.notify(0, mBuilder.build());

                    changeStatus(snapshot.getRef().toString());
                }
                else if ((Float.parseFloat(snapshot.child("light").getValue().toString()) < limit_light_min ||
                        Float.parseFloat(snapshot.child("light").getValue().toString()) > limit_light_max) &&
                        snapshot.child("status").getValue().toString().equals("false")) {

                    Notification.Builder mBuilder =
                            new Notification.Builder(getBaseContext())
                                    .setSmallIcon(R.drawable.ic_announcement_black_24dp)
                                    .setContentTitle(snapshot.getKey().toString())
                                    .setContentText("Light is off limits")
                                    .setAutoCancel(true);
                    // Creates an explicit intent for an Activity in your app
                    Intent resultIntent = new Intent(getBaseContext(), MainActivity.class);

                    // The stack builder object will contain an artificial back stack for the
                    // started Activity.
                    // This ensures that navigating backward from the Activity leads out of
                    // your application to the Home screen.
                    TaskStackBuilder stackBuilder = TaskStackBuilder.create(getBaseContext());
                    // Adds the back stack for the Intent (but not the Intent itself)
                    stackBuilder.addParentStack(MainActivity.class);
                    // Adds the Intent that starts the Activity to the top of the stack
                    stackBuilder.addNextIntent(resultIntent);
                    PendingIntent resultPendingIntent =
                            stackBuilder.getPendingIntent(
                                    0,
                                    PendingIntent.FLAG_UPDATE_CURRENT
                            );
                    mBuilder.setContentIntent(resultPendingIntent);
                    NotificationManager mNotificationManager =
                            (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                    // mId allows you to update the notification later on.
                    mNotificationManager.notify(0, mBuilder.build());

                    changeStatus(snapshot.getRef().toString());
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String previousChildName) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String previousChildName) {

            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
            }
        });
        return mStartMode;
    }

    @Override
    public IBinder onBind(Intent intent) {
        // A client is binding to the service with bindService()
        return mBinder;
    }

    public void changeStatus(String reference) {
        DatabaseReference myRef2 = database.getReference(reference.substring(35));
        myRef2.child("status").setValue("true");
    }
}
