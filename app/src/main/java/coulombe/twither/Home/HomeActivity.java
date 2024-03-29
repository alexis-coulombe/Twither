package coulombe.twither.Home;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.coulombe.Message;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import coulombe.twither.Global.ErrorParser;
import coulombe.twither.Login.LoginActivity;
import coulombe.twither.Profile.ProfileActivity;
import coulombe.twither.R;
import coulombe.twither.Service.HttpService;
import coulombe.twither.Service.message.MessageService;
import coulombe.twither.Signup.SignupActivity;
import coulombe.twither.Singleton.Session;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeActivity extends AppCompatActivity {

    ActionBarDrawerToggle toggleDrawer;
    ArrayAdapter<Message> adapter;
    List<Message> twitMessages = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        getSupportActionBar().setTitle(R.string.Home);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#3e8dfb")));

        FloatingActionButton sendMessage = findViewById(R.id.floatingActionButton);
        sendMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openSendMessageActivity();
            }
        });


        adapter = new HomeListViewAdapter(this);
        MessageService service = HttpService.getMessage();
        service.getAll().enqueue(new Callback<List<Message>>() {
            @Override
            public void onResponse(Call<List<Message>> call, Response<List<Message>> response) {
                if(response.body() == null) {
                    try {
                        Toast.makeText(getApplicationContext(), ErrorParser.parse(response.errorBody().string()), Toast.LENGTH_SHORT).show();
                        return;
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                for(Message m : response.body()) {
                    twitMessages.add(m);
                }
                adapter.clear();
                adapter.addAll(twitMessages);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(Call<List<Message>> call, Throwable t) {
                Toast.makeText(HomeActivity.this, "Can't reach server", Toast.LENGTH_SHORT).show();
            }
        });

        ListView twit_list_view = findViewById(R.id.home_list_view);
        twit_list_view.setAdapter(adapter);

        final NavigationView drawer = findViewById(R.id.drawer);
        final DrawerLayout drawer_layout = findViewById(R.id.drawer_layout);
        final NavigationView nv = (NavigationView) findViewById(R.id.drawer);
        View header = nv.getHeaderView(0);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        toggleDrawer = new ActionBarDrawerToggle(this, drawer_layout, R.string.OpenDrawer, R.string.CloseDrawer);
        drawer_layout.addDrawerListener(toggleDrawer);
        TextView drawerTitle = header.findViewById(R.id.textView10);
        drawerTitle.setText(Session.getInstance().nickname);
        toggleDrawer.syncState();

        drawer.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem){
                int id = menuItem.getItemId();

                switch(id){
                    case R.id.navigation_item_1:{
                        ListView lv = findViewById(R.id.home_list_view);
                        lv.smoothScrollToPosition(0);
                        break;
                    }
                    case R.id.navigation_item_2:{
                        openProfileActivity();
                        break;
                    }
                    case R.id.navigation_sub_item_1:{
                        Session.setInstance(null);
                        Toast.makeText(HomeActivity.this, "Vous êtes déconnecté", Toast.LENGTH_SHORT).show();
                        openLoginActivity();
                        break;
                    }
                    case R.id.navigation_sub_item_2:{
                        AlertDialog alertDialog = new AlertDialog.Builder(HomeActivity.this).create();
                        alertDialog.setTitle("À propos de Twither");
                        alertDialog.setMessage(Html.fromHtml("<p><b>Alexis Coulombe</b></p>"));
                        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                });
                        alertDialog.show();
                        break;
                    }
                }

                drawer_layout.closeDrawers();
                return false;
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(toggleDrawer.onOptionsItemSelected(item)){
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        toggleDrawer.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        toggleDrawer.onConfigurationChanged(newConfig);
        super.onConfigurationChanged(newConfig);
    }

    private void openSendMessageActivity(){
        Intent i = new Intent(this, SendMessageActivity.class);
        startActivity(i);
    }

    private void openProfileActivity(){
        Intent i = new Intent(this, ProfileActivity.class);
        startActivity(i);
    }

    private void openLoginActivity(){
        Intent i = new Intent(this, LoginActivity.class);
        startActivity(i);
    }
}
