package com.avoscloud.leanchatlib_demo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.avos.avoscloud.im.v2.AVIMClient;
import com.avos.avoscloud.im.v2.AVIMConversation;
import com.avos.avoscloud.im.v2.AVIMException;
import com.avos.avoscloud.im.v2.callback.AVIMClientCallback;
import com.avos.avoscloud.im.v2.callback.AVIMConversationCreatedCallback;
import com.avoscloud.leanchatlib.controller.ChatManager;
import com.avoscloud.leanchatlib.utils.Constants;


public class MainActivity extends Activity implements View.OnClickListener {
  private EditText otherIdEditText;
  private Button chatButton;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    otherIdEditText = (EditText) findViewById(R.id.otherIdEditText);
    chatButton = (Button) findViewById(R.id.chatButton);
    chatButton.setOnClickListener(this);
  }


  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    getMenuInflater().inflate(R.menu.menu_main, menu);
    return true;
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    int id = item.getItemId();
    if (id == R.id.action_settings) {
      ChatManager.getInstance().closeWithCallback(new AVIMClientCallback() {
        @Override
        public void done(AVIMClient avimClient, AVIMException e) {
          finish();
        }
      });
      return true;
    }
    return super.onOptionsItemSelected(item);
  }

  @Override
  public void onClick(View view) {
    String otherId = otherIdEditText.getText().toString();
    if (TextUtils.isEmpty(otherId) == false) {
      final ChatManager chatManager = ChatManager.getInstance();
      chatManager.fetchConversationWithUserId(otherId, new AVIMConversationCreatedCallback() {
        @Override
        public void done(AVIMConversation conversation, AVIMException e) {
          if (e != null) {
            Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
          } else {
            Intent intent = new Intent(MainActivity.this, ChatRoomActivity.class);
            intent.putExtra(Constants.CONVERSATION_ID, conversation.getConversationId());
            startActivity(intent);
          }
        }
      });
    }
  }
}
