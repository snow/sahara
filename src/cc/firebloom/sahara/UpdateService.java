package cc.firebloom.sahara;

import java.io.IOException;

import android.app.IntentService;
import android.content.Intent;
import cc.firebloom.sahara.keyword.Keyword;
import cc.firebloom.sahara.sender.Sender;

public class UpdateService extends IntentService {

  public UpdateService() {
    super("UpdateService");
  }

  @Override
  protected void onHandleIntent(Intent intent) {
    try {
      Sender.getInst(this).updatePublicList();
      Keyword.getInst(this).updateList();
      
      //Log.w("-w-", "updated");
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

}
